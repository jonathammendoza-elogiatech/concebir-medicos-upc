package com.example.medicitas.data.repository

import com.example.medicitas.data.mock.SysmedicalMockDataSource
import com.example.medicitas.domain.model.Paciente
import com.example.medicitas.domain.repository.PacienteRepository
import javax.inject.Inject

class PacienteRepositoryImpl @Inject constructor(
    private val dataSource: SysmedicalMockDataSource
) : PacienteRepository {

    override suspend fun getPacientes(): Result<List<Paciente>> = runCatching {
        dataSource.simularLatencia()
        dataSource.pacientes.value
    }

    override suspend fun getPaciente(id: String): Result<Paciente> = runCatching {
        dataSource.pacientes.value.firstOrNull { it.id == id } ?: throw NoSuchElementException("Paciente no encontrado")
    }
}
