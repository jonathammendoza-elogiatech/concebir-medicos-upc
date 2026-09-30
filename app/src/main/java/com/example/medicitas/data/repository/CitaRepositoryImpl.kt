package com.example.medicitas.data.repository

import com.example.medicitas.data.mock.SysmedicalMockDataSource
import com.example.medicitas.domain.model.Atencion
import com.example.medicitas.domain.model.Cita
import com.example.medicitas.domain.model.EstadoCita
import com.example.medicitas.domain.model.TipoNota
import com.example.medicitas.domain.repository.CitaRepository
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

class CitaRepositoryImpl @Inject constructor(
    private val dataSource: SysmedicalMockDataSource
) : CitaRepository {

    override val citas: StateFlow<List<Cita>> = dataSource.citas.asStateFlow()
    override val fechaHoy: LocalDate get() = dataSource.fechaHoy
    override val horaActual: LocalTime get() = dataSource.horaActual

    override suspend fun getCita(id: String): Result<Cita> = runCatching {
        dataSource.citas.value.firstOrNull { it.id == id } ?: throw NoSuchElementException("Cita no encontrada")
    }

    override suspend fun registrarAtencion(
        citaId: String,
        tipoNota: TipoNota,
        nota: String,
        marcarAtendida: Boolean
    ): Result<Unit> = runCatching {
        dataSource.simularLatencia()
        val cita = getCita(citaId).getOrThrow()
        if (marcarAtendida) {
            dataSource.citas.update { lista ->
                lista.map { if (it.id == citaId) it.copy(estado = EstadoCita.ATENDIDA) else it }
            }
        }
        val medico = dataSource.medico.value.nombreCompleto
        dataSource.pacientes.update { lista ->
            lista.map { p ->
                if (p.id != cita.pacienteId) p
                else p.copy(
                    ultimaAtencion = cita.fecha.atTime(cita.hora),
                    atenciones = listOf(Atencion(cita.fecha, cita.hora, cita.tipo, medico)) + p.atenciones
                )
            }
        }
    }
}
