package com.example.medicitas.domain.usecase

import com.example.medicitas.data.mock.SysmedicalMockDataSource
import com.example.medicitas.domain.model.EstadoCita
import com.example.medicitas.domain.model.TipoNota
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ConfirmarAtencionUseCaseTest {

    private val datos = SysmedicalMockDataSource()
    private val repository = CitaRepositoryFalso(datos)
    private val useCase = ConfirmarAtencionUseCase(repository)

    @Before
    fun cargar() = runBlocking { repository.cargarCitas().getOrThrow() }

    @Test
    fun `confirmar marca la cita como atendida y descuenta pendientes`() = runBlocking {
        val resultado = useCase("c04", TipoNota.EVOLUCION, "Control ecográfico sin incidencias.", marcarAtendida = true)

        assertTrue(resultado.isSuccess)
        assertEquals(EstadoCita.ATENDIDA, repository.citas.value.first { it.id == "c04" }.estado)
        val resumen = GetResumenDiaUseCase.calcular(repository.citas.value, datos.medico.value, datos.fechaHoy, datos.horaActual)
        assertEquals(4, resumen.pendientesRegistro)
    }

    @Test
    fun `registrar nota sin marcar atendida mantiene el estado`() = runBlocking {
        useCase("c04", TipoNota.OBSERVACION, "Paciente refiere molestia leve.", marcarAtendida = false)
        assertEquals(EstadoCita.CONFIRMADA, repository.citas.value.first { it.id == "c04" }.estado)
    }

    @Test
    fun `nota vacia se rechaza`() = runBlocking {
        val resultado = useCase("c04", TipoNota.EVOLUCION, "   ", marcarAtendida = true)
        assertTrue(resultado.isFailure)
        assertEquals(EstadoCita.CONFIRMADA, repository.citas.value.first { it.id == "c04" }.estado)
    }
}
