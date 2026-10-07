package com.example.medicitas.presentation.screens.resultados

import com.example.medicitas.domain.model.Paciente
import com.example.medicitas.domain.model.ResultadoExamen
import com.example.medicitas.domain.model.TipoResultado

data class ResultadosUiState(
    val cargando: Boolean = true,
    val paciente: Paciente? = null,
    val tipo: TipoResultado = TipoResultado.LABORATORIO,
    val resultados: List<ResultadoExamen> = emptyList(),
    val error: String? = null
) {
    val visibles: List<ResultadoExamen> get() = resultados.filter { it.tipo == tipo }
}
