package com.example.medicitas.presentation.screens.pacientes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicitas.domain.model.EstadoTratamiento
import com.example.medicitas.domain.model.Paciente
import com.example.medicitas.domain.usecase.BuscarPacientesUseCase
import com.example.medicitas.domain.usecase.GetPerfilUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// Estado propio de la pantalla; la sede llega del perfil y se combina al armar el UiState
private data class BusquedaPacientes(
    val todos: List<Paciente>? = null,
    val consulta: String = "",
    val estado: EstadoTratamiento? = null,
    val error: String? = null
)

@HiltViewModel
class PacientesViewModel @Inject constructor(
    private val buscarPacientesUseCase: BuscarPacientesUseCase,
    getPerfilUseCase: GetPerfilUseCase
) : ViewModel() {

    private val busqueda = MutableStateFlow(BusquedaPacientes())
    private val medico = getPerfilUseCase()

    val uiState: StateFlow<PacientesUiState> =
        combine(busqueda, medico) { busqueda, medico ->
            val lista = busqueda.todos.orEmpty()
            PacientesUiState(
                cargando = busqueda.todos == null && busqueda.error == null,
                consulta = busqueda.consulta,
                filtroEstado = busqueda.estado,
                sede = medico.sedeActiva,
                totalAsignados = lista.count { it.sede == medico.sedeActiva },
                pacientes = BuscarPacientesUseCase.filtrar(lista, medico.sedeActiva, busqueda.estado, busqueda.consulta),
                error = busqueda.error
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), PacientesUiState())

    init {
        cargarPacientes()
    }

    fun onEvent(event: PacientesEvent) {
        when (event) {
            is PacientesEvent.ConsultaChange -> busqueda.update { it.copy(consulta = event.texto) }
            is PacientesEvent.FiltrarEstado -> busqueda.update { it.copy(estado = event.estado) }
        }
    }

    private fun cargarPacientes() {
        viewModelScope.launch {
            buscarPacientesUseCase()
                .onSuccess { lista -> busqueda.update { it.copy(todos = lista, error = null) } }
                .onFailure { busqueda.update { it.copy(error = "No se pudo cargar la lista de pacientes") } }
        }
    }
}
