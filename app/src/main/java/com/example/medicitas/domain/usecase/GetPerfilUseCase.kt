package com.example.medicitas.domain.usecase

import com.example.medicitas.domain.model.Medico
import com.example.medicitas.domain.repository.MedicoRepository
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class GetPerfilUseCase @Inject constructor(private val repository: MedicoRepository) {
    operator fun invoke(): StateFlow<Medico> = repository.medico
}
