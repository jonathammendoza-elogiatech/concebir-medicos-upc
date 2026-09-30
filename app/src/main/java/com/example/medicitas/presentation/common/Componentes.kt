package com.example.medicitas.presentation.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.medicitas.domain.model.EstadoCita
import com.example.medicitas.domain.model.EstadoResultado
import com.example.medicitas.ui.theme.AvisoBorde
import com.example.medicitas.ui.theme.AvisoFondo
import com.example.medicitas.ui.theme.EstadoAlerta
import com.example.medicitas.ui.theme.EstadoAlertaFondo
import com.example.medicitas.ui.theme.EstadoAtendida
import com.example.medicitas.ui.theme.EstadoAtendidaFondo
import com.example.medicitas.ui.theme.EstadoConfirmada
import com.example.medicitas.ui.theme.EstadoConfirmadaFondo
import com.example.medicitas.ui.theme.EstadoPendiente
import com.example.medicitas.ui.theme.EstadoPendienteFondo
import com.example.medicitas.ui.theme.EstadoPendientePunto
import com.example.medicitas.ui.theme.PetroleoClaro
import com.example.medicitas.ui.theme.SuperficieVariante
import com.example.medicitas.ui.theme.TextoSecundario

data class ColoresEstado(val texto: Color, val fondo: Color, val punto: Color)

fun EstadoCita.colores(): ColoresEstado = when (this) {
    EstadoCita.CONFIRMADA -> ColoresEstado(EstadoConfirmada, EstadoConfirmadaFondo, EstadoConfirmada)
    EstadoCita.ATENDIDA -> ColoresEstado(EstadoAtendida, EstadoAtendidaFondo, EstadoAtendida)
    EstadoCita.REPROGRAMADA -> ColoresEstado(EstadoPendiente, EstadoPendienteFondo, EstadoPendientePunto)
    EstadoCita.ANULADA -> ColoresEstado(EstadoAlerta, EstadoAlertaFondo, EstadoAlerta)
}

fun EstadoResultado.colores(): ColoresEstado = when (this) {
    EstadoResultado.EN_RANGO -> ColoresEstado(EstadoAtendida, EstadoAtendidaFondo, EstadoAtendida)
    EstadoResultado.FUERA_DE_RANGO -> ColoresEstado(EstadoAlerta, EstadoAlertaFondo, EstadoAlerta)
    EstadoResultado.PENDIENTE -> ColoresEstado(TextoSecundario, SuperficieVariante, TextoSecundario)
}

@Composable
fun EstadoChip(texto: String, colores: ColoresEstado, modifier: Modifier = Modifier, icono: ImageVector? = null) {
    Row(
        modifier = modifier
            .background(colores.fondo, CircleShape)
            .padding(horizontal = 10.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icono != null) {
            Icon(icono, contentDescription = null, tint = colores.punto, modifier = Modifier.size(14.dp))
        } else {
            Box(Modifier.size(6.dp).background(colores.punto, CircleShape))
        }
        Spacer(Modifier.width(6.dp))
        Text(texto, style = MaterialTheme.typography.labelMedium, color = colores.texto)
    }
}

@Composable
fun EstadoCitaChip(estado: EstadoCita, modifier: Modifier = Modifier) {
    EstadoChip(texto = estado.etiqueta, colores = estado.colores(), modifier = modifier)
}

@Composable
fun AvatarIniciales(
    iniciales: String,
    modifier: Modifier = Modifier,
    tamano: Dp = 44.dp,
    fondo: Color = PetroleoClaro,
    color: Color = MaterialTheme.colorScheme.primary
) {
    Box(
        modifier = modifier.size(tamano).background(fondo, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = iniciales,
            style = if (tamano >= 56.dp) MaterialTheme.typography.titleLarge else MaterialTheme.typography.titleSmall,
            color = color
        )
    }
}

@Composable
fun TarjetaClinica(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    radio: Dp = 16.dp,
    borde: BorderStroke = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
    contentPadding: Dp = 16.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    val shape = RoundedCornerShape(radio)
    val interior: @Composable () -> Unit = {
        Column(modifier = Modifier.padding(contentPadding), content = content)
    }
    if (onClick != null) {
        Surface(onClick = onClick, modifier = modifier, shape = shape, color = MaterialTheme.colorScheme.surface, border = borde, content = interior)
    } else {
        Surface(modifier = modifier, shape = shape, color = MaterialTheme.colorScheme.surface, border = borde, content = interior)
    }
}

@Composable
fun SeccionTitulo(texto: String, modifier: Modifier = Modifier, accion: @Composable (() -> Unit)? = null) {
    Row(modifier = modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = texto,
            style = MaterialTheme.typography.labelSmall,
            color = TextoSecundario,
            modifier = Modifier.weight(1f)
        )
        accion?.invoke()
    }
}

@Composable
fun TituloTarjeta(texto: String, icono: ImageVector, modifier: Modifier = Modifier, accion: @Composable (() -> Unit)? = null) {
    Row(modifier = modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Icon(icono, contentDescription = null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(8.dp))
        Text(texto, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
        accion?.invoke()
    }
}

@Composable
fun AvisoInfo(texto: String, modifier: Modifier = Modifier, icono: ImageVector = Icons.Outlined.Info) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(AvisoFondo, RoundedCornerShape(12.dp))
            .border(1.dp, AvisoBorde, RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(icono, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(8.dp))
        Text(texto, style = MaterialTheme.typography.bodySmall, color = TextoSecundario)
    }
}

@Composable
fun DatoEtiquetado(etiqueta: String, valor: String, modifier: Modifier = Modifier, detalle: String? = null) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(etiqueta, style = MaterialTheme.typography.labelMedium, color = TextoSecundario)
        Text(valor, style = MaterialTheme.typography.titleSmall)
        if (detalle != null) {
            Text(detalle, style = MaterialTheme.typography.bodySmall, color = TextoSecundario)
        }
    }
}

@Composable
fun EstadoVacio(texto: String, icono: ImageVector, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth().padding(vertical = 40.dp, horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier.size(56.dp).background(PetroleoClaro, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(icono, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        }
        Text(texto, style = MaterialTheme.typography.bodyMedium, color = TextoSecundario, textAlign = TextAlign.Center)
    }
}

@Composable
fun CargandoContenido(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
    }
}
