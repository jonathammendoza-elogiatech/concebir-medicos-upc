package com.example.medicitas.domain.usecase

import com.example.medicitas.data.mock.SysmedicalMockDataSource
import com.example.medicitas.domain.model.EstadoCita
import com.example.medicitas.domain.model.Sede
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalTime

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
    fun `siguientes citas son solo las pendientes de hoy que vienen despues de la proxima`() {
        val resumen = GetResumenDiaUseCase.calcular(mock.citas.value, mock.medico.value, mock.fechaHoy, mock.horaActual)
        assertEquals(listOf("c05", "c06", "c07", "c08"), resumen.siguientesCitas.map { it.id })
        assertTrue(resumen.siguientesCitas.none { it.estado == EstadoCita.ATENDIDA || it.fecha != mock.fechaHoy })
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
    fun `una cita pasada sin registrar no es la proxima atencion`() {
        // 11:00: c04 (10:30) sigue sin registrar, la próxima es c05 (11:15)
        val resumen = GetResumenDiaUseCase.calcular(mock.citas.value, mock.medico.value, mock.fechaHoy, LocalTime.of(11, 0))
        assertEquals("c05", resumen.proximaCita?.id)
        assertEquals(5, resumen.pendientesRegistro)
    }

    @Test
    fun `al terminar el dia no hay proxima ni siguientes`() {
        val resumen = GetResumenDiaUseCase.calcular(mock.citas.value, mock.medico.value, mock.fechaHoy, LocalTime.of(20, 0))
        assertEquals(null, resumen.proximaCita)
        assertTrue(resumen.siguientesCitas.isEmpty())
        assertEquals(8, resumen.totalCitas)
    }

    @Test
    fun `otra sede sin citas hoy devuelve resumen vacio`() {
        val medico = mock.medico.value.copy(sedeActiva = Sede.SAN_MIGUEL)
        val resumen = GetResumenDiaUseCase.calcular(mock.citas.value, medico, mock.fechaHoy, mock.horaActual)
        assertEquals(0, resumen.totalCitas)
        assertEquals(null, resumen.proximaCita)
    }
}
