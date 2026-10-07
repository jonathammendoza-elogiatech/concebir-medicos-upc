package com.example.medicitas.presentation.screens.pacientes

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.PersonSearch
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.medicitas.domain.model.EstadoTratamiento
import com.example.medicitas.domain.model.Paciente
import com.example.medicitas.presentation.common.AppTopBar
import com.example.medicitas.presentation.common.AvatarIniciales
import com.example.medicitas.presentation.common.CargandoContenido
import com.example.medicitas.presentation.common.ColoresEstado
import com.example.medicitas.presentation.common.EstadoChip
import com.example.medicitas.presentation.common.EstadoVacio
import com.example.medicitas.presentation.common.formatoCorto
import com.example.medicitas.ui.theme.EstadoConfirmada
import com.example.medicitas.ui.theme.EstadoConfirmadaFondo
import com.example.medicitas.ui.theme.PetroleoClaro
import com.example.medicitas.ui.theme.SuperficieVariante
import com.example.medicitas.ui.theme.TextoSecundario

@Composable
fun PacientesScreen(
    onVerPaciente: (String) -> Unit,
    viewModel: PacientesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    PacientesContent(uiState = uiState, onEvent = viewModel::onEvent, onVerPaciente = onVerPaciente)
}

@Composable
private fun PacientesContent(uiState: PacientesUiState, onEvent: (PacientesEvent) -> Unit, onVerPaciente: (String) -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        AppTopBar(titulo = "Pacientes", subtitulo = "Sede ${uiState.sede.nombre}")
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                OutlinedTextField(
                    value = uiState.consulta,
                    onValueChange = { onEvent(PacientesEvent.ConsultaChange(it)) },
                    placeholder = { Text("Buscar por nombre o DNI") },
                    leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null) },
                    trailingIcon = {
                        if (uiState.consulta.isNotEmpty()) {
                            IconButton(onClick = { onEvent(PacientesEvent.ConsultaChange("")) }) {
                                Icon(Icons.Outlined.Close, contentDescription = "Limpiar búsqueda")
                            }
                        }
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
            item { FiltroEstado(uiState.filtroEstado) { onEvent(PacientesEvent.FiltrarEstado(it)) } }
            item {
                Text(
                    "${uiState.totalAsignados} pacientes asignados",
                    style = MaterialTheme.typography.titleSmall,
                    color = TextoSecundario
                )
            }
            when {
                uiState.cargando -> item { CargandoContenido(Modifier.padding(top = 48.dp)) }
                uiState.error != null -> item { EstadoVacio(uiState.error.orEmpty(), Icons.Outlined.PersonSearch) }
                uiState.pacientes.isEmpty() -> item {
                    EstadoVacio("No encontramos pacientes con ese nombre o DNI", Icons.Outlined.PersonSearch)
                }
                else -> items(uiState.pacientes, key = { it.id }) { paciente ->
                    FilaPaciente(paciente, onClick = { onVerPaciente(paciente.id) })
                }
            }
        }
    }
}

@Composable
private fun FiltroEstado(seleccionado: EstadoTratamiento?, onSeleccionar: (EstadoTratamiento?) -> Unit) {
    val opciones: List<EstadoTratamiento?> = listOf(null) + EstadoTratamiento.entries
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(opciones) { estado ->
            val activo = estado == seleccionado
            FilterChip(
                selected = activo,
                onClick = { onSeleccionar(estado) },
                label = { Text(estado?.etiqueta ?: "Todas") },
                leadingIcon = if (activo) {
                    { Icon(Icons.Filled.Check, contentDescription = null, modifier = Modifier.size(FilterChipDefaults.IconSize)) }
                } else null,
                shape = CircleShape,
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = PetroleoClaro,
                    selectedLabelColor = MaterialTheme.colorScheme.primary,
                    selectedLeadingIconColor = MaterialTheme.colorScheme.primary,
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    }
}

@Composable
private fun FilaPaciente(paciente: Paciente, onClick: () -> Unit) {
    val enTratamiento = paciente.estadoTratamiento == EstadoTratamiento.EN_TRATAMIENTO
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            AvatarIniciales(paciente.iniciales, tamano = 44.dp)
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(paciente.nombre, style = MaterialTheme.typography.titleSmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(
                    "${paciente.edad} años · ${paciente.historiaClinica}",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextoSecundario
                )
                EstadoChip(
                    texto = paciente.tratamiento?.let { "${it.protocolo} Ciclo ${it.ciclo} · Día ${it.dia}" } ?: paciente.resumenTratamiento,
                    colores = if (enTratamiento) {
                        ColoresEstado(EstadoConfirmada, EstadoConfirmadaFondo, MaterialTheme.colorScheme.secondary)
                    } else {
                        ColoresEstado(TextoSecundario, SuperficieVariante, TextoSecundario)
                    }
                )
            }
            Spacer(Modifier.width(8.dp))
            Column(horizontalAlignment = Alignment.End) {
                Text("Última atención", style = MaterialTheme.typography.labelSmall, color = TextoSecundario)
                Text(paciente.ultimaAtencion.toLocalDate().formatoCorto(), style = MaterialTheme.typography.labelMedium)
            }
            Icon(Icons.Outlined.ChevronRight, contentDescription = null, tint = TextoSecundario)
        }
    }
}
