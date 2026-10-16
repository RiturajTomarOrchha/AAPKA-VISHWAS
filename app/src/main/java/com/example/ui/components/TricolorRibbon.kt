package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.RathoreGreen
import com.example.ui.theme.RathoreOrange

@Composable
fun TricolorRibbon(
    modifier: Modifier = Modifier,
    height: Dp = 4.dp
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(RathoreOrange)
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(Color.White)
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(RathoreGreen)
        )
    }
}

@Composable
fun TricolorWaveHeader(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(6.dp)
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        RathoreOrange,
                        RathoreOrange,
                        Color.White,
                        RathoreGreen,
                        RathoreGreen
                    )
                )
            )
    )
}
