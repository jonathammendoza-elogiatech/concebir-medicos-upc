package com.example.medicitas.presentation.screens.ficha

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicitas.domain.model.Paciente
import com.example.medicitas.domain.usecase.GetFichaPacienteUseCase
import com.example.medicitas.presentation.navigation.RutasNav
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class PestanaFicha(val titulo: String) { DATOS("Datos"), ANTECEDENTES("Antecedentes"), HISTORIAL("Historial") }

data class FichaPacienteUiState(
    val cargando: Boolean = true,
    val paciente: Paciente? = null,
    val pestana: PestanaFicha = PestanaFicha.DATOS,
    val error: String? = null
)

@HiltViewModel
class FichaPacienteViewModel @Inject constructor(
    private val getFichaPacienteUseCase: GetFichaPacienteUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val pacienteId: String = checkNotNull(savedStateHandle[RutasNav.ARG_PACIENTE_ID])

    private val _uiState = MutableStateFlow(FichaPacienteUiState())
    val uiState: StateFlow<FichaPacienteUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            getFichaPacienteUseCase(pacienteId)
                .onSuccess { paciente -> _uiState.update { it.copy(cargando = false, paciente = paciente) } }
                .onFailure { e -> _uiState.update { it.copy(cargando = false, error = e.message) } }
        }
    }

    fun seleccionarPestana(pestana: PestanaFicha) = _uiState.update { it.copy(pestana = pestana) }
}
