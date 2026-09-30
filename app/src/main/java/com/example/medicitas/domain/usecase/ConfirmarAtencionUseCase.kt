package com.example.medicitas.domain.usecase

import com.example.medicitas.domain.model.TipoNota
import com.example.medicitas.domain.repository.CitaRepository
import javax.inject.Inject

class ConfirmarAtencionUseCase @Inject constructor(private val repository: CitaRepository) {
    suspend operator fun invoke(citaId: String, tipoNota: TipoNota, nota: String, marcarAtendida: Boolean): Result<Unit> {
        if (nota.isBlank()) return Result.failure(IllegalArgumentException("La nota no puede estar vacía"))
        if (nota.length > MAX_CARACTERES) return Result.failure(IllegalArgumentException("La nota supera los $MAX_CARACTERES caracteres"))
        return repository.registrarAtencion(citaId, tipoNota, nota.trim(), marcarAtendida)
    }

    companion object {
        const val MAX_CARACTERES = 1000
    }
}
