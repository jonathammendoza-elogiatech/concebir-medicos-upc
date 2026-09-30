package com.example.medicitas.presentation.common

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.medicitas.presentation.navigation.BottomNavItem
import com.example.medicitas.ui.theme.PetroleoClaro
import com.example.medicitas.ui.theme.TextoSecundario

@Composable
fun AppBottomBar(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val rutaActual = navBackStackEntry?.destination?.route

    Column {
        HorizontalDivider(color = MaterialTheme.colorScheme.outline)
        NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
            BottomNavItem.opciones.forEach { item ->
                val seleccionado = rutaActual == item.ruta
                NavigationBarItem(
                    icon = { Icon(if (seleccionado) item.iconoSeleccionado else item.icono, contentDescription = null) },
                    label = { Text(item.titulo, style = MaterialTheme.typography.labelMedium) },
                    selected = seleccionado,
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = PetroleoClaro,
                        unselectedIconColor = TextoSecundario,
                        unselectedTextColor = TextoSecundario
                    ),
                    onClick = {
                        navController.navigate(item.ruta) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    }
}
