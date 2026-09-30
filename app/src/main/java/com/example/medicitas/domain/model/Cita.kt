package com.example.medicitas.domain.model

import java.time.LocalDate
import java.time.LocalTime

enum class EstadoCita(val etiqueta: String) {
    CONFIRMADA("Confirmada"),
    ATENDIDA("Atendida"),
    REPROGRAMADA("Reprogramada"),
    ANULADA("Anulada")
}

enum class TipoNota(val etiqueta: String) {
    EVOLUCION("Evolución"),
    INDICACION("Indicación"),
    OBSERVACION("Observación")
}

data class Cita(
    val id: String,
    val fecha: LocalDate,
    val hora: LocalTime,
    val duracionMinutos: Int,
    val pacienteId: String,
    val pacienteNombre: String,
    val pacienteIniciales: String,
    val historiaClinica: String,
    val edad: Int,
    val sexo: String,
    val tipo: String,
    val etiquetaTratamiento: String? = null,
    val sede: Sede,
    val consultorio: String,
    val detalleConsultorio: String? = null,
    val estado: EstadoCita,
    val horaAnterior: LocalTime? = null,
    val esProcedimientoMayor: Boolean = false
) {
    val horaFin: LocalTime get() = hora.plusMinutes(duracionMinutos.toLong())
}
