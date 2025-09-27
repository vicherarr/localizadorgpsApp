package com.vicherarr.locgps.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorPalette = darkColorScheme(
    primary = AccentOrange,
    onPrimary = DarkBackground,
    secondary = PrimaryBlue,
    onSecondary = SoftBackground,
    background = DarkBackground,
    onBackground = SoftBackground,
    surface = DarkBackground,
    onSurface = SoftBackground,
    tertiary = AccentTeal
)

private val LightColorPalette = lightColorScheme(
    primary = PrimaryBlue,
    onPrimary = Color.White,
    secondary = AccentTeal,
    onSecondary = Color.White,
    background = SoftBackground,
    onBackground = Color(0xFF1B1B1F),
    surface = Color.White,
    onSurface = Color(0xFF1B1B1F),
    tertiary = AccentOrange
)

@Composable
fun LocalizadorGpsTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorPalette
        else -> LightColorPalette
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
