package com.example.medicitas.data.local

import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId

// Fecha y hora de la clínica (Lima), no las del dispositivo: la agenda se programa en esa zona
object RelojClinica {
    private val ZONA: ZoneId = ZoneId.of("America/Lima")

    fun hoy(): LocalDate = LocalDate.now(ZONA)

    fun ahora(): LocalTime = LocalTime.now(ZONA)
}
