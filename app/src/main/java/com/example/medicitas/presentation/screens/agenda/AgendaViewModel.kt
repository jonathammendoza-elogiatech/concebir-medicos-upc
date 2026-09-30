package com.example.medicitas.presentation.screens.agenda

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicitas.domain.model.Cita
import com.example.medicitas.domain.model.Sede
import com.example.medicitas.domain.usecase.GetAgendaUseCase
import com.example.medicitas.domain.usecase.GetPerfilUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

enum class VistaAgenda { DIA, SEMANA }

data class AgendaUiState(
    val hoy: LocalDate,
    val horaActual: LocalTime,
    val sedeActiva: Sede,
    val vista: VistaAgenda = VistaAgenda.DIA,
    val fechaSeleccionada: LocalDate = hoy,
    val sedeFiltro: Sede? = sedeActiva,
    val citas: List<Cita> = emptyList(),
    val diasConCitas: Set<LocalDate> = emptySet()
)

private data class FiltrosAgenda(val vista: VistaAgenda, val fecha: LocalDate, val sede: Sede?)

@HiltViewModel
class AgendaViewModel @Inject constructor(
    private val getAgendaUseCase: GetAgendaUseCase,
    getPerfilUseCase: GetPerfilUseCase
) : ViewModel() {

    private val medico = getPerfilUseCase()

    private val filtros = MutableStateFlow(
        FiltrosAgenda(VistaAgenda.DIA, getAgendaUseCase.fechaHoy, medico.value.sedeActiva)
    )

    val uiState: StateFlow<AgendaUiState> =
        combine(getAgendaUseCase(), medico, filtros) { citas, medico, filtros ->
            AgendaUiState(
                hoy = getAgendaUseCase.fechaHoy,
                horaActual = getAgendaUseCase.horaActual,
                sedeActiva = medico.sedeActiva,
                vista = filtros.vista,
                fechaSeleccionada = filtros.fecha,
                sedeFiltro = filtros.sede,
                citas = GetAgendaUseCase.filtrar(citas, filtros.fecha, filtros.vista == VistaAgenda.SEMANA, filtros.sede),
                diasConCitas = citas.filter { filtros.sede == null || it.sede == filtros.sede }.map { it.fecha }.toSet()
            )
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            AgendaUiState(getAgendaUseCase.fechaHoy, getAgendaUseCase.horaActual, medico.value.sedeActiva)
        )

    init {
        // Al cambiar la sede activa en Perfil, la agenda pasa a filtrar por esa sede
        medico.map { it.sedeActiva }
            .distinctUntilChanged()
            .onEach { sede -> filtros.update { it.copy(sede = sede) } }
            .launchIn(viewModelScope)
    }

    fun cambiarVista(vista: VistaAgenda) = filtros.update { it.copy(vista = vista) }

    fun seleccionarFecha(fecha: LocalDate) = filtros.update { it.copy(fecha = fecha) }

    fun filtrarSede(sede: Sede?) = filtros.update { it.copy(sede = sede) }
}
