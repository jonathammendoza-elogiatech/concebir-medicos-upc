package com.example.medicitas.presentation.common

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.medicitas.ui.theme.EstadoAlerta
import com.example.medicitas.ui.theme.EstadoAtendida
import com.example.medicitas.ui.theme.EstadoPendiente
import com.example.medicitas.ui.theme.Petroleo

@Composable
fun CustomSnackbarHost(hostState: SnackbarHostState, modifier: Modifier = Modifier) {
    SnackbarHost(hostState = hostState, modifier = modifier) { data ->
        val tipo = (data.visuals as? TypedSnackbarVisuals)?.tipo ?: SnackbarType.INFO
        val (fondo, icono) = when (tipo) {
            SnackbarType.SUCCESS -> EstadoAtendida to Icons.Outlined.CheckCircle
            SnackbarType.ERROR -> EstadoAlerta to Icons.Outlined.ErrorOutline
            SnackbarType.WARNING -> EstadoPendiente to Icons.Outlined.WarningAmber
            SnackbarType.INFO -> Petroleo to Icons.Outlined.Info
        }
        Surface(
            modifier = Modifier.padding(16.dp),
            shape = RoundedCornerShape(10.dp),
            color = fondo,
            contentColor = Color.White,
            shadowElevation = 6.dp
        ) {
            Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(icono, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(12.dp))
                Text(data.visuals.message, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
