package com.example.medicitas.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.medicitas.presentation.screens.agenda.AgendaScreen
import com.example.medicitas.presentation.screens.detalle.DetalleCitaScreen
import com.example.medicitas.presentation.screens.ficha.FichaPacienteScreen
import com.example.medicitas.presentation.screens.inicio.InicioScreen
import com.example.medicitas.presentation.screens.login.LoginScreen
import com.example.medicitas.presentation.screens.pacientes.PacientesScreen
import com.example.medicitas.presentation.screens.perfil.PerfilScreen
import com.example.medicitas.presentation.screens.registrar.RegistrarAtencionScreen
import com.example.medicitas.presentation.screens.resultados.ResultadosScreen

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
        composable(BottomNavItem.Pacientes.ruta) {
            PacientesScreen(onVerPaciente = { navController.navigate(RutasNav.ficha(it)) })
        }
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
        composable(RutasNav.REGISTRAR, arguments = argCita) {
            RegistrarAtencionScreen(
                onBack = { navController.popBackStack() },
                onRegistrada = { navController.popBackStack() }
            )
        }
        composable(RutasNav.FICHA, arguments = argPaciente) {
            FichaPacienteScreen(
                onBack = { navController.popBackStack() },
                onVerResultados = { navController.navigate(RutasNav.resultados(it)) }
            )
        }
        composable(RutasNav.RESULTADOS, arguments = argPaciente) {
            ResultadosScreen(onBack = { navController.popBackStack() })
        }
    }
}

// Navegación entre pestañas. El ancla es Inicio y no el start destination (login),
// porque login se retira del back stack al iniciar sesión.
fun NavHostController.navegarATab(item: BottomNavItem) {
    navigate(item.ruta) {
        popUpTo(BottomNavItem.Inicio.ruta) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}
