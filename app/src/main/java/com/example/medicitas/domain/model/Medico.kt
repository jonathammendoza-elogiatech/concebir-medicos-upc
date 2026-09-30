package com.example.medicitas.domain.model

data class Medico(
    val cmp: String,
    val rne: String,
    val nombre: String,
    val tratamiento: String,
    val apellido: String,
    val iniciales: String,
    val especialidad: String,
    val correo: String,
    val telefono: String,
    val sedeActiva: Sede,
    val notificacionesAgenda: Boolean
) {
    val nombreCompleto: String get() = "$tratamiento $nombre"
    val nombreCorto: String get() = "$tratamiento $apellido"
}
