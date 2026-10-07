package com.example.medicitas.presentation.screens.registrar

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicitas.domain.usecase.ConfirmarAtencionUseCase
import com.example.medicitas.domain.usecase.GetDetalleCitaUseCase
import com.example.medicitas.domain.usecase.GetPerfilUseCase
import com.example.medicitas.presentation.common.SnackbarType
import com.example.medicitas.presentation.event.UiEvent
import com.example.medicitas.presentation.event.UiEventBus
import com.example.medicitas.presentation.navigation.RutasNav
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegistrarAtencionViewModel @Inject constructor(
    private val getDetalleCitaUseCase: GetDetalleCitaUseCase,
    private val confirmarAtencionUseCase: ConfirmarAtencionUseCase,
    private val eventBus: UiEventBus,
    getPerfilUseCase: GetPerfilUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val citaId: String = checkNotNull(savedStateHandle[RutasNav.ARG_CITA_ID])

    private val _uiState = MutableStateFlow(RegistrarAtencionUiState(medico = getPerfilUseCase().value))
    val uiState: StateFlow<RegistrarAtencionUiState> = _uiState.asStateFlow()

    private val _effects = Channel<RegistrarAtencionEffect>(Channel.BUFFERED)
    val effects: Flow<RegistrarAtencionEffect> = _effects.receiveAsFlow()

    init {
        viewModelScope.launch {
            getDetalleCitaUseCase(citaId).first()
                .onSuccess { detalle ->
                    _uiState.update {
                        it.copy(
                            detalle = detalle,
                            fechaHora = getDetalleCitaUseCase.fechaHoy.atTime(getDetalleCitaUseCase.horaActual)
                        )
                    }
                }
                .onFailure { e -> _uiState.update { it.copy(error = e.message) } }
        }
    }

    fun onEvent(event: RegistrarAtencionEvent) {
        when (event) {
            is RegistrarAtencionEvent.SeleccionarTipo -> _uiState.update { it.copy(tipoNota = event.tipo) }
            is RegistrarAtencionEvent.NotaChange ->
                _uiState.update { it.copy(nota = event.texto.take(ConfirmarAtencionUseCase.MAX_CARACTERES), error = null) }
            is RegistrarAtencionEvent.InsertarFragmento -> insertarFragmento(event.fragmento)
            is RegistrarAtencionEvent.MarcarAtendidaChange -> _uiState.update { it.copy(marcarAtendida = event.valor) }
            RegistrarAtencionEvent.SolicitarFirma -> solicitarFirma()
            RegistrarAtencionEvent.CancelarFirma ->
                _uiState.update { it.copy(firma = EstadoFirma.OCULTA, usarContrasena = false, contrasena = "") }
            RegistrarAtencionEvent.AlternarContrasena -> _uiState.update { it.copy(usarContrasena = !it.usarContrasena) }
            is RegistrarAtencionEvent.ContrasenaChange -> _uiState.update { it.copy(contrasena = event.valor) }
            RegistrarAtencionEvent.Firmar -> firmar()
        }
    }

    private fun insertarFragmento(fragmento: String) = _uiState.update {
        val separador = if (it.nota.isEmpty() || it.nota.endsWith(" ") || it.nota.endsWith("\n")) "" else " "
        it.copy(nota = (it.nota + separador + fragmento).take(ConfirmarAtencionUseCase.MAX_CARACTERES))
    }

    private fun solicitarFirma() {
        if (_uiState.value.puedeGuardar) _uiState.update { it.copy(firma = EstadoFirma.ESPERANDO, error = null) }
    }

    // Firma biométrica simulada: en el piloto no se invoca BiometricPrompt
    private fun firmar() {
        val estado = _uiState.value
        if (estado.firma == EstadoFirma.FIRMANDO) return
        if (estado.usarContrasena && estado.contrasena.isBlank()) return
        _uiState.update { it.copy(firma = EstadoFirma.FIRMANDO) }
        viewModelScope.launch {
            delay(DURACION_LECTURA_MS)
            confirmarAtencionUseCase(citaId, estado.tipoNota, estado.nota, estado.marcarAtendida)
                .onSuccess {
                    _uiState.update { it.copy(firma = EstadoFirma.FIRMADA) }
                    delay(DURACION_CONFIRMACION_MS)
                    // El mensaje va antes del efecto: al navegar se cancela el viewModelScope
                    val hc = _uiState.value.detalle?.paciente?.historiaClinica
                    eventBus.sendEvent(UiEvent.ShowSnackbar(listOfNotNull("Atención registrada", hc).joinToString(" · "), SnackbarType.SUCCESS))
                    _effects.send(RegistrarAtencionEffect.Registrada)
                }
                .onFailure { e ->
                    _uiState.update { it.copy(firma = EstadoFirma.OCULTA, error = e.message) }
                    eventBus.sendEvent(UiEvent.ShowSnackbar(e.message ?: "No se pudo registrar la atención", SnackbarType.ERROR))
                }
        }
    }

    private companion object {
        const val DURACION_LECTURA_MS = 900L
        const val DURACION_CONFIRMACION_MS = 1_200L
    }
}
