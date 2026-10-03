package com.example.medicitas.data.repository

import com.example.medicitas.data.remote.api.CitaApiService
import com.example.medicitas.data.remote.dto.AtencionRequestDto
import com.example.medicitas.data.remote.dto.datosOError
import com.example.medicitas.domain.model.Cita
import com.example.medicitas.domain.model.TipoNota
import com.example.medicitas.domain.repository.CitaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

class CitaRepositoryImpl @Inject constructor(
    private val apiCita: CitaApiService
) : CitaRepository {

    // Caché en memoria de la agenda del médico; Inicio, Agenda y Detalle la observan
    private val _citas = MutableStateFlow<List<Cita>>(emptyList())
    override val citas: StateFlow<List<Cita>> = _citas.asStateFlow()

    // Jueves fijo del prototipo: los datos de ejemplo del API están en esa semana
    override val fechaHoy: LocalDate = LocalDate.of(2026, 9, 24)
    override val horaActual: LocalTime = LocalTime.of(10, 15)

    override suspend fun cargarCitas(): Result<Unit> = runCatching {
        _citas.value = apiCita.getCitas().datosOError().items.map { it.toDomain() }
    }

    override suspend fun getCita(id: String): Result<Cita> = runCatching {
        _citas.value.firstOrNull { it.id == id } ?: apiCita.getCita(id).datosOError().toDomain()
    }

    override suspend fun registrarAtencion(
        citaId: String,
        tipoNota: TipoNota,
        nota: String,
        marcarAtendida: Boolean
    ): Result<Unit> = runCatching {
        // El API guarda la nota firmada, marca la cita y la agrega al historial del paciente
        val actualizada = apiCita.registrarAtencion(citaId, AtencionRequestDto(tipoNota.name, nota, marcarAtendida))
            .datosOError()
            .toDomain()
        _citas.update { lista -> lista.map { if (it.id == citaId) actualizada else it } }
    }
}
