package com.example.medicitas.presentation.screens.ficha

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Badge
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.EventRepeat
import androidx.compose.material.icons.outlined.HealthAndSafety
import androidx.compose.material.icons.outlined.HistoryEdu
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.MedicalServices
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.medicitas.domain.model.Atencion
import com.example.medicitas.domain.model.EstadoCita
import com.example.medicitas.domain.model.EstadoTratamiento
import com.example.medicitas.domain.model.Paciente
import com.example.medicitas.presentation.common.AppTopBar
import com.example.medicitas.presentation.common.AvatarIniciales
import com.example.medicitas.presentation.common.CargandoContenido
import com.example.medicitas.presentation.common.DatoEtiquetado
import com.example.medicitas.presentation.common.EstadoChip
import com.example.medicitas.presentation.common.EstadoVacio
import com.example.medicitas.presentation.common.TarjetaClinica
import com.example.medicitas.presentation.common.TituloTarjeta
import com.example.medicitas.presentation.common.colores
import com.example.medicitas.presentation.common.formatoCorto
import com.example.medicitas.presentation.common.formatoHora
import com.example.medicitas.ui.theme.TextoSecundario

private const val MAX_ATENCIONES_RESUMEN = 3

@Composable
fun FichaPacienteScreen(
    onBack: () -> Unit,
    onVerResultados: (String) -> Unit,
    viewModel: FichaPacienteViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    FichaPacienteContent(uiState = uiState, onEvent = viewModel::onEvent, onBack = onBack, onVerResultados = onVerResultados)
}

@Composable
private fun FichaPacienteContent(
    uiState: FichaPacienteUiState,
    onEvent: (FichaPacienteEvent) -> Unit,
    onBack: () -> Unit,
    onVerResultados: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        AppTopBar(titulo = "Ficha del paciente", onBack = onBack)
        val paciente = uiState.paciente
        when {
            uiState.cargando -> CargandoContenido()
            paciente == null -> EstadoVacio(uiState.error ?: "No se encontró al paciente", Icons.Outlined.ErrorOutline)
            else -> {
                Encabezado(paciente)
                TabRow(selectedTabIndex = uiState.pestana.ordinal, containerColor = MaterialTheme.colorScheme.surface) {
                    PestanaFicha.entries.forEach { pestana ->
                        Tab(
                            selected = pestana == uiState.pestana,
                            onClick = { onEvent(FichaPacienteEvent.SeleccionarPestana(pestana)) },
                            text = { Text(pestana.titulo) },
                            unselectedContentColor = TextoSecundario
                        )
                    }
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    when (uiState.pestana) {
                        PestanaFicha.DATOS -> {
                            TarjetaTratamiento(paciente)
                            TarjetaDatos(paciente)
                            TarjetaAtenciones(paciente.atenciones.take(MAX_ATENCIONES_RESUMEN), total = paciente.atenciones.size)
                        }
                        PestanaFicha.ANTECEDENTES -> TarjetaAntecedentes(paciente)
                        PestanaFicha.HISTORIAL -> TarjetaAtenciones(paciente.atenciones, total = paciente.atenciones.size)
                    }
                }
                Surface(color = MaterialTheme.colorScheme.surface, shadowElevation = 8.dp) {
                    Button(
                        onClick = { onVerResultados(paciente.id) },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .padding(16.dp)
                            .height(48.dp)
                    ) {
                        Icon(Icons.Outlined.Description, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Ver resultados de exámenes", style = MaterialTheme.typography.labelLarge)
                    }
                }
            }
        }
    }
}

@Composable
private fun Encabezado(paciente: Paciente) {
    Surface(color = MaterialTheme.colorScheme.surface) {
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            AvatarIniciales(paciente.iniciales, tamano = 56.dp)
            Spacer(Modifier.width(14.dp))
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val activa = paciente.estadoTratamiento == EstadoTratamiento.EN_TRATAMIENTO
                    EstadoChip(
                        if (activa) "Activa" else "Seguimiento",
                        if (activa) EstadoCita.ATENDIDA.colores() else EstadoCita.CONFIRMADA.colores()
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(paciente.historiaClinica, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                }
                Text(paciente.nombre, style = MaterialTheme.typography.headlineSmall)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("${paciente.edad} años · ", style = MaterialTheme.typography.bodySmall, color = TextoSecundario)
                    Icon(Icons.Outlined.LocationOn, contentDescription = null, tint = TextoSecundario, modifier = Modifier.size(14.dp))
                    Text("Sede ${paciente.sede.nombre}", style = MaterialTheme.typography.bodySmall, color = TextoSecundario)
                }
            }
        }
    }
}

