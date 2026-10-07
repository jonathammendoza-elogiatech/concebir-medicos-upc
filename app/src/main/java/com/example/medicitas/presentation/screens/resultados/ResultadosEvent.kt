package com.example.medicitas.presentation.screens.resultados

import com.example.medicitas.domain.model.TipoResultado

sealed interface ResultadosEvent {
    data class SeleccionarTipo(val tipo: TipoResultado) : ResultadosEvent
}
