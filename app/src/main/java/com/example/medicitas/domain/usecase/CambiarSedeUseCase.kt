package com.example.medicitas.domain.usecase

import com.example.medicitas.domain.model.Sede
import com.example.medicitas.domain.repository.MedicoRepository
import javax.inject.Inject

class CambiarSedeUseCase @Inject constructor(private val repository: MedicoRepository) {
    suspend operator fun invoke(sede: Sede): Result<Unit> = repository.cambiarSede(sede)
}
