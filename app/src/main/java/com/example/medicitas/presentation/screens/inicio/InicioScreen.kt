package com.example.medicitas.presentation.screens.inicio

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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.outlined.EventBusy
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.MeetingRoom
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.medicitas.domain.model.Cita
import com.example.medicitas.domain.model.ResumenDia
import com.example.medicitas.presentation.common.AppTopBar
import com.example.medicitas.presentation.common.CargandoContenido
import com.example.medicitas.presentation.common.CitaCard
import com.example.medicitas.presentation.common.EstadoVacio
import com.example.medicitas.presentation.common.SeccionTitulo
import com.example.medicitas.presentation.common.formatoDiaMes
import com.example.medicitas.presentation.common.formatoHora
import com.example.medicitas.ui.theme.EstadoAtendida
import com.example.medicitas.ui.theme.EstadoAtendidaFondo
import com.example.medicitas.ui.theme.EstadoPendiente
import com.example.medicitas.ui.theme.MetricaGrande
import com.example.medicitas.ui.theme.MetricaMedia
import com.example.medicitas.ui.theme.TextoSecundario
import java.time.Duration

private const val MAX_SIGUIENTES = 4

@Composable
fun InicioScreen(
    onVerCita: (String) -> Unit,
    onVerAgenda: () -> Unit,
    viewModel: InicioViewModel = hiltViewModel()
) {
    val resumen by viewModel.resumen.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize()) {
        val datos = resumen
        if (datos == null) {
            AppTopBar(titulo = "Resumen del día")
            CargandoContenido()
            return@Column
        }
        val saludo = if (datos.horaActual.hour < 12) "Buenos días" else "Buenas tardes"
        AppTopBar(
            titulo = "$saludo, ${datos.medico.nombreCorto}",
            subtitulo = "${datos.fecha.formatoDiaMes()} · Sede ${datos.medico.sedeActiva.nombre}",
            iconoSubtitulo = Icons.Outlined.LocationOn
        ) {
            IconButton(onClick = {}) { Icon(Icons.Outlined.Notifications, contentDescription = "Notificaciones") }
        }
        ContenidoInicio(datos, onVerCita, onVerAgenda)
    }
}

@Composable
private fun ContenidoInicio(resumen: ResumenDia, onVerCita: (String) -> Unit, onVerAgenda: () -> Unit) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Metrica(resumen.totalCitas, "Citas de hoy", MaterialTheme.colorScheme.primary, Modifier.weight(1f))
                Metrica(resumen.atendidas, "Atendidas", EstadoAtendida, Modifier.weight(1f))
                Metrica(resumen.pendientesRegistro, "Pendientes de registro", EstadoPendiente, Modifier.weight(1f))
            }
        }
        if (resumen.totalCitas == 0) {
            item { EstadoVacio("Hoy no tienes citas programadas", Icons.Outlined.EventBusy) }
            return@LazyColumn
        }
        resumen.proximaCita?.let { proxima ->
            item {
                ProximaAtencion(proxima, minutosRestantes(resumen, proxima), onVerDetalle = { onVerCita(proxima.id) })
            }
        }
        item {
            SeccionTitulo("SIGUIENTES CITAS", modifier = Modifier.padding(top = 8.dp)) {
                TextButton(onClick = onVerAgenda) {
                    Text("Ver todas en Agenda", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.secondary)
                }
            }
        }
        items(resumen.siguientesCitas.take(MAX_SIGUIENTES), key = { it.id }) { cita ->
            CitaCard(cita = cita, onClick = { onVerCita(cita.id) })
        }
    }
}

private fun minutosRestantes(resumen: ResumenDia, cita: Cita): Long =
    Duration.between(resumen.horaActual, cita.hora).toMinutes()

@Composable
private fun Metrica(valor: Int, etiqueta: String, color: Color, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.height(96.dp),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.SpaceBetween) {
            Text(valor.toString(), style = MetricaGrande, color = color)
            Text(etiqueta, style = MaterialTheme.typography.labelMedium, color = TextoSecundario, maxLines = 2)
        }
    }
}

@Composable
private fun ProximaAtencion(cita: Cita, minutos: Long, onVerDetalle: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        shadowElevation = 2.dp
    ) {
        Column {
            Box(Modifier.fillMaxWidth().height(4.dp).background(MaterialTheme.colorScheme.secondary))
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val etiqueta = if (minutos >= 0) "PRÓXIMA ATENCIÓN · EN $minutos MIN" else "PRÓXIMA ATENCIÓN"
                    Row(
                        modifier = Modifier.background(EstadoAtendidaFondo, CircleShape).padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(Modifier.size(6.dp).background(EstadoAtendida, CircleShape))
                        Spacer(Modifier.width(6.dp))
                        Text(etiqueta, style = MaterialTheme.typography.labelSmall, color = EstadoAtendida)
                    }
                    Spacer(Modifier.weight(1f))
                    Text(cita.hora.formatoHora(), style = MetricaMedia, color = MaterialTheme.colorScheme.primary)
                }
                Text(cita.pacienteNombre, style = MaterialTheme.typography.headlineSmall)
                Text(
                    listOfNotNull(cita.tipo, cita.etiquetaTratamiento).joinToString(" · "),
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextoSecundario
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.MeetingRoom, contentDescription = null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("${cita.sede.nombre} · ${cita.consultorio}", style = MaterialTheme.typography.bodyMedium, color = TextoSecundario)
                }
                Button(
                    onClick = onVerDetalle,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                ) {
                    Text("Ver detalle", style = MaterialTheme.typography.labelLarge)
                    Spacer(Modifier.width(8.dp))
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}