@Composable
private fun TarjetaTratamiento(paciente: Paciente) {
    val tratamiento = paciente.tratamiento
    TarjetaClinica(modifier = Modifier.fillMaxWidth()) {
        TituloTarjeta("Tratamiento vigente", Icons.Outlined.MedicalServices) {
            if (tratamiento != null) EstadoChip("Ciclo activo", EstadoCita.ATENDIDA.colores())
        }
        Spacer(Modifier.height(12.dp))
        if (tratamiento == null) {
            Text(paciente.resumenTratamiento, style = MaterialTheme.typography.titleSmall)
            Text("Sin protocolo farmacológico activo", style = MaterialTheme.typography.bodySmall, color = TextoSecundario)
            return@TarjetaClinica
        }
        Text(
            "${tratamiento.protocolo} · Ciclo ${tratamiento.ciclo} · Día ${tratamiento.dia}",
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(Modifier.height(4.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Outlined.CalendarMonth, contentDescription = null, tint = TextoSecundario, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(6.dp))
            Text("Inicio: ${tratamiento.inicio.formatoCorto()}", style = MaterialTheme.typography.bodySmall, color = TextoSecundario)
        }
        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = MaterialTheme.colorScheme.outline)
        DatoEtiquetado("Protocolo farmacológico", tratamiento.esquema, detalle = tratamiento.medicacion)
        Spacer(Modifier.height(12.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            AvatarIniciales(iniciales(tratamiento.medicoResponsable), tamano = 28.dp)
            Spacer(Modifier.width(8.dp))
            Text("${tratamiento.medicoResponsable} · Médico responsable", style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun TarjetaDatos(paciente: Paciente) {
    TarjetaClinica(modifier = Modifier.fillMaxWidth()) {
        TituloTarjeta("Datos del paciente", Icons.Outlined.Badge)
        Spacer(Modifier.height(12.dp))
        Row {
            DatoEtiquetado("DNI", paciente.dni, Modifier.weight(1f))
            DatoEtiquetado("F. nacimiento", paciente.fechaNacimiento.formatoCorto(), Modifier.weight(1f))
        }
        Spacer(Modifier.height(12.dp))
        DatoEtiquetado("Teléfono", paciente.telefono)
        Spacer(Modifier.height(12.dp))
        DatoEtiquetado("Correo electrónico", paciente.correo)
        Spacer(Modifier.height(12.dp))
        DatoEtiquetado("Seguro / cobertura", paciente.seguro, detalle = paciente.plan)
    }
}

@Composable
private fun TarjetaAntecedentes(paciente: Paciente) {
    val antecedentes = paciente.antecedentes
    TarjetaClinica(modifier = Modifier.fillMaxWidth()) {
        TituloTarjeta("Antecedentes clave", Icons.Outlined.HistoryEdu)
        Spacer(Modifier.height(12.dp))
        DatoEtiquetado("Obstétricos · ${antecedentes.obstetricos}", antecedentes.obstetricosDetalle)
        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = MaterialTheme.colorScheme.outline)
        DatoEtiquetado("Quirúrgicos", antecedentes.quirurgicos)
        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = MaterialTheme.colorScheme.outline)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Outlined.HealthAndSafety, contentDescription = null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text("Alergias medicamentosas", style = MaterialTheme.typography.titleSmall, modifier = Modifier.weight(1f))
            EstadoChip(antecedentes.alergias, EstadoCita.ATENDIDA.colores())
        }
        Spacer(Modifier.height(12.dp))
        DatoEtiquetado("Grupo sanguíneo", antecedentes.grupoSanguineo)
    }
}

@Composable
private fun TarjetaAtenciones(atenciones: List<Atencion>, total: Int) {
    TarjetaClinica(modifier = Modifier.fillMaxWidth()) {
        TituloTarjeta("Últimas atenciones", Icons.Outlined.EventRepeat) {
            Text("$total registros", style = MaterialTheme.typography.labelMedium, color = TextoSecundario)
        }
        Spacer(Modifier.height(8.dp))
        if (atenciones.isEmpty()) {
            Text("Sin atenciones registradas", style = MaterialTheme.typography.bodySmall, color = TextoSecundario)
        }
        atenciones.forEachIndexed { index, atencion ->
            FilaAtencion(atencion, ultima = index == atenciones.lastIndex)
        }
    }
}

@Composable
private fun FilaAtencion(atencion: Atencion, ultima: Boolean) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(20.dp)) {
            Box(Modifier.padding(top = 4.dp).size(10.dp).background(MaterialTheme.colorScheme.secondary, CircleShape))
        }
        Spacer(Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                "${atencion.fecha.formatoCorto()} · ${atencion.hora.formatoHora()}",
                style = MaterialTheme.typography.labelMedium,
                color = TextoSecundario
            )
            Text(atencion.tipo, style = MaterialTheme.typography.titleSmall)
            Text(atencion.medico, style = MaterialTheme.typography.bodySmall, color = TextoSecundario)
        }
    }
    if (!ultima) HorizontalDivider(color = MaterialTheme.colorScheme.outline)
}

// "Dra. Ana Torres Delgado" -> "AT"
private fun iniciales(nombre: String): String =
    nombre.split(" ").filter { it.isNotBlank() && !it.endsWith(".") }.take(2).joinToString("") { it.first().uppercase() }
