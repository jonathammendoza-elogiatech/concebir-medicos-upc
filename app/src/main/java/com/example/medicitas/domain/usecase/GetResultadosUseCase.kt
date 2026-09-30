package com.example.medicitas.domain.usecase

import com.example.medicitas.domain.model.ResultadoExamen
import com.example.medicitas.domain.repository.ResultadoRepository
import javax.inject.Inject

class GetResultadosUseCase @Inject constructor(private val repository: ResultadoRepository) {
    suspend operator fun invoke(pacienteId: String): Result<List<ResultadoExamen>> =
        repository.getResultados(pacienteId).map { lista -> lista.sortedByDescending { it.fechaHora } }
}
