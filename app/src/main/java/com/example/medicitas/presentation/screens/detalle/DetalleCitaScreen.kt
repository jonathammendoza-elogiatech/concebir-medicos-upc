package com.example.medicitas.presentation.screens.detalle

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Assignment
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.EditCalendar
import androidx.compose.material.icons.outlined.EditNote
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.MedicalServices
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Science
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.medicitas.domain.model.DetalleCita
import com.example.medicitas.domain.model.EstadoCita
import com.example.medicitas.presentation.common.AppTopBar
import com.example.medicitas.presentation.common.AvatarIniciales
import com.example.medicitas.presentation.common.CargandoContenido
import com.example.medicitas.presentation.common.DatoEtiquetado
import com.example.medicitas.presentation.common.EstadoChip
import com.example.medicitas.presentation.common.EstadoCitaChip
import com.example.medicitas.presentation.common.EstadoVacio
import com.example.medicitas.presentation.common.TarjetaClinica
import com.example.medicitas.presentation.common.TituloTarjeta
import com.example.medicitas.presentation.common.colores
import com.example.medicitas.presentation.common.formatoHora
import com.example.medicitas.presentation.common.formatoLargo
import com.example.medicitas.ui.theme.EstadoAtendida
import com.example.medicitas.ui.theme.Fondo
import com.example.medicitas.ui.theme.MetricaMedia
import com.example.medicitas.ui.theme.TextoSecundario
import java.time.Duration

@Composable
fun DetalleCitaScreen(
    onBack: () -> Unit,
    onVerFicha: (String) -> Unit,
    onVerResultados: (String) -> Unit,
    onRegistrarAtencion: (String) -> Unit,
    viewModel: DetalleCitaViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize()) {
        AppTopBar(titulo = "Detalle de la cita", onBack = onBack)
        val detalle = uiState.detalle
        when {
            uiState.cargando -> CargandoContenido()
            detalle == null -> EstadoVacio(uiState.error ?: "No se encontró la cita", Icons.Outlined.ErrorOutline)
            else -> {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    TarjetaHorario(detalle, uiState)
                    TarjetaPaciente(detalle)
                    TarjetaAtencion(detalle)
                    TarjetaTratamiento(detalle)
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        BotonSecundario("Ver ficha del paciente", Icons.Outlined.EditNote, Modifier.weight(1f)) {
                            onVerFicha(detalle.paciente.id)
                        }
                        BotonSecundario("Ver resultados", Icons.Outlined.Description, Modifier.weight(1f)) {
                            onVerResultados(detalle.paciente.id)
                        }
                    }
                }
                BarraRegistrar(detalle, onRegistrar = { onRegistrarAtencion(detalle.cita.id) })
            }
        }
    }
}

@Composable
private fun TarjetaHorario(detalle: DetalleCita, uiState: DetalleCitaUiState) {
    val cita = detalle.cita
    TarjetaClinica(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.Top) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Hora programada", style = MaterialTheme.typography.labelMedium, color = TextoSecundario)
                Text(cita.hora.formatoHora(), style = MaterialTheme.typography.headlineLarge, color = MaterialTheme.colorScheme.primary)
            }
            EstadoCitaChip(cita.estado)
        }
        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = MaterialTheme.colorScheme.outline)
        Text(cita.fecha.formatoLargo(), style = MaterialTheme.typography.titleSmall)
        val minutos = if (cita.fecha == uiState.hoy && uiState.horaActual != null) {
            Duration.between(uiState.horaActual, cita.hora).toMinutes()
        } else null
        if (cita.estado != EstadoCita.ATENDIDA && minutos != null && minutos >= 0) {
            Spacer(Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.Schedule, contentDescription = null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text("En horario · En $minutos min", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.secondary)
            }
        }
    }
}

