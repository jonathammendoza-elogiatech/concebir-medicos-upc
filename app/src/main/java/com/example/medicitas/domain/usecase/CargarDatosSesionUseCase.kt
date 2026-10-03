package com.example.medicitas.domain.usecase

import com.example.medicitas.domain.repository.CitaRepository
import com.example.medicitas.domain.repository.MedicoRepository
import javax.inject.Inject

// Perfil y agenda deben estar cargados antes de entrar: varias pantallas leen el médico al abrirse
class CargarDatosSesionUseCase @Inject constructor(
    private val medicoRepository: MedicoRepository,
    private val citaRepository: CitaRepository
) {
    suspend operator fun invoke(): Result<Unit> = runCatching {
        medicoRepository.cargarPerfil().getOrThrow()
        citaRepository.cargarCitas().getOrThrow()
    }.recoverCatching { causa ->
        throw IllegalStateException("No se pudo cargar tu perfil y agenda. Intenta nuevamente", causa)
    }
}
