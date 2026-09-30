package com.example.medicitas.presentation.common

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

// Formatos en español de Perú ("setiembre", "a. m.", "12:00 m.")
private val MESES = listOf(
    "enero", "febrero", "marzo", "abril", "mayo", "junio",
    "julio", "agosto", "setiembre", "octubre", "noviembre", "diciembre"
)

private val FORMATO_FECHA_CORTA = DateTimeFormatter.ofPattern("dd/MM/yyyy")

fun DayOfWeek.nombre(): String = when (this) {
    DayOfWeek.MONDAY -> "Lunes"
    DayOfWeek.TUESDAY -> "Martes"
    DayOfWeek.WEDNESDAY -> "Miércoles"
    DayOfWeek.THURSDAY -> "Jueves"
    DayOfWeek.FRIDAY -> "Viernes"
    DayOfWeek.SATURDAY -> "Sábado"
    DayOfWeek.SUNDAY -> "Domingo"
}

fun DayOfWeek.abreviatura(): String = nombre().take(2)

fun LocalTime.formatoHora(): String {
    if (hour == 12 && minute == 0) return "12:00 m."
    val hora12 = if (hour % 12 == 0) 12 else hour % 12
    val sufijo = if (hour < 12) "a. m." else "p. m."
    return "%02d:%02d %s".format(hora12, minute, sufijo)
}

fun LocalDate.nombreMes(): String = MESES[monthValue - 1]

/** "Jueves, 22 de setiembre" */
fun LocalDate.formatoDiaMes(): String = "${dayOfWeek.nombre()}, $dayOfMonth de ${nombreMes()}"

/** "Jueves, 22 de setiembre de 2024" */
fun LocalDate.formatoLargo(): String = "${formatoDiaMes()} de $year"

/** "22/09/2024" */
fun LocalDate.formatoCorto(): String = format(FORMATO_FECHA_CORTA)

/** "22/09/2024 · 10:30 a. m." */
fun LocalDateTime.formatoCorto(): String = "${toLocalDate().formatoCorto()} · ${toLocalTime().formatoHora()}"
