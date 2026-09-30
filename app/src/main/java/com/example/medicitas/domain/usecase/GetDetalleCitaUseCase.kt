package com.example.medicitas.domain.usecase

import com.example.medicitas.domain.model.DetalleCita
import com.example.medicitas.domain.repository.CitaRepository
import com.example.medicitas.domain.repository.PacienteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetDetalleCitaUseCase @Inject constructor(
    private val citaRepository: CitaRepository,
    private val pacienteRepository: PacienteRepository
) {
    val fechaHoy get() = citaRepository.fechaHoy
    val horaActual get() = citaRepository.horaActual

    // Se re-emite cuando cambia el estado de la cita (p. ej. al registrar la atención)
    operator fun invoke(citaId: String): Flow<Result<DetalleCita>> =
        citaRepository.citas.map { citas ->
            runCatching {
                val cita = citas.firstOrNull { it.id == citaId } ?: throw NoSuchElementException("Cita no encontrada")
                DetalleCita(cita, pacienteRepository.getPaciente(cita.pacienteId).getOrThrow())
            }
        }
}
