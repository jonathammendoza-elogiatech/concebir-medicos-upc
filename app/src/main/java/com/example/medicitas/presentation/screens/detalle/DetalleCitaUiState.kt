package com.example.medicitas.presentation.screens.detalle

import com.example.medicitas.domain.model.DetalleCita
import java.time.LocalDate
import java.time.LocalTime

data class DetalleCitaUiState(
    val cargando: Boolean = true,
    val detalle: DetalleCita? = null,
    val error: String? = null,
    val hoy: LocalDate? = null,
    val horaActual: LocalTime? = null
)
