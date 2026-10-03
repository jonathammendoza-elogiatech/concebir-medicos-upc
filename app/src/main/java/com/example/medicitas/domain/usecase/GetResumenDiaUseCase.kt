package com.example.medicitas.domain.usecase

import com.example.medicitas.domain.model.Cita
import com.example.medicitas.domain.model.EstadoCita
import com.example.medicitas.domain.model.Medico
import com.example.medicitas.domain.model.ResumenDia
import com.example.medicitas.domain.repository.CitaRepository
import com.example.medicitas.domain.repository.MedicoRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

class GetResumenDiaUseCase @Inject constructor(
    private val citaRepository: CitaRepository,
    private val medicoRepository: MedicoRepository
) {
    // Se recalcula con cada cambio de datos y cada minuto, para que "próxima atención" avance sola
    operator fun invoke(): Flow<ResumenDia> =
        combine(citaRepository.citas, medicoRepository.medico, cadaMinuto()) { citas, medico, _ ->
            calcular(citas, medico, citaRepository.fechaHoy, citaRepository.horaActual)
        }

    private fun cadaMinuto(): Flow<Unit> = flow {
        while (true) {
            emit(Unit)
            delay(60_000)
        }
    }

    companion object {
        fun calcular(citas: List<Cita>, medico: Medico, hoy: LocalDate, horaActual: LocalTime): ResumenDia {
            val delDia = citas
                .filter { it.fecha == hoy && it.sede == medico.sedeActiva && it.estado != EstadoCita.ANULADA }
                .sortedBy { it.hora }
            val pendientes = delDia.filter { it.estado != EstadoCita.ATENDIDA }
            // Solo citas de hoy que aún no empiezan; las ya pasadas sin registrar cuentan como pendientes
            val porVenir = pendientes.filter { !it.hora.isBefore(horaActual) }
            return ResumenDia(
                fecha = hoy,
                horaActual = horaActual,
                medico = medico,
                totalCitas = delDia.size,
                atendidas = delDia.count { it.estado == EstadoCita.ATENDIDA },
                pendientesRegistro = pendientes.size,
                proximaCita = porVenir.firstOrNull(),
                siguientesCitas = porVenir.drop(1)
            )
        }
    }
}
