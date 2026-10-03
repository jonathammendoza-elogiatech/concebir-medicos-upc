package com.example.medicitas.domain.usecase

import com.example.medicitas.data.mock.SysmedicalMockDataSource
import com.example.medicitas.domain.model.Cita
import com.example.medicitas.domain.model.EstadoCita
import com.example.medicitas.domain.model.Medico
import com.example.medicitas.domain.model.Sede
import com.example.medicitas.domain.model.TipoNota
import com.example.medicitas.domain.repository.CitaRepository
import com.example.medicitas.domain.repository.MedicoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalDate
import java.time.LocalTime

// Repositorios en memoria con los datos fijos de SysmedicalMockDataSource (sin red)

class CitaRepositoryFalso(
    private val datos: SysmedicalMockDataSource = SysmedicalMockDataSource(),
    private val falloAlCargar: Boolean = false
) : CitaRepository {
    private val _citas = MutableStateFlow<List<Cita>>(emptyList())
    override val citas: StateFlow<List<Cita>> = _citas.asStateFlow()
    private val _actualizadoA = MutableStateFlow<LocalTime?>(null)
    override val actualizadoA: StateFlow<LocalTime?> = _actualizadoA.asStateFlow()
    override val fechaHoy: LocalDate get() = datos.fechaHoy
    override val horaActual: LocalTime get() = datos.horaActual

    override suspend fun cargarCitas(): Result<Unit> = runCatching {
        if (falloAlCargar) throw IllegalStateException("Sin conexión")
        _citas.value = datos.citas.value
        _actualizadoA.value = datos.horaActual
    }

    override suspend fun getCita(id: String): Result<Cita> = runCatching {
        _citas.value.first { it.id == id }
    }

    override suspend fun registrarAtencion(citaId: String, tipoNota: TipoNota, nota: String, marcarAtendida: Boolean): Result<Unit> =
        runCatching {
            getCita(citaId).getOrThrow()
            if (marcarAtendida) {
                _citas.update { lista -> lista.map { if (it.id == citaId) it.copy(estado = EstadoCita.ATENDIDA) else it } }
            }
        }
}

class MedicoRepositoryFalso(datos: SysmedicalMockDataSource = SysmedicalMockDataSource()) : MedicoRepository {
    private val perfil = datos.medico.value
    private val _medico = MutableStateFlow(perfil.copy(nombre = ""))
    override val medico: StateFlow<Medico> = _medico.asStateFlow()
    override suspend fun cargarPerfil(): Result<Unit> = runCatching { _medico.value = perfil }
    override suspend fun cambiarSede(sede: Sede): Result<Unit> = runCatching { _medico.update { it.copy(sedeActiva = sede) } }
    override suspend fun cambiarNotificaciones(activas: Boolean): Result<Unit> =
        runCatching { _medico.update { it.copy(notificacionesAgenda = activas) } }
}
