package com.example.medicitas.domain.usecase

import com.example.medicitas.domain.model.EstadoTratamiento
import com.example.medicitas.domain.model.Paciente
import com.example.medicitas.domain.model.Sede
import com.example.medicitas.domain.repository.PacienteRepository
import java.text.Normalizer
import javax.inject.Inject

class BuscarPacientesUseCase @Inject constructor(private val repository: PacienteRepository) {

    suspend operator fun invoke(): Result<List<Paciente>> = repository.getPacientes()

    companion object {
        /** Filtra por sede, estado de tratamiento (null = todos) y texto libre por nombre o DNI. */
        fun filtrar(pacientes: List<Paciente>, sede: Sede, estado: EstadoTratamiento?, consulta: String): List<Paciente> {
            val termino = normalizar(consulta.trim())
            return pacientes
                .filter { it.sede == sede }
                .filter { estado == null || it.estadoTratamiento == estado }
                .filter { termino.isEmpty() || normalizar(it.nombre).contains(termino) || it.dni.startsWith(termino) }
                .sortedByDescending { it.ultimaAtencion }
        }

        private fun normalizar(texto: String): String =
            Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replace(Regex("\\p{Mn}+"), "")
                .lowercase()
    }
}
