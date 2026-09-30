package com.example.medicitas.presentation.screens.inicio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicitas.domain.model.ResumenDia
import com.example.medicitas.domain.usecase.GetResumenDiaUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class InicioViewModel @Inject constructor(getResumenDiaUseCase: GetResumenDiaUseCase) : ViewModel() {

    val resumen: StateFlow<ResumenDia?> = getResumenDiaUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)
}
