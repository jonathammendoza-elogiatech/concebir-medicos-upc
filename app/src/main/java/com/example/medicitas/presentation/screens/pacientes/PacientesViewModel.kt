package com.example.medicitas.presentation.screens.pacientes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicitas.domain.model.EstadoTratamiento
import com.example.medicitas.domain.model.Paciente
import com.example.medicitas.domain.model.Sede
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

data class PacientesUiState(
    val cargando: Boolean = true,
    val consulta: String = "",
    val filtroEstado: EstadoTratamiento? = null,
    val sede: Sede = Sede.SAN_ISIDRO,
    val totalAsignados: Int = 0,
    val pacientes: List<Paciente> = emptyList(),
    val error: String? = null
)

private data class Filtros(val consulta: String = "", val estado: EstadoTratamiento? = null)

@HiltViewModel
class PacientesViewModel @Inject constructor(
    private val buscarPacientesUseCase: BuscarPacientesUseCase,
    getPerfilUseCase: GetPerfilUseCase
) : ViewModel() {

    private val todos = MutableStateFlow<List<Paciente>?>(null)
    private val filtros = MutableStateFlow(Filtros())
    private val error = MutableStateFlow<String?>(null)
    private val medico = getPerfilUseCase()

    val uiState: StateFlow<PacientesUiState> =
        combine(todos, filtros, medico, error) { todos, filtros, medico, error ->
            val lista = todos.orEmpty()
            PacientesUiState(
                cargando = todos == null && error == null,
                consulta = filtros.consulta,
                filtroEstado = filtros.estado,
                sede = medico.sedeActiva,
                totalAsignados = lista.count { it.sede == medico.sedeActiva },
                pacientes = BuscarPacientesUseCase.filtrar(lista, medico.sedeActiva, filtros.estado, filtros.consulta),
                error = error
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), PacientesUiState())

    init {
        cargarPacientes()
    }

    fun cargarPacientes() {
        viewModelScope.launch {
            buscarPacientesUseCase()
                .onSuccess { todos.value = it; error.value = null }
                .onFailure { error.value = "No se pudo cargar la lista de pacientes" }
        }
    }

    fun onConsultaChange(texto: String) = filtros.update { it.copy(consulta = texto) }

    fun filtrarEstado(estado: EstadoTratamiento?) = filtros.update { it.copy(estado = estado) }
}
