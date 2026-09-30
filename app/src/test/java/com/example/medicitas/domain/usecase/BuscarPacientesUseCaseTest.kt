package com.example.medicitas.domain.usecase

import com.example.medicitas.data.mock.SysmedicalMockDataSource
import com.example.medicitas.domain.model.EstadoTratamiento
import com.example.medicitas.domain.model.Sede
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class BuscarPacientesUseCaseTest {

    private val pacientes = SysmedicalMockDataSource().pacientes.value

    @Test
    fun `busca por nombre sin importar tildes ni mayusculas`() {
        val resultado = BuscarPacientesUseCase.filtrar(pacientes, Sede.SAN_ISIDRO, null, "LUCIA fernandez")
        assertEquals(listOf("p01"), resultado.map { it.id })
    }

    @Test
    fun `busca por prefijo de DNI`() {
        val resultado = BuscarPacientesUseCase.filtrar(pacientes, Sede.SAN_ISIDRO, null, "458921")
        assertEquals(listOf("p01"), resultado.map { it.id })
    }

    @Test
    fun `filtra por sede y estado de tratamiento`() {
        val resultado = BuscarPacientesUseCase.filtrar(pacientes, Sede.SAN_ISIDRO, EstadoTratamiento.EN_SEGUIMIENTO, "")
        assertTrue(resultado.isNotEmpty())
        assertTrue(resultado.all { it.sede == Sede.SAN_ISIDRO && it.estadoTratamiento == EstadoTratamiento.EN_SEGUIMIENTO })
        assertEquals(2, BuscarPacientesUseCase.filtrar(pacientes, Sede.LOS_OLIVOS, null, "").size)
    }
}
