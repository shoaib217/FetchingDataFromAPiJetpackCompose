package com.example.jetpackcomposepractise.ui.theme // Replace with your actual package name

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

private val DarkMustardColorScheme = darkColorScheme(
    primary = DarkMustardYellowPrimary,
    onPrimary = DarkMustardYellowOnPrimary,
    primaryContainer = DarkMustardYellowPrimaryContainer,
    onPrimaryContainer = DarkMustardYellowOnPrimaryContainer,
    secondary = DarkMustardYellowSecondary,
    onSecondary = DarkMustardYellowOnSecondary,
    secondaryContainer = DarkMustardYellowSecondaryContainer,
    onSecondaryContainer = DarkMustardYellowOnSecondaryContainer,
    tertiary = DarkMustardYellowTertiary,
    onTertiary = DarkMustardYellowOnTertiary,
    tertiaryContainer = DarkMustardYellowTertiaryContainer,
    onTertiaryContainer = DarkMustardYellowOnTertiaryContainer,
    error = DarkMustardYellowError,
    errorContainer = DarkMustardYellowErrorContainer,
    onError = DarkMustardYellowOnError,
    onErrorContainer = DarkMustardYellowOnErrorContainer,
    background = DarkMustardYellowBackground,
    onBackground = DarkMustardYellowOnBackground,
    surface = DarkMustardYellowSurface,
    onSurface = DarkMustardYellowOnSurface,
    surfaceVariant = DarkMustardYellowSurfaceVariant,
    onSurfaceVariant = DarkMustardYellowOnSurfaceVariant,
    outline = DarkMustardYellowOutline
)

private val LightMustardColorScheme = lightColorScheme(
    primary = MustardYellowPrimary,
    onPrimary = MustardYellowOnPrimary,
    primaryContainer = MustardYellowPrimaryContainer,
    onPrimaryContainer = MustardYellowOnPrimaryContainer,
    secondary = MustardYellowSecondary,
    onSecondary = MustardYellowOnSecondary,
    secondaryContainer = MustardYellowSecondaryContainer,
    onSecondaryContainer = MustardYellowOnSecondaryContainer,
    tertiary = MustardYellowTertiary,
    onTertiary = MustardYellowOnTertiary,
    tertiaryContainer = MustardYellowTertiaryContainer,
    onTertiaryContainer = MustardYellowOnTertiaryContainer,
    error = MustardYellowError,
    errorContainer = MustardYellowErrorContainer,
    onError = MustardYellowOnError,
    onErrorContainer = MustardYellowOnErrorContainer,
    background = MustardYellowBackground,
    onBackground = MustardYellowOnBackground,
    surface = MustardYellowSurface,
    onSurface = MustardYellowOnSurface,
    surfaceVariant = MustardYellowSurfaceVariant,
    onSurfaceVariant = MustardYellowOnSurfaceVariant,
    outline = MustardYellowOutline
)

@Composable
fun JetpackComposePractiseTheme(
    // Or your app's theme name
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    // Set to false if you want to strictly use your mustard theme
    // and not colors from the user's wallpaper.
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkMustardColorScheme
        else -> LightMustardColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window

            // Set status bar and navigation bar to be transparent to allow drawing behind them
            // This is often desired for edge-to-edge UIs
            window.statusBarColor = Color.Transparent.toArgb() // Make status bar transparent
            window.navigationBarColor = Color.Transparent.toArgb() // Make navigation bar transparent

            // Ensure content draws behind system bars
            WindowCompat.setDecorFitsSystemWindows(window, false)

            val insetsController = WindowCompat.getInsetsController(window, view)

            // Set the appearance of status bar icons (light or dark)
            insetsController.isAppearanceLightStatusBars = !darkTheme
            // Set the appearance of navigation bar icons (light or dark)
            insetsController.isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography, // Make sure you have Typography defined in ui/theme/Type.kt
        content = content
    )
}
