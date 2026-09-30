package com.example.medicitas.domain.usecase

import com.example.medicitas.domain.model.Cita
import com.example.medicitas.domain.model.EstadoCita
import com.example.medicitas.domain.model.Medico
import com.example.medicitas.domain.model.ResumenDia
import com.example.medicitas.domain.repository.CitaRepository
import com.example.medicitas.domain.repository.MedicoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

class GetResumenDiaUseCase @Inject constructor(
    private val citaRepository: CitaRepository,
    private val medicoRepository: MedicoRepository
) {
    operator fun invoke(): Flow<ResumenDia> =
        combine(citaRepository.citas, medicoRepository.medico) { citas, medico ->
            calcular(citas, medico, citaRepository.fechaHoy, citaRepository.horaActual)
        }

    companion object {
        fun calcular(citas: List<Cita>, medico: Medico, hoy: LocalDate, horaActual: LocalTime): ResumenDia {
            val delDia = citas
                .filter { it.fecha == hoy && it.sede == medico.sedeActiva && it.estado != EstadoCita.ANULADA }
                .sortedBy { it.hora }
            val pendientes = delDia.filter { it.estado != EstadoCita.ATENDIDA }
            val proxima = pendientes.firstOrNull { !it.hora.isBefore(horaActual) } ?: pendientes.firstOrNull()
            val siguientes = (pendientes - setOfNotNull(proxima)) + delDia.filter { it.estado == EstadoCita.ATENDIDA }
            return ResumenDia(
                fecha = hoy,
                horaActual = horaActual,
                medico = medico,
                totalCitas = delDia.size,
                atendidas = delDia.count { it.estado == EstadoCita.ATENDIDA },
                pendientesRegistro = pendientes.size,
                proximaCita = proxima,
                siguientesCitas = siguientes
            )
        }
    }
}
