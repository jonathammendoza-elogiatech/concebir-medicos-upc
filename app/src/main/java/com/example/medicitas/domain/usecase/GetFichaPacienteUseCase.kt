package com.example.medicitas.domain.usecase

import com.example.medicitas.domain.model.Paciente
import com.example.medicitas.domain.repository.PacienteRepository
import javax.inject.Inject

class GetFichaPacienteUseCase @Inject constructor(private val repository: PacienteRepository) {
    suspend operator fun invoke(pacienteId: String): Result<Paciente> = repository.getPaciente(pacienteId)
}
