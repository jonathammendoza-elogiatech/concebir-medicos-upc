package com.example.medicitas.presentation.screens.login

data class LoginUiState(
    val cmp: String = "",
    val contrasena: String = "",
    val recordarUsuario: Boolean = false,
    val mostrarContrasena: Boolean = false,
    val cargando: Boolean = false,
    val error: String? = null,
    val mostrarBiometria: Boolean = false,
    val biometriaDisponible: Boolean = false
) {
    val puedeIngresar: Boolean get() = cmp.isNotBlank() && contrasena.isNotBlank() && !cargando
}
