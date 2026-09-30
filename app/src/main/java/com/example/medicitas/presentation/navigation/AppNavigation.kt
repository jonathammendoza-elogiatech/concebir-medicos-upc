package com.example.medicitas.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.medicitas.presentation.common.PantallaPendiente

@Composable
fun AppNavigation(navController: NavHostController) {
    val argCita = listOf(navArgument(RutasNav.ARG_CITA_ID) { type = NavType.StringType })
    val argPaciente = listOf(navArgument(RutasNav.ARG_PACIENTE_ID) { type = NavType.StringType })

    NavHost(navController = navController, startDestination = RutasNav.LOGIN) {
        composable(RutasNav.LOGIN) { PantallaPendiente("Inicio de sesión") }
        composable(BottomNavItem.Inicio.ruta) { PantallaPendiente("Resumen del día") }
        composable(BottomNavItem.Agenda.ruta) { PantallaPendiente("Agenda") }
        composable(BottomNavItem.Pacientes.ruta) { PantallaPendiente("Pacientes") }
        composable(BottomNavItem.Perfil.ruta) { PantallaPendiente("Perfil") }
        composable(RutasNav.DETALLE, arguments = argCita) { PantallaPendiente("Detalle de la cita") }
        composable(RutasNav.REGISTRAR, arguments = argCita) { PantallaPendiente("Registrar atención") }
        composable(RutasNav.FICHA, arguments = argPaciente) { PantallaPendiente("Ficha del paciente") }
        composable(RutasNav.RESULTADOS, arguments = argPaciente) { PantallaPendiente("Resultados") }
    }
}
