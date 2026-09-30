package com.example.medicitas.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val ruta: String,
    val titulo: String,
    val icono: ImageVector,
    val iconoSeleccionado: ImageVector
) {
    object Inicio : BottomNavItem(RutasNav.INICIO, "Inicio", Icons.Outlined.Home, Icons.Filled.Home)
    object Agenda : BottomNavItem(RutasNav.AGENDA, "Agenda", Icons.Outlined.CalendarToday, Icons.Filled.CalendarToday)
    object Pacientes : BottomNavItem(RutasNav.PACIENTES, "Pacientes", Icons.Outlined.Groups, Icons.Filled.Groups)
    object Perfil : BottomNavItem(RutasNav.PERFIL, "Perfil", Icons.Outlined.Person, Icons.Filled.Person)

    companion object {
        val opciones: List<BottomNavItem> by lazy { listOf(Inicio, Agenda, Pacientes, Perfil) }
    }
}
