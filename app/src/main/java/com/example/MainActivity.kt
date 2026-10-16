package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.AppBottomNav
import com.example.ui.components.AppHeader
import com.example.ui.screens.*
import com.example.ui.theme.*
import com.example.viewmodel.AppScreen
import com.example.viewmodel.AppViewModel
import com.example.viewmodel.MainTab

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = androidx.activity.SystemBarStyle.light(
                android.graphics.Color.TRANSPARENT,
                android.graphics.Color.TRANSPARENT
            ),
            navigationBarStyle = androidx.activity.SystemBarStyle.light(
                android.graphics.Color.TRANSPARENT,
                android.graphics.Color.TRANSPARENT
            )
        )
        setContent {
            TeamRathoreTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.White
                ) {
                    TeamRathoreApp()
                }
            }
        }
    }
}

@Composable
fun TeamRathoreApp(viewModel: AppViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    // Show toast messages
    LaunchedEffect(uiState.toastMessage) {
        uiState.toastMessage?.let { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            viewModel.clearToast()
        }
    }

    // Handle system back navigation
    BackHandler(enabled = uiState.currentScreen != AppScreen.Main && uiState.currentScreen != AppScreen.Splash) {
        when (uiState.currentScreen) {
            AppScreen.RegistrationStep3 -> viewModel.navigateTo(AppScreen.RegistrationStep2)
            AppScreen.RegistrationStep2 -> viewModel.navigateTo(AppScreen.RegistrationStep1)
            AppScreen.RegistrationStep1 -> viewModel.navigateTo(AppScreen.RoleSelect)
            AppScreen.RoleSelect -> viewModel.navigateTo(AppScreen.Login)
            AppScreen.OtpVerify -> viewModel.navigateTo(AppScreen.Login)
            else -> viewModel.navigateTo(AppScreen.Main)
        }
    }

    when (val screen = uiState.currentScreen) {
        is AppScreen.Splash, is AppScreen.LanguageSelect -> {
            SplashScreen(viewModel = viewModel, language = uiState.language)
        }
        is AppScreen.Login -> {
            LoginScreen(
                viewModel = viewModel,
                language = uiState.language,
                mobileInput = uiState.mobileInput
            )
        }
        is AppScreen.OtpVerify -> {
            OtpVerifyScreen(
                viewModel = viewModel,
                language = uiState.language,
                mobile = uiState.mobileInput,
                otpInput = uiState.otpInput
            )
        }
        is AppScreen.RoleSelect -> {
            RoleSelectionScreen(
                viewModel = viewModel,
                language = uiState.language,
                selectedRole = uiState.registrationRole
            )
        }
        is AppScreen.RegistrationStep1 -> {
            RegistrationStep1Screen(
                viewModel = viewModel,
                language = uiState.language,
                profile = uiState.userProfile
            )
        }
        is AppScreen.RegistrationStep2 -> {
            RegistrationStep2Screen(
                viewModel = viewModel,
                language = uiState.language,
                profile = uiState.userProfile
            )
        }
        is AppScreen.RegistrationStep3 -> {
            RegistrationStep3Screen(
                viewModel = viewModel,
                language = uiState.language,
                profile = uiState.userProfile
            )
        }
        is AppScreen.RegistrationSuccess -> {
            RegistrationSuccessScreen(
                viewModel = viewModel,
                language = uiState.language,
                profile = uiState.userProfile
            )
        }
        is AppScreen.EditingTable -> {
            EditingTableScreen(
                viewModel = viewModel,
                language = uiState.language,
                posterConfig = uiState.posterConfig,
                isDownloadSuccessShown = uiState.isDownloadSuccessShown
            )
        }
        is AppScreen.DirectConnect -> {
            DirectConnectScreen(
                viewModel = viewModel,
                language = uiState.language,
                requests = uiState.citizenRequests
            )
        }
        is AppScreen.Notifications -> {
            NotificationsScreen(
                viewModel = viewModel,
                language = uiState.language,
                notifications = uiState.notifications
            )
        }
        is AppScreen.AdminPanel -> {
            AdminPanelScreen(
                viewModel = viewModel,
                language = uiState.language
            )
        }
        is AppScreen.UxStatesShowcase -> {
            UxStatesShowcaseScreen(
                viewModel = viewModel,
                language = uiState.language
            )
        }
        is AppScreen.Main, is AppScreen.Referral, is AppScreen.MyActivity, is AppScreen.Settings -> {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                snackbarHost = { SnackbarHost(snackbarHostState) },
                topBar = {
                    AppHeader(
                        language = uiState.language,
                        userRole = uiState.userProfile.role,
                        unreadNotificationsCount = uiState.notifications.count { it.isUnread },
                        onToggleLanguage = { viewModel.toggleLanguage() },
                        onNotificationsClick = { viewModel.navigateTo(AppScreen.Notifications) },
                        onRoleClick = { viewModel.selectTab(MainTab.PROFILE) }
                    )
                },
                bottomBar = {
                    AppBottomNav(
                        selectedTab = uiState.selectedTab,
                        userRole = uiState.userProfile.role,
                        language = uiState.language,
                        onTabSelected = { tab -> viewModel.selectTab(tab) },
                        onCreateClick = {
                            if (uiState.userProfile.role == com.example.model.UserRole.ADMIN) {
                                viewModel.navigateTo(AppScreen.AdminPanel)
                            } else {
                                if (!uiState.userProfile.isApproved) {
                                    viewModel.showProfileReviewDialog()
                                } else {
                                    viewModel.navigateTo(AppScreen.EditingTable)
                                }
                            }
                        }
                    )
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    when (uiState.selectedTab) {
                        MainTab.HOME -> {
                            HomeScreen(
                                viewModel = viewModel,
                                language = uiState.language,
                                feedItems = uiState.feedList,
                                activeFilter = uiState.feedFilter,
                                posterConfig = uiState.posterConfig
                            )
                        }
                        MainTab.VIDEOS -> {
                            VideosScreen(
                                viewModel = viewModel,
                                language = uiState.language,
                                videoList = uiState.videoList,
                                isVideoPlaying = uiState.isVideoPlaying,
                                activePlayingVideo = uiState.activePlayingVideo,
                                isVideoProcessing = uiState.isVideoProcessing
                            )
                        }
                        MainTab.CREATE -> {
                            // Handled via navigation to EditingTable
                        }
                        MainTab.WAR_ROOM -> {
                            WarRoomScreen(
                                viewModel = viewModel,
                                language = uiState.language,
                                meetingInfo = uiState.meetingInfo,
                                programs = uiState.programs,
                                notices = uiState.notices,
                                isMicMuted = uiState.isMicMuted,
                                isCameraOff = uiState.isCameraOff
                            )
                        }
                        MainTab.TASKS -> {
                            TasksScreen(
                                viewModel = viewModel,
                                language = uiState.language,
                                tasks = uiState.tasks
                            )
                        }
                        MainTab.PROFILE -> {
                            ProfileScreen(
                                viewModel = viewModel,
                                language = uiState.language,
                                profile = uiState.userProfile,
                                activity = uiState.activity,
                                referral = uiState.referral
                            )
                        }
                        MainTab.DIRECT_CONNECT -> {
                            DirectConnectScreen(
                                viewModel = viewModel,
                                language = uiState.language,
                                requests = uiState.citizenRequests
                            )
                        }
                    }
                }
            }
        }
    }

    // Global "Profile Under Review" Popup Modal
    if (uiState.showProfilePendingReviewDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissProfileReviewDialog() },
            icon = {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFEF3C7)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.HourglassTop,
                        contentDescription = null,
                        tint = Color(0xFFD97706),
                        modifier = Modifier.size(30.dp)
                    )
                }
            },
            title = {
                Text(
                    text = "⚠️ प्रोफाइल सत्यापन समीक्षाधीन है",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = Color(0xFF92400E),
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (uiState.userProfile.reviewReason.isNotBlank()) uiState.userProfile.reviewReason else "नाम, पद अथवा संगठनात्मक श्रेणी में किए गए बदलाव के कारण आपकी प्रोफाइल वर्तमान में केंद्रीय व्यवस्थापक द्वारा सत्यापन समीक्षाधीन है।",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextPrimary,
                        textAlign = TextAlign.Center
                    )

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFFFFFBEB),
                        border = BorderStroke(1.dp, Color(0xFFFDE68A)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                "🔒 वर्तमान अधिकार स्थिति:",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = Color(0xFFB45309)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "• आप सभी पोस्ट, समाचार, विज्ञप्तियां व वीडियो देख सकते हैं।\n• स्वयं के फोटो एवं नाम वाली प्रचार सामग्री (पोस्टर/वीडियो जैकेट) बनाने व डाउनलोड करने के लिए केंद्रीय अनुमोदन अनिवार्य है।",
                                fontSize = 11.sp,
                                color = Color(0xFF78350F),
                                lineHeight = 15.sp
                            )
                        }
                    }

                    // Direct Contact Helpline Button
                    OutlinedButton(
                        onClick = { viewModel.contactAdminFromPopup() },
                        modifier = Modifier.fillMaxWidth().height(44.dp),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.2.dp, RathoreOrange),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Icon(Icons.Default.Phone, contentDescription = null, tint = RathoreOrange, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("आईटी व्यवस्थापक संपर्क (+91 94251 00111)", fontSize = 11.sp, color = RathoreOrange, fontWeight = FontWeight.Bold)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.approveProfileDirectly() },
                    colors = ButtonDefaults.buttonColors(containerColor = RathoreGreen),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("त्वरित स्वीकृति (Demo Instant Approval) ✓", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { viewModel.dismissProfileReviewDialog() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("समझ गया, प्रतीक्षा करें (बंद करें)", color = TextSecondary, fontSize = 12.sp)
                }
            }
        )
    }
}
