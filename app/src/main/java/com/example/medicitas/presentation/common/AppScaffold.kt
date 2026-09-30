package com.example.medicitas.presentation.common

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.medicitas.presentation.navigation.BottomNavItem

// Cada pantalla dibuja su propia barra superior; aquí solo se decide si va la barra inferior
@Composable
fun AppScaffold(navController: NavHostController, content: @Composable (PaddingValues) -> Unit) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val rutaActual = navBackStackEntry?.destination?.route
    val mostrarBottomBar = BottomNavItem.opciones.any { it.ruta == rutaActual }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0),
        bottomBar = { if (mostrarBottomBar) AppBottomBar(navController) }
    ) { paddingValues ->
        content(paddingValues)
    }
}
