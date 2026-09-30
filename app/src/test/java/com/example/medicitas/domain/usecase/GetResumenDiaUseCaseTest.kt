package com.example.medicitas.domain.usecase

import com.example.medicitas.data.mock.SysmedicalMockDataSource
import com.example.medicitas.domain.model.EstadoCita
import com.example.medicitas.domain.model.Sede
import org.junit.Assert.assertEquals
import org.junit.Test

class GetResumenDiaUseCaseTest {

    private val mock = SysmedicalMockDataSource()

    @Test
    fun `resumen cuenta citas atendidas y pendientes de la sede activa`() {
        val resumen = GetResumenDiaUseCase.calcular(mock.citas.value, mock.medico.value, mock.fechaHoy, mock.horaActual)
        assertEquals(8, resumen.totalCitas)
        assertEquals(3, resumen.atendidas)
        assertEquals(5, resumen.pendientesRegistro)
        assertEquals("c04", resumen.proximaCita?.id)
    }

    @Test
    fun `atender la proxima cita la descuenta de pendientes`() {
        val citas = mock.citas.value.map { if (it.id == "c04") it.copy(estado = EstadoCita.ATENDIDA) else it }
        val resumen = GetResumenDiaUseCase.calcular(citas, mock.medico.value, mock.fechaHoy, mock.horaActual)
        assertEquals(4, resumen.atendidas)
        assertEquals(4, resumen.pendientesRegistro)
        assertEquals("c05", resumen.proximaCita?.id)
    }

    @Test
    fun `otra sede sin citas hoy devuelve resumen vacio`() {
        val medico = mock.medico.value.copy(sedeActiva = Sede.SAN_MIGUEL)
        val resumen = GetResumenDiaUseCase.calcular(mock.citas.value, medico, mock.fechaHoy, mock.horaActual)
        assertEquals(0, resumen.totalCitas)
        assertEquals(null, resumen.proximaCita)
    }
}
