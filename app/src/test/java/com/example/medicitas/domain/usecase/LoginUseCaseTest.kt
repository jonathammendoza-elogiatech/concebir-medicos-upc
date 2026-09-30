package com.example.medicitas.domain.usecase

import com.example.medicitas.domain.repository.AuthRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class LoginUseCaseTest {

    private class FakeAuthRepository : AuthRepository {
        var cmpRecibido: String? = null
        override suspend fun login(cmp: String, contrasena: String, recordarUsuario: Boolean): Result<Unit> {
            cmpRecibido = cmp
            return if (cmp == "45782" && contrasena == "clave") Result.success(Unit)
            else Result.failure(IllegalArgumentException("CMP o contraseña incorrectos"))
        }
        override suspend fun loginBiometrico(): Result<Unit> = Result.success(Unit)
        override fun getCmpRecordado(): String? = null
        override suspend fun logout(): Result<Unit> = Result.success(Unit)
    }

    @Test
    fun `credenciales validas inician sesion`() = runBlocking {
        val repo = FakeAuthRepository()
        val resultado = LoginUseCase(repo)(" 45782 ", "clave", recordarUsuario = true)
        assertTrue(resultado.isSuccess)
        assertEquals("45782", repo.cmpRecibido)
    }

    @Test
    fun `credenciales invalidas devuelven error`() = runBlocking {
        val resultado = LoginUseCase(FakeAuthRepository())("45782", "otra", recordarUsuario = false)
        assertTrue(resultado.isFailure)
    }
}
