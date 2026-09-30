package com.example.medicitas.data.mock

import com.example.medicitas.domain.model.Antecedentes
import com.example.medicitas.domain.model.Atencion
import com.example.medicitas.domain.model.Cita
import com.example.medicitas.domain.model.EstadoCita
import com.example.medicitas.domain.model.EstadoResultado
import com.example.medicitas.domain.model.EstadoTratamiento
import com.example.medicitas.domain.model.Medico
import com.example.medicitas.domain.model.Paciente
import com.example.medicitas.domain.model.ResultadoExamen
import com.example.medicitas.domain.model.Sede
import com.example.medicitas.domain.model.TipoResultado
import com.example.medicitas.domain.model.Tratamiento
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Simula Sysmedical mientras no exista el servicio REST (E6).
 * Todos los datos son ficticios; el estado vive en memoria durante la sesión.
 */
@Singleton
class SysmedicalMockDataSource @Inject constructor() {

    // Jueves fijo para que el prototipo sea reproducible; dia(n) desplaza el calendario de Stitch (22/09 = hoy)
    val fechaHoy: LocalDate = dia(22)
    val horaActual: LocalTime = LocalTime.of(10, 15)

    val medico = MutableStateFlow(
        Medico(
            cmp = CMP_DEMO,
            rne = "23910",
            nombre = "Ana Torres Delgado",
            tratamiento = "Dra.",
            apellido = "Torres",
            iniciales = "AT",
            especialidad = "Medicina reproductiva",
            correo = "ana.torres@concebir.pe",
            telefono = "+51 984 312 900",
            sedeActiva = Sede.SAN_ISIDRO,
            notificacionesAgenda = true
        )
    )

    val pacientes = MutableStateFlow(crearPacientes())

    val citas = MutableStateFlow(crearCitas())

    val resultados: List<ResultadoExamen> = crearResultados()

    suspend fun simularLatencia() = delay(LATENCIA_MS)

    fun credencialesValidas(cmp: String, contrasena: String): Boolean =
        cmp == CMP_DEMO && contrasena == CONTRASENA_DEMO

    private fun dia(d: Int): LocalDate = LocalDate.of(2026, 9, d + DESPLAZAMIENTO_DIAS)

