package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = RathoreGreen,
    onPrimary = Color.White,
    primaryContainer = RathoreGreenContainer,
    onPrimaryContainer = RathoreGreenDark,
    secondary = RathoreOrange,
    onSecondary = Color.White,
    secondaryContainer = RathoreOrangeContainer,
    onSecondaryContainer = RathoreOrangeDark,
    tertiary = RathoreGreenDark,
    onTertiary = Color.White,
    background = RathoreBackground,
    onBackground = TextPrimary,
    surface = RathoreSurface,
    onSurface = TextPrimary,
    surfaceVariant = RathoreSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = BorderStroke,
    outlineVariant = Color(0xFFCAC4D0),
    error = StatusError,
    onError = Color.White
)

@Composable
fun TeamRathoreTheme(
    darkTheme: Boolean = false, // Forced clean light mode as specified in prompt: "Light background, Clean & modern"
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}
