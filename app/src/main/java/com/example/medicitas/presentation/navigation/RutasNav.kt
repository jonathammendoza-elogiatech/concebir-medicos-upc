package com.example.medicitas.presentation.navigation

object RutasNav {
    const val ARG_CITA_ID = "citaId"
    const val ARG_PACIENTE_ID = "pacienteId"

    const val LOGIN = "login"
    const val INICIO = "inicio"
    const val AGENDA = "agenda"
    const val PACIENTES = "pacientes"
    const val PERFIL = "perfil"
    const val DETALLE = "detalle/{$ARG_CITA_ID}"
    const val REGISTRAR = "registrar/{$ARG_CITA_ID}"
    const val FICHA = "ficha/{$ARG_PACIENTE_ID}"
    const val RESULTADOS = "resultados/{$ARG_PACIENTE_ID}"

    fun detalle(citaId: String): String = "detalle/$citaId"
    fun registrar(citaId: String): String = "registrar/$citaId"
    fun ficha(pacienteId: String): String = "ficha/$pacienteId"
    fun resultados(pacienteId: String): String = "resultados/$pacienteId"
}
