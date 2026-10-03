package com.example.medicitas.data.remote.dto

import com.example.medicitas.domain.model.Cita
import com.example.medicitas.domain.model.EstadoCita
import java.time.LocalDate
import java.time.LocalTime

data class CitaDto(
    val id: String?,
    val fecha: String?,
    val hora: String?,
    val duracionMinutos: Int?,
    val pacienteId: String?,
    val pacienteNombre: String?,
    val pacienteIniciales: String?,
    val historiaClinica: String?,
    val edad: Int?,
    val sexo: String?,
    val tipo: String?,
    val etiquetaTratamiento: String?,
    val sede: String?,
    val consultorio: String?,
    val detalleConsultorio: String?,
    val estado: String?,
    val horaAnterior: String?,
    val esProcedimientoMayor: Boolean?
) {
    fun toDomain(): Cita {
        return Cita(
            id = id.orEmpty(),
            fecha = LocalDate.parse(fecha.orEmpty()),
            hora = LocalTime.parse(hora.orEmpty()),
            duracionMinutos = duracionMinutos ?: 20,
            pacienteId = pacienteId.orEmpty(),
            pacienteNombre = pacienteNombre.orEmpty(),
            pacienteIniciales = pacienteIniciales.orEmpty(),
            historiaClinica = historiaClinica.orEmpty(),
            edad = edad ?: 0,
            sexo = sexo.orEmpty(),
            tipo = tipo.orEmpty(),
            etiquetaTratamiento = etiquetaTratamiento,
            sede = sedeDesdeApi(sede),
            consultorio = consultorio.orEmpty(),
            detalleConsultorio = detalleConsultorio,
            estado = EstadoCita.entries.firstOrNull { it.name == estado } ?: EstadoCita.CONFIRMADA,
            horaAnterior = horaAnterior?.let { LocalTime.parse(it) },
            esProcedimientoMayor = esProcedimientoMayor ?: false
        )
    }
}

// POST /citas/{id}/atencion
data class AtencionRequestDto(
    val tipoNota: String,
    val nota: String,
    val marcarAtendida: Boolean
)
