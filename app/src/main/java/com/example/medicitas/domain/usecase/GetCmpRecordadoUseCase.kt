package com.example.medicitas.domain.usecase

import com.example.medicitas.domain.repository.AuthRepository
import javax.inject.Inject

class GetCmpRecordadoUseCase @Inject constructor(private val repository: AuthRepository) {
    operator fun invoke(): String? = repository.getCmpRecordado()
}
