package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.RathoreGreenDark
import com.example.ui.theme.RathoreOrangeDark
import com.example.ui.theme.TextSecondary

/**
 * Official Team Rathore Congress Emblem:
 * Uses the official Indian National Congress tricolor circular hand emblem (भारतीय राष्ट्रीय कांग्रेस - हाथ / पंजा)
 * provided by the user with bold "टीम राठौर" and authentic party branding.
 */
@Composable
fun TeamRathoreEmblem(
    modifier: Modifier = Modifier,
    size: Dp = 100.dp,
    showSubtext: Boolean = true
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Surface(
            modifier = Modifier
                .size(size)
                .shadow(6.dp, CircleShape),
            shape = CircleShape,
            color = Color.White
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_congress_logo),
                contentDescription = "भारतीय राष्ट्रीय कांग्रेस - टीम राठौर",
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape),
                contentScale = ContentScale.Fit
            )
        }

        if (showSubtext) {
            Spacer(modifier = Modifier.height(12.dp))
            // Big Bold "टीम राठौर" as explicitly requested by user
            Text(
                text = "टीम राठौर",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Black,
                    fontSize = 24.sp,
                    letterSpacing = 0.5.sp
                ),
                color = RathoreGreenDark
            )
            Spacer(modifier = Modifier.height(3.dp))
            // Prominent "भारतीय राष्ट्रीय कांग्रेस" (without any constituency restriction)
            Text(
                text = "भारतीय राष्ट्रीय कांग्रेस",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    letterSpacing = 0.3.sp
                ),
                color = RathoreOrangeDark
            )
            Spacer(modifier = Modifier.height(4.dp))
            // Tagline: साथ • सेवा • समर्पण • संगठन
            Text(
                text = "साथ • सेवा • समर्पण • संगठन",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp,
                    letterSpacing = 0.5.sp
                ),
                color = TextSecondary
            )
        }
    }
}

/**
 * Compact circular logo icon for AppHeader and compact bars
 */
@Composable
fun TeamRathoreLogoIcon(
    size: Dp = 40.dp,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .size(size)
            .shadow(2.dp, CircleShape),
        shape = CircleShape,
        color = Color.White
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_congress_logo),
            contentDescription = "टीम राठौर लोगो",
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape),
            contentScale = ContentScale.Fit
        )
    }
}

