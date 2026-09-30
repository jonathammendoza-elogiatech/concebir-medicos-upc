package com.example.medicitas.domain.repository

import com.example.medicitas.domain.model.ResultadoExamen

interface ResultadoRepository {
    suspend fun getResultados(pacienteId: String): Result<List<ResultadoExamen>>
}
