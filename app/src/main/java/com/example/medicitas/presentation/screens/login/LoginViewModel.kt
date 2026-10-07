package com.example.medicitas.presentation.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicitas.domain.usecase.GetBiometriaDisponibleUseCase
import com.example.medicitas.domain.usecase.GetCmpRecordadoUseCase
import com.example.medicitas.domain.usecase.LoginBiometricoUseCase
import com.example.medicitas.domain.usecase.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val loginBiometricoUseCase: LoginBiometricoUseCase,
    getCmpRecordadoUseCase: GetCmpRecordadoUseCase,
    getBiometriaDisponibleUseCase: GetBiometriaDisponibleUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState(biometriaDisponible = getBiometriaDisponibleUseCase()))
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private val _effects = Channel<LoginEffect>(Channel.BUFFERED)
    val effects: Flow<LoginEffect> = _effects.receiveAsFlow()

    init {
        val cmpRecordado = getCmpRecordadoUseCase()
        if (cmpRecordado != null) {
            _uiState.update { it.copy(cmp = cmpRecordado, recordarUsuario = true) }
        }
    }

    fun onEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.CmpChange -> onCmpChange(event.valor)
            is LoginEvent.ContrasenaChange -> _uiState.update { it.copy(contrasena = event.valor, error = null) }
            is LoginEvent.RecordarChange -> _uiState.update { it.copy(recordarUsuario = event.valor) }
            LoginEvent.AlternarVisibilidad -> _uiState.update { it.copy(mostrarContrasena = !it.mostrarContrasena) }
            LoginEvent.IniciarSesion -> iniciarSesion()
            LoginEvent.AbrirBiometria -> _uiState.update { it.copy(mostrarBiometria = true) }
            LoginEvent.CerrarBiometria -> _uiState.update { it.copy(mostrarBiometria = false) }
            LoginEvent.ConfirmarBiometria -> confirmarBiometria()
        }
    }

    private fun onCmpChange(valor: String) {
        val soloDigitos = valor.filter { it.isDigit() }.take(MAX_DIGITOS_CMP)
        _uiState.update { it.copy(cmp = soloDigitos, error = null) }
    }

    private fun iniciarSesion() {
        val estado = _uiState.value
        if (!estado.puedeIngresar) return
        _uiState.update { it.copy(cargando = true, error = null) }
        viewModelScope.launch {
            loginUseCase(estado.cmp, estado.contrasena, estado.recordarUsuario)
                .onSuccess {
                    _uiState.update { it.copy(cargando = false) }
                    _effects.send(LoginEffect.SesionIniciada)
                }
                .onFailure { e -> _uiState.update { it.copy(cargando = false, error = e.message ?: MENSAJE_CREDENCIALES) } }
        }
    }

    private fun confirmarBiometria() {
        _uiState.update { it.copy(mostrarBiometria = false, cargando = true, error = null) }
        viewModelScope.launch {
            loginBiometricoUseCase()
                .onSuccess {
                    _uiState.update { it.copy(cargando = false) }
                    _effects.send(LoginEffect.SesionIniciada)
                }
                .onFailure { e -> _uiState.update { it.copy(cargando = false, error = e.message ?: "No se pudo validar tu biometría") } }
        }
    }

    private companion object {
        const val MAX_DIGITOS_CMP = 6
        const val MENSAJE_CREDENCIALES = "CMP o contraseña incorrectos"
    }
}
