package com.example.medicitas.presentation.screens.pacientes

import com.example.medicitas.domain.model.EstadoTratamiento
import com.example.medicitas.domain.model.Paciente
import com.example.medicitas.domain.model.Sede

data class PacientesUiState(
    val cargando: Boolean = true,
    val consulta: String = "",
    val filtroEstado: EstadoTratamiento? = null,
    val sede: Sede = Sede.SAN_ISIDRO,
    val totalAsignados: Int = 0,
    val pacientes: List<Paciente> = emptyList(),
    val error: String? = null
)
