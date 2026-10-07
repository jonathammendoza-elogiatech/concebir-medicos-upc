package com.example.medicitas.presentation.screens.perfil

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Mail
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.medicitas.domain.model.Medico
import com.example.medicitas.domain.model.Sede
import com.example.medicitas.presentation.common.AppTopBar
import com.example.medicitas.presentation.common.AvatarIniciales
import com.example.medicitas.presentation.common.SeccionTitulo
import com.example.medicitas.presentation.common.TarjetaClinica
import com.example.medicitas.presentation.common.versionApp
import com.example.medicitas.ui.theme.EstadoAtendida
import com.example.medicitas.ui.theme.PetroleoClaro
import com.example.medicitas.ui.theme.TextoSecundario
import com.example.medicitas.ui.theme.TurquesaClaro

@Composable
fun PerfilScreen(
    onSesionCerrada: () -> Unit,
    viewModel: PerfilViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effects.collect { efecto ->
            when (efecto) {
                PerfilEffect.SesionCerrada -> onSesionCerrada()
            }
        }
    }

    PerfilContent(uiState = uiState, onEvent = viewModel::onEvent)
}

@Composable
private fun PerfilContent(uiState: PerfilUiState, onEvent: (PerfilEvent) -> Unit) {
    val medico = uiState.medico
    Column(modifier = Modifier.fillMaxSize()) {
        AppTopBar(titulo = "Perfil") {
            IconButton(onClick = {}) { Icon(Icons.Outlined.Notifications, contentDescription = "Notificaciones") }
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            EncabezadoMedico(medico)
            TarjetaClinica(modifier = Modifier.fillMaxWidth()) {
                FilaContacto(Icons.Outlined.Mail, "Correo institucional", medico.correo)
                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = MaterialTheme.colorScheme.outline)
                FilaContacto(Icons.Outlined.Phone, "Teléfono de contacto", medico.telefono)
            }

            SeccionTitulo("SEDE Y ATENCIÓN CLÍNICA")
            TarjetaClinica(modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.LocationOn, contentDescription = null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Sede activa", style = MaterialTheme.typography.titleMedium)
                }
                Spacer(Modifier.height(12.dp))
                SelectorSede(sedeActiva = medico.sedeActiva, onSeleccionar = { onEvent(PerfilEvent.CambiarSede(it)) })
                Spacer(Modifier.height(12.dp))
                Text(
                    "Cambiar la sede filtra tu agenda y lista de pacientes en toda la app.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextoSecundario
                )
            }

            SeccionTitulo("PREFERENCIAS")
            TarjetaClinica(modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Notificaciones de agenda", style = MaterialTheme.typography.titleSmall)
                        Text("Alertas de confirmaciones, retrasos y nuevas citas", style = MaterialTheme.typography.bodySmall, color = TextoSecundario)
                    }
                    Spacer(Modifier.width(12.dp))
                    Switch(
                        checked = medico.notificacionesAgenda,
                        onCheckedChange = { onEvent(PerfilEvent.CambiarNotificaciones(it)) },
                        thumbContent = if (medico.notificacionesAgenda) {
                            { Icon(Icons.Filled.Check, contentDescription = null, modifier = Modifier.size(SwitchDefaults.IconSize)) }
                        } else null
                    )
                }
                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = MaterialTheme.colorScheme.outline)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Acerca de la aplicación", style = MaterialTheme.typography.titleSmall)
                        Text("Concebir Médicos · SERPROSA", style = MaterialTheme.typography.bodySmall, color = TextoSecundario)
                    }
                    Text("v${versionApp()}", style = MaterialTheme.typography.labelMedium, color = TextoSecundario)
                    Icon(Icons.Outlined.ChevronRight, contentDescription = null, tint = TextoSecundario)
                }
            }

            TextButton(onClick = { onEvent(PerfilEvent.CerrarSesion) }, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                Icon(Icons.AutoMirrored.Outlined.Logout, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                Spacer(Modifier.width(8.dp))
                Text("Cerrar sesión", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.error)
            }
            Text(
                "SERPROSA · Clínica Concebir · Sistema médico móvil",
                style = MaterialTheme.typography.bodySmall,
                color = TextoSecundario,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun EncabezadoMedico(medico: Medico) {
    TarjetaClinica(modifier = Modifier.fillMaxWidth(), contentPadding = 20.dp) {
        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            AvatarIniciales(iniciales = medico.iniciales, tamano = 72.dp, fondo = TurquesaClaro, color = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(12.dp))
            Text(medico.nombreCompleto, style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .background(PetroleoClaro, RoundedCornerShape(6.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text("CMP ${medico.cmp}", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
                }
                Spacer(Modifier.width(8.dp))
                Text("RNE ${medico.rne}", style = MaterialTheme.typography.bodySmall, color = TextoSecundario)
            }
            Spacer(Modifier.height(6.dp))
            Text(medico.especialidad, style = MaterialTheme.typography.bodyMedium, color = TextoSecundario)
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = MaterialTheme.colorScheme.outline)
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(8.dp).background(EstadoAtendida, CircleShape))
                Spacer(Modifier.width(6.dp))
                Text("Sesión activa", style = MaterialTheme.typography.labelMedium, color = EstadoAtendida, modifier = Modifier.weight(1f))
                Text("Sede ${medico.sedeActiva.nombre}", style = MaterialTheme.typography.labelMedium, color = TextoSecundario)
            }
        }
    }
}

@Composable
private fun FilaContacto(icono: ImageVector, etiqueta: String, valor: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier.size(40.dp).background(PetroleoClaro, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(icono, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
        }
        Spacer(Modifier.width(12.dp))
        Column {
            Text(etiqueta, style = MaterialTheme.typography.labelMedium, color = TextoSecundario)
            Text(valor, style = MaterialTheme.typography.titleSmall)
        }
    }
}

@Composable
private fun SelectorSede(sedeActiva: Sede, onSeleccionar: (Sede) -> Unit) {
    val sedes = Sede.entries
    SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
        sedes.forEachIndexed { index, sede ->
            val activa = sede == sedeActiva
            SegmentedButton(
                selected = activa,
                onClick = { onSeleccionar(sede) },
                shape = SegmentedButtonDefaults.itemShape(index = index, count = sedes.size),
                icon = {},
                colors = SegmentedButtonDefaults.colors(
                    activeContainerColor = MaterialTheme.colorScheme.primary,
                    activeContentColor = MaterialTheme.colorScheme.onPrimary,
                    inactiveBorderColor = MaterialTheme.colorScheme.outline,
                    activeBorderColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(vertical = 4.dp)) {
                    Text(sede.nombre, style = MaterialTheme.typography.labelLarge, maxLines = 1)
                    Text(if (activa) "Activa" else "Disponible", style = MaterialTheme.typography.labelMedium)
                }
            }
        }
    }
}
