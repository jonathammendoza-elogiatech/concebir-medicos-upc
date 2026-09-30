package com.example.medicitas.domain.usecase

import com.example.medicitas.data.mock.SysmedicalMockDataSource
import com.example.medicitas.domain.model.Sede
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GetAgendaUseCaseTest {

    private val mock = SysmedicalMockDataSource()

    @Test
    fun `vista dia filtra por sede`() {
        val citas = GetAgendaUseCase.filtrar(mock.citas.value, mock.fechaHoy, semana = false, sede = Sede.SAN_ISIDRO)
        assertEquals(8, citas.size)
        assertTrue(citas.zipWithNext().all { (a, b) -> !a.hora.isAfter(b.hora) })
    }

    @Test
    fun `vista semana incluye todas las sedes cuando no hay filtro`() {
        val citas = GetAgendaUseCase.filtrar(mock.citas.value, mock.fechaHoy, semana = true, sede = null)
        assertEquals(mock.citas.value.size, citas.size)
        assertEquals(setOf(Sede.SAN_ISIDRO, Sede.LOS_OLIVOS, Sede.SAN_MIGUEL), citas.map { it.sede }.toSet())
    }
}
