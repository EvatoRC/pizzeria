package com.example.pizzeriaapp.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp

private val PizzeriaColorScheme = lightColorScheme(
    primary = PrimaryContainer,           // el diseño usa #CD212A en los botones principales
    onPrimary = OnPrimary,
    primaryContainer = PrimaryFixed,
    onPrimaryContainer = Primary,
    secondary = Secondary,
    onSecondary = OnSecondary,
    secondaryContainer = SecondaryContainer,
    onSecondaryContainer = OnSecondaryContainer,
    tertiary = Tertiary,
    onTertiary = OnTertiary,
    tertiaryContainer = TertiaryFixed,
    onTertiaryContainer = Tertiary,
    background = Background,
    onBackground = OnBackground,
    surface = Surface,
    onSurface = OnSurface,
    surfaceVariant = SurfaceVariant,
    onSurfaceVariant = OnSurfaceVariant,
    surfaceContainerLowest = SurfaceContainerLowest,
    surfaceContainerLow = SurfaceContainerLow,
    surfaceContainer = SurfaceContainer,
    surfaceContainerHigh = SurfaceContainerHigh,
    surfaceContainerHighest = SurfaceContainerHighest,
    inverseSurface = InverseSurface,
    inverseOnSurface = InverseOnSurface,
    outline = Outline,
    outlineVariant = OutlineVariant,
    error = ErrorColor,
    onError = OnError,
    errorContainer = ErrorContainer,
    onErrorContainer = OnErrorContainer
)

private val PizzeriaShapes = Shapes(
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(24.dp)
)

/**
 * Tema de la app según el sistema de diseño "Artisanal Trattoria".
 * Se quitó el color dinámico de la plantilla (Android 12+): reemplazaba la paleta del
 * diseño por los colores del fondo de pantalla del teléfono. El diseño solo define modo claro.
 */
@Composable
fun PizzeriaAppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = PizzeriaColorScheme,
        typography = Typography,
        shapes = PizzeriaShapes,
        content = content
    )
}
