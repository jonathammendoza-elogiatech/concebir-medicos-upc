package com.example.medicitas.data.repository

import com.example.medicitas.data.remote.api.PacienteApiService
import com.example.medicitas.data.remote.dto.datosOError
import com.example.medicitas.domain.model.Paciente
import com.example.medicitas.domain.repository.PacienteRepository
import javax.inject.Inject

class PacienteRepositoryImpl @Inject constructor(
    private val apiPaciente: PacienteApiService
) : PacienteRepository {

    override suspend fun getPacientes(): Result<List<Paciente>> = runCatching {
        apiPaciente.getPacientes().datosOError().items.map { it.toDomain() }
    }

    override suspend fun getPaciente(id: String): Result<Paciente> = runCatching {
        apiPaciente.getPaciente(id).datosOError().toDomain()
    }
}
