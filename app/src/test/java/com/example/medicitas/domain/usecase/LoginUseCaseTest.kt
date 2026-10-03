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
        override fun tieneSesionGuardada(): Boolean = false
        override suspend fun logout(): Result<Unit> = Result.success(Unit)
    }

    private val medicoRepository = MedicoRepositoryFalso()
    private val citaRepository = CitaRepositoryFalso()
    private fun useCase(repo: AuthRepository, citas: CitaRepositoryFalso = citaRepository) =
        LoginUseCase(repo, CargarDatosSesionUseCase(medicoRepository, citas))

    @Test
    fun `credenciales validas inician sesion y cargan perfil y agenda`() = runBlocking {
        val repo = FakeAuthRepository()
        val resultado = useCase(repo)(" 45782 ", "clave", recordarUsuario = true)
        assertTrue(resultado.isSuccess)
        assertEquals("45782", repo.cmpRecibido)
        assertEquals("Ana Torres Delgado", medicoRepository.medico.value.nombre)
        assertTrue(citaRepository.citas.value.isNotEmpty())
    }

    @Test
    fun `credenciales invalidas devuelven error`() = runBlocking {
        val resultado = useCase(FakeAuthRepository())("45782", "otra", recordarUsuario = false)
        assertTrue(resultado.isFailure)
    }

    @Test
    fun `si no carga la agenda el login falla con mensaje claro`() = runBlocking {
        val resultado = useCase(FakeAuthRepository(), CitaRepositoryFalso(falloAlCargar = true))("45782", "clave", recordarUsuario = false)
        assertTrue(resultado.isFailure)
        assertEquals("No se pudo cargar tu perfil y agenda. Intenta nuevamente", resultado.exceptionOrNull()?.message)
    }
}
