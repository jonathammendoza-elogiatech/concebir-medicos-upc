package com.example.medicitas.data.remote.dto

import com.example.medicitas.domain.model.Medico
import com.example.medicitas.domain.model.Sede

data class MedicoDto(
    val cmp: String?,
    val rne: String?,
    val nombre: String?,
    val tratamiento: String?,
    val apellido: String?,
    val iniciales: String?,
    val especialidad: String?,
    val correo: String?,
    val telefono: String?,
    val sedeActiva: String?,
    val notificacionesAgenda: Boolean?
) {
    fun toDomain(): Medico {
        return Medico(
            cmp = cmp.orEmpty(),
            rne = rne.orEmpty(),
            nombre = nombre.orEmpty(),
            tratamiento = tratamiento.orEmpty(),
            apellido = apellido.orEmpty(),
            iniciales = iniciales.orEmpty(),
            especialidad = especialidad.orEmpty(),
            correo = correo.orEmpty(),
            telefono = telefono.orEmpty(),
            sedeActiva = sedeDesdeApi(sedeActiva),
            notificacionesAgenda = notificacionesAgenda ?: false
        )
    }
}

// Cambios del perfil propio (PATCH /medicos/me); los null no se envían
data class PerfilRequestDto(
    val sedeActiva: String? = null,
    val notificacionesAgenda: Boolean? = null
)

// El API usa el nombre del enum (SAN_ISIDRO, ...)
fun sedeDesdeApi(valor: String?): Sede = Sede.entries.firstOrNull { it.name == valor } ?: Sede.SAN_ISIDRO
