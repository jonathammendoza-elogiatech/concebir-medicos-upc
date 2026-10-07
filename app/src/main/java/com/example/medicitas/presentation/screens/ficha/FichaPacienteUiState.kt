package com.example.medicitas.presentation.screens.ficha

import com.example.medicitas.domain.model.Paciente

enum class PestanaFicha(val titulo: String) { DATOS("Datos"), ANTECEDENTES("Antecedentes"), HISTORIAL("Historial") }

data class FichaPacienteUiState(
    val cargando: Boolean = true,
    val paciente: Paciente? = null,
    val pestana: PestanaFicha = PestanaFicha.DATOS,
    val error: String? = null
)
