package com.example.medicitas.presentation.common

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.medicitas.presentation.event.UiEvent
import com.example.medicitas.presentation.event.UiEventBus
import com.example.medicitas.presentation.navigation.BottomNavItem
import kotlinx.coroutines.flow.collectLatest

// Cada pantalla dibuja su propia barra superior; aquí se decide la barra inferior y se muestran los mensajes globales
@Composable
fun AppScaffold(navController: NavHostController, eventBus: UiEventBus, content: @Composable (PaddingValues) -> Unit) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val rutaActual = navBackStackEntry?.destination?.route
    val mostrarBottomBar = BottomNavItem.opciones.any { it.ruta == rutaActual }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        eventBus.events.collectLatest { event ->
            when (event) {
                is UiEvent.ShowSnackbar -> snackbarHostState.showSnackbar(TypedSnackbarVisuals(event.mensaje, event.tipo))
            }
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0),
        snackbarHost = {
            CustomSnackbarHost(
                hostState = snackbarHostState,
                modifier = if (mostrarBottomBar) Modifier else Modifier.navigationBarsPadding()
            )
        },
        bottomBar = { if (mostrarBottomBar) AppBottomBar(navController) }
    ) { paddingValues ->
        content(paddingValues)
    }
}
