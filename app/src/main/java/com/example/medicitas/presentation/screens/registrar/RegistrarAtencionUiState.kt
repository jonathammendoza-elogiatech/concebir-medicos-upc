package com.example.medicitas.presentation.screens.registrar

import com.example.medicitas.domain.model.DetalleCita
import com.example.medicitas.domain.model.Medico
import com.example.medicitas.domain.model.TipoNota
import java.time.LocalDateTime

enum class EstadoFirma { OCULTA, ESPERANDO, FIRMANDO, FIRMADA }

data class RegistrarAtencionUiState(
    val detalle: DetalleCita? = null,
    val medico: Medico? = null,
    val fechaHora: LocalDateTime? = null,
    val tipoNota: TipoNota = TipoNota.EVOLUCION,
    val nota: String = "",
    val marcarAtendida: Boolean = true,
    val firma: EstadoFirma = EstadoFirma.OCULTA,
    val usarContrasena: Boolean = false,
    val contrasena: String = "",
    val error: String? = null
) {
    val puedeGuardar: Boolean get() = nota.isNotBlank() && detalle != null
}
