package com.fluidgit.app.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

import androidx.compose.material3.lightColorScheme

private val DarkColorScheme = darkColorScheme(
    background = Background,
    surface = SurfaceElevated,
    primary = CyanNeon,
    secondary = HotMagenta,
    error = ErrorNeon,
    onBackground = TextPrimary,
    onSurface = TextPrimary
)

private val AmoledColorScheme = darkColorScheme(
    background = Color.Black,
    surface = Color(0xFF0F0F0F),
    primary = CyanNeon,
    secondary = HotMagenta,
    error = ErrorNeon,
    onBackground = TextPrimary,
    onSurface = TextPrimary
)

private val LightColorScheme = lightColorScheme(
    background = Slate100, // White variant
    surface = Color.White,
    primary = CyanNeon,
    secondary = HotMagenta,
    error = ErrorNeon,
    onBackground = Background, // Dark void text
    onSurface = Background
)

@Composable
fun FluidGitTheme(
    isLiquidLight: Boolean = false,
    isAmoled: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        isLiquidLight -> LightColorScheme
        isAmoled -> AmoledColorScheme
        else -> DarkColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color.Transparent.toArgb()
            window.navigationBarColor = Color.Transparent.toArgb()
            WindowCompat.setDecorFitsSystemWindows(window, false)
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = false
                isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
