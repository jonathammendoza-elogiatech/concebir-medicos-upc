package com.example.medicitas.domain.usecase

import com.example.medicitas.domain.repository.AuthRepository
import javax.inject.Inject

class LogoutUseCase @Inject constructor(private val repository: AuthRepository) {
    suspend operator fun invoke(): Result<Unit> = repository.logout()
}
