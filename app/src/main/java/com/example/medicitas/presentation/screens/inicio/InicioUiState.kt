package com.example.medicitas.presentation.screens.inicio

import com.example.medicitas.domain.model.ResumenDia

data class InicioUiState(
    val cargando: Boolean = true,
    val resumen: ResumenDia? = null
)
