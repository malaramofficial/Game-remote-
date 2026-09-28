package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = NeonCyan,
    onPrimary = Color.Black,
    primaryContainer = CockpitCardElevated,
    onPrimaryContainer = NeonCyan,
    secondary = NeonBlue,
    onSecondary = Color.White,
    secondaryContainer = CockpitCardBg,
    onSecondaryContainer = NeonBlue,
    tertiary = NeonAmber,
    onTertiary = Color.Black,
    background = CockpitDarkBg,
    onBackground = TextPrimary,
    surface = CockpitCardBg,
    onSurface = TextPrimary,
    surfaceVariant = CockpitCardElevated,
    onSurfaceVariant = TextSecondary,
    outline = CockpitBorder,
    error = NeonRed,
    onError = Color.White
)

@Composable
fun DriveRemoteTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
