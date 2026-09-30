package com.example.medicitas.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val LightColorScheme = lightColorScheme(
    primary = Petroleo,
    onPrimary = Color.White,
    primaryContainer = PetroleoClaro,
    onPrimaryContainer = PetroleoOscuro,
    secondary = Turquesa,
    onSecondary = Color.White,
    secondaryContainer = TurquesaClaro,
    onSecondaryContainer = Color(0xFF00504C),
    tertiary = EstadoAtendida,
    onTertiary = Color.White,
    tertiaryContainer = EstadoAtendidaFondo,
    onTertiaryContainer = EstadoAtendida,
    error = EstadoAlerta,
    onError = Color.White,
    errorContainer = EstadoAlertaFondo,
    onErrorContainer = EstadoAlerta,
    background = Fondo,
    onBackground = TextoPrincipal,
    surface = Superficie,
    onSurface = TextoPrincipal,
    surfaceVariant = SuperficieVariante,
    onSurfaceVariant = TextoSecundario,
    surfaceContainerLowest = Superficie,
    surfaceContainerLow = Superficie,
    surfaceContainer = Superficie,
    surfaceContainerHigh = Superficie,
    surfaceContainerHighest = SuperficieVariante,
    outline = Borde,
    outlineVariant = Borde
)

private val MedicitasShapes = Shapes(
    extraSmall = RoundedCornerShape(6.dp),
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

// Tema white-label: sin color dinámico para respetar la marca de la entidad
@Composable
fun MedicitasTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        shapes = MedicitasShapes,
        content = content
    )
}
