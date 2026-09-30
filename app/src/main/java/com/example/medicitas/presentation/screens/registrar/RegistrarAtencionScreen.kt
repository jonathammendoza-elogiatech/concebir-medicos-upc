package com.example.medicitas.presentation.screens.registrar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.medicitas.domain.model.TipoNota
import com.example.medicitas.presentation.common.AppTopBar
import com.example.medicitas.presentation.common.AvatarIniciales
import com.example.medicitas.presentation.common.AvisoInfo
import com.example.medicitas.presentation.common.CargandoContenido
import com.example.medicitas.presentation.common.EstadoVacio
import com.example.medicitas.presentation.common.TarjetaClinica
import com.example.medicitas.presentation.common.formatoHora
import com.example.medicitas.ui.theme.PetroleoClaro
import com.example.medicitas.ui.theme.TextoSecundario
import com.example.medicitas.ui.theme.TextoTerciario

private val FRAGMENTOS = listOf(
    "Eco TV" to "Ecografía transvaginal: ",
    "Endometrio" to "Endometrio trilaminar: ",
    "Folículos" to "Foliculometría: "
)

@Composable
fun RegistrarAtencionScreen(
    onBack: () -> Unit,
    onRegistrada: () -> Unit,
    viewModel: RegistrarAtencionViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.registrada) {
        if (uiState.registrada) onRegistrada()
    }

    val detalle = uiState.detalle
    val tratamiento = detalle?.paciente?.tratamiento
    Column(modifier = Modifier.fillMaxSize().imePadding()) {
        AppTopBar(
            titulo = "Registrar atención",
            subtitulo = uiState.medico?.nombreCompleto,
            onBack = onBack
        ) {
            if (tratamiento != null) {
                Text(
                    "${tratamiento.protocolo} · DÍA ${tratamiento.dia}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .padding(end = 12.dp)
                        .background(PetroleoClaro, CircleShape)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
        }
        when {
            detalle == null && uiState.error != null -> EstadoVacio(uiState.error.orEmpty(), Icons.Outlined.ErrorOutline)
            detalle == null -> CargandoContenido()
            else -> {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    TarjetaClinica(modifier = Modifier.fillMaxWidth()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            AvatarIniciales(detalle.paciente.iniciales, tamano = 40.dp)
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text(detalle.paciente.nombre, style = MaterialTheme.typography.titleMedium)
                                Text(
                                    "${detalle.paciente.historiaClinica} · Sede ${detalle.cita.sede.nombre}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextoSecundario
                                )
                            }
                        }
                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = MaterialTheme.colorScheme.outline)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(detalle.cita.tipo, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
                            Icon(Icons.Outlined.Schedule, contentDescription = null, tint = TextoSecundario, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text(detalle.cita.hora.formatoHora(), style = MaterialTheme.typography.bodySmall, color = TextoSecundario)
                        }
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Tipo de nota", style = MaterialTheme.typography.titleSmall, modifier = Modifier.weight(1f))
                            Text("Obligatorio", style = MaterialTheme.typography.labelMedium, color = TextoTerciario)
                        }
                        SelectorTipoNota(uiState.tipoNota, viewModel::seleccionarTipo)
                    }

                    CampoNota(uiState.nota, uiState.tipoNota, viewModel::onNotaChange, viewModel::insertarFragmento)

                    TarjetaClinica(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { viewModel.onMarcarAtendidaChange(!uiState.marcarAtendida) },
                        contentPadding = 12.dp
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = uiState.marcarAtendida, onCheckedChange = viewModel::onMarcarAtendidaChange)
                            Column {
                                Text("Marcar la atención como realizada", style = MaterialTheme.typography.titleSmall)
                                Text(
                                    "Actualiza el estado de la cita a \"Atendida\" en la agenda de hoy.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextoSecundario
                                )
                            }
                        }
                    }
                    AvisoInfo("Se registrará en el sistema de la clínica y no podrá editarse desde la app.")
                    if (uiState.error != null) {
                        Text(uiState.error.orEmpty(), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
                    }
                }
                Surface(color = MaterialTheme.colorScheme.surface, shadowElevation = 8.dp) {
                    Row(
                        modifier = Modifier.fillMaxWidth().navigationBarsPadding().padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(onClick = onBack, modifier = Modifier.weight(1f).height(48.dp), shape = RoundedCornerShape(10.dp)) {
                            Text("Cancelar", style = MaterialTheme.typography.labelLarge)
                        }
                        Button(
                            onClick = viewModel::solicitarFirma,
                            enabled = uiState.puedeGuardar,
                            modifier = Modifier.weight(1.4f).height(48.dp),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Filled.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Guardar", style = MaterialTheme.typography.labelLarge)
                        }
                    }
                }
            }
        }
    }

    if (uiState.firma != EstadoFirma.OCULTA && detalle != null) {
        FirmaBiometricaSheet(
            uiState = uiState,
            onCancelar = viewModel::cancelarFirma,
            onFirmar = viewModel::firmar,
            onAlternarContrasena = viewModel::alternarContrasena,
            onContrasenaChange = viewModel::onContrasenaChange
        )
    }
}

@Composable
private fun SelectorTipoNota(seleccionado: TipoNota, onSeleccionar: (TipoNota) -> Unit) {
    val tipos = TipoNota.entries
    SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
        tipos.forEachIndexed { index, tipo ->
            SegmentedButton(
                selected = tipo == seleccionado,
                onClick = { onSeleccionar(tipo) },
                shape = SegmentedButtonDefaults.itemShape(index = index, count = tipos.size),
                colors = SegmentedButtonDefaults.colors(
                    activeContainerColor = PetroleoClaro,
                    activeContentColor = MaterialTheme.colorScheme.primary,
                    inactiveBorderColor = MaterialTheme.colorScheme.outline,
                    activeBorderColor = MaterialTheme.colorScheme.outline
                )
            ) {
                Text(tipo.etiqueta, style = MaterialTheme.typography.labelLarge, maxLines = 1)
            }
        }
    }
}

@Composable
private fun CampoNota(
    nota: String,
    tipoNota: TipoNota,
    onNotaChange: (String) -> Unit,
    onInsertar: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Nota de ${tipoNota.etiqueta.lowercase()}", style = MaterialTheme.typography.titleSmall)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text("INSERTAR:", style = MaterialTheme.typography.labelSmall, color = TextoTerciario)
                FRAGMENTOS.forEach { (etiqueta, texto) ->
                    AssistChip(
                        onClick = { onInsertar(texto) },
                        label = { Text("+ $etiqueta", style = MaterialTheme.typography.labelMedium) },
                        shape = CircleShape,
                        colors = AssistChipDefaults.assistChipColors(labelColor = MaterialTheme.colorScheme.primary)
                    )
                }
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.outline)
            Box(modifier = Modifier.fillMaxWidth().heightIn(min = 180.dp).padding(12.dp)) {
                if (nota.isEmpty()) {
                    Text("Describe los hallazgos y las indicaciones...", style = MaterialTheme.typography.bodyMedium, color = TextoTerciario)
                }
                BasicTextField(
                    value = nota,
                    onValueChange = onNotaChange,
                    textStyle = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurface),
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                    modifier = Modifier.fillMaxWidth().heightIn(min = 156.dp)
                )
            }
            Text(
                "${nota.length}/1000",
                style = MaterialTheme.typography.labelMedium,
                color = TextoTerciario,
                textAlign = TextAlign.End,
                modifier = Modifier.fillMaxWidth().padding(end = 12.dp, bottom = 8.dp)
            )
        }
    }
}
