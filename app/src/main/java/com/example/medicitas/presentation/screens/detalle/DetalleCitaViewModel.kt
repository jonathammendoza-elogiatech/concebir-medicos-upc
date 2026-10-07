package com.example.medicitas.presentation.screens.detalle

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicitas.domain.usecase.GetDetalleCitaUseCase
import com.example.medicitas.presentation.navigation.RutasNav
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class DetalleCitaViewModel @Inject constructor(
    getDetalleCitaUseCase: GetDetalleCitaUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val citaId: String = checkNotNull(savedStateHandle[RutasNav.ARG_CITA_ID])

    val uiState: StateFlow<DetalleCitaUiState> = getDetalleCitaUseCase(citaId)
        .map { resultado ->
            resultado.fold(
                onSuccess = {
                    DetalleCitaUiState(
                        cargando = false,
                        detalle = it,
                        hoy = getDetalleCitaUseCase.fechaHoy,
                        horaActual = getDetalleCitaUseCase.horaActual
                    )
                },
                onFailure = { DetalleCitaUiState(cargando = false, error = it.message) }
            )
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), DetalleCitaUiState())
}
