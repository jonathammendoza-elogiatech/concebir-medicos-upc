package com.example.medicitas.presentation.screens.perfil

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicitas.domain.model.Sede
import com.example.medicitas.domain.usecase.CambiarNotificacionesUseCase
import com.example.medicitas.domain.usecase.CambiarSedeUseCase
import com.example.medicitas.domain.usecase.GetPerfilUseCase
import com.example.medicitas.domain.usecase.LogoutUseCase
import com.example.medicitas.presentation.common.SnackbarType
import com.example.medicitas.presentation.event.UiEvent
import com.example.medicitas.presentation.event.UiEventBus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PerfilViewModel @Inject constructor(
    getPerfilUseCase: GetPerfilUseCase,
    private val cambiarSedeUseCase: CambiarSedeUseCase,
    private val cambiarNotificacionesUseCase: CambiarNotificacionesUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val eventBus: UiEventBus
) : ViewModel() {

    private val medico = getPerfilUseCase()

    val uiState: StateFlow<PerfilUiState> = medico
        .map { PerfilUiState(medico = it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), PerfilUiState(medico = medico.value))

    private val _effects = Channel<PerfilEffect>(Channel.BUFFERED)
    val effects: Flow<PerfilEffect> = _effects.receiveAsFlow()

    fun onEvent(event: PerfilEvent) {
        when (event) {
            is PerfilEvent.CambiarSede -> cambiarSede(event.sede)
            is PerfilEvent.CambiarNotificaciones -> cambiarNotificaciones(event.activas)
            PerfilEvent.CerrarSesion -> cerrarSesion()
        }
    }

    private fun cambiarSede(sede: Sede) {
        viewModelScope.launch {
            cambiarSedeUseCase(sede)
                .onSuccess { mostrarMensaje("Sede activa: ${sede.nombre}", SnackbarType.SUCCESS) }
                .onFailure { mostrarMensaje("No se pudo cambiar la sede", SnackbarType.ERROR) }
        }
    }

    private fun cambiarNotificaciones(activas: Boolean) {
        viewModelScope.launch {
            cambiarNotificacionesUseCase(activas)
                .onSuccess { mostrarMensaje(if (activas) "Notificaciones activadas" else "Notificaciones desactivadas") }
                .onFailure { mostrarMensaje("No se pudo actualizar las notificaciones", SnackbarType.ERROR) }
        }
    }

    private fun cerrarSesion() {
        viewModelScope.launch {
            logoutUseCase()
                .onSuccess { _effects.send(PerfilEffect.SesionCerrada) }
                .onFailure { mostrarMensaje("No se pudo cerrar sesión", SnackbarType.ERROR) }
        }
    }

    private suspend fun mostrarMensaje(mensaje: String, tipo: SnackbarType = SnackbarType.INFO) =
        eventBus.sendEvent(UiEvent.ShowSnackbar(mensaje, tipo))
}
