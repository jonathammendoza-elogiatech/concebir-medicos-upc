package com.example.medicitas.presentation.screens.agenda

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
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.DoneAll
import androidx.compose.material.icons.outlined.EventAvailable
import androidx.compose.material.icons.outlined.EventBusy
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.medicitas.domain.model.Cita
import com.example.medicitas.domain.model.EstadoCita
import com.example.medicitas.domain.model.Sede
import com.example.medicitas.domain.usecase.GetAgendaUseCase
import com.example.medicitas.presentation.common.AppTopBar
import com.example.medicitas.presentation.common.CitaCard
import com.example.medicitas.presentation.common.EstadoVacio
import com.example.medicitas.presentation.common.SeccionTitulo
import com.example.medicitas.presentation.common.abreviatura
import com.example.medicitas.presentation.common.formatoDiaMes
import com.example.medicitas.presentation.common.formatoHora
import com.example.medicitas.presentation.common.nombreMes
import com.example.medicitas.ui.theme.EstadoAtendida
import com.example.medicitas.ui.theme.PetroleoClaro
import com.example.medicitas.ui.theme.TextoSecundario
import com.example.medicitas.ui.theme.TextoTerciario
import java.time.Duration
import java.time.LocalDate
import java.time.temporal.WeekFields

private const val SEMANAS_VISIBLES = 3L
private const val MINUTOS_AVISO_PROXIMA = 60L

@Composable
fun AgendaScreen(
    onVerCita: (String) -> Unit,
    viewModel: AgendaViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val sedeTexto = uiState.sedeFiltro?.let { "Sede ${it.nombre}" } ?: "Todas las sedes"
    val subtitulo = listOfNotNull(sedeTexto, uiState.actualizadoA?.let { "Actualizado ${it.formatoHora()}" }).joinToString(" · ")

    Column(modifier = Modifier.fillMaxSize()) {
        AppTopBar(titulo = "Agenda", subtitulo = subtitulo, iconoSubtitulo = Icons.Outlined.CalendarToday)
        TabRow(selectedTabIndex = uiState.vista.ordinal, containerColor = MaterialTheme.colorScheme.surface) {
            Tab(selected = uiState.vista == VistaAgenda.DIA, onClick = { viewModel.cambiarVista(VistaAgenda.DIA) }, text = { Text("Día") }, unselectedContentColor = TextoSecundario)
            Tab(selected = uiState.vista == VistaAgenda.SEMANA, onClick = { viewModel.cambiarVista(VistaAgenda.SEMANA) }, text = { Text("Semana") }, unselectedContentColor = TextoSecundario)
        }
        LazyColumn(
            contentPadding = PaddingValues(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { SelectorFecha(uiState, onSeleccionar = viewModel::seleccionarFecha) }
            item { FiltroSedes(uiState.sedeFiltro, onSeleccionar = viewModel::filtrarSede) }
            if (uiState.vista == VistaAgenda.DIA) {
                listaDia(uiState, onVerCita)
            } else {
                listaSemana(uiState, onVerCita)
            }
        }
    }
}

@Composable
private fun SelectorFecha(uiState: AgendaUiState, onSeleccionar: (LocalDate) -> Unit) {
    val lunesActual = GetAgendaUseCase.inicioSemana(uiState.hoy)
    val dias = (0 until 7 * SEMANAS_VISIBLES).map { lunesActual.minusWeeks(1).plusDays(it) }
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = 7)
    val semana = uiState.fechaSeleccionada.get(WeekFields.ISO.weekOfWeekBasedYear())

    Surface(color = MaterialTheme.colorScheme.surface) {
        Column(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)) {
            Row(modifier = Modifier.padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "${uiState.fechaSeleccionada.nombreMes().replaceFirstChar { it.uppercase() }} ${uiState.fechaSeleccionada.year}",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    "Semana $semana",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.background(PetroleoClaro, CircleShape).padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
            Spacer(Modifier.height(12.dp))
            LazyRow(
                state = listState,
                contentPadding = PaddingValues(horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(dias, key = { it.toEpochDay() }) { dia ->
                    DiaChip(
                        dia = dia,
                        seleccionado = dia == uiState.fechaSeleccionada,
                        esHoy = dia == uiState.hoy,
                        tieneCitas = dia in uiState.diasConCitas,
                        onClick = { onSeleccionar(dia) }
                    )
                }
            }
        }
    }
}

@Composable
private fun DiaChip(dia: LocalDate, seleccionado: Boolean, esHoy: Boolean, tieneCitas: Boolean, onClick: () -> Unit) {
    val fondo = if (seleccionado) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
    val texto = if (seleccionado) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
    val border = if (esHoy && !seleccionado) BorderStroke(4.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)) else null //borde fecha
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = fondo,
        border = border,
        modifier = Modifier.width(46.dp)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(dia.dayOfWeek.abreviatura(), style = MaterialTheme.typography.labelMedium, color = if (seleccionado) texto else TextoSecundario)
            Text(dia.dayOfMonth.toString(), style = MaterialTheme.typography.titleMedium, color = texto)
            val punto = when {
                esHoy -> if (seleccionado) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.secondary
                tieneCitas -> if (seleccionado) MaterialTheme.colorScheme.onPrimary else TextoTerciario
                else -> null
            }
            Box(Modifier.size(5.dp).background(punto ?: fondo, CircleShape))
        }
    }
}

