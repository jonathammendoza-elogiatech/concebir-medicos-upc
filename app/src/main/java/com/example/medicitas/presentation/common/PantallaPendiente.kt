package com.example.medicitas.presentation.common

import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Construction
import androidx.compose.runtime.Composable

// Marcador temporal para rutas cuya historia aún no se integra
@Composable
fun PantallaPendiente(titulo: String) {
    Column {
        AppTopBar(titulo = titulo)
        EstadoVacio(texto = "Pantalla en construcción", icono = Icons.Outlined.Construction)
    }
}
