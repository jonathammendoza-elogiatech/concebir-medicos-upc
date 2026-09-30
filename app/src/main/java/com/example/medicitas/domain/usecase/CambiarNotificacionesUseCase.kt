package com.example.medicitas.domain.usecase

import com.example.medicitas.domain.repository.MedicoRepository
import javax.inject.Inject

class CambiarNotificacionesUseCase @Inject constructor(private val repository: MedicoRepository) {
    suspend operator fun invoke(activas: Boolean): Result<Unit> = repository.cambiarNotificaciones(activas)
}