@Composable
private fun FiltroSedes(sedeFiltro: Sede?, onSeleccionar: (Sede?) -> Unit) {
    val opciones: List<Sede?> = listOf(null) + Sede.entries
    LazyRow(contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(opciones) { sede ->
            val seleccionado = sede == sedeFiltro
            FilterChip(
                selected = seleccionado,
                onClick = { onSeleccionar(sede) },
                label = { Text(sede?.nombre ?: "Todas") },
                leadingIcon = if (seleccionado) {
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

private fun LazyListScope.listaDia(uiState: AgendaUiState, onVerCita: (String) -> Unit) {
    val pendientes = uiState.citas.filter { it.estado != EstadoCita.ATENDIDA }
    // Las que ya pasaron sin registrar no son "próximas": quedan como pendientes de registro
    val porRegistrar = pendientes.filter { yaPaso(uiState, it) }
    val proximas = pendientes.filterNot { yaPaso(uiState, it) }
    val atendidas = uiState.citas.filter { it.estado == EstadoCita.ATENDIDA }

    item {
        Row(modifier = Modifier.padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Outlined.EventAvailable, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(6.dp))
            Text(
                "${uiState.citas.size} citas programadas",
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.weight(1f)
            )
            Text(uiState.fechaSeleccionada.formatoDiaMes(), style = MaterialTheme.typography.labelMedium, color = TextoSecundario)
        }
    }
    if (uiState.citas.isEmpty()) {
        item { EstadoVacio("Sin citas para este día", Icons.Outlined.EventBusy) }
        return
    }
    if (proximas.isNotEmpty()) {
        item { SeccionTitulo("PRÓXIMAS ATENCIONES", modifier = Modifier.padding(horizontal = 16.dp)) }
        items(proximas, key = { it.id }) { cita ->
            CitaCard(
                cita = cita,
                onClick = { onVerCita(cita.id) },
                detallada = true,
                etiquetaTiempo = etiquetaTiempo(uiState, cita),
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
    if (porRegistrar.isNotEmpty()) {
        item { SeccionTitulo("PENDIENTES DE REGISTRO (${porRegistrar.size})", modifier = Modifier.padding(horizontal = 16.dp)) }
        items(porRegistrar, key = { it.id }) { cita ->
            CitaCard(cita = cita, onClick = { onVerCita(cita.id) }, detallada = true, modifier = Modifier.padding(horizontal = 16.dp))
        }
    }
    if (atendidas.isNotEmpty()) {
        item {
            Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                Text("ATENCIONES COMPLETADAS (${atendidas.size})", style = MaterialTheme.typography.labelSmall, color = TextoSecundario)
                Spacer(Modifier.width(6.dp))
                Icon(Icons.Outlined.DoneAll, contentDescription = null, tint = EstadoAtendida, modifier = Modifier.size(16.dp))
            }
        }
        items(atendidas, key = { it.id }) { cita ->
            CitaCard(cita = cita, onClick = { onVerCita(cita.id) }, detallada = true, modifier = Modifier.padding(horizontal = 16.dp))
        }
    }
}

private fun LazyListScope.listaSemana(uiState: AgendaUiState, onVerCita: (String) -> Unit) {
    val lunes = GetAgendaUseCase.inicioSemana(uiState.fechaSeleccionada)
    val porDia = uiState.citas.groupBy { it.fecha }
    (0L until 7L).map { lunes.plusDays(it) }.forEach { dia ->
        val citasDia = porDia[dia].orEmpty()
        item(key = "dia-${dia.toEpochDay()}") {
            val titulo = dia.formatoDiaMes().uppercase() + if (dia == uiState.hoy) " · HOY" else ""
            SeccionTitulo(titulo, modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp)) {
                Text("${citasDia.size} citas", style = MaterialTheme.typography.labelMedium, color = TextoSecundario)
            }
        }
        if (citasDia.isEmpty()) {
            item(key = "vacio-${dia.toEpochDay()}") {
                Text(
                    "Sin citas",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextoTerciario,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        } else {
            items(citasDia, key = { it.id }) { cita ->
                CitaCard(cita = cita, onClick = { onVerCita(cita.id) }, modifier = Modifier.padding(horizontal = 16.dp))
            }
        }
    }
}

private fun yaPaso(uiState: AgendaUiState, cita: Cita): Boolean =
    cita.fecha.isBefore(uiState.hoy) || (cita.fecha == uiState.hoy && cita.hora.isBefore(uiState.horaActual))

private fun etiquetaTiempo(uiState: AgendaUiState, cita: Cita): String? {
    if (cita.fecha != uiState.hoy) return null
    val minutos = Duration.between(uiState.horaActual, cita.hora).toMinutes()
    return if (minutos in 0..MINUTOS_AVISO_PROXIMA) "En $minutos min" else null
}
