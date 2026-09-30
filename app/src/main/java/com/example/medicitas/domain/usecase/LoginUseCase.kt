package com.example.medicitas.domain.usecase

import com.example.medicitas.domain.repository.AuthRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(private val repository: AuthRepository) {
    suspend operator fun invoke(cmp: String, contrasena: String, recordarUsuario: Boolean): Result<Unit> =
        repository.login(cmp.trim(), contrasena, recordarUsuario)
}
