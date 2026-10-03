package com.example.medicitas.data.remote.dto

import com.example.medicitas.domain.model.Antecedentes
import com.example.medicitas.domain.model.Atencion
import com.example.medicitas.domain.model.EstadoTratamiento
import com.example.medicitas.domain.model.Paciente
import com.example.medicitas.domain.model.Tratamiento
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

data class PacienteDto(
    val id: String?,
    val nombre: String?,
    val iniciales: String?,
    val edad: Int?,
    val sexo: String?,
    val dni: String?,
    val historiaClinica: String?,
    val sede: String?,
    val fechaNacimiento: String?,
    val telefono: String?,
    val correo: String?,
    val seguro: String?,
    val plan: String?,
    val estadoTratamiento: String?,
    val resumenTratamiento: String?,
    val tratamiento: TratamientoDto?,
    val ultimaAtencion: String?,
    val antecedentes: AntecedentesDto?,
    val atenciones: List<AtencionDto>?
) {
    fun toDomain(): Paciente {
        return Paciente(
            id = id.orEmpty(),
            nombre = nombre.orEmpty(),
            iniciales = iniciales.orEmpty(),
            edad = edad ?: 0,
            sexo = sexo.orEmpty(),
            dni = dni.orEmpty(),
            historiaClinica = historiaClinica.orEmpty(),
            sede = sedeDesdeApi(sede),
            fechaNacimiento = LocalDate.parse(fechaNacimiento.orEmpty()),
            telefono = telefono.orEmpty(),
            correo = correo.orEmpty(),
            seguro = seguro.orEmpty(),
            plan = plan.orEmpty(),
            estadoTratamiento = EstadoTratamiento.entries.firstOrNull { it.name == estadoTratamiento }
                ?: EstadoTratamiento.EN_SEGUIMIENTO,
            resumenTratamiento = resumenTratamiento.orEmpty(),
            tratamiento = tratamiento?.toDomain(),
            ultimaAtencion = LocalDateTime.parse(ultimaAtencion.orEmpty()),
            antecedentes = (antecedentes ?: AntecedentesDto()).toDomain(),
            atenciones = atenciones.orEmpty().map { it.toDomain() }
        )
    }
}

data class TratamientoDto(
    val protocolo: String?,
    val ciclo: Int?,
    val dia: Int?,
    val esquema: String?,
    val medicacion: String?,
    val inicio: String?,
    val medicoResponsable: String?
) {
    fun toDomain(): Tratamiento {
        return Tratamiento(
            protocolo = protocolo.orEmpty(),
            ciclo = ciclo ?: 0,
            dia = dia ?: 0,
            esquema = esquema.orEmpty(),
            medicacion = medicacion.orEmpty(),
            inicio = LocalDate.parse(inicio.orEmpty()),
            medicoResponsable = medicoResponsable.orEmpty()
        )
    }
}

data class AntecedentesDto(
    val obstetricos: String? = null,
    val obstetricosDetalle: String? = null,
    val quirurgicos: String? = null,
    val alergias: String? = null,
    val grupoSanguineo: String? = null
) {
    fun toDomain(): Antecedentes {
        return Antecedentes(
            obstetricos = obstetricos.orEmpty(),
            obstetricosDetalle = obstetricosDetalle.orEmpty(),
            quirurgicos = quirurgicos.orEmpty(),
            alergias = alergias.orEmpty(),
            grupoSanguineo = grupoSanguineo.orEmpty()
        )
    }
}

data class AtencionDto(
    val fecha: String?,
    val hora: String?,
    val tipo: String?,
    val medico: String?
) {
    fun toDomain(): Atencion {
        return Atencion(
            fecha = LocalDate.parse(fecha.orEmpty()),
            hora = LocalTime.parse(hora.orEmpty()),
            tipo = tipo.orEmpty(),
            medico = medico.orEmpty()
        )
    }
}
