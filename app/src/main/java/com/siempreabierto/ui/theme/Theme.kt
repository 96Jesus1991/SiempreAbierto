package com.siempreabierto.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Esquema de colores oscuro (por defecto - mejor para carretera)
private val DarkColorScheme = darkColorScheme(
    primary = Primary,
    onPrimary = TextPrimary,
    primaryContainer = PrimaryDark,
    onPrimaryContainer = TextPrimary,
    
    secondary = Accent,
    onSecondary = TextPrimary,
    secondaryContainer = AccentDark,
    onSecondaryContainer = TextPrimary,
    
    tertiary = CatWorkshop,
    onTertiary = TextPrimary,
    
    background = BackgroundDark,
    onBackground = TextPrimary,
    
    surface = SurfaceDark,
    onSurface = TextPrimary,
    surfaceVariant = CardDark,
    onSurfaceVariant = TextSecondary,
    
    error = Error,
    onError = TextPrimary,
    errorContainer = EmergencyRedDark,
    onErrorContainer = TextPrimary
)

// Esquema de colores claro (modo día)
private val LightColorScheme = lightColorScheme(
    primary = Primary,
    onPrimary = TextPrimary,
    primaryContainer = PrimaryLight,
    onPrimaryContainer = TextDark,
    
    secondary = Accent,
    onSecondary = TextPrimary,
    secondaryContainer = AccentLight,
    onSecondaryContainer = TextDark,
    
    tertiary = CatWorkshop,
    onTertiary = TextPrimary,
    
    background = BackgroundLight,
    onBackground = TextDark,
    
    surface = SurfaceLight,
    onSurface = TextDark,
    surfaceVariant = CardLight,
    onSurfaceVariant = TextDarkSecondary,
    
    error = Error,
    onError = TextPrimary,
    errorContainer = EmergencyRed,
    onErrorContainer = TextPrimary
)

@Composable
fun SiempreAbiertoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
