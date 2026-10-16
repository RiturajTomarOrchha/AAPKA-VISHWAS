package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * CongressAtmosphereBackground:
 * Adds a subtle, dignified Congress Tricolor atmospheric gradient
 * (soft saffron glow at top, pristine radiant center, and fresh emerald green anchor at bottom)
 * perfect for Splash, Login, Registration, and Home screens without overwhelming readability.
 */
@Composable
fun CongressAtmosphereBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val congressGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0x18FF9933), // Soft Saffron / केसरिया glow at top
            Color(0x06FFF3E8),
            Color(0xFFFAFAF7), // Clean warm white reading canvas
            Color(0xFFF7FAF8),
            Color(0x120D5C3A)  // Subtle Forest / Emerald Congress Green at bottom
        )
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(congressGradient)
    ) {
        content()
    }
}
