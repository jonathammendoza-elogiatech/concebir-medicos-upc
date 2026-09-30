package com.example.medicitas.domain.model

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

enum class EstadoTratamiento(val etiqueta: String) {
    EN_TRATAMIENTO("En tratamiento"),
    EN_SEGUIMIENTO("En seguimiento")
}

data class Tratamiento(
    val protocolo: String,
    val ciclo: Int,
    val dia: Int,
    val esquema: String,
    val medicacion: String,
    val inicio: LocalDate,
    val medicoResponsable: String
)

data class Antecedentes(
    val obstetricos: String,
    val obstetricosDetalle: String,
    val quirurgicos: String,
    val alergias: String,
    val grupoSanguineo: String
)

data class Atencion(
    val fecha: LocalDate,
    val hora: LocalTime,
    val tipo: String,
    val medico: String
)

data class Paciente(
    val id: String,
    val nombre: String,
    val iniciales: String,
    val edad: Int,
    val sexo: String,
    val dni: String,
    val historiaClinica: String,
    val sede: Sede,
    val fechaNacimiento: LocalDate,
    val telefono: String,
    val correo: String,
    val seguro: String,
    val plan: String,
    val estadoTratamiento: EstadoTratamiento,
    val resumenTratamiento: String,
    val tratamiento: Tratamiento?,
    val ultimaAtencion: LocalDateTime,
    val antecedentes: Antecedentes,
    val atenciones: List<Atencion>
)
