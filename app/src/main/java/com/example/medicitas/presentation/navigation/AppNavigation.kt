package com.example.medicitas.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.medicitas.presentation.common.PantallaPendiente
import com.example.medicitas.presentation.screens.agenda.AgendaScreen
import com.example.medicitas.presentation.screens.detalle.DetalleCitaScreen
import com.example.medicitas.presentation.screens.inicio.InicioScreen
import com.example.medicitas.presentation.screens.login.LoginScreen
import com.example.medicitas.presentation.screens.perfil.PerfilScreen

@Composable
fun AppNavigation(navController: NavHostController) {
    val argCita = listOf(navArgument(RutasNav.ARG_CITA_ID) { type = NavType.StringType })
    val argPaciente = listOf(navArgument(RutasNav.ARG_PACIENTE_ID) { type = NavType.StringType })

    NavHost(navController = navController, startDestination = RutasNav.LOGIN) {
        composable(RutasNav.LOGIN) {
            LoginScreen(onLoginExitoso = {
                navController.navigate(BottomNavItem.Inicio.ruta) {
                    popUpTo(RutasNav.LOGIN) { inclusive = true }
                }
            })
        }
        composable(BottomNavItem.Inicio.ruta) {
            InicioScreen(
                onVerCita = { navController.navigate(RutasNav.detalle(it)) },
                onVerAgenda = { navController.navegarATab(BottomNavItem.Agenda) }
            )
        }
        composable(BottomNavItem.Agenda.ruta) {
            AgendaScreen(onVerCita = { navController.navigate(RutasNav.detalle(it)) })
        }
        composable(BottomNavItem.Pacientes.ruta) { PantallaPendiente("Pacientes") }
        composable(BottomNavItem.Perfil.ruta) {
            PerfilScreen(onSesionCerrada = {
                navController.navigate(RutasNav.LOGIN) {
                    popUpTo(navController.graph.id) { inclusive = true }
                }
            })
        }
        composable(RutasNav.DETALLE, arguments = argCita) {
            DetalleCitaScreen(
                onBack = { navController.popBackStack() },
                onVerFicha = { navController.navigate(RutasNav.ficha(it)) },
                onVerResultados = { navController.navigate(RutasNav.resultados(it)) },
                onRegistrarAtencion = { navController.navigate(RutasNav.registrar(it)) }
            )
        }
        composable(RutasNav.REGISTRAR, arguments = argCita) { PantallaPendiente("Registrar atención") }
        composable(RutasNav.FICHA, arguments = argPaciente) { PantallaPendiente("Ficha del paciente") }
        composable(RutasNav.RESULTADOS, arguments = argPaciente) { PantallaPendiente("Resultados") }
    }
}

// Misma navegación que la barra inferior, para accesos directos entre pestañas
fun NavHostController.navegarATab(item: BottomNavItem) {
    navigate(item.ruta) {
        popUpTo(graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}
