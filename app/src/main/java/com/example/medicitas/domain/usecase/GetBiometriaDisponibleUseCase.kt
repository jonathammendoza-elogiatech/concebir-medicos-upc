package com.example.medicitas.domain.usecase

import com.example.medicitas.domain.repository.AuthRepository
import javax.inject.Inject

// La biometría solo sirve si hay una sesión guardada ("Recordar usuario" en un ingreso anterior)
class GetBiometriaDisponibleUseCase @Inject constructor(private val repository: AuthRepository) {
    operator fun invoke(): Boolean = repository.tieneSesionGuardada()
}
