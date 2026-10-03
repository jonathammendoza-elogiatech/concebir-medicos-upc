package com.example.medicitas.data.repository

import com.example.medicitas.data.remote.api.ResultadoApiService
import com.example.medicitas.data.remote.dto.datosOError
import com.example.medicitas.domain.model.ResultadoExamen
import com.example.medicitas.domain.repository.ResultadoRepository
import javax.inject.Inject

class ResultadoRepositoryImpl @Inject constructor(
    private val apiResultado: ResultadoApiService
) : ResultadoRepository {

    override suspend fun getResultados(pacienteId: String): Result<List<ResultadoExamen>> = runCatching {
        apiResultado.getResultados(pacienteId).datosOError().items.map { it.toDomain() }
    }
}
