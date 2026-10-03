package com.example.medicitas.data.repository

import com.example.medicitas.data.local.SesionCipher
import com.example.medicitas.data.local.SesionManager
import com.example.medicitas.data.local.SesionPreferences
import com.example.medicitas.data.remote.ApiConfig
import com.example.medicitas.data.remote.api.CognitoApiService
import com.example.medicitas.data.remote.dto.AuthenticationResultDto
import com.example.medicitas.data.remote.dto.InitiateAuthRequestDto
import com.example.medicitas.domain.repository.AuthRepository
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val apiCognito: CognitoApiService,
    private val sesionManager: SesionManager,
    private val sesionPreferences: SesionPreferences,
    private val sesionCipher: SesionCipher
) : AuthRepository {

    override suspend fun login(cmp: String, contrasena: String, recordarUsuario: Boolean): Result<Unit> = runCatching {
        val tokens = autenticar(
            flujo = FLUJO_CONTRASENA,
            parametros = mapOf("USERNAME" to cmp, "PASSWORD" to contrasena),
            mensajeRechazo = "CMP o contraseña incorrectos"
        )
        sesionManager.iniciar(tokens.accessToken.orEmpty())
        sesionPreferences.cmpRecordado = if (recordarUsuario) cmp else null
        // Recordar usuario también habilita el ingreso con biometría
        sesionPreferences.refreshTokenCifrado =
            if (recordarUsuario) tokens.refreshToken?.let { sesionCipher.cifrar(it) } else null
    }

    // La lectura biométrica se simula en la pantalla; aquí se renueva la sesión con el refresh token guardado
    override suspend fun loginBiometrico(): Result<Unit> = runCatching {
        val refreshToken = sesionPreferences.refreshTokenCifrado?.let { sesionCipher.descifrar(it) }
            ?: throw CredencialesInvalidasException("Ingresa con tu contraseña y marca \"Recordar usuario\" para usar biometría")
        val tokens = autenticar(
            flujo = FLUJO_REFRESH,
            parametros = mapOf("REFRESH_TOKEN" to refreshToken),
            mensajeRechazo = "Tu sesión guardada venció. Ingresa con tu contraseña"
        )
        sesionManager.iniciar(tokens.accessToken.orEmpty())
    }

    override fun getCmpRecordado(): String? = sesionPreferences.cmpRecordado

    override suspend fun logout(): Result<Unit> = runCatching {
        sesionManager.cerrar()
    }

    private suspend fun autenticar(flujo: String, parametros: Map<String, String>, mensajeRechazo: String): AuthenticationResultDto {
        val response = try {
            apiCognito.initiateAuth(InitiateAuthRequestDto(flujo, ApiConfig.COGNITO_CLIENT_ID, parametros))
        } catch (e: HttpException) {
            // Cognito responde 400 + NotAuthorizedException si la contraseña o el token no son válidos
            val error = e.response()?.errorBody()?.string().orEmpty()
            if (e.code() == 400 && "NotAuthorizedException" in error) throw CredencialesInvalidasException(mensajeRechazo)
            throw e
        } catch (e: IOException) {
            throw IOException("No se pudo conectar con el servidor. Revisa tu conexión", e)
        }
        val tokens = response.authenticationResult
        if (tokens?.accessToken == null) {
            throw IllegalStateException("Cognito solicitó un paso adicional: ${response.challengeName}")
        }
        return tokens
    }

    private companion object {
        const val FLUJO_CONTRASENA = "USER_PASSWORD_AUTH"
        const val FLUJO_REFRESH = "REFRESH_TOKEN_AUTH"
    }
}

class CredencialesInvalidasException(mensaje: String = "CMP o contraseña incorrectos") : Exception(mensaje)
