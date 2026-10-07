package com.example.medicitas.presentation.screens.login

sealed interface LoginEvent {
    data class CmpChange(val valor: String) : LoginEvent
    data class ContrasenaChange(val valor: String) : LoginEvent
    data class RecordarChange(val valor: Boolean) : LoginEvent
    data object AlternarVisibilidad : LoginEvent
    data object IniciarSesion : LoginEvent
    data object AbrirBiometria : LoginEvent
    data object CerrarBiometria : LoginEvent
    data object ConfirmarBiometria : LoginEvent
}

// Efectos de una sola vez: no se guardan en el UiState
sealed interface LoginEffect {
    data object SesionIniciada : LoginEffect
}
