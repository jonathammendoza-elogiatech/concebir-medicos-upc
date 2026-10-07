package com.example.medicitas.presentation.screens.ficha

sealed interface FichaPacienteEvent {
    data class SeleccionarPestana(val pestana: PestanaFicha) : FichaPacienteEvent
}