    private fun crearPacientes(): List<Paciente> {
        val medicoResponsable = "Dra. Ana Torres Delgado"
        val sinAntecedentes = Antecedentes(
            obstetricos = "G0 P0",
            obstetricosDetalle = "Sin gestaciones previas.",
            quirurgicos = "Sin antecedentes quirúrgicos relevantes.",
            alergias = "No referidas",
            grupoSanguineo = "O+"
        )

        fun paciente(
            id: String, nombre: String, iniciales: String, edad: Int, sexo: String, dni: String,
            hc: String, sede: Sede, nacimiento: LocalDate, estado: EstadoTratamiento,
            resumen: String, ultima: LocalDateTime, tratamiento: Tratamiento? = null,
            antecedentes: Antecedentes = sinAntecedentes, atenciones: List<Atencion> = emptyList()
        ) = Paciente(
            id = id, nombre = nombre, iniciales = iniciales, edad = edad, sexo = sexo, dni = dni,
            historiaClinica = hc, sede = sede, fechaNacimiento = nacimiento,
            telefono = "+51 9${dni.takeLast(2)} 555 ${dni.take(3)}",
            correo = "${nombre.substringBefore(" ").lowercase()}.${hc.takeLast(4)}@email.com",
            seguro = "Pacífico Salud", plan = "Plan EPS Integral",
            estadoTratamiento = estado, resumenTratamiento = resumen, tratamiento = tratamiento,
            ultimaAtencion = ultima, antecedentes = antecedentes, atenciones = atenciones
        )

        val lucia = paciente(
            id = "p01", nombre = "Lucía Fernández Ramos", iniciales = "LF", edad = 34, sexo = "Femenino",
            dni = "45892147", hc = "HC-20481", sede = Sede.SAN_ISIDRO, nacimiento = LocalDate.of(1990, 5, 14),
            estado = EstadoTratamiento.EN_TRATAMIENTO, resumen = "FIV Ciclo 2 · Día 8",
            ultima = dia(19).atTime(9, 15),
            tratamiento = Tratamiento(
                protocolo = "FIV", ciclo = 2, dia = 8, esquema = "Protocolo antagonista GnRH",
                medicacion = "Menopur 150 UI + Gonal-F 225 UI", inicio = dia(15),
                medicoResponsable = medicoResponsable
            ),
            antecedentes = Antecedentes(
                obstetricos = "G1 P0 A1",
                obstetricosDetalle = "1 aborto espontáneo en semana 8 (2022). Sin legrados posteriores.",
                quirurgicos = "Laparoscopía diagnóstica (2023) · Endometriosis grado I mínima.",
                alergias = "No referidas",
                grupoSanguineo = "O+"
            ),
            atenciones = listOf(
                Atencion(dia(19), LocalTime.of(9, 15), "Ecografía basal y analítica hormonal", medicoResponsable),
                Atencion(dia(15), LocalTime.of(11, 0), "Inicio de estimulación ovárica", medicoResponsable),
                Atencion(dia(5), LocalTime.of(16, 0), "Consulta de planificación de ciclo", medicoResponsable)
            )
        ).copy(telefono = "+51 987 654 321", correo = "lucia.fernandez@email.com", plan = "Plan EPS Integral (cubre procedimientos reproductivos)")

        return listOf(
            lucia,
            paciente("p02", "María José Ugarte", "MU", 31, "Femenino", "46120873", "HC-19402", Sede.SAN_ISIDRO,
                LocalDate.of(1993, 2, 3), EstadoTratamiento.EN_TRATAMIENTO, "Transferencia embrionaria", dia(18).atTime(10, 0)),
            paciente("p03", "Claudia Morales Peñaloza", "CM", 38, "Femenino", "41236590", "HC-18765", Sede.SAN_ISIDRO,
                LocalDate.of(1986, 7, 21), EstadoTratamiento.EN_TRATAMIENTO, "FIV Ciclo 1 · Programada", dia(20).atTime(9, 0)),
            paciente("p04", "Valeria Benavides Torres", "VB", 32, "Femenino", "47015528", "HC-17482", Sede.SAN_ISIDRO,
                LocalDate.of(1992, 11, 9), EstadoTratamiento.EN_SEGUIMIENTO, "Estimulación ovárica", dia(22).atTime(8, 45)),
            paciente("p05", "Gabriela Salas Wong", "GS", 36, "Femenino", "43987012", "HC-17980", Sede.SAN_ISIDRO,
                LocalDate.of(1988, 4, 30), EstadoTratamiento.EN_SEGUIMIENTO, "Criopreservación ovocitaria", dia(5).atTime(12, 0)),
            paciente("p06", "Carlos Mendoza Vargas", "CV", 37, "Masculino", "42658731", "HC-18930", Sede.SAN_ISIDRO,
                LocalDate.of(1987, 1, 17), EstadoTratamiento.EN_SEGUIMIENTO, "Estudio de factor masculino", dia(10).atTime(11, 30)),
            paciente("p07", "Rosa Paredes Salinas", "RP", 38, "Femenino", "40871264", "HC-21019", Sede.SAN_ISIDRO,
                LocalDate.of(1986, 3, 12), EstadoTratamiento.EN_SEGUIMIENTO, "Evaluación inicial de fertilidad", dia(22).atTime(9, 30)),
            paciente("p08", "Fiorella Castro Medina", "FC", 29, "Femenino", "72514039", "HC-22105", Sede.SAN_ISIDRO,
                LocalDate.of(1995, 8, 2), EstadoTratamiento.EN_TRATAMIENTO, "Estimulación ovárica · Día 5", dia(17).atTime(15, 0)),
            paciente("p09", "Andrea Quispe León", "AQ", 33, "Femenino", "45310987", "HC-21560", Sede.SAN_ISIDRO,
                LocalDate.of(1991, 6, 25), EstadoTratamiento.EN_TRATAMIENTO, "FIV Ciclo 1 · Día 3", dia(19).atTime(16, 30)),
            paciente("p10", "Kiara Soto Mejía", "KS", 30, "Femenino", "73145862", "HC-22318", Sede.SAN_ISIDRO,
                LocalDate.of(1994, 10, 8), EstadoTratamiento.EN_SEGUIMIENTO, "Revisión de resultados", dia(22).atTime(7, 30)),
            paciente("p11", "Diana Chávez Ríos", "DC", 35, "Femenino", "44723618", "HC-19877", Sede.LOS_OLIVOS,
                LocalDate.of(1989, 12, 1), EstadoTratamiento.EN_TRATAMIENTO, "Inseminación intrauterina", dia(20).atTime(10, 0)),
            paciente("p12", "Paola Vega Castillo", "PV", 31, "Femenino", "46581230", "HC-22410", Sede.SAN_MIGUEL,
                LocalDate.of(1993, 9, 14), EstadoTratamiento.EN_SEGUIMIENTO, "Evaluación inicial de fertilidad", dia(21).atTime(15, 0)),
            paciente("p13", "Milagros Huamán Torres", "MH", 37, "Femenino", "42097315", "HC-18214", Sede.LOS_OLIVOS,
                LocalDate.of(1987, 5, 19), EstadoTratamiento.EN_SEGUIMIENTO, "Control post transferencia", dia(12).atTime(9, 30))
        )
    }

