package com.example.medicitas.presentation.screens.perfil

import com.example.medicitas.domain.model.Sede

sealed interface PerfilEvent {
    data class CambiarSede(val sede: Sede) : PerfilEvent
    data class CambiarNotificaciones(val activas: Boolean) : PerfilEvent
    data object CerrarSesion : PerfilEvent
}

// Efectos de una sola vez: no se guardan en el UiState
sealed interface PerfilEffect {
    data object SesionCerrada : PerfilEffect
}
