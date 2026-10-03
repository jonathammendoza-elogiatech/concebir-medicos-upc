package com.example.medicitas.data.repository

import com.example.medicitas.data.remote.api.MedicoApiService
import com.example.medicitas.data.remote.dto.PerfilRequestDto
import com.example.medicitas.data.remote.dto.datosOError
import com.example.medicitas.domain.model.Medico
import com.example.medicitas.domain.model.Sede
import com.example.medicitas.domain.repository.MedicoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class MedicoRepositoryImpl @Inject constructor(
    private val apiMedico: MedicoApiService
) : MedicoRepository {

    // Caché en memoria: Inicio, Agenda y Pacientes reaccionan cuando cambia la sede activa
    private val _medico = MutableStateFlow(MEDICO_SIN_SESION)
    override val medico: StateFlow<Medico> = _medico.asStateFlow()

    override suspend fun cargarPerfil(): Result<Unit> = runCatching {
        _medico.value = apiMedico.getPerfil().datosOError().toDomain()
    }

    override suspend fun cambiarSede(sede: Sede): Result<Unit> = runCatching {
        _medico.value = apiMedico.actualizarPerfil(PerfilRequestDto(sedeActiva = sede.name)).datosOError().toDomain()
    }

    override suspend fun cambiarNotificaciones(activas: Boolean): Result<Unit> = runCatching {
        _medico.value = apiMedico.actualizarPerfil(PerfilRequestDto(notificacionesAgenda = activas)).datosOError().toDomain()
    }

    private companion object {
        // Valor inicial hasta cargar el perfil; el login no navega hasta tenerlo
        val MEDICO_SIN_SESION = Medico(
            cmp = "", rne = "", nombre = "", tratamiento = "", apellido = "", iniciales = "",
            especialidad = "", correo = "", telefono = "", sedeActiva = Sede.SAN_ISIDRO,
            notificacionesAgenda = false
        )
    }
}
