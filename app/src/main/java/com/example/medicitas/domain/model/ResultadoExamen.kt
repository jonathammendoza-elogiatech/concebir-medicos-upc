package com.example.medicitas.domain.model

import java.time.LocalDateTime

enum class EstadoResultado(val etiqueta: String) {
    EN_RANGO("En rango"),
    FUERA_DE_RANGO("Fuera de rango"),
    PENDIENTE("Resultado pendiente")
}

enum class TipoResultado { LABORATORIO, GENETICA }

data class ResultadoExamen(
    val id: String,
    val pacienteId: String,
    val tipo: TipoResultado,
    val grupo: String,
    val grupoDetalle: String,
    val categoria: String,
    val nombre: String,
    val fechaHora: LocalDateTime,
    val valor: String?,
    val unidad: String,
    val interpretacion: String?,
    val rangoReferencia: String,
    val estado: EstadoResultado,
    val notaClinica: String? = null,
    val disponibleAprox: String? = null
)
