package com.example.medicitas.domain.usecase

import com.example.medicitas.domain.repository.AuthRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val repository: AuthRepository,
    private val cargarDatosSesionUseCase: CargarDatosSesionUseCase
) {
    suspend operator fun invoke(cmp: String, contrasena: String, recordarUsuario: Boolean): Result<Unit> =
        repository.login(cmp.trim(), contrasena, recordarUsuario)
            .mapCatching { cargarDatosSesionUseCase().getOrThrow() }
}
