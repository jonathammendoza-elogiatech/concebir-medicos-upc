package com.example.medicitas.presentation.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Domain
import androidx.compose.material.icons.outlined.MedicalServices
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.foundation.BorderStroke
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.medicitas.domain.model.Cita
import com.example.medicitas.domain.model.EstadoCita
import com.example.medicitas.ui.theme.EstadoAlerta
import com.example.medicitas.ui.theme.EstadoAlertaFondo
import com.example.medicitas.ui.theme.Fondo
import com.example.medicitas.ui.theme.MetricaMedia
import com.example.medicitas.ui.theme.TextoSecundario
import com.example.medicitas.ui.theme.TextoTerciario

/**
 * Tarjeta de cita con barra lateral del color de su estado.
 * [detallada] muestra HC, edad, tratamiento y ubicación (agenda); la versión compacta se usa en el resumen del día.
 */
@Composable
fun CitaCard(
    cita: Cita,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    detallada: Boolean = false,
    etiquetaTiempo: String? = null
) {
    val colores = cita.estado.colores()
    val atendida = cita.estado == EstadoCita.ATENDIDA
    Surface(
        onClick = onClick,
        modifier = modifier.fillMaxWidth().alpha(if (atendida) 0.85f else 1f),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Row(modifier = Modifier.height(IntrinsicSize.Min)) {
            Box(Modifier.width(4.dp).fillMaxHeight().background(colores.punto))
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(cita.hora.formatoHora(), style = MetricaMedia, color = if (atendida) TextoSecundario else MaterialTheme.colorScheme.primary)
                    if (cita.horaAnterior != null) {
                        Spacer(Modifier.width(8.dp))
                        Text(
                            cita.horaAnterior.formatoHora(),
                            style = MaterialTheme.typography.bodySmall,
                            color = TextoTerciario,
                            textDecoration = TextDecoration.LineThrough
                        )
                    }
                    if (etiquetaTiempo != null) {
                        Spacer(Modifier.width(8.dp))
                        Text(etiquetaTiempo, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.secondary)
                    }
                    Spacer(Modifier.weight(1f))
                    EstadoCitaChip(cita.estado)
                }
                Text(cita.pacienteNombre, style = MaterialTheme.typography.titleMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                if (detallada) {
                    Text(
                        "${cita.historiaClinica} · ${cita.edad} años · ${cita.sexo}",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextoSecundario
                    )
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Fondo, RoundedCornerShape(8.dp))
                            .padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.MedicalServices, contentDescription = null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text(cita.tipo, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f, fill = false))
                            if (cita.etiquetaTratamiento != null) {
                                Spacer(Modifier.width(8.dp))
                                Text(cita.etiquetaTratamiento, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                            }
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.Domain, contentDescription = null, tint = TextoSecundario, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text(ubicacion(cita), style = MaterialTheme.typography.bodySmall, color = TextoSecundario)
                        }
                    }
                    if (cita.esProcedimientoMayor) {
                        EstadoChip("Procedimiento mayor", ColoresEstado(EstadoAlerta, EstadoAlertaFondo, EstadoAlerta))
                    }
                } else {
                    Text(
                        "${cita.tipo} · ${cita.sede.nombre} ${cita.consultorio}",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextoSecundario,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

fun ubicacion(cita: Cita): String = buildString {
    append("${cita.sede.nombre} · ${cita.consultorio}")
    if (cita.detalleConsultorio != null) append(" (${cita.detalleConsultorio})")
}
