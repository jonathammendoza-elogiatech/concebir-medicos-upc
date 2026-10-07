package com.example.medicitas.presentation.screens.registrar

import com.example.medicitas.domain.model.TipoNota

sealed interface RegistrarAtencionEvent {
    data class SeleccionarTipo(val tipo: TipoNota) : RegistrarAtencionEvent
    data class NotaChange(val texto: String) : RegistrarAtencionEvent
    data class InsertarFragmento(val fragmento: String) : RegistrarAtencionEvent
    data class MarcarAtendidaChange(val valor: Boolean) : RegistrarAtencionEvent
    data object SolicitarFirma : RegistrarAtencionEvent
    data object CancelarFirma : RegistrarAtencionEvent
    data object AlternarContrasena : RegistrarAtencionEvent
    data class ContrasenaChange(val valor: String) : RegistrarAtencionEvent
    data object Firmar : RegistrarAtencionEvent
}

// Efectos de una sola vez: no se guardan en el UiState
sealed interface RegistrarAtencionEffect {
    data object Registrada : RegistrarAtencionEffect
}
