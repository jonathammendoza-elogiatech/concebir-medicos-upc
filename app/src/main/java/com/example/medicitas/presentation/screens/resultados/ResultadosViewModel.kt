package com.example.medicitas.presentation.screens.resultados

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicitas.domain.model.Paciente
import com.example.medicitas.domain.model.ResultadoExamen
import com.example.medicitas.domain.model.TipoResultado
import com.example.medicitas.domain.usecase.GetFichaPacienteUseCase
import com.example.medicitas.domain.usecase.GetResultadosUseCase
import com.example.medicitas.presentation.navigation.RutasNav
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ResultadosUiState(
    val cargando: Boolean = true,
    val paciente: Paciente? = null,
    val tipo: TipoResultado = TipoResultado.LABORATORIO,
    val resultados: List<ResultadoExamen> = emptyList(),
    val error: String? = null
) {
    val visibles: List<ResultadoExamen> get() = resultados.filter { it.tipo == tipo }
}

@HiltViewModel
class ResultadosViewModel @Inject constructor(
    private val getResultadosUseCase: GetResultadosUseCase,
    private val getFichaPacienteUseCase: GetFichaPacienteUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val pacienteId: String = checkNotNull(savedStateHandle[RutasNav.ARG_PACIENTE_ID])

    private val _uiState = MutableStateFlow(ResultadosUiState())
    val uiState: StateFlow<ResultadosUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val paciente = getFichaPacienteUseCase(pacienteId).getOrNull()
            getResultadosUseCase(pacienteId)
                .onSuccess { lista -> _uiState.update { it.copy(cargando = false, paciente = paciente, resultados = lista) } }
                .onFailure { _uiState.update { it.copy(cargando = false, paciente = paciente, error = "No se pudieron cargar los resultados") } }
        }
    }

    fun seleccionarTipo(tipo: TipoResultado) = _uiState.update { it.copy(tipo = tipo) }
}
