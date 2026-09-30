package com.example.medicitas.domain.repository

import com.example.medicitas.domain.model.Paciente

interface PacienteRepository {
    suspend fun getPacientes(): Result<List<Paciente>>
    suspend fun getPaciente(id: String): Result<Paciente>
}