@Composable
private fun TarjetaPaciente(detalle: DetalleCita) {
    val paciente = detalle.paciente
    TarjetaClinica(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AvatarIniciales(paciente.iniciales, tamano = 48.dp)
            Spacer(Modifier.width(12.dp))
            Column {
                Text(paciente.nombre, style = MaterialTheme.typography.titleMedium)
                Text(
                    "${paciente.edad} años · ${paciente.sexo} · DNI ${paciente.dni}",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextoSecundario
                )
            }
        }
        Spacer(Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth().background(Fondo, RoundedCornerShape(10.dp)).padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Historia clínica", style = MaterialTheme.typography.bodyMedium, color = TextoSecundario, modifier = Modifier.weight(1f))
            Text(paciente.historiaClinica, style = MetricaMedia, color = MaterialTheme.colorScheme.primary)
        }
        Spacer(Modifier.height(12.dp))
        Row(modifier = Modifier.height(IntrinsicSize.Min)) {
            Box(Modifier.width(3.dp).fillMaxHeight().background(MaterialTheme.colorScheme.secondary))
            Spacer(Modifier.width(10.dp))
            Icon(Icons.Outlined.Info, contentDescription = null, tint = TextoSecundario, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(6.dp))
            Text(
                buildAnnotatedString {
                    append("Alergias medicamentosas: ${paciente.antecedentes.alergias.lowercase()} · ")
                    withStyle(SpanStyle(fontWeight = FontWeight.SemiBold)) { append("Grupo ${paciente.antecedentes.grupoSanguineo}") }
                },
                style = MaterialTheme.typography.bodySmall,
                color = TextoSecundario
            )
        }
    }
}

@Composable
private fun TarjetaAtencion(detalle: DetalleCita) {
    val cita = detalle.cita
    TarjetaClinica(modifier = Modifier.fillMaxWidth()) {
        TituloTarjeta("Datos de la atención", Icons.AutoMirrored.Outlined.Assignment)
        Spacer(Modifier.height(12.dp))
        Row {
            DatoEtiquetado("Tipo", cita.tipo, Modifier.weight(1f))
            DatoEtiquetado(
                "Duración",
                "${cita.duracionMinutos} minutos",
                Modifier.weight(1f),
                detalle = "${cita.hora.formatoHora()} - ${cita.horaFin.formatoHora()}"
            )
        }
        Spacer(Modifier.height(12.dp))
        Row {
            DatoEtiquetado("Sede", cita.sede.nombre, Modifier.weight(1f))
            DatoEtiquetado("Consultorio", cita.consultorio, Modifier.weight(1f), detalle = cita.detalleConsultorio)
        }
    }
}

@Composable
private fun TarjetaTratamiento(detalle: DetalleCita) {
    val tratamiento = detalle.paciente.tratamiento ?: return
    TarjetaClinica(modifier = Modifier.fillMaxWidth()) {
        TituloTarjeta("Tratamiento vigente", Icons.Outlined.Science) {
            EstadoChip("Ciclo activo", EstadoCita.ATENDIDA.colores())
        }
        Spacer(Modifier.height(12.dp))
        Text(
            "${tratamiento.protocolo} · Ciclo ${tratamiento.ciclo} · Día ${tratamiento.dia} de estimulación",
            style = MaterialTheme.typography.titleSmall
        )
        Spacer(Modifier.height(4.dp))
        Text(
            buildAnnotatedString {
                append("${tratamiento.esquema} · ")
                withStyle(SpanStyle(fontWeight = FontWeight.SemiBold)) { append(tratamiento.medicacion) }
            },
            style = MaterialTheme.typography.bodySmall,
            color = TextoSecundario
        )
        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = MaterialTheme.colorScheme.outline)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Outlined.MedicalServices, contentDescription = null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(6.dp))
            Text("Especialista: ${tratamiento.medicoResponsable}", style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun BotonSecundario(texto: String, icono: ImageVector, modifier: Modifier, onClick: () -> Unit) {
    OutlinedButton(onClick = onClick, modifier = modifier.height(56.dp), shape = RoundedCornerShape(10.dp)) {
        Icon(icono, contentDescription = null, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(6.dp))
        Text(texto, style = MaterialTheme.typography.labelLarge, maxLines = 2)
    }
}

@Composable
private fun BarraRegistrar(detalle: DetalleCita, onRegistrar: () -> Unit) {
    Surface(color = MaterialTheme.colorScheme.surface, shadowElevation = 8.dp) {
        Box(modifier = Modifier.fillMaxWidth().navigationBarsPadding().padding(16.dp)) {
            if (detalle.cita.estado == EstadoCita.ATENDIDA) {
                Row(
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Outlined.CheckCircle, contentDescription = null, tint = EstadoAtendida)
                    Spacer(Modifier.width(8.dp))
                    Text("Atención registrada", style = MaterialTheme.typography.labelLarge, color = EstadoAtendida)
                }
            } else {
                Button(onClick = onRegistrar, modifier = Modifier.fillMaxWidth().height(48.dp), shape = RoundedCornerShape(10.dp)) {
                    Icon(Icons.Outlined.EditCalendar, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Registrar atención", style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    }
}
