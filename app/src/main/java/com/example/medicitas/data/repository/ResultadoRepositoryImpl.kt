package com.example.medicitas.data.repository

import com.example.medicitas.data.mock.SysmedicalMockDataSource
import com.example.medicitas.domain.model.ResultadoExamen
import com.example.medicitas.domain.repository.ResultadoRepository
import javax.inject.Inject

class ResultadoRepositoryImpl @Inject constructor(
    private val dataSource: SysmedicalMockDataSource
) : ResultadoRepository {

    override suspend fun getResultados(pacienteId: String): Result<List<ResultadoExamen>> = runCatching {
        dataSource.simularLatencia()
        dataSource.resultados.filter { it.pacienteId == pacienteId }
    }
}
