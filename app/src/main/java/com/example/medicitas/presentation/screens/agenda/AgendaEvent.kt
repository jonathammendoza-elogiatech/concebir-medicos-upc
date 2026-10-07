package com.example.medicitas.presentation.screens.agenda

import com.example.medicitas.domain.model.Sede
import java.time.LocalDate

sealed interface AgendaEvent {
    data class CambiarVista(val vista: VistaAgenda) : AgendaEvent
    data class SeleccionarFecha(val fecha: LocalDate) : AgendaEvent
    data class FiltrarSede(val sede: Sede?) : AgendaEvent
}