    private fun crearCitas(): List<Cita> {
        val pacientesPorId = pacientes.value.associateBy { it.id }

        fun cita(
            id: String, fecha: LocalDate, hora: LocalTime, pacienteId: String, tipo: String,
            sede: Sede, consultorio: String, estado: EstadoCita, duracion: Int = 20,
            etiqueta: String? = null, detalle: String? = null, horaAnterior: LocalTime? = null,
            mayor: Boolean = false
        ): Cita {
            val p = pacientesPorId.getValue(pacienteId)
            return Cita(
                id = id, fecha = fecha, hora = hora, duracionMinutos = duracion, pacienteId = p.id,
                pacienteNombre = p.nombre, pacienteIniciales = p.iniciales, historiaClinica = p.historiaClinica,
                edad = p.edad, sexo = p.sexo, tipo = tipo, etiquetaTratamiento = etiqueta, sede = sede,
                consultorio = consultorio, detalleConsultorio = detalle, estado = estado,
                horaAnterior = horaAnterior, esProcedimientoMayor = mayor
            )
        }

        val hoy = fechaHoy
        return listOf(
            cita("c01", hoy, LocalTime.of(7, 30), "p10", "Consulta de resultados", Sede.SAN_ISIDRO, "Consultorio 3", EstadoCita.ATENDIDA),
            cita("c02", hoy, LocalTime.of(8, 45), "p04", "Monitoreo ovulatorio", Sede.SAN_ISIDRO, "Consultorio 3", EstadoCita.ATENDIDA),
            cita("c03", hoy, LocalTime.of(9, 30), "p07", "Consulta inicial fertilidad", Sede.SAN_ISIDRO, "Consultorio 3", EstadoCita.ATENDIDA, duracion = 40),
            cita("c04", hoy, LocalTime.of(10, 30), "p01", "Control folicular · FIV", Sede.SAN_ISIDRO, "Consultorio 3", EstadoCita.CONFIRMADA,
                etiqueta = "Ciclo 2, Día 8", detalle = "Ecografía ginecológica"),
            cita("c05", hoy, LocalTime.of(11, 15), "p06", "Espermatograma analítico", Sede.SAN_ISIDRO, "Consultorio 3", EstadoCita.CONFIRMADA),
            cita("c06", hoy, LocalTime.of(12, 0), "p02", "Transferencia embrionaria", Sede.SAN_ISIDRO, "Quirófano 1", EstadoCita.CONFIRMADA,
                duracion = 45, mayor = true),
            cita("c07", hoy, LocalTime.of(15, 30), "p08", "Ecografía transvaginal", Sede.SAN_ISIDRO, "Consultorio 2", EstadoCita.REPROGRAMADA,
                horaAnterior = LocalTime.of(14, 15)),
            cita("c08", hoy, LocalTime.of(16, 30), "p09", "Control de estimulación", Sede.SAN_ISIDRO, "Consultorio 3", EstadoCita.CONFIRMADA,
                etiqueta = "Ciclo 1, Día 3"),
            cita("c09", dia(20), LocalTime.of(9, 0), "p03", "Consulta de planificación FIV", Sede.LOS_OLIVOS, "Consultorio 1", EstadoCita.ATENDIDA, duracion = 30),
            cita("c10", dia(20), LocalTime.of(10, 0), "p11", "Histerosonografía", Sede.LOS_OLIVOS, "Consultorio 2", EstadoCita.ATENDIDA),
            cita("c11", dia(21), LocalTime.of(15, 0), "p12", "Consulta inicial fertilidad", Sede.SAN_MIGUEL, "Consultorio 1", EstadoCita.ATENDIDA, duracion = 40),
            cita("c12", dia(23), LocalTime.of(9, 0), "p05", "Consulta de criopreservación", Sede.SAN_ISIDRO, "Consultorio 3", EstadoCita.CONFIRMADA, duracion = 30),
            cita("c13", dia(23), LocalTime.of(11, 0), "p06", "Entrega de resultados", Sede.SAN_ISIDRO, "Consultorio 3", EstadoCita.CONFIRMADA),
            cita("c14", dia(24), LocalTime.of(10, 30), "p11", "Control ecográfico", Sede.LOS_OLIVOS, "Consultorio 2", EstadoCita.CONFIRMADA)
        )
    }

