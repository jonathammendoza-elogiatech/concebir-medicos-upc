package com.example.medicitas.presentation.screens.registrar

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.outlined.DoneAll
import androidx.compose.material.icons.outlined.Key
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.medicitas.presentation.common.formatoCorto
import com.example.medicitas.ui.theme.EstadoAtendida
import com.example.medicitas.ui.theme.EstadoAtendidaFondo
import com.example.medicitas.ui.theme.Fondo
import com.example.medicitas.ui.theme.PetroleoClaro
import com.example.medicitas.ui.theme.TextoSecundario

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FirmaBiometricaSheet(
    uiState: RegistrarAtencionUiState,
    onCancelar: () -> Unit,
    onFirmar: () -> Unit,
    onAlternarContrasena: () -> Unit,
    onContrasenaChange: (String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val bloqueada = uiState.firma == EstadoFirma.FIRMANDO || uiState.firma == EstadoFirma.FIRMADA

    ModalBottomSheet(
        onDismissRequest = { if (!bloqueada) onCancelar() },
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp)
                .padding(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (uiState.firma == EstadoFirma.FIRMADA) {
                ConfirmacionRegistro(uiState)
            } else {
                ContenidoFirma(uiState, bloqueada, onCancelar, onFirmar, onAlternarContrasena, onContrasenaChange)
            }
        }
    }
}

@Composable
private fun ContenidoFirma(
    uiState: RegistrarAtencionUiState,
    bloqueada: Boolean,
    onCancelar: () -> Unit,
    onFirmar: () -> Unit,
    onAlternarContrasena: () -> Unit,
    onContrasenaChange: (String) -> Unit
) {
    val detalle = uiState.detalle ?: return
    Row(
        modifier = Modifier.background(PetroleoClaro, CircleShape).padding(horizontal = 10.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Outlined.VerifiedUser, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(14.dp))
        Spacer(Modifier.width(6.dp))
        Text("NO REPUDIO", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
    }
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text("Firma digital y confirmación", style = MaterialTheme.typography.titleLarge)
        Text(
            "Firma la nota para registrar la atención y garantizar el no repudio clínico.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextoSecundario
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Fondo, RoundedCornerShape(16.dp))
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SensorHuella(leyendo = uiState.firma == EstadoFirma.FIRMANDO)
        Text(
            if (uiState.firma == EstadoFirma.FIRMANDO) "Validando firma..." else "Usa tu huella digital o rostro",
            style = MaterialTheme.typography.titleSmall
        )
        Text(
            "Toca el sensor biométrico para firmar la nota clínica",
            style = MaterialTheme.typography.bodySmall,
            color = TextoSecundario,
            textAlign = TextAlign.Center
        )
        Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            val medico = uiState.medico
            if (medico != null) FilaMetadato("Médico", "${medico.nombreCompleto} · CMP ${medico.cmp}")
            FilaMetadato("Paciente", "${detalle.paciente.nombre} (${detalle.paciente.historiaClinica})")
            uiState.fechaHora?.let { FilaMetadato("Fecha y hora", it.formatoCorto()) }
        }
    }

    TextButton(onClick = onAlternarContrasena, enabled = !bloqueada) {
        Icon(Icons.Outlined.Key, contentDescription = null, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(6.dp))
        Text("Firmar con contraseña de respaldo", style = MaterialTheme.typography.labelLarge)
    }
    AnimatedVisibility(visible = uiState.usarContrasena) {
        OutlinedTextField(
            value = uiState.contrasena,
            onValueChange = onContrasenaChange,
            label = { Text("Contraseña institucional") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth()
        )
    }

    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        OutlinedButton(
            onClick = onCancelar,
            enabled = !bloqueada,
            modifier = Modifier.weight(1f).height(48.dp),
            shape = RoundedCornerShape(10.dp)
        ) {
            Text("Cancelar", style = MaterialTheme.typography.labelLarge)
        }
        Button(
            onClick = onFirmar,
            enabled = !bloqueada && (!uiState.usarContrasena || uiState.contrasena.isNotBlank()),
            modifier = Modifier.weight(1.4f).height(48.dp),
            shape = RoundedCornerShape(10.dp)
        ) {
            if (uiState.firma == EstadoFirma.FIRMANDO) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.onPrimary)
            } else {
                Icon(
                    if (uiState.usarContrasena) Icons.Outlined.Key else Icons.Filled.Fingerprint,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(if (uiState.usarContrasena) "Confirmar" else "Escanear y firmar", style = MaterialTheme.typography.labelLarge)
            }
        }
    }
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Outlined.Lock, contentDescription = null, tint = EstadoAtendida, modifier = Modifier.size(14.dp))
        Spacer(Modifier.width(6.dp))
        Text("Firma electrónica médica · Protocolo de no repudio SERPROSA", style = MaterialTheme.typography.bodySmall, color = TextoSecundario)
    }
}

@Composable
private fun SensorHuella(leyendo: Boolean) {
    val transicion = rememberInfiniteTransition(label = "pulso")
    val escala by transicion.animateFloat(
        initialValue = 1f,
        targetValue = if (leyendo) 1.25f else 1.12f,
        animationSpec = infiniteRepeatable(tween(if (leyendo) 450 else 1_100), RepeatMode.Reverse),
        label = "escala"
    )
    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(88.dp)) {
        Box(Modifier.size(72.dp).scale(escala).background(PetroleoClaro, CircleShape))
        Box(
            modifier = Modifier.size(64.dp).background(MaterialTheme.colorScheme.primary, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Filled.Fingerprint, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(34.dp))
        }
    }
}

@Composable
private fun ConfirmacionRegistro(uiState: RegistrarAtencionUiState) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier.size(64.dp).background(EstadoAtendidaFondo, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Outlined.DoneAll, contentDescription = null, tint = EstadoAtendida, modifier = Modifier.size(32.dp))
        }
        Text("Atención registrada", style = MaterialTheme.typography.titleLarge)
        Text(
            "Guardada y firmada · ${uiState.detalle?.paciente?.historiaClinica.orEmpty()}",
            style = MaterialTheme.typography.bodyMedium,
            color = TextoSecundario
        )
    }
}

@Composable
private fun FilaMetadato(etiqueta: String, valor: String) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(etiqueta, style = MaterialTheme.typography.bodySmall, color = TextoSecundario, modifier = Modifier.width(96.dp))
        Text(valor, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface)
    }
}
