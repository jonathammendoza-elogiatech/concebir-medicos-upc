package com.example.medicitas.domain.repository

import com.example.medicitas.domain.model.Cita
import com.example.medicitas.domain.model.TipoNota
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalDate
import java.time.LocalTime

interface CitaRepository {
    val citas: StateFlow<List<Cita>>
    // Hora de la última carga desde el servidor (null si aún no se cargó)
    val actualizadoA: StateFlow<LocalTime?>
    val fechaHoy: LocalDate
    val horaActual: LocalTime
    suspend fun cargarCitas(): Result<Unit>
    suspend fun getCita(id: String): Result<Cita>
    suspend fun registrarAtencion(citaId: String, tipoNota: TipoNota, nota: String, marcarAtendida: Boolean): Result<Unit>
}