    private fun crearResultados(): List<ResultadoExamen> {
        val grupoHoy = "Hoy · ${fechaLarga(dia(22))}"
        val detalleHoy = "Control Día 8 FIV · Protocolo antagonista"
        val grupoBasal = fechaLarga(dia(19))
        val detalleBasal = "Basal / Día 5 del ciclo estimulado"
        val tomaHoy = dia(22).atTime(7, 0)
        val tomaBasal = dia(19).atTime(8, 30)
        return listOf(
            ResultadoExamen("r1", "p01", TipoResultado.LABORATORIO, grupoHoy, detalleHoy, "ANALÍTICA HORMONAL",
                "Estradiol (E2) en suero", tomaHoy, "1,850", "pg/mL", "Óptimo para 9 folículos",
                "200 - 3,000 pg/mL (estimulación)", EstadoResultado.EN_RANGO),
            ResultadoExamen("r2", "p01", TipoResultado.LABORATORIO, grupoHoy, detalleHoy, "ANALÍTICA HORMONAL",
                "Progesterona (P4)", tomaHoy, "1.65", "ng/mL", "Elevado", "< 1.00 ng/mL", EstadoResultado.FUERA_DE_RANGO,
                notaClinica = "Ligeramente elevado previo a trigger. Se sugiere evaluar congelación total (freeze-all) para evitar asincronía endometrial."),
            ResultadoExamen("r3", "p01", TipoResultado.LABORATORIO, grupoHoy, detalleHoy, "MARCADOR DE PICO",
                "Hormona luteinizante (LH)", tomaHoy, null, "mUI/mL", null,
                "< 5.0 mUI/mL (sin pico prematuro)", EstadoResultado.PENDIENTE, disponibleAprox = "12:00 m."),
            ResultadoExamen("r4", "p01", TipoResultado.LABORATORIO, grupoBasal, detalleBasal, "RESERVA OVÁRICA",
                "Hormona antimülleriana (AMH)", tomaBasal, "2.80", "ng/mL", "Buena reserva ovárica",
                "1.20 - 3.50 ng/mL", EstadoResultado.EN_RANGO),
            ResultadoExamen("r5", "p01", TipoResultado.LABORATORIO, grupoBasal, detalleBasal, "PERFIL TIROIDEO",
                "TSH (tirotropina)", tomaBasal, "1.95", "µUI/mL", "Óptimo para fertilidad",
                "0.40 - 2.50 µUI/mL (objetivo preconcepción)", EstadoResultado.EN_RANGO),
            ResultadoExamen("r6", "p01", TipoResultado.GENETICA, fechaLarga(dia(10)), "Estudio pre-FIV", "CITOGENÉTICA",
                "Cariotipo en sangre periférica", dia(10).atTime(9, 0), "46,XX", "", "Fórmula cromosómica normal",
                "46,XX / 46,XY", EstadoResultado.EN_RANGO),
            ResultadoExamen("r7", "p01", TipoResultado.GENETICA, fechaLarga(dia(10)), "Estudio pre-FIV", "PORTADORES",
                "Panel expandido de portadores", dia(10).atTime(9, 0), null, "", null,
                "Sin variantes patogénicas", EstadoResultado.PENDIENTE, disponibleAprox = "02/10/2026")
        )
    }

    private fun fechaLarga(fecha: LocalDate): String = "${fecha.dayOfMonth} de setiembre de ${fecha.year}"

    private companion object {
        const val CMP_DEMO = "45782"
        const val CONTRASENA_DEMO = "concebir123"
        const val LATENCIA_MS = 350L
        const val DESPLAZAMIENTO_DIAS = 2
    }
}
