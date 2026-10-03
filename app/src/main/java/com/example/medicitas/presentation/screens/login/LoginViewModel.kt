package com.example.medicitas.presentation.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicitas.domain.usecase.GetBiometriaDisponibleUseCase
import com.example.medicitas.domain.usecase.GetCmpRecordadoUseCase
import com.example.medicitas.domain.usecase.LoginBiometricoUseCase
import com.example.medicitas.domain.usecase.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LoginUiState(
    val cmp: String = "",
    val contrasena: String = "",
    val recordarUsuario: Boolean = false,
    val mostrarContrasena: Boolean = false,
    val cargando: Boolean = false,
    val error: String? = null,
    val mostrarBiometria: Boolean = false,
    val biometriaDisponible: Boolean = false,
    val sesionIniciada: Boolean = false
) {
    val puedeIngresar: Boolean get() = cmp.isNotBlank() && contrasena.isNotBlank() && !cargando
}

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val loginBiometricoUseCase: LoginBiometricoUseCase,
    getCmpRecordadoUseCase: GetCmpRecordadoUseCase,
    getBiometriaDisponibleUseCase: GetBiometriaDisponibleUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState(biometriaDisponible = getBiometriaDisponibleUseCase()))
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    init {
        val cmpRecordado = getCmpRecordadoUseCase()
        if (cmpRecordado != null) {
            _uiState.update { it.copy(cmp = cmpRecordado, recordarUsuario = true) }
        }
    }

    fun onCmpChange(valor: String) {
        val soloDigitos = valor.filter { it.isDigit() }.take(MAX_DIGITOS_CMP)
        _uiState.update { it.copy(cmp = soloDigitos, error = null) }
    }

    fun onContrasenaChange(valor: String) {
        _uiState.update { it.copy(contrasena = valor, error = null) }
    }

    fun onRecordarChange(valor: Boolean) {
        _uiState.update { it.copy(recordarUsuario = valor) }
    }

    fun alternarVisibilidad() {
        _uiState.update { it.copy(mostrarContrasena = !it.mostrarContrasena) }
    }

    fun iniciarSesion() {
        val estado = _uiState.value
        if (!estado.puedeIngresar) return
        _uiState.update { it.copy(cargando = true, error = null) }
        viewModelScope.launch {
            loginUseCase(estado.cmp, estado.contrasena, estado.recordarUsuario)
                .onSuccess { _uiState.update { it.copy(cargando = false, sesionIniciada = true) } }
                .onFailure { e -> _uiState.update { it.copy(cargando = false, error = e.message ?: MENSAJE_CREDENCIALES) } }
        }
    }

    fun mostrarBiometria(mostrar: Boolean) {
        _uiState.update { it.copy(mostrarBiometria = mostrar) }
    }

    fun confirmarBiometria() {
        _uiState.update { it.copy(mostrarBiometria = false, cargando = true, error = null) }
        viewModelScope.launch {
            loginBiometricoUseCase()
                .onSuccess { _uiState.update { it.copy(cargando = false, sesionIniciada = true) } }
                .onFailure { e -> _uiState.update { it.copy(cargando = false, error = e.message ?: "No se pudo validar tu biometría") } }
        }
    }

    private companion object {
        const val MAX_DIGITOS_CMP = 6
        const val MENSAJE_CREDENCIALES = "CMP o contraseña incorrectos"
    }
}
