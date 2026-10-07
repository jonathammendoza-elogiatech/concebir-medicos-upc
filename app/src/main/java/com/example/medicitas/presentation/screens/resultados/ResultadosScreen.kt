package com.example.medicitas.presentation.screens.resultados

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.HourglassEmpty
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Science
import androidx.compose.material.icons.outlined.Sync
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.medicitas.domain.model.EstadoResultado
import com.example.medicitas.domain.model.ResultadoExamen
import com.example.medicitas.domain.model.TipoResultado
import com.example.medicitas.presentation.common.AppTopBar
import com.example.medicitas.presentation.common.CargandoContenido
import com.example.medicitas.presentation.common.EstadoChip
import com.example.medicitas.presentation.common.EstadoVacio
import com.example.medicitas.presentation.common.colores
import com.example.medicitas.presentation.common.formatoCorto
import com.example.medicitas.ui.theme.EstadoAlerta
import com.example.medicitas.ui.theme.EstadoAlertaFondo
import com.example.medicitas.ui.theme.MetricaGrande
import com.example.medicitas.ui.theme.TextoSecundario
import com.example.medicitas.ui.theme.TextoTerciario

@Composable
fun ResultadosScreen(
    onBack: () -> Unit,
    viewModel: ResultadosViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    ResultadosContent(uiState = uiState, onEvent = viewModel::onEvent, onBack = onBack)
}

@Composable
private fun ResultadosContent(uiState: ResultadosUiState, onEvent: (ResultadosEvent) -> Unit, onBack: () -> Unit) {
    val paciente = uiState.paciente

    Column(modifier = Modifier.fillMaxSize()) {
        AppTopBar(
            titulo = "Resultados",
            subtitulo = paciente?.let { "${it.nombre} · ${it.historiaClinica}" },
            onBack = onBack
        )
        TabRow(selectedTabIndex = uiState.tipo.ordinal, containerColor = MaterialTheme.colorScheme.surface) {
            Tab(
                selected = uiState.tipo == TipoResultado.LABORATORIO,
                onClick = { onEvent(ResultadosEvent.SeleccionarTipo(TipoResultado.LABORATORIO)) },
                text = { Text("Laboratorio") },
                unselectedContentColor = TextoSecundario
            )
            Tab(
                selected = uiState.tipo == TipoResultado.GENETICA,
                onClick = { onEvent(ResultadosEvent.SeleccionarTipo(TipoResultado.GENETICA)) },
                text = { Text("Genética") },
                unselectedContentColor = TextoSecundario
            )
        }
        when {
            uiState.cargando -> CargandoContenido()
            uiState.error != null -> EstadoVacio(uiState.error.orEmpty(), Icons.Outlined.Science)
            uiState.visibles.isEmpty() -> EstadoVacio("Aún no hay resultados registrados", Icons.Outlined.Science)
            else -> ListaResultados(uiState.visibles)
        }
    }
}

@Composable
private fun ListaResultados(resultados: List<ResultadoExamen>) {
    val grupos = resultados.groupBy { it.grupo }
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { Leyenda() }
        grupos.forEach { (grupo, lista) ->
            item(key = "grupo-$grupo") {
                Column(modifier = Modifier.padding(top = 8.dp)) {
                    Text(grupo, style = MaterialTheme.typography.titleSmall)
                    Text(lista.first().grupoDetalle, style = MaterialTheme.typography.bodySmall, color = TextoSecundario)
                }
            }
            items(lista, key = { it.id }) { TarjetaResultado(it) }
        }
        item { Spacer(Modifier.navigationBarsPadding()) }
    }
}

@Composable
private fun Leyenda() {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        EstadoResultado.entries.forEach { estado ->
            EstadoChip(if (estado == EstadoResultado.PENDIENTE) "Pendiente" else estado.etiqueta, estado.colores())
        }
    }
}

@Composable
private fun TarjetaResultado(resultado: ResultadoExamen) {
    val fueraDeRango = resultado.estado == EstadoResultado.FUERA_DE_RANGO
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(if (fueraDeRango) 2.dp else 1.dp, if (fueraDeRango) EstadoAlerta else MaterialTheme.colorScheme.outline)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.Top) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(resultado.categoria, style = MaterialTheme.typography.labelSmall, color = TextoTerciario)
                    Text(resultado.nombre, style = MaterialTheme.typography.titleMedium)
                    Text(resultado.fechaHora.formatoCorto(), style = MaterialTheme.typography.bodySmall, color = TextoSecundario)
                }
                EstadoChip(resultado.estado.etiqueta, resultado.estado.colores(), icono = iconoEstado(resultado.estado))
            }
            if (resultado.valor != null) {
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(resultado.valor, style = MetricaGrande, color = if (fueraDeRango) EstadoAlerta else MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.width(6.dp))
                    Text(resultado.unidad, style = MaterialTheme.typography.bodyMedium, color = TextoSecundario, modifier = Modifier.padding(bottom = 2.dp))
                    Spacer(Modifier.weight(1f))
                    if (resultado.interpretacion != null) {
                        if (fueraDeRango) {
                            Icon(Icons.Filled.ArrowUpward, contentDescription = null, tint = EstadoAlerta, modifier = Modifier.size(16.dp))
                        }
                        Text(
                            resultado.interpretacion,
                            style = MaterialTheme.typography.labelMedium,
                            color = if (fueraDeRango) EstadoAlerta else MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            } else {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.Sync, contentDescription = null, tint = TextoSecundario, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Resultado en análisis", style = MaterialTheme.typography.bodyMedium.copy(fontStyle = FontStyle.Italic), color = TextoSecundario)
                }
            }
            Text("Rango de referencia: ${resultado.rangoReferencia}", style = MaterialTheme.typography.bodySmall, color = TextoSecundario)
            if (resultado.notaClinica != null) {
                Row(
                    modifier = Modifier.fillMaxWidth().background(EstadoAlertaFondo, RoundedCornerShape(8.dp)).padding(10.dp)
                ) {
                    Icon(Icons.Outlined.Info, contentDescription = null, tint = EstadoAlerta, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(
                        buildAnnotatedString {
                            withStyle(SpanStyle(fontWeight = FontWeight.SemiBold)) { append("Nota clínica: ") }
                            append(resultado.notaClinica)
                        },
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            if (resultado.disponibleAprox != null) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(6.dp).background(TextoTerciario, CircleShape))
                    Spacer(Modifier.width(6.dp))
                    Text("En procesamiento", style = MaterialTheme.typography.labelMedium, color = TextoSecundario, modifier = Modifier.weight(1f))
                    Text("Disp. aprox. ${resultado.disponibleAprox}", style = MaterialTheme.typography.labelMedium, color = TextoSecundario)
                }
            }
        }
    }
}

private fun iconoEstado(estado: EstadoResultado): ImageVector = when (estado) {
    EstadoResultado.EN_RANGO -> Icons.Filled.CheckCircle
    EstadoResultado.FUERA_DE_RANGO -> Icons.Filled.Warning
    EstadoResultado.PENDIENTE -> Icons.Outlined.HourglassEmpty
}
