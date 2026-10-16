package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Language
import com.example.model.UserRole
import com.example.ui.theme.BorderStroke
import com.example.ui.theme.RathoreGreen
import com.example.ui.theme.RathoreOrange
import com.example.ui.theme.TextTertiary
import com.example.util.AppStrings
import com.example.viewmodel.MainTab

@Composable
fun AppBottomNav(
    selectedTab: MainTab,
    userRole: UserRole,
    language: Language,
    onTabSelected: (MainTab) -> Unit,
    onCreateClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding(),
        contentAlignment = Alignment.BottomCenter
    ) {
        // Upward-facing shadow (elevation casting above the bottom nav bar)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(12.dp)
                .align(Alignment.BottomCenter)
                .offset(y = (-66).dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color(0x08000000),
                            Color(0x1F000000)
                        )
                    )
                )
        )

        // Bottom White Navigation Bar Surface
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(66.dp),
            color = Color.White,
            shadowElevation = 10.dp,
            border = androidx.compose.foundation.BorderStroke(0.8.dp, BorderStroke)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 1. Home Tab
                NavIconButton(
                    title = AppStrings.get("nav_home", language),
                    icon = Icons.Default.Home,
                    isSelected = selectedTab == MainTab.HOME,
                    onClick = { onTabSelected(MainTab.HOME) },
                    tag = "tab_home"
                )

                // 2. Videos Tab
                NavIconButton(
                    title = AppStrings.get("nav_videos", language),
                    icon = Icons.Default.SmartDisplay,
                    isSelected = selectedTab == MainTab.VIDEOS,
                    onClick = { onTabSelected(MainTab.VIDEOS) },
                    tag = "tab_videos"
                )

                // Reserved space for the protruding Center Create Button (Admin only)
                if (userRole == UserRole.ADMIN) {
                    Spacer(modifier = Modifier.width(68.dp))
                }

                // 4. Role-based 4th Tab
                when (userRole) {
                    UserRole.WORKER, UserRole.OFFICE_BEARER -> {
                        NavIconButton(
                            title = AppStrings.get("nav_war_room", language),
                            icon = Icons.Default.Shield,
                            isSelected = selectedTab == MainTab.WAR_ROOM,
                            onClick = { onTabSelected(MainTab.WAR_ROOM) },
                            tag = "tab_war_room"
                        )
                    }
                    UserRole.SUPPORTER -> {
                        // Directly opens Suggestion / Direct Connect Box
                        NavIconButton(
                            title = if (language == Language.HINDI) "सीधा संवाद" else "Connect",
                            icon = Icons.Default.VolunteerActivism,
                            isSelected = selectedTab == MainTab.DIRECT_CONNECT,
                            onClick = { onTabSelected(MainTab.DIRECT_CONNECT) },
                            tag = "tab_connect"
                        )
                    }
                    UserRole.ADMIN -> {
                        NavIconButton(
                            title = if (language == Language.HINDI) "वॉर रूम" else "War Room",
                            icon = Icons.Default.Shield,
                            isSelected = selectedTab == MainTab.WAR_ROOM,
                            onClick = { onTabSelected(MainTab.WAR_ROOM) },
                            tag = "tab_admin_war_room"
                        )
                    }
                }

                // 5. Profile Tab
                NavIconButton(
                    title = AppStrings.get("nav_profile", language),
                    icon = Icons.Default.Person,
                    isSelected = selectedTab == MainTab.PROFILE,
                    onClick = { onTabSelected(MainTab.PROFILE) },
                    tag = "tab_profile"
                )
            }
        }

        // Center Prominent ✦ ADMIN CONTROL PANEL Button - Protruding clearly above the white bar (Admin only)
        if (userRole == UserRole.ADMIN) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = (-20).dp)
                    .size(66.dp)
                    .shadow(10.dp, CircleShape)
                    .clip(CircleShape)
                    .background(RathoreOrange)
                    .border(3.5.dp, Color.White, CircleShape)
                    .clickable { onCreateClick() }
                    .testTag("tab_create_central"),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = "Control Panel",
                        tint = Color.White,
                        modifier = Modifier.size(26.dp)
                    )
                    Text(
                        text = if (language == Language.HINDI) "कंट्रोल पैनल" else "Control",
                        color = Color.White,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun NavIconButton(
    title: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    tag: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .testTag(tag)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = if (isSelected) RathoreGreen else TextTertiary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 10.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            ),
            color = if (isSelected) RathoreGreen else TextTertiary
        )
    }
}
