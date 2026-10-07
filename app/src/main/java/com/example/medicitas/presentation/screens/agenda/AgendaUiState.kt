package com.example.medicitas.presentation.screens.agenda

import com.example.medicitas.domain.model.Cita
import com.example.medicitas.domain.model.Sede
import java.time.LocalDate
import java.time.LocalTime

enum class VistaAgenda { DIA, SEMANA }

data class AgendaUiState(
    val hoy: LocalDate,
    val horaActual: LocalTime,
    val sedeActiva: Sede,
    val vista: VistaAgenda = VistaAgenda.DIA,
    val fechaSeleccionada: LocalDate = hoy,
    val sedeFiltro: Sede? = sedeActiva,
    val citas: List<Cita> = emptyList(),
    val diasConCitas: Set<LocalDate> = emptySet(),
    val actualizadoA: LocalTime? = null
)
