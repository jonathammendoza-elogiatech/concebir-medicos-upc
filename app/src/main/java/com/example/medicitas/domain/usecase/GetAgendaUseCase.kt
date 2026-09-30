package com.example.medicitas.domain.usecase

import com.example.medicitas.domain.model.Cita
import com.example.medicitas.domain.model.EstadoCita
import com.example.medicitas.domain.model.Sede
import com.example.medicitas.domain.repository.CitaRepository
import kotlinx.coroutines.flow.StateFlow
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

class GetAgendaUseCase @Inject constructor(private val repository: CitaRepository) {

    val fechaHoy: LocalDate get() = repository.fechaHoy
    val horaActual: LocalTime get() = repository.horaActual

    operator fun invoke(): StateFlow<List<Cita>> = repository.citas

    companion object {
        fun inicioSemana(fecha: LocalDate): LocalDate = fecha.with(DayOfWeek.MONDAY)

        /** Citas visibles (sin anuladas) del día o de la semana de [fecha], filtradas por sede (null = todas). */
        fun filtrar(citas: List<Cita>, fecha: LocalDate, semana: Boolean, sede: Sede?): List<Cita> {
            val desde = if (semana) inicioSemana(fecha) else fecha
            val hasta = if (semana) desde.plusDays(6) else fecha
            return citas
                .filter { it.estado != EstadoCita.ANULADA }
                .filter { sede == null || it.sede == sede }
                .filter { !it.fecha.isBefore(desde) && !it.fecha.isAfter(hasta) }
                .sortedWith(compareBy({ it.fecha }, { it.hora }))
        }
    }
}
