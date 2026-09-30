package com.example.medicitas.domain.model

import java.time.LocalDate
import java.time.LocalTime

data class ResumenDia(
    val fecha: LocalDate,
    val horaActual: LocalTime,
    val medico: Medico,
    val totalCitas: Int,
    val atendidas: Int,
    val pendientesRegistro: Int,
    val proximaCita: Cita?,
    val siguientesCitas: List<Cita>
)
