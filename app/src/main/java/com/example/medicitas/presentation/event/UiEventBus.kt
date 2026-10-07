package com.example.medicitas.presentation.event

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UiEventBus @Inject constructor() {
    // El buffer evita que el ViewModel quede suspendido mientras se muestra otro mensaje
    private val _events = MutableSharedFlow<UiEvent>(extraBufferCapacity = 8)
    val events: SharedFlow<UiEvent> = _events.asSharedFlow()

    suspend fun sendEvent(event: UiEvent) {
        _events.emit(event)
    }
}
