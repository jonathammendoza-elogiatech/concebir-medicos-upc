package com.example.medicitas.presentation.screens.perfil

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicitas.domain.model.Medico
import com.example.medicitas.domain.model.Sede
import com.example.medicitas.domain.usecase.CambiarNotificacionesUseCase
import com.example.medicitas.domain.usecase.CambiarSedeUseCase
import com.example.medicitas.domain.usecase.GetPerfilUseCase
import com.example.medicitas.domain.usecase.LogoutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PerfilViewModel @Inject constructor(
    getPerfilUseCase: GetPerfilUseCase,
    private val cambiarSedeUseCase: CambiarSedeUseCase,
    private val cambiarNotificacionesUseCase: CambiarNotificacionesUseCase,
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {

    val medico: StateFlow<Medico> = getPerfilUseCase()

    private val _sesionCerrada = MutableStateFlow(false)
    val sesionCerrada: StateFlow<Boolean> = _sesionCerrada.asStateFlow()

    fun cambiarSede(sede: Sede) {
        viewModelScope.launch {
            cambiarSedeUseCase(sede).onFailure { Log.e(TAG, "Error cambiando sede", it) }
        }
    }

    fun cambiarNotificaciones(activas: Boolean) {
        viewModelScope.launch {
            cambiarNotificacionesUseCase(activas).onFailure { Log.e(TAG, "Error cambiando notificaciones", it) }
        }
    }

    fun cerrarSesion() {
        viewModelScope.launch {
            logoutUseCase().onSuccess { _sesionCerrada.value = true }
        }
    }

    private companion object {
        const val TAG = "PerfilViewModel"
    }
}
