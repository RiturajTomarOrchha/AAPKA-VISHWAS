package com.example.ui.screens

import android.content.Intent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.*
import com.example.ui.components.TeamRathoreLogoIcon
import com.example.ui.theme.*
import com.example.util.AppStrings
import com.example.viewmodel.AppScreen
import com.example.viewmodel.AppViewModel
import com.example.viewmodel.MainTab

@Composable
fun ProfileScreen(
    viewModel: AppViewModel,
    language: Language,
    profile: UserProfile,
    activity: UserActivitySummary,
    referral: ReferralSummary
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showRoleDialog by remember { mutableStateOf(false) }
    var showRoleMatrixDialog by remember { mutableStateOf(false) }
    var showLogoutConfirmDialog by remember { mutableStateOf(false) }
    var showDeactivateAccountDialog by remember { mutableStateOf(false) }
    var showDeleteAccountDialog by remember { mutableStateOf(false) }
    var showHelpSupportDialog by remember { mutableStateOf(false) }
    var showPrivacySecurityDialog by remember { mutableStateOf(false) }
    var showAboutAppDialog by remember { mutableStateOf(false) }
    var candidateToReview by remember { mutableStateOf<PendingWorkerVerification?>(null) }
    var showEditProfileDialog by remember { mutableStateOf(false) }
    var showTestModeDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(RathoreBackground)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 🧪 Active Test Mode Top Banner (Displayed when Admin is previewing Worker/Supporter/Office Bearer mode)
        if (uiState.isTestModeActive) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("active_test_mode_banner"),
                shape = RoundedCornerShape(14.dp),
                color = Color(0xFFFEF3C7),
                border = BorderStroke(1.5.dp, Color(0xFFF59E0B)),
                shadowElevation = 3.dp
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Science,
                                contentDescription = null,
                                tint = Color(0xFFD97706),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "🧪 एडमिन टेस्ट मोड सक्रिय",
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF92400E),
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "वर्तमान इंटरफ़ेस: ${profile.role.titleHi} (${profile.designation})",
                                    fontSize = 11.5.sp,
                                    color = Color(0xFFB45309),
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFFFDE68A)
                        ) {
                            Text(
                                text = "TEST VIEW",
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF92400E),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "अन्य भूमिका में स्विच करें:",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF78350F)
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(UserRole.SUPPORTER, UserRole.WORKER, UserRole.OFFICE_BEARER).forEach { r ->
                            val isCurrent = profile.role == r
                            FilterChip(
                                selected = isCurrent,
                                onClick = { viewModel.enterTestMode(r) },
                                label = { Text(r.titleHi, fontSize = 11.sp, fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color(0xFFD97706),
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = { viewModel.exitTestMode() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(38.dp)
                            .testTag("btn_exit_test_mode"),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828))
                    ) {
                        Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("एडमिन मोड में वापस जाएं (Exit Test Mode)", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Under Review Banner if profile is pending review or unapproved
        if (profile.isPendingReview || !profile.isApproved) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFFFFBEB),
                border = BorderStroke(1.2.dp, Color(0xFFF59E0B)),
                shadowElevation = 1.dp
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.HourglassTop, contentDescription = null, tint = Color(0xFFD97706), modifier = Modifier.size(22.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("प्रोफाइल सत्यापन लंबित (Under Review)", fontWeight = FontWeight.Bold, color = Color(0xFFB45309), fontSize = 14.sp)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (profile.reviewReason.isNotBlank()) profile.reviewReason else "प्रोफाइल में किए गए बदलाव (नाम/पद/श्रेणी) केंद्रीय एडमिन द्वारा समीक्षाधीन हैं। स्वीकृति मिलने तक आप सभी पोस्ट व वीडियो देख सकते हैं।",
                        fontSize = 11.sp,
                        color = Color(0xFF78350F),
                        lineHeight = 15.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { viewModel.showProfileReviewDialog() },
                            modifier = Modifier.weight(1f).height(36.dp),
                            shape = RoundedCornerShape(6.dp),
                            border = BorderStroke(1.dp, Color(0xFFD97706)),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text("स्थिति विवरण", fontSize = 11.sp, color = Color(0xFFB45309), fontWeight = FontWeight.Bold)
                        }
                        Button(
                            onClick = { viewModel.approveProfileDirectly() },
                            modifier = Modifier.weight(1f).height(36.dp),
                            shape = RoundedCornerShape(6.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = RathoreGreen),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("त्वरित स्वीकृति (Demo)", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // 🧪 Admin Test Mode Card (Placed at the very TOP of Admin Profile, above Admin Team Name)
        if (profile.role == UserRole.ADMIN || uiState.isAdminLoggedIn || uiState.isTestModeActive) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("admin_test_mode_card"),
                shape = RoundedCornerShape(14.dp),
                color = Color(0xFFF8FAFC),
                border = BorderStroke(1.5.dp, Color(0xFF4F46E5)),
                shadowElevation = 2.dp
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFEEF2FF)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Science,
                                    contentDescription = "Test Mode",
                                    tint = Color(0xFF4F46E5),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "🧪 टेस्ट मोड (UI इंटरफ़ेस प्रिव्यू)",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Color(0xFF312E81)
                                )
                                Text(
                                    text = "भूमिका (Role) बदलकर ऐप इंटरफेस देखें",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFEEF2FF)
                        ) {
                            Text(
                                text = "एडमिन टूल",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF4F46E5),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "एडमिन के रूप में आप किसी भी श्रेणी का चयन करके देख सकते हैं कि कार्यकर्ता, समर्थक या पदाधिकारी को ऐप कैसा दिखाई देता है:",
                        fontSize = 12.sp,
                        color = Color(0xFF475569),
                        lineHeight = 17.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Supporter Mode
                        OutlinedButton(
                            onClick = { viewModel.enterTestMode(UserRole.SUPPORTER) },
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .testTag("btn_test_mode_supporter"),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.2.dp, RathoreOrange),
                            contentPadding = PaddingValues(horizontal = 4.dp)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("👤 समर्थक", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = RathoreOrange)
                                Text("मोड देखें", fontSize = 9.sp, color = RathoreOrangeDark)
                            }
                        }

                        // Worker Mode
                        OutlinedButton(
                            onClick = { viewModel.enterTestMode(UserRole.WORKER) },
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .testTag("btn_test_mode_worker"),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.2.dp, RathoreGreen),
                            contentPadding = PaddingValues(horizontal = 4.dp)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("🚩 कार्यकर्ता", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = RathoreGreen)
                                Text("मोड देखें", fontSize = 9.sp, color = Color(0xFF1B5E20))
                            }
                        }

                        // Office Bearer Mode
                        OutlinedButton(
                            onClick = { viewModel.enterTestMode(UserRole.OFFICE_BEARER) },
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .testTag("btn_test_mode_office_bearer"),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.2.dp, Color(0xFF5E35B1)),
                            contentPadding = PaddingValues(horizontal = 4.dp)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("🏛️ पदाधिकारी", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF5E35B1))
                                Text("मोड देखें", fontSize = 9.sp, color = Color(0xFF311B92))
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        // User Profile Summary Card
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            shadowElevation = 2.dp,
            border = BorderStroke(1.dp, BorderStroke)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE8F5E9))
                            .border(2.dp, RathoreGreen, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Person, contentDescription = null, tint = RathoreGreen, modifier = Modifier.size(38.dp))
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = profile.fullName,
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = TextPrimary
                        )
                        Text(
                            text = "${profile.designation} • ${profile.village}, ${profile.district}",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        // Role Tag pill with switch button
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = when (profile.role) {
                                UserRole.SUPPORTER -> Color(0xFFFFF3E0)
                                UserRole.WORKER -> Color(0xFFE8F5E9)
                                UserRole.OFFICE_BEARER -> Color(0xFFEDE7F6)
                                UserRole.ADMIN -> Color(0xFFFFEBEE)
                            },
                            modifier = Modifier.clickable {
                                if (profile.role == UserRole.ADMIN || uiState.isAdminLoggedIn || uiState.isTestModeActive) {
                                    showTestModeDialog = true
                                } else {
                                    showRoleDialog = true
                                }
                            }
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = if (language == Language.HINDI) profile.role.titleHi else profile.role.titleEn,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = when (profile.role) {
                                        UserRole.SUPPORTER -> RathoreOrange
                                        UserRole.WORKER -> RathoreGreen
                                        UserRole.OFFICE_BEARER -> Color(0xFF5E35B1)
                                        UserRole.ADMIN -> Color(0xFFC62828)
                                    }
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                if (profile.role == UserRole.ADMIN || uiState.isAdminLoggedIn || uiState.isTestModeActive) {
                                    Icon(Icons.Default.Science, contentDescription = "Test Mode", modifier = Modifier.size(13.dp), tint = Color(0xFFC62828))
                                } else {
                                    Icon(Icons.Default.SwapHoriz, contentDescription = "Switch Role", modifier = Modifier.size(12.dp), tint = TextSecondary)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Profile Edit Text Button
                OutlinedButton(
                    onClick = { showEditProfileDialog = true },
                    modifier = Modifier.fillMaxWidth().height(36.dp),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, RathoreGreen.copy(alpha = 0.5f)),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null, tint = RathoreGreen, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("प्रोफाइल संपादित करें (नाम, पद, श्रेणी, फोटो)", fontSize = 11.sp, color = RathoreGreen, fontWeight = FontWeight.SemiBold)
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Profile Completion Progress Bar (For non-admin volunteers only)
                if (profile.role != UserRole.ADMIN) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Profile पूर्णता", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                            Text("${profile.profileCompletion}%", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = RathoreGreen)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        LinearProgressIndicator(
                            progress = { profile.profileCompletion / 100f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = RathoreGreen,
                            trackColor = Color(0xFFE2E8F0)
                        )
                    }
                }
            }
        }

        // Activity Stats Card (PDF page 24) - Displayed for Workers and Office Bearers
        if (profile.role != UserRole.ADMIN) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = Color.White,
                border = BorderStroke(1.dp, BorderStroke)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = AppStrings.get("my_activity_title", language),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = RathoreGreen
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    if (profile.role == UserRole.SUPPORTER) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            StatBox(number = "${activity.graphicsReused}", label = "ग्राफिक्स शेयर", color = RathoreOrange)
                            StatBox(number = "${activity.videosReused}", label = "वीडियो शेयर", color = Color(0xFF1E88E5))
                        }
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            StatBox(number = "${activity.graphicsReused}", label = "ग्राफिक्स शेयर", color = RathoreOrange)
                            StatBox(number = "${activity.videosReused}", label = "वीडियो शेयर", color = Color(0xFF1E88E5))
                            StatBox(number = "${activity.meetingsJoined}", label = "मीटिंग्स", color = RathoreGreen)
                            StatBox(number = "${activity.tasksCompleted}", label = "टास्क पूर्ण", color = Color(0xFF8E24AA))
                        }
                    }
                }
            }
        }

        // Dedicated "मेरी संगठनात्मक जिम्मेदारियाँ" for Office Bearers (as explicitly requested)
        if (profile.role == UserRole.OFFICE_BEARER) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = Color.White,
                border = BorderStroke(1.2.dp, Color(0xFF5E35B1).copy(alpha = 0.35f)),
                shadowElevation = 2.dp
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFEDE7F6)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Assignment,
                                    contentDescription = null,
                                    tint = Color(0xFF5E35B1),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = if (language == Language.HINDI) "मेरी संगठनात्मक जिम्मेदारियाँ" else "My Assigned Responsibilities",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color(0xFF5E35B1)
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFEDE7F6)
                        ) {
                            Text(
                                text = "3 सक्रिय दायित्व",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = Color(0xFF5E35B1),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Task 1
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFFF8FAFC),
                        border = BorderStroke(1.dp, BorderStroke)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "1. बूथ सत्यापन एवं सशक्तिकरण",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    color = TextPrimary
                                )
                                Text("8/12 पूर्ण", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = RathoreGreen)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "सेक्टर-4 के सभी 12 मतदान केंद्रों पर बूथ प्रभारियों से संपर्क कर सूची सत्यापित करें।",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            LinearProgressIndicator(
                                progress = { 8f / 12f },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(4.dp)
                                    .clip(RoundedCornerShape(2.dp)),
                                color = RathoreGreen,
                                trackColor = Color(0xFFE2E8F0)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Task 2
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFFF8FAFC),
                        border = BorderStroke(1.dp, BorderStroke)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "2. युवा संवाद चौपाल सम्मेलन",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    color = TextPrimary
                                )
                                Text("समय सीमा: 18 सितं", style = MaterialTheme.typography.labelSmall, color = RathoreOrangeDark)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "मंडल स्तर पर 50 नए युवा साथियों को संगठन से जोड़ने हेतु बैठक समन्वय करें।",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedButton(
                        onClick = { viewModel.selectTab(MainTab.WAR_ROOM) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, Color(0xFF5E35B1))
                    ) {
                        Icon(Icons.Default.UploadFile, contentDescription = null, tint = Color(0xFF5E35B1), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("कार्य प्रगति रिपोर्ट व साक्ष्य अपलोड करें", color = Color(0xFF5E35B1), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }

        // Referral Card (PDF page 23) - For volunteer mobilization only
        if (profile.role != UserRole.ADMIN) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = Color(0xFFFFF7ED),
                border = BorderStroke(1.dp, Color(0xFFFFD8A8))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = AppStrings.get("referral_title", language),
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = RathoreOrangeDark
                            )
                            Text(
                                text = "आपकी टीम में ${referral.totalReferrals} साथी जुड़ चुके हैं",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                        }
                        Surface(shape = RoundedCornerShape(8.dp), color = RathoreOrange) {
                            Text(
                                text = referral.referralCode,
                                color = Color.White,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = {
                            val shareIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, "टीम राठौर ऐप से जुड़ें और अपने क्षेत्र के विकास में सहभागी बनें!\nरेफरल कोड: ${referral.referralCode}\nडाउनलोड करें: https://teamrathore.in")
                                type = "text/plain"
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "Share Referral"))
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = RathoreOrange)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("व्हाट्सएप पर साथियों को जोड़ें", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Dedicated Admin Summary Statistics & Verification Queue for Admin
        if (profile.role == UserRole.ADMIN) {
            val adminScheduledMeetings = (if (uiState.meetingInfo.status == MeetingStatus.UPCOMING) 1 else 0) + uiState.programs.size
            val adminPendingTasks = uiState.tasks.count { it.status != TaskStatus.COMPLETED }

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("admin_profile_actionable_metrics"),
                shape = RoundedCornerShape(14.dp),
                color = Color.White,
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                shadowElevation = 2.dp
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(RathoreGreen.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Analytics, contentDescription = null, tint = RathoreGreen, modifier = Modifier.size(16.dp))
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "एडमिन डैशबोर्ड मेट्रिक्स",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = TextPrimary
                            )
                        }

                        TextButton(
                            onClick = { viewModel.navigateTo(AppScreen.AdminPanel) },
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text("कंट्रोल पैनल >", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = RathoreGreen)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Metric 1: Graphics
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { viewModel.navigateTo(AppScreen.AdminPanel) },
                            shape = RoundedCornerShape(10.dp),
                            color = RathoreGreen.copy(alpha = 0.08f),
                            border = BorderStroke(1.dp, RathoreGreen.copy(alpha = 0.3f))
                        ) {
                            Column(
                                modifier = Modifier.padding(10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("${uiState.feedList.size}", fontWeight = FontWeight.Black, fontSize = 18.sp, color = RathoreGreen)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text("अपलोड ग्राफिक्स", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextPrimary, textAlign = TextAlign.Center)
                            }
                        }

                        // Metric 2: Videos
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { viewModel.navigateTo(AppScreen.AdminPanel) },
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFF9333EA).copy(alpha = 0.08f),
                            border = BorderStroke(1.dp, Color(0xFF9333EA).copy(alpha = 0.3f))
                        ) {
                            Column(
                                modifier = Modifier.padding(10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("${uiState.videoList.size}", fontWeight = FontWeight.Black, fontSize = 18.sp, color = Color(0xFF9333EA))
                                Spacer(modifier = Modifier.height(2.dp))
                                Text("प्रोसेस वीडियो", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextPrimary, textAlign = TextAlign.Center)
                            }
                        }

                        // Metric 3: Scheduled Meetings
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    viewModel.navigateTo(AppScreen.Main)
                                    viewModel.selectTab(com.example.viewmodel.MainTab.WAR_ROOM)
                                },
                            shape = RoundedCornerShape(10.dp),
                            color = RathoreOrange.copy(alpha = 0.08f),
                            border = BorderStroke(1.dp, RathoreOrange.copy(alpha = 0.3f))
                        ) {
                            Column(
                                modifier = Modifier.padding(10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("$adminScheduledMeetings", fontWeight = FontWeight.Black, fontSize = 18.sp, color = RathoreOrangeDark)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text("शेड्यूल्ड मीटिंग्स", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextPrimary, textAlign = TextAlign.Center)
                            }
                        }

                        // Metric 4: Pending Tasks
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    viewModel.navigateTo(AppScreen.Main)
                                    viewModel.selectTab(com.example.viewmodel.MainTab.TASKS)
                                },
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFFDC2626).copy(alpha = 0.08f),
                            border = BorderStroke(1.dp, Color(0xFFDC2626).copy(alpha = 0.3f))
                        ) {
                            Column(
                                modifier = Modifier.padding(10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("$adminPendingTasks", fontWeight = FontWeight.Black, fontSize = 18.sp, color = Color(0xFFDC2626))
                                Spacer(modifier = Modifier.height(2.dp))
                                Text("लंबित टास्क", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextPrimary, textAlign = TextAlign.Center)
                            }
                        }
                    }
                }
            }

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = Color.White,
                border = BorderStroke(1.2.dp, RathoreGreen.copy(alpha = 0.4f)),
                shadowElevation = 2.dp
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFE8F5E9)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.VerifiedUser,
                                    contentDescription = null,
                                    tint = RathoreGreen,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "कार्यकर्ता सत्यापन सूची",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = RathoreGreen
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = RathoreOrange.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "${uiState.pendingVerifications.size} लंबित आवेदन",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = RathoreOrangeDark,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "आवेदक की पूरी जानकारी व सत्यापन हेतु नाम पर क्लिक करें:",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    if (uiState.pendingVerifications.isEmpty()) {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFF8FAFC)
                        ) {
                            Text(
                                text = "✓ कोई नया सत्यापन लंबित नहीं है।",
                                style = MaterialTheme.typography.bodySmall,
                                color = RathoreGreen,
                                modifier = Modifier.padding(12.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    } else {
                        uiState.pendingVerifications.forEach { candidate ->
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clickable { candidateToReview = candidate },
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFFF8FAFC),
                                border = BorderStroke(1.dp, BorderStroke)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(36.dp)
                                                .clip(CircleShape)
                                                .background(Color(0xFFE2E8F0)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                Icons.Default.Person,
                                                contentDescription = null,
                                                tint = TextSecondary,
                                                modifier = Modifier.size(22.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(
                                                text = candidate.name,
                                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                                color = TextPrimary
                                            )
                                            Text(
                                                text = "${candidate.area} • ${candidate.roleRequested}",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = TextSecondary
                                            )
                                        }
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = Color(0xFFEFF6FF),
                                        border = BorderStroke(1.dp, Color(0xFFBFDBFE))
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "समीक्षा करें",
                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                color = Color(0xFF2563EB)
                                            )
                                            Spacer(modifier = Modifier.width(2.dp))
                                            Icon(
                                                Icons.AutoMirrored.Filled.ArrowForward,
                                                contentDescription = null,
                                                tint = Color(0xFF2563EB),
                                                modifier = Modifier.size(12.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Menu items
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            color = Color.White,
            border = BorderStroke(1.dp, BorderStroke)
        ) {
            Column {
                if (profile.role != UserRole.ADMIN) {
                    MenuItem(
                        title = AppStrings.get("vidhayak_title", language),
                        icon = Icons.Default.VolunteerActivism,
                        onClick = { viewModel.navigateTo(AppScreen.DirectConnect) }
                    )
                    Divider(color = BorderStroke, thickness = 0.6.dp)
                }

                if (profile.role == UserRole.OFFICE_BEARER) {
                    MenuItem(
                        title = AppStrings.get("nav_tasks", language),
                        icon = Icons.AutoMirrored.Filled.Assignment,
                        onClick = { viewModel.navigateTo(AppScreen.Main) }
                    )
                    Divider(color = BorderStroke, thickness = 0.6.dp)
                }

                if (profile.role == UserRole.ADMIN) {
                    MenuItem(
                        title = "Admin कंट्रोल पैनल (व्यवस्थापक)",
                        icon = Icons.Default.AdminPanelSettings,
                        onClick = { viewModel.navigateTo(AppScreen.AdminPanel) }
                    )
                    Divider(color = BorderStroke, thickness = 0.6.dp)
                }

                MenuItem(
                    title = if (language == Language.HINDI) "भूमिका अधिकार मैट्रिक्स (Role Access Matrix)" else "Role Access Matrix",
                    icon = Icons.Default.Security,
                    onClick = { showRoleMatrixDialog = true }
                )
                Divider(color = BorderStroke, thickness = 0.6.dp)

                MenuItem(
                    title = if (language == Language.HINDI) "सहायता एवं समर्थन (Help & Support)" else "Help & Support",
                    icon = Icons.Default.HelpOutline,
                    onClick = { showHelpSupportDialog = true }
                )
                Divider(color = BorderStroke, thickness = 0.6.dp)

                MenuItem(
                    title = if (language == Language.HINDI) "गोपनीयता एवं सुरक्षा (Privacy & Security)" else "Privacy & Security",
                    icon = Icons.Default.Lock,
                    onClick = { showPrivacySecurityDialog = true }
                )
                Divider(color = BorderStroke, thickness = 0.6.dp)

                MenuItem(
                    title = if (language == Language.HINDI) "टीम राठौर ऐप के बारे में (About App)" else "About Team Rathore App",
                    icon = Icons.Default.Info,
                    onClick = { showAboutAppDialog = true }
                )
                Divider(color = BorderStroke, thickness = 0.6.dp)

                if (profile.role != UserRole.ADMIN) {
                    MenuItem(
                        title = if (language == Language.HINDI) "खाता निष्क्रिय करें (Deactivate Account)" else "Deactivate Account",
                        icon = Icons.Default.PauseCircleOutline,
                        iconTint = RathoreOrangeDark,
                        onClick = { showDeactivateAccountDialog = true }
                    )
                    Divider(color = BorderStroke, thickness = 0.6.dp)

                    MenuItem(
                        title = if (language == Language.HINDI) "खाता हटाएं (Delete Account)" else "Delete Account",
                        icon = Icons.Default.DeleteForever,
                        iconTint = Color(0xFFDC2626),
                        onClick = { showDeleteAccountDialog = true }
                    )
                    Divider(color = BorderStroke, thickness = 0.6.dp)
                }

                if (profile.role == UserRole.ADMIN || uiState.isAdminLoggedIn || uiState.isTestModeActive) {
                    MenuItem(
                        title = "🧪 टेस्ट मोड (कार्यकर्ता / समर्थक / पदाधिकारी प्रिव्यू)",
                        icon = Icons.Default.Science,
                        iconTint = Color(0xFF4F46E5),
                        onClick = { showTestModeDialog = true }
                    )
                    Divider(color = BorderStroke, thickness = 0.6.dp)
                }

                MenuItem(
                    title = if (language == Language.HINDI) "भाषा बदलें (हिंदी / English)" else "Switch Language (Hindi / English)",
                    icon = Icons.Default.Translate,
                    onClick = { viewModel.toggleLanguage() }
                )
                Divider(color = BorderStroke, thickness = 0.6.dp)

                MenuItem(
                    title = "लॉगआउट (Logout)",
                    icon = Icons.Default.Logout,
                    iconTint = Color(0xFFDC2626),
                    onClick = { showLogoutConfirmDialog = true }
                )
            }
        }

        Spacer(modifier = Modifier.height(60.dp))
    }

    // Candidate Detailed Review Dialog (Admin only - clicks required, no instant approval)
    candidateToReview?.let { candidate ->
        AlertDialog(
            onDismissRequest = { candidateToReview = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = RathoreGreen)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("कार्यकर्ता सत्यापन विवरण", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(54.dp)
                                .clip(CircleShape)
                                .background(RathoreOrange.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Person, contentDescription = null, tint = RathoreOrangeDark, modifier = Modifier.size(32.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(candidate.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                            Text("आवेदित दायित्व: ${candidate.roleRequested}", color = RathoreGreen, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                        }
                    }

                    Divider(color = BorderStroke, thickness = 0.8.dp)

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("मोबाइल / व्हाट्सएप:", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                            Text(candidate.phone, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("विधानसभा / क्षेत्र:", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                            Text(candidate.area, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("मतदान केंद्र (बूथ):", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                            Text(candidate.boothNo, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("आवेदन दिनांक:", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                            Text(candidate.appliedDate, style = MaterialTheme.typography.bodySmall)
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFF1F5F9),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "पुष्टि करें कि आवेदक संगठन का सक्रिय सदस्य है और संबंधित दायित्व हेतु अधिकृत है।",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.approveWorkerVerification(candidate.id)
                        candidateToReview = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RathoreGreen)
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("स्वीकृत करें", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                Row {
                    OutlinedButton(
                        onClick = {
                            viewModel.rejectWorkerVerification(candidate.id)
                            candidateToReview = null
                        },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFDC2626)),
                        border = BorderStroke(1.dp, Color(0xFFDC2626))
                    ) {
                        Text("अस्वीकृत करें")
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    TextButton(onClick = { candidateToReview = null }) {
                        Text("बंद करें")
                    }
                }
            }
        )
    }

    // Role Switch Dialog
    if (showRoleDialog) {
        AlertDialog(
            onDismissRequest = { showRoleDialog = false },
            title = { Text("भूमिका (Role) बदलें", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    // Top 3 primary user base roles: समर्थक, कार्यकर्ता, पदाधिकारी
                    val primaryRoles = listOf(UserRole.SUPPORTER, UserRole.WORKER, UserRole.OFFICE_BEARER)
                    primaryRoles.forEach { role ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.setUserRole(role)
                                    showRoleDialog = false
                                }
                                .padding(vertical = 10.dp)
                        ) {
                            RadioButton(selected = profile.role == role, onClick = {
                                viewModel.setUserRole(role)
                                showRoleDialog = false
                            })
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(if (language == Language.HINDI) role.titleHi else role.titleEn, fontWeight = FontWeight.Bold)
                                Text(if (language == Language.HINDI) role.descHi else role.descEn, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Divider(color = BorderStroke, thickness = 0.8.dp)
                    Spacer(modifier = Modifier.height(8.dp))

                    // Admin / Administrator role at the very bottom
                    val adminRole = UserRole.ADMIN
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                viewModel.setUserRole(adminRole)
                                showRoleDialog = false
                            }
                            .padding(vertical = 10.dp)
                    ) {
                        RadioButton(selected = profile.role == adminRole, onClick = {
                            viewModel.setUserRole(adminRole)
                            showRoleDialog = false
                        })
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(if (language == Language.HINDI) "प्रशासक / व्यवस्थापक (Admin)" else "Administrator (Admin)", fontWeight = FontWeight.Bold, color = Color(0xFFC62828))
                            Text(if (language == Language.HINDI) adminRole.descHi else adminRole.descEn, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showRoleDialog = false }) { Text("बंद करें") }
            }
        )
    }

    // 1. Role Access Matrix Dialog
    if (showRoleMatrixDialog) {
        AlertDialog(
            onDismissRequest = { showRoleMatrixDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Security, contentDescription = null, tint = RathoreGreen)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("भूमिका अधिकार मैट्रिक्स", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        "प्रत्येक संगठनात्मक स्तर के लिए निर्धारित कार्य एवं अधिकार (भूमिका पर क्लिक करके स्विच करें):",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )

                    val matrixRoles = listOf(
                        Triple(UserRole.SUPPORTER, "प्रचार ग्राफिक्स व वीडियो डाउनलोड, विधायक जी को सीधी समस्या प्रेषित करना।", RathoreOrange),
                        Triple(UserRole.WORKER, "समर्थक सुविधाएं + वार रूम बैठक, बूथ फीडबैक, सुझाव प्रेषण एवं फील्ड रिपोर्ट।", RathoreGreen),
                        Triple(UserRole.OFFICE_BEARER, "कार्यकर्ता सुविधाएं + टास्क आवंटन, दायित्व रिपोर्टिंग, साक्ष्य सत्यापन, मंडल समन्वय।", Color(0xFF5E35B1)),
                        Triple(UserRole.ADMIN, "पूर्ण नियंत्रण + कंटेंट निर्माण, सूचना प्रसारण, कार्यकर्ता सत्यापन, सदस्य प्रबंधन।", Color(0xFFC62828))
                    )

                    matrixRoles.forEach { (targetRole, perms, tagColor) ->
                        val isCurrentRole = profile.role == targetRole
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isCurrentRole) tagColor.copy(alpha = 0.15f) else tagColor.copy(alpha = 0.05f),
                            border = BorderStroke(if (isCurrentRole) 2.dp else 1.dp, if (isCurrentRole) tagColor else tagColor.copy(alpha = 0.3f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.setUserRole(targetRole)
                                    showRoleMatrixDialog = false
                                }
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "${if (language == Language.HINDI) targetRole.titleHi else targetRole.titleEn}",
                                        fontWeight = FontWeight.Bold,
                                        color = tagColor,
                                        style = MaterialTheme.typography.titleSmall
                                    )
                                    if (isCurrentRole) {
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = tagColor
                                        ) {
                                            Text(
                                                text = "✓ सक्रिय भूमिका",
                                                color = Color.White,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    } else {
                                        Text(
                                            text = "स्विच करें →",
                                            fontSize = 11.sp,
                                            color = tagColor,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(perms, style = MaterialTheme.typography.bodySmall, color = TextPrimary)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showRoleMatrixDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = RathoreGreen)
                ) {
                    Text("समझ गया (Understood)", color = Color.White)
                }
            }
        )
    }

    // 2. Logout Confirmation Dialog
    if (showLogoutConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutConfirmDialog = false },
            icon = {
                Icon(Icons.Default.Logout, contentDescription = null, tint = Color(0xFFDC2626), modifier = Modifier.size(36.dp))
            },
            title = {
                Text("लॉगआउट की पुष्टि", fontWeight = FontWeight.Bold)
            },
            text = {
                Text(
                    "क्या आप सचमुच ऐप से लॉगआउट करना चाहते हैं? दोबारा जुड़ने के लिए मोबाइल नंबर और ओटीपी की आवश्यकता होगी।",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    textAlign = TextAlign.Center
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutConfirmDialog = false
                        viewModel.navigateTo(AppScreen.Login)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
                ) {
                    Text("हाँ, लॉगआउट करें", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showLogoutConfirmDialog = false }) {
                    Text("रद्द करें", color = TextSecondary)
                }
            }
        )
    }

    // 3. Deactivate Account Dialog
    if (showDeactivateAccountDialog) {
        AlertDialog(
            onDismissRequest = { showDeactivateAccountDialog = false },
            icon = {
                Icon(Icons.Default.PauseCircleOutline, contentDescription = null, tint = RathoreOrangeDark, modifier = Modifier.size(36.dp))
            },
            title = {
                Text("खाता अस्थायी निष्क्रिय करें", fontWeight = FontWeight.Bold)
            },
            text = {
                Column {
                    Text(
                        "खाता निष्क्रिय करने पर आपकी प्रोफ़ाइल और संगठन के दायित्व अस्थायी रूप से रोक दिए जाएंगे। आप किसी भी समय पुनः लॉगिन करके अपना खाता सक्रिय कर सकते हैं।",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDeactivateAccountDialog = false
                        viewModel.navigateTo(AppScreen.Login)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RathoreOrangeDark)
                ) {
                    Text("निष्क्रिय करें", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showDeactivateAccountDialog = false }) {
                    Text("रद्द करें", color = TextSecondary)
                }
            }
        )
    }

    // 4. Delete Account Dialog
    if (showDeleteAccountDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteAccountDialog = false },
            icon = {
                Icon(Icons.Default.WarningAmber, contentDescription = null, tint = Color(0xFFDC2626), modifier = Modifier.size(36.dp))
            },
            title = {
                Text("खाता स्थायी रूप से हटाएं", fontWeight = FontWeight.Bold, color = Color(0xFFDC2626))
            },
            text = {
                Column {
                    Text(
                        "सावधानी: यह प्रक्रिया अपरिवर्तनीय है! आपका सारा डेटा, संचित रेफरल, प्रगति रिपोर्ट और संगठनात्मक दायित्व स्थायी रूप से मिटा दिए जाएंगे।",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "क्या आप वाकई खाता हटाना चाहते हैं?",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                        color = Color(0xFFDC2626)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteAccountDialog = false
                        viewModel.navigateTo(AppScreen.Login)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
                ) {
                    Text("स्थायी हटाएं", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showDeleteAccountDialog = false }) {
                    Text("रद्द करें", color = TextSecondary)
                }
            }
        )
    }

    // 5. Help & Support Dialog
    if (showHelpSupportDialog) {
        AlertDialog(
            onDismissRequest = { showHelpSupportDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.HelpOutline, contentDescription = null, tint = RathoreGreen)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("सहायता एवं समर्थन (Help & Support)", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("केंद्रीय कार्यालय एवं आईटी हेल्पडेस्क से संपर्क करें:", style = MaterialTheme.typography.bodySmall, color = TextSecondary)

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFF8FAFC),
                        border = BorderStroke(1.dp, BorderStroke)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Phone, contentDescription = null, tint = RathoreGreen, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("हेल्पलाइन: +91 94251 00000", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Email, contentDescription = null, tint = RathoreGreen, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("ईमेल: support@teamrathore.in", style = MaterialTheme.typography.bodySmall)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.AccessTime, contentDescription = null, tint = TextTertiary, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("समय: प्रातः 9:00 से सायं 8:00 बजे तक", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showHelpSupportDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = RathoreGreen)
                ) {
                    Text("ठीक है", color = Color.White)
                }
            }
        )
    }

    // 6. Privacy & Security Dialog
    if (showPrivacySecurityDialog) {
        AlertDialog(
            onDismissRequest = { showPrivacySecurityDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Lock, contentDescription = null, tint = RathoreGreen)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("गोपनीयता एवं सुरक्षा (Privacy & Security)", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                }
            },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        "हम आपकी व्यक्तिगत जानकारी की सुरक्षा एवं गोपनीयता हेतु पूर्णतः प्रतिबद्ध हैं:",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                    Text("• डेटा एन्क्रिप्शन: आपके सभी व्यक्तिगत विवरण एवं संपर्क जानकारी 256-बिट SSL सुरक्षित हैं।", style = MaterialTheme.typography.bodySmall)
                    Text("• सीमित पहुंच: केवल अधिकृत व्यवस्थापक और जिला समन्वय समिति ही संगठनात्मक रिपोर्टिंग देख सकती है।", style = MaterialTheme.typography.bodySmall)
                    Text("• तृतीय-पक्ष शून्य साझाकरण: आपका डेटा किसी भी बाहरी व्यावसायिक एजेंसी के साथ साझा नहीं किया जाता।", style = MaterialTheme.typography.bodySmall)
                    Text("• डिवाइस सुरक्षा: सत्र की सुरक्षा के लिए सुरक्षित टोकन व ओटीपी प्रमाणीकरण का प्रयोग होता है।", style = MaterialTheme.typography.bodySmall)
                }
            },
            confirmButton = {
                Button(
                    onClick = { showPrivacySecurityDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = RathoreGreen)
                ) {
                    Text("सहमति है (Agreed)", color = Color.White)
                }
            }
        )
    }

    // Edit Profile Modal Dialog
    if (showEditProfileDialog) {
        var editName by remember { mutableStateOf(profile.fullName) }
        var editDesignation by remember { mutableStateOf(profile.designation) }
        var editRole by remember { mutableStateOf(profile.role) }
        var selectedAvatar by remember { mutableStateOf(profile.customPhotoUri ?: "avatar_default") }
        val avatarOptions = listOf(
            "avatar_default" to "मूल अवतार",
            "avatar_kurta_white" to "सफेद कुर्ता",
            "avatar_kurta_saffron" to "केसरिया पटका",
            "avatar_jacket_blue" to "नेहरू जैकेट",
            "avatar_formal" to "फॉर्मल"
        )

        AlertDialog(
            onDismissRequest = { showEditProfileDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Edit, contentDescription = null, tint = RathoreGreen)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("प्रोफाइल संपादित करें", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Profile Photo selection
                    Text("1. प्रोफाइल फोटो / अवतार चुनें", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = RathoreGreen)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        avatarOptions.forEach { (avatarKey, label) ->
                            val isSelected = selectedAvatar == avatarKey
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) RathoreGreen.copy(alpha = 0.15f) else Color(0xFFF8FAFC),
                                border = BorderStroke(if (isSelected) 2.dp else 1.dp, if (isSelected) RathoreGreen else BorderStroke),
                                modifier = Modifier
                                    .clickable { selectedAvatar = avatarKey }
                                    .padding(vertical = 2.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(8.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(44.dp)
                                            .clip(CircleShape)
                                            .background(if (isSelected) RathoreGreen else Color(0xFFE2E8F0)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            Icons.Default.Person,
                                            contentDescription = null,
                                            tint = if (isSelected) Color.White else TextSecondary,
                                            modifier = Modifier.size(26.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(label, fontSize = 10.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal, color = if (isSelected) RathoreGreen else TextPrimary)
                                }
                            }
                        }
                    }

                    Divider(color = BorderStroke, thickness = 0.6.dp)

                    // Full Name
                    Text("2. पूरा नाम (Full Name) *", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        modifier = Modifier.fillMaxWidth().testTag("input_edit_name"),
                        shape = RoundedCornerShape(8.dp),
                        singleLine = true
                    )

                    // Designation
                    Text("3. पद / दायित्व (Designation) *", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                    OutlinedTextField(
                        value = editDesignation,
                        onValueChange = { editDesignation = it },
                        modifier = Modifier.fillMaxWidth().testTag("input_edit_designation"),
                        shape = RoundedCornerShape(8.dp),
                        placeholder = { Text("उदा. युवा कांग्रेस मंडल प्रभारी") },
                        singleLine = true
                    )

                    // Role / Category Selection
                    Text("4. संगठन श्रेणी / भूमिका (Category / Role) *", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                    if (profile.role == UserRole.ADMIN) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFFFEBEE),
                            border = BorderStroke(1.dp, Color(0xFFEF9A9A)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFFC62828), modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text("व्यवस्थापक (Admin - अपरिवर्तनीय)", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFFC62828))
                                    Text("एडमिनिस्ट्रेटर स्तर केवल केंद्रीय प्रणाली द्वारा प्रबंधित होता है।", fontSize = 10.sp, color = TextSecondary)
                                }
                            }
                        }
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf(
                                UserRole.SUPPORTER to ("समर्थक (Supporter)" to "सामान्य नागरिक, डिजिटल प्रचारक"),
                                UserRole.WORKER to ("कार्यकर्ता (Active Worker)" to "वार रूम, बूथ सत्यापन व फील्ड समन्वयक"),
                                UserRole.OFFICE_BEARER to ("पदाधिकारी (Office Bearer)" to "दायित्वधारी, टास्क आवंटन व मंडल नेतृत्व")
                            ).forEach { (role, info) ->
                                val isSelected = editRole == role
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isSelected) RathoreGreen.copy(alpha = 0.1f) else Color(0xFFF8FAFC),
                                    border = BorderStroke(if (isSelected) 1.5.dp else 1.dp, if (isSelected) RathoreGreen else BorderStroke),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { editRole = role }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        RadioButton(
                                            selected = isSelected,
                                            onClick = { editRole = role },
                                            colors = RadioButtonDefaults.colors(selectedColor = RathoreGreen)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Column {
                                            Text(info.first, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = if (isSelected) RathoreGreen else TextPrimary)
                                            Text(info.second, fontSize = 10.sp, color = TextSecondary)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Warning notice about review trigger
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFFFFBEB),
                        border = BorderStroke(1.dp, Color(0xFFFCD34D))
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFFD97706), modifier = Modifier.size(16.dp).padding(top = 2.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "सूचना: नाम, पद अथवा श्रेणी में कोई भी बदलाव करने पर आपकी प्रोफाइल स्वतः समीक्षाधीन (Under Review) हो जाएगी। एडमिन की स्वीकृति के उपरांत ही पोस्टर व वीडियो निर्माण की सुविधा पुनः प्रारंभ होगी।",
                                fontSize = 10.sp,
                                color = Color(0xFF92400E),
                                lineHeight = 14.sp
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (editName.isNotBlank() && editDesignation.isNotBlank()) {
                            viewModel.editUserProfile(
                                newName = editName,
                                newDesignation = editDesignation,
                                newRole = editRole,
                                newPhotoUri = selectedAvatar
                            )
                            showEditProfileDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RathoreGreen),
                    modifier = Modifier.testTag("btn_save_profile_edits")
                ) {
                    Text("बदलाव सहेजें (Submit) ✓", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditProfileDialog = false }) {
                    Text("रद्द करें", color = TextSecondary)
                }
            }
        )
    }

    // About Team Rathore App Dialog
    if (showAboutAppDialog) {
        AlertDialog(
            onDismissRequest = { showAboutAppDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    TeamRathoreLogoIcon(size = 32.dp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("टीम राठौर", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFFF0FDF4),
                        border = BorderStroke(1.dp, Color(0xFFBBF7D0))
                    ) {
                        Text(
                            text = "यह माननीय पृथ्वीपुर विधायक नितेंद्र बृजेंद्र सिंह राठौर जी के मार्गदर्शन में संचालित एक संगठनात्मक व नागरिक संवाद डिजिटल मंच है।",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                            color = Color(0xFF14532D),
                            modifier = Modifier.padding(14.dp)
                        )
                    }
                    Text(
                        text = "वर्जन 2.4.0 • मध्य प्रदेश विधानसभा क्षेत्र क्रमांक 45 (पृथ्वीपुर)",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { showAboutAppDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = RathoreGreen)
                ) {
                    Text("ठीक है")
                }
            }
        )
    }

    // 🧪 Admin Test Mode Selection Dialog
    if (showTestModeDialog) {
        AlertDialog(
            onDismissRequest = { showTestModeDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFEEF2FF)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Science, contentDescription = null, tint = Color(0xFF4F46E5), modifier = Modifier.size(18.dp))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("एडमिन टेस्ट मोड (इंटरफेस प्रिव्यू)", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "कृपया वह भूमिका चुनें जिसके रूप में आप ऐप का इंटरफ़ेस देखना और जांचना चाहते हैं:",
                        fontSize = 12.5.sp,
                        color = TextSecondary
                    )

                    // 1. Supporter
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                showTestModeDialog = false
                                viewModel.enterTestMode(UserRole.SUPPORTER)
                            },
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFFFFF7ED),
                        border = BorderStroke(1.dp, RathoreOrange)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Favorite, contentDescription = null, tint = RathoreOrange, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("1. समर्थक (Supporter Mode)", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextPrimary)
                                Text("सुविचार, दैनिक पोस्ट, पोस्टर डाउनलोड व वीडियो फीड", fontSize = 10.5.sp, color = TextSecondary)
                            }
                        }
                    }

                    // 2. Worker
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                showTestModeDialog = false
                                viewModel.enterTestMode(UserRole.WORKER)
                            },
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFFF0FDF4),
                        border = BorderStroke(1.dp, RathoreGreen)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Groups, contentDescription = null, tint = RathoreGreen, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("2. कार्यकर्ता (Worker Mode)", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextPrimary)
                                Text("वॉर रूम, वीडियो संपीड़न व सुझाव, टास्क रिपोर्टिंग", fontSize = 10.5.sp, color = TextSecondary)
                            }
                        }
                    }

                    // 3. Office Bearer
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                showTestModeDialog = false
                                viewModel.enterTestMode(UserRole.OFFICE_BEARER)
                            },
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFFEDE7F6),
                        border = BorderStroke(1.dp, Color(0xFF5E35B1))
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Shield, contentDescription = null, tint = Color(0xFF5E35B1), modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("3. पदाधिकारी (Office Bearer Mode)", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextPrimary)
                                Text("बूथ सत्यापन, क्षेत्र समन्वय, टास्क आवंटन व समीक्षा", fontSize = 10.5.sp, color = TextSecondary)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                if (uiState.isTestModeActive) {
                    Button(
                        onClick = {
                            showTestModeDialog = false
                            viewModel.exitTestMode()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828))
                    ) {
                        Text("एडमिन मोड में वापस जाएं")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { showTestModeDialog = false }) {
                    Text("रद्द करें")
                }
            }
        )
    }
}

@Composable
private fun StatBox(number: String, label: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(number, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black), color = color)
        Text(label, style = MaterialTheme.typography.labelSmall, color = TextSecondary)
    }
}

@Composable
private fun MenuItem(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit,
    iconTint: Color = RathoreGreen
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(22.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Text(title, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium), color = TextPrimary)
        }
        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = TextTertiary, modifier = Modifier.size(18.dp))
    }
}
