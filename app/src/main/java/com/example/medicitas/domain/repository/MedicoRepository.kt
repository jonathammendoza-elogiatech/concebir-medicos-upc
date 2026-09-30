package com.example.medicitas.domain.repository

import com.example.medicitas.domain.model.Medico
import com.example.medicitas.domain.model.Sede
import kotlinx.coroutines.flow.StateFlow

interface MedicoRepository {
    val medico: StateFlow<Medico>
    suspend fun cambiarSede(sede: Sede): Result<Unit>
    suspend fun cambiarNotificaciones(activas: Boolean): Result<Unit>
}
