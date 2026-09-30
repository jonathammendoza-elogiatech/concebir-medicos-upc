package com.example.medicitas.domain.usecase

import com.example.medicitas.domain.repository.AuthRepository
import javax.inject.Inject

class LoginBiometricoUseCase @Inject constructor(private val repository: AuthRepository) {
    suspend operator fun invoke(): Result<Unit> = repository.loginBiometrico()
}
