package com.example.medicitas.presentation.screens.registrar

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicitas.domain.model.DetalleCita
import com.example.medicitas.domain.model.Medico
import com.example.medicitas.domain.model.TipoNota
import com.example.medicitas.domain.usecase.ConfirmarAtencionUseCase
import com.example.medicitas.domain.usecase.GetDetalleCitaUseCase
import com.example.medicitas.domain.usecase.GetPerfilUseCase
import com.example.medicitas.presentation.navigation.RutasNav
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

enum class EstadoFirma { OCULTA, ESPERANDO, FIRMANDO, FIRMADA }

data class RegistrarAtencionUiState(
    val detalle: DetalleCita? = null,
    val medico: Medico? = null,
    val fechaHora: LocalDateTime? = null,
    val tipoNota: TipoNota = TipoNota.EVOLUCION,
    val nota: String = "",
    val marcarAtendida: Boolean = true,
    val firma: EstadoFirma = EstadoFirma.OCULTA,
    val usarContrasena: Boolean = false,
    val contrasena: String = "",
    val error: String? = null,
    val registrada: Boolean = false
) {
    val puedeGuardar: Boolean get() = nota.isNotBlank() && detalle != null
}

@HiltViewModel
class RegistrarAtencionViewModel @Inject constructor(
    private val getDetalleCitaUseCase: GetDetalleCitaUseCase,
    private val confirmarAtencionUseCase: ConfirmarAtencionUseCase,
    getPerfilUseCase: GetPerfilUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val citaId: String = checkNotNull(savedStateHandle[RutasNav.ARG_CITA_ID])

    private val _uiState = MutableStateFlow(RegistrarAtencionUiState(medico = getPerfilUseCase().value))
    val uiState: StateFlow<RegistrarAtencionUiState> = _uiState.asStateFlow()

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

    fun seleccionarTipo(tipo: TipoNota) = _uiState.update { it.copy(tipoNota = tipo) }

    fun onNotaChange(texto: String) =
        _uiState.update { it.copy(nota = texto.take(ConfirmarAtencionUseCase.MAX_CARACTERES), error = null) }

    fun insertarFragmento(fragmento: String) = _uiState.update {
        val separador = if (it.nota.isEmpty() || it.nota.endsWith(" ") || it.nota.endsWith("\n")) "" else " "
        it.copy(nota = (it.nota + separador + fragmento).take(ConfirmarAtencionUseCase.MAX_CARACTERES))
    }

    fun onMarcarAtendidaChange(valor: Boolean) = _uiState.update { it.copy(marcarAtendida = valor) }

    fun solicitarFirma() {
        if (_uiState.value.puedeGuardar) _uiState.update { it.copy(firma = EstadoFirma.ESPERANDO, error = null) }
    }

    fun cancelarFirma() = _uiState.update { it.copy(firma = EstadoFirma.OCULTA, usarContrasena = false, contrasena = "") }

    fun alternarContrasena() = _uiState.update { it.copy(usarContrasena = !it.usarContrasena) }

    fun onContrasenaChange(valor: String) = _uiState.update { it.copy(contrasena = valor) }

    // Firma biométrica simulada: en el piloto no se invoca BiometricPrompt
    fun firmar() {
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
                    _uiState.update { it.copy(registrada = true) }
                }
                .onFailure { e -> _uiState.update { it.copy(firma = EstadoFirma.OCULTA, error = e.message) } }
        }
    }

    private companion object {
        const val DURACION_LECTURA_MS = 900L
        const val DURACION_CONFIRMACION_MS = 1_200L
    }
}
