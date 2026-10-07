package com.example.medicitas.presentation.screens.pacientes

import com.example.medicitas.domain.model.EstadoTratamiento

sealed interface PacientesEvent {
    data class ConsultaChange(val texto: String) : PacientesEvent
    data class FiltrarEstado(val estado: EstadoTratamiento?) : PacientesEvent
}
