package com.example.medicitas.data.repository

import com.example.medicitas.data.mock.SysmedicalMockDataSource
import com.example.medicitas.domain.model.Medico
import com.example.medicitas.domain.model.Sede
import com.example.medicitas.domain.repository.MedicoRepository
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

class MedicoRepositoryImpl @Inject constructor(
    private val dataSource: SysmedicalMockDataSource
) : MedicoRepository {

    override val medico: StateFlow<Medico> = dataSource.medico.asStateFlow()

    override suspend fun cambiarSede(sede: Sede): Result<Unit> = runCatching {
        dataSource.medico.update { it.copy(sedeActiva = sede) }
    }

    override suspend fun cambiarNotificaciones(activas: Boolean): Result<Unit> = runCatching {
        dataSource.medico.update { it.copy(notificacionesAgenda = activas) }
    }
}
