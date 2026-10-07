package com.example.medicitas.presentation.event

import com.example.medicitas.presentation.common.SnackbarType

// Mensajes globales: se muestran en el Scaffold principal aunque la pantalla que los envía ya se haya cerrado
sealed interface UiEvent {
    data class ShowSnackbar(val mensaje: String, val tipo: SnackbarType = SnackbarType.INFO) : UiEvent
}
