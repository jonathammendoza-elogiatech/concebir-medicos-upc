package com.example.medicitas.data.remote.dto

import com.example.medicitas.domain.model.EstadoResultado
import com.example.medicitas.domain.model.ResultadoExamen
import com.example.medicitas.domain.model.TipoResultado
import java.time.LocalDateTime

data class ResultadoDto(
    val id: String?,
    val pacienteId: String?,
    val tipo: String?,
    val grupo: String?,
    val grupoDetalle: String?,
    val categoria: String?,
    val nombre: String?,
    val fechaHora: String?,
    val valor: String?,
    val unidad: String?,
    val interpretacion: String?,
    val rangoReferencia: String?,
    val estado: String?,
    val notaClinica: String?,
    val disponibleAprox: String?
) {
    fun toDomain(): ResultadoExamen {
        return ResultadoExamen(
            id = id.orEmpty(),
            pacienteId = pacienteId.orEmpty(),
            tipo = TipoResultado.entries.firstOrNull { it.name == tipo } ?: TipoResultado.LABORATORIO,
            grupo = grupo.orEmpty(),
            grupoDetalle = grupoDetalle.orEmpty(),
            categoria = categoria.orEmpty(),
            nombre = nombre.orEmpty(),
            fechaHora = LocalDateTime.parse(fechaHora.orEmpty()),
            valor = valor,
            unidad = unidad.orEmpty(),
            interpretacion = interpretacion,
            rangoReferencia = rangoReferencia.orEmpty(),
            estado = EstadoResultado.entries.firstOrNull { it.name == estado } ?: EstadoResultado.PENDIENTE,
            notaClinica = notaClinica,
            disponibleAprox = disponibleAprox
        )
    }
}
