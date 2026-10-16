package com.example.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.model.*
import com.example.ui.components.AdminFrameCreatorStudioDialog
import com.example.ui.components.AdminImageAndCollageEditorDialog
import com.example.ui.components.AdminVideoEditorDialog
import com.example.ui.components.CollagePhotoSlotGrid
import com.example.ui.components.GoogleSheetsDatabaseHub
import com.example.ui.theme.*
import com.example.util.AppStrings
import com.example.util.CompressionResult
import com.example.util.ImageCompressionPreset
import com.example.viewmodel.AppScreen
import com.example.viewmodel.AppViewModel

@Composable
fun NotificationsScreen(
    viewModel: AppViewModel,
    language: Language,
    notifications: List<NotificationItem>
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(RathoreBackground)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color.White,
            shadowElevation = 2.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { viewModel.navigateTo(AppScreen.Main) },
                    modifier = Modifier.testTag("btn_back_notifs")
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = RathoreGreen)
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = AppStrings.get("notifications_title", language),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = RathoreGreen
                )
            }
        }

        if (notifications.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFF1F5F9)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.NotificationsNone,
                            contentDescription = null,
                            tint = TextTertiary,
                            modifier = Modifier.size(44.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = if (language == Language.HINDI) "कोई नई सूचना नहीं है" else "No New Notifications",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = if (language == Language.HINDI)
                            "सभी जरूरी सूचनाएं एवं संगठन से जुड़े अपडेट यहाँ प्रदर्शित होंगे।"
                        else
                            "All important notices and organizational updates will appear here.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(notifications) { notif ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                viewModel.markNotificationAsRead(notif.id)
                                if (notif.type == "grievance_resolved" || notif.type == "urgent") {
                                    viewModel.navigateTo(AppScreen.DirectConnect)
                                }
                            },
                        shape = RoundedCornerShape(12.dp),
                        color = if (notif.isUnread) Color(0xFFF0FDF4) else Color.White,
                        border = BorderStroke(1.dp, if (notif.isUnread) RathoreGreen else BorderStroke)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when (notif.type) {
                                            "urgent" -> Color(0xFFFFEBEE)
                                            "graphic" -> Color(0xFFFFF3E0)
                                            "grievance_resolved" -> Color(0xFFDCFCE7)
                                            else -> Color(0xFFE8F5E9)
                                        }
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = when (notif.type) {
                                        "urgent" -> Icons.Default.Warning
                                        "graphic" -> Icons.Default.Palette
                                        "grievance_resolved" -> Icons.Default.CheckCircle
                                        else -> Icons.Default.VideoCall
                                    },
                                    contentDescription = null,
                                    tint = when (notif.type) {
                                        "urgent" -> Color(0xFFDC2626)
                                        "graphic" -> RathoreOrange
                                        "grievance_resolved" -> Color(0xFF16A34A)
                                        else -> RathoreGreen
                                    },
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (language == Language.HINDI) notif.titleHi else notif.titleEn,
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = TextPrimary
                                )
                                Text(
                                    text = if (language == Language.HINDI) notif.messageHi else notif.messageEn,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary,
                                    modifier = Modifier.padding(vertical = 2.dp)
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(notif.timeAgoHi, style = MaterialTheme.typography.labelSmall, color = TextTertiary)
                                    if (notif.type == "grievance_resolved") {
                                        Text(
                                            text = "विवरण व फोटो देखें →",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF16A34A)
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
}

enum class AdminUploadMode {
    PRACHAR,
    UPDATE,
    VIDEO,
    NOTICE,
    USERS
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AdminPanelScreen(
    viewModel: AppViewModel,
    language: Language
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var uploadMode by remember { mutableStateOf(AdminUploadMode.PRACHAR) }
    var showCategoryManagerDialog by remember { mutableStateOf(false) }

    val categoryOptions = uiState.feedCategories
    var newCategoryName by remember { mutableStateOf("") }
    var editingCategory by remember { mutableStateOf<String?>(null) }
    var editCategoryNewName by remember { mutableStateOf("") }
    val context = LocalContext.current
    var showDeleteConfirmDialog by remember { mutableStateOf<String?>(null) }

    // User Management State (Block & Role Management)
    var userSearchQuery by remember { mutableStateOf("") }
    var selectedRoleFilter by remember { mutableStateOf<UserRole?>(null) }
    var roleChangeSuccessMessage by remember { mutableStateOf<String?>(null) }

    // 1. Prachar (Photo & Collage) State
    var pracharMatter by remember { mutableStateOf("") }
    var pracharCategory by remember { mutableStateOf("प्रचार") }
    var pracharCollageCount by remember { mutableIntStateOf(1) }
    var pracharPhotos by remember { mutableStateOf(emptyList<String>()) }
    var pracharPhotoUri by remember { mutableStateOf<Uri?>(null) }
    var pracharPublishedNotice by remember { mutableStateOf(false) }

    // 2. Update (Daily Activities & Social) State
    var updateTitle by remember { mutableStateOf("") }
    var updateContent by remember { mutableStateOf("") }
    var updateUrl by remember { mutableStateOf("") }
    var updateCategory by remember { mutableStateOf("अपडेट") }
    var updateCollageCount by remember { mutableIntStateOf(1) }
    var updatePhotos by remember { mutableStateOf(emptyList<String>()) }
    var updatePhotoUri by remember { mutableStateOf<Uri?>(null) }
    var updatePublishedNotice by remember { mutableStateOf(false) }

    val pracharPhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            pracharPhotoUri = uri
            val name = "gallery_${uri.lastPathSegment?.takeLast(8) ?: "img"}.jpg"
            if (pracharPhotos.size < pracharCollageCount) {
                pracharPhotos = pracharPhotos + name
            } else {
                pracharPhotos = listOf(name)
            }
        }
    }

    val updatePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            updatePhotoUri = uri
            val name = "gallery_${uri.lastPathSegment?.takeLast(8) ?: "img"}.jpg"
            updatePhotos = listOf(name)
        }
    }

    var selectedVideoUri by remember { mutableStateOf<Uri?>(null) }
    val videoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedVideoUri = uri
            selectedVideoFileName = uri.lastPathSegment ?: "video.mp4"
        }
    }

    // 3. Video Content State with Aspect Ratios & Templates
    var videoTitle by remember { mutableStateOf("") }
    var videoCaption by remember { mutableStateOf("") }
    var videoAspectRatio by remember { mutableStateOf("9:16") }
    var videoDuration by remember { mutableStateOf("2:30") }
    var videoRequiresJacket by remember { mutableStateOf(true) }
    var selectedVideoFileName by remember { mutableStateOf("samvad_rally_2026.mp4") }
    var selectedJacketTemplate by remember { mutableStateOf("हेमंत राठौर आधिकारिक जैकेट") }
    var showAddJacketDialog by remember { mutableStateOf(false) }
    var newJacketInputName by remember { mutableStateOf("") }
    var editingJacket by remember { mutableStateOf<VideoJacketTemplate?>(null) }
    var editJacketName by remember { mutableStateOf("") }
    var editJacketHeaderText by remember { mutableStateOf("") }
    var editJacketFooterText by remember { mutableStateOf("") }
    var editJacketColorHex by remember { mutableLongStateOf(0xFF9333EAL) }
    var availableJackets by remember {
        mutableStateOf(
            listOf(
                "हेमंत राठौर आधिकारिक जैकेट",
                "जनसंवाद चौपाल हेडर-फुटर फ्रेम",
                "युवा क्रांति बॉर्डर जैकेट",
                "विकास संकल्प विशेष जैकेट"
            )
        )
    }
    val allVideoTags = listOf("वीडियो", "राहुल गांधी", "प्रियंका गांधी", "हेमंत राठौर", "जनसंवाद चौपाल", "युवा संवाद", "किसान महापंचायत", "विकास कार्य")
    var selectedVideoTags by remember { mutableStateOf(setOf("वीडियो", "हेमंत राठौर")) }
    var videoPublishedNotice by remember { mutableStateOf(false) }

    // 4. Notice (Broadcast & Urgent Alerts) State
    var noticeTitle by remember { mutableStateOf("") }
    var noticeBody by remember { mutableStateOf("") }
    var noticeIsUrgent by remember { mutableStateOf(false) }
    var noticePhotoAttached by remember { mutableStateOf(false) }
    var noticePhotos by remember { mutableStateOf<List<String>>(emptyList()) }
    var noticePublishedNotice by remember { mutableStateOf(false) }

    // 5. Media Editor & Template Studio State
    var showFrameCreatorStudioDialog by remember { mutableStateOf(false) }
    var showVideoEditorDialog by remember { mutableStateOf(false) }
    var showScheduledMeetingsDialog by remember { mutableStateOf(false) }
    var showPendingTasksDialog by remember { mutableStateOf(false) }

    // Actionable Metrics Calculations
    val totalGraphicsUploaded = uiState.feedList.size
    val totalVideosProcessed = uiState.videoList.size
    val scheduledMeetingsCount = (if (uiState.meetingInfo.isScheduled && uiState.meetingInfo.status != MeetingStatus.ENDED) 1 else 0) + uiState.programs.size
    val pendingTasksCount = uiState.tasks.count { it.status != TaskStatus.COMPLETED }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(RathoreBackground)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // Header
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color.White,
            shadowElevation = 2.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { viewModel.navigateTo(AppScreen.Main) },
                    modifier = Modifier.testTag("btn_back_admin")
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = RathoreGreen)
                }
                Spacer(modifier = Modifier.width(6.dp))
                Column {
                    Text("Admin कंट्रोल पैनल", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = RathoreGreen)
                    Text("कंटेंट अपलोड एवं प्रसारण केंद्र", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 🎨 Admin Frame Creator Studio Hero Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = Color(0xFF0F172A),
                shadowElevation = 4.dp,
                border = BorderStroke(1.dp, Color(0xFF334155))
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
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(RathoreGreen.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = RathoreGreen, modifier = Modifier.size(20.dp))
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "एडमिन फ्रेम क्रिएटर स्टूडियो",
                                    color = Color.White,
                                    fontSize = 13.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "आधिकारिक 4:5 ग्राफ़िक टेम्पलेट्स • नेवी व रेड रिबन डिजाइन",
                                    color = Color(0xFF94A3B8),
                                    fontSize = 10.sp
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = RathoreGreen.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = "एडिटर टूल्स",
                                color = RathoreGreen,
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // PRIMARY ACTION 1: ADMIN FRAME CREATOR STUDIO
                    Button(
                        onClick = { showFrameCreatorStudioDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("btn_open_frame_creator_studio"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = RathoreOrangeDark),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                    ) {
                        Icon(Icons.Default.CropFree, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "🖼️ एडमिन फ्रेम क्रिएटर स्टूडियो",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            // Top 3 Tabs: 1. प्रचार (Prachar) | 2. अपडेट (Update) | 3. वीडियो (Video)
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = Color.White,
                border = BorderStroke(1.dp, BorderStroke)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Tab 1: प्रचार (Prachar)
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { uploadMode = AdminUploadMode.PRACHAR }
                            .testTag("tab_upload_prachar"),
                        shape = RoundedCornerShape(8.dp),
                        color = if (uploadMode == AdminUploadMode.PRACHAR) RathoreGreen else Color.Transparent
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 9.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Default.Campaign,
                                contentDescription = null,
                                tint = if (uploadMode == AdminUploadMode.PRACHAR) Color.White else TextSecondary,
                                modifier = Modifier.size(19.dp)
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = "1. प्रचार",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (uploadMode == AdminUploadMode.PRACHAR) Color.White else TextPrimary
                            )
                        }
                    }

                    // Tab 2: अपडेट (Update / Activities)
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { uploadMode = AdminUploadMode.UPDATE }
                            .testTag("tab_upload_update"),
                        shape = RoundedCornerShape(8.dp),
                        color = if (uploadMode == AdminUploadMode.UPDATE) Color(0xFF1877F2) else Color.Transparent
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 9.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Default.DynamicFeed,
                                contentDescription = null,
                                tint = if (uploadMode == AdminUploadMode.UPDATE) Color.White else TextSecondary,
                                modifier = Modifier.size(19.dp)
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = "2. अपडेट",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (uploadMode == AdminUploadMode.UPDATE) Color.White else TextPrimary
                            )
                        }
                    }

                    // Tab 3: वीडियो (Video)
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { uploadMode = AdminUploadMode.VIDEO }
                            .testTag("tab_upload_video"),
                        shape = RoundedCornerShape(8.dp),
                        color = if (uploadMode == AdminUploadMode.VIDEO) Color(0xFF9333EA) else Color.Transparent
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 9.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Default.Videocam,
                                contentDescription = null,
                                tint = if (uploadMode == AdminUploadMode.VIDEO) Color.White else TextSecondary,
                                modifier = Modifier.size(19.dp)
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = "3. वीडियो",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (uploadMode == AdminUploadMode.VIDEO) Color.White else TextPrimary
                            )
                        }
                    }

                    // Tab 4: सूचना (Notice / Broadcast)
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { uploadMode = AdminUploadMode.NOTICE }
                            .testTag("tab_upload_notice"),
                        shape = RoundedCornerShape(8.dp),
                        color = if (uploadMode == AdminUploadMode.NOTICE) RathoreOrangeDark else Color.Transparent
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 9.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Default.Campaign,
                                contentDescription = null,
                                tint = if (uploadMode == AdminUploadMode.NOTICE) Color.White else TextSecondary,
                                modifier = Modifier.size(19.dp)
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = "4. सूचना",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (uploadMode == AdminUploadMode.NOTICE) Color.White else TextPrimary
                            )
                        }
                    }

                    // Tab 5: यूजर प्रबंधन (User & Worker Management)
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { uploadMode = AdminUploadMode.USERS }
                            .testTag("tab_upload_users"),
                        shape = RoundedCornerShape(8.dp),
                        color = if (uploadMode == AdminUploadMode.USERS) Color(0xFF0D9488) else Color.Transparent
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 9.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Default.Group,
                                contentDescription = null,
                                tint = if (uploadMode == AdminUploadMode.USERS) Color.White else TextSecondary,
                                modifier = Modifier.size(19.dp)
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = "5. यूजर",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (uploadMode == AdminUploadMode.USERS) Color.White else TextPrimary
                            )
                        }
                    }
                }
            }

            // Category Management Card: STRICTLY restricted to Prachar & Update only (as instructed by user)
            if (uploadMode == AdminUploadMode.PRACHAR || uploadMode == AdminUploadMode.UPDATE) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showCategoryManagerDialog = true }
                        .testTag("btn_category_manager"),
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White,
                    border = BorderStroke(1.2.dp, Color(0xFF7C3AED).copy(alpha = 0.35f)),
                    shadowElevation = 1.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFEDE9FE)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Category,
                                    contentDescription = null,
                                    tint = Color(0xFF7C3AED),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "श्रेणी प्रबंधन ⚙️",
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color(0xFF5B21B6)
                                )
                                Text(
                                    text = "${categoryOptions.size} सक्रिय श्रेणियां • प्रचार एवं अपडेट हेतु प्रबंधित करें",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextSecondary
                                )
                            }
                        }
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = Color(0xFF7C3AED),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // CONTENT BASED ON MODE
            when (uploadMode) {
                AdminUploadMode.PRACHAR -> {
                    // Prachar (Single 4:5 Photo) Upload:
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        color = Color.White,
                        border = BorderStroke(1.dp, BorderStroke)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFE8F5E9)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, tint = RathoreGreen, modifier = Modifier.size(20.dp))
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text("प्रचार फोटो अपलोड (4:5 प्रारूप)", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                                    Text("गैलरी से 4:5 अनुपात की प्रचार फोटो चुनें", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // 1. Category Selector (श्रेणी प्रबंधन)
                            Text("श्रेणी चुनें (Category)*", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                categoryOptions.forEach { cat ->
                                    val isSelected = pracharCategory == cat
                                    Surface(
                                        modifier = Modifier.clickable { pracharCategory = cat },
                                        shape = RoundedCornerShape(16.dp),
                                        color = if (isSelected) RathoreGreen else Color(0xFFF1F5F9),
                                        border = BorderStroke(1.dp, if (isSelected) RathoreGreen else Color(0xFFCBD5E1))
                                    ) {
                                        Text(
                                            text = cat,
                                            fontSize = 11.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isSelected) Color.White else TextPrimary,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // 2. REAL-TIME 4:5 LIVE PREVIEW (DIRECTLY BELOW CATEGORY MANAGEMENT)
                            Text("लाइव प्रचार प्रीव्यू (4:5 अनुपात):", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Text("यहाँ सिर्फ आपकी 4:5 फोटो दिखेगी, कोई टेक्स्ट या जैकेट नहीं", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                            Spacer(modifier = Modifier.height(8.dp))

                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(4f / 5f)
                                    .clip(RoundedCornerShape(12.dp)),
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFF0F172A),
                                border = BorderStroke(1.5.dp, if (pracharPhotoUri != null) RathoreGreen else Color(0xFF334155))
                            ) {
                                if (pracharPhotoUri != null) {
                                    AsyncImage(
                                        model = pracharPhotoUri,
                                        contentDescription = "लाइव 4:5 प्रचार फोटो प्रीव्यू",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            modifier = Modifier.padding(24.dp)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(56.dp)
                                                    .background(Color(0xFF1E293B), CircleShape),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    Icons.Default.Image,
                                                    contentDescription = null,
                                                    tint = Color(0xFF94A3B8),
                                                    modifier = Modifier.size(32.dp)
                                                )
                                            }
                                            Spacer(modifier = Modifier.height(12.dp))
                                            Text(
                                                text = "4:5 प्रचार फोटो का लाइव प्रीव्यू",
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = "नीचे से 4:5 फोटो चुनें • फोटो यहाँ बिना किसी टेक्स्ट के ठीक वैसे ही दिखेगी जैसे वह फ़ीड में जाएगी",
                                                fontSize = 11.5.sp,
                                                color = Color(0xFF94A3B8),
                                                textAlign = TextAlign.Center,
                                                lineHeight = 16.sp
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // 3. Single Photo Upload Option (Collage/Editor completely removed per directive)
                            Text("प्रचार फोटो चुनें (4:5 अनुपात):", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(6.dp))

                            if (pracharPhotoUri == null) {
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            pracharPhotoPickerLauncher.launch(
                                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                            )
                                        },
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color(0xFFF8FAFC),
                                    border = BorderStroke(1.5.dp, RathoreGreen.copy(alpha = 0.5f))
                                ) {
                                    Column(
                                        modifier = Modifier.padding(20.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(44.dp)
                                                .background(RathoreGreen.copy(alpha = 0.12f), CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, tint = RathoreGreen, modifier = Modifier.size(24.dp))
                                        }
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = "गैलरी से 4:5 प्रचार फोटो चुनें",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = RathoreGreen
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = "टैप करके अपनी फोटो सिलेक्ट करें (सिर्फ एक 4:5 फोटो)",
                                            fontSize = 11.sp,
                                            color = TextSecondary
                                        )
                                    }
                                }
                            } else {
                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color(0xFFF0FDF4),
                                    border = BorderStroke(1.dp, RathoreGreen)
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
                                                    .background(RathoreGreen, CircleShape),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                                            }
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Column {
                                                Text("✓ 4:5 फोटो चुनी गई", fontWeight = FontWeight.Bold, fontSize = 12.5.sp, color = TextPrimary)
                                                Text("ऊपर लाइव प्रीव्यू में दृश्यमान है", fontSize = 10.5.sp, color = RathoreGreen)
                                            }
                                        }

                                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                            FilledTonalButton(
                                                onClick = {
                                                    pracharPhotoPickerLauncher.launch(
                                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                                    )
                                                },
                                                shape = RoundedCornerShape(8.dp),
                                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                                colors = ButtonDefaults.filledTonalButtonColors(containerColor = Color(0xFFE2E8F0))
                                            ) {
                                                Text("फोटो बदलें", fontSize = 11.sp, color = TextPrimary)
                                            }

                                            Button(
                                                onClick = {
                                                    pracharPhotoUri = null
                                                    pracharPhotos = emptyList()
                                                },
                                                shape = RoundedCornerShape(8.dp),
                                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))
                                            ) {
                                                Text("हटाएं", fontSize = 11.sp, color = Color.White)
                                            }
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // 4. Client-Side Image Compression & Bandwidth Optimization Card
                            AdminImageCompressionCard(
                                currentPreset = uiState.selectedCompressionPreset,
                                onPresetSelected = { viewModel.setCompressionPreset(it) },
                                compressionResult = uiState.lastImageCompressionResult,
                                isCompressing = uiState.isCompressingAndUploadingImage,
                                uploadProgress = uiState.imageUploadProgress,
                                statusText = uiState.imageCompressionStatusText,
                                accentColor = RathoreGreen,
                                hasAttachedPhoto = pracharPhotoUri != null
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            // 5. Prachar Caption / Title box (optional)
                            Text("प्रचार शीर्षक / संदेश (फ़ीड के लिए)", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            OutlinedTextField(
                                value = pracharMatter,
                                onValueChange = { pracharMatter = it },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(72.dp),
                                placeholder = { Text("फ़ीड पर दिखने वाला संदेश (उदा. विकास की नई उड़ान)...") },
                                shape = RoundedCornerShape(8.dp)
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Publish to Home Feed Button
                            Button(
                                onClick = {
                                    if (pracharPhotoUri == null) {
                                        Toast.makeText(context, "कृपया पहले 4:5 प्रचार फोटो चुनें", Toast.LENGTH_SHORT).show()
                                        return@Button
                                    }
                                    val headline = pracharMatter.ifBlank { "आधिकारिक प्रचार • $pracharCategory" }
                                    viewModel.compressAndUploadAdminGraphic(
                                        context = context,
                                        imageUri = pracharPhotoUri,
                                        headline = headline,
                                        category = pracharCategory,
                                        photoCount = 1
                                    ) {
                                        pracharPublishedNotice = true
                                        Toast.makeText(context, "प्रचार फोटो होम फ़ीड में लाइव हो गई!", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                enabled = pracharPhotoUri != null && !uiState.isCompressingAndUploadingImage,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("btn_publish_prachar"),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = RathoreGreen)
                            ) {
                                if (uiState.isCompressingAndUploadingImage) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(18.dp),
                                        color = Color.White,
                                        strokeWidth = 2.dp
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("ऑप्टिमाइज़ व अपलोड हो रहा है...", fontWeight = FontWeight.Bold, color = Color.White)
                                } else {
                                    Icon(Icons.Default.CloudUpload, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("होम फ़ीड में प्रचार पब्लिश करें ✓", fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            }

                            if (pracharPublishedNotice) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFFDCFCE7),
                                    border = BorderStroke(1.dp, Color(0xFF86EFAC))
                                ) {
                                    Row(
                                        modifier = Modifier.padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF16A34A), modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("🎉 4:5 प्रचार फोटो होम फ़ीड में सफलतापूर्वक लाइव हो गई!", color = Color(0xFF15803D), fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }

                AdminUploadMode.UPDATE -> {
                    // Update / Daily Activity Upload:
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        color = Color.White,
                        border = BorderStroke(1.dp, BorderStroke)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFE3F2FD)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.DynamicFeed, contentDescription = null, tint = Color(0xFF1877F2), modifier = Modifier.size(20.dp))
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text("प्रतिदिन की अपडेट व गतिविधि अपलोड", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                                    Text("कार्यकर्ताओं द्वारा कॉपी व शेयर करने योग्य दैनिक गतिविधियां", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // 1. Category Selector (श्रेणी प्रबंधन)
                            Text("श्रेणी चुनें (Category)", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                categoryOptions.forEach { cat ->
                                    val isSelected = updateCategory == cat
                                    Surface(
                                        modifier = Modifier.clickable { updateCategory = cat },
                                        shape = RoundedCornerShape(16.dp),
                                        color = if (isSelected) Color(0xFF1877F2) else Color(0xFFF1F5F9),
                                        border = BorderStroke(1.dp, if (isSelected) Color(0xFF1877F2) else Color(0xFFCBD5E1))
                                    ) {
                                        Text(
                                            text = cat,
                                            fontSize = 11.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isSelected) Color.White else TextPrimary,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // 2. LIVE 4:5 UPDATE PREVIEW (AT THE TOP)
                            Text("लाइव अपडेट प्रीव्यू (4:5 अनुपात):", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Text("अपडेट में अपलोड होने वाली 4:5 फोटो का लाइव प्रीव्यू", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                            Spacer(modifier = Modifier.height(8.dp))

                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(4f / 5f)
                                    .clip(RoundedCornerShape(12.dp)),
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFF0F172A),
                                border = BorderStroke(1.5.dp, if (updatePhotoUri != null) Color(0xFF1877F2) else Color(0xFF334155))
                            ) {
                                if (updatePhotoUri != null) {
                                    Box(modifier = Modifier.fillMaxSize()) {
                                        AsyncImage(
                                            model = updatePhotoUri,
                                            contentDescription = "लाइव 4:5 अपडेट फोटो प्रीव्यू",
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Crop
                                        )
                                        // Bottom gradient overlay showing title & metadata
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .align(Alignment.BottomCenter)
                                                .background(
                                                    Brush.verticalGradient(
                                                        listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f))
                                                    )
                                                )
                                                .padding(14.dp)
                                        ) {
                                            Column {
                                                Surface(
                                                    color = Color(0xFF1877F2),
                                                    shape = RoundedCornerShape(4.dp)
                                                ) {
                                                    Text(
                                                        text = updateCategory,
                                                        color = Color.White,
                                                        fontSize = 10.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                    )
                                                }
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Text(
                                                    text = updateTitle.ifBlank { "दैनिक गतिविधि अपडेट" },
                                                    color = Color.White,
                                                    fontSize = 13.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    maxLines = 2
                                                )
                                            }
                                        }
                                    }
                                } else {
                                    Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            modifier = Modifier.padding(24.dp)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(56.dp)
                                                    .background(Color(0xFF1E293B), CircleShape),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    Icons.Default.DynamicFeed,
                                                    contentDescription = null,
                                                    tint = Color(0xFF94A3B8),
                                                    modifier = Modifier.size(32.dp)
                                                )
                                            }
                                            Spacer(modifier = Modifier.height(12.dp))
                                            Text(
                                                text = "4:5 अपडेट फोटो का लाइव प्रीव्यू",
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = "नीचे से गैलरी से 4:5 फोटो चुनें • फोटो यहाँ लाइव दिखेगी और फ़ीड में सीधे पब्लिश होगी",
                                                fontSize = 11.5.sp,
                                                color = Color(0xFF94A3B8),
                                                textAlign = TextAlign.Center,
                                                lineHeight = 16.sp
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // 3. Single Photo Upload Option Only (No collage count, no external editor)
                            Text("अपडेट फोटो चुनें (सिंगल फोटो - 4:5 अनुपात):", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(6.dp))

                            if (updatePhotoUri == null) {
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            updatePhotoPickerLauncher.launch(
                                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                            )
                                        },
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color(0xFFF8FAFC),
                                    border = BorderStroke(1.5.dp, Color(0xFF1877F2).copy(alpha = 0.5f))
                                ) {
                                    Column(
                                        modifier = Modifier.padding(20.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(44.dp)
                                                .background(Color(0xFF1877F2).copy(alpha = 0.12f), CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, tint = Color(0xFF1877F2), modifier = Modifier.size(24.dp))
                                        }
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = "गैलरी से 4:5 अपडेट फोटो चुनें",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = Color(0xFF1877F2)
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = "टैप करके गतिविधि की फोटो चुनें (सिर्फ एक फोटो)",
                                            fontSize = 11.sp,
                                            color = TextSecondary
                                        )
                                    }
                                }
                            } else {
                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color(0xFFF0FDF4),
                                    border = BorderStroke(1.dp, RathoreGreen)
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
                                                    .background(RathoreGreen, CircleShape),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                                            }
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Column {
                                                Text("✓ 4:5 अपडेट फोटो चुनी गई", fontWeight = FontWeight.Bold, fontSize = 12.5.sp, color = TextPrimary)
                                                Text("ऊपर लाइव प्रीव्यू में सक्रिय है", fontSize = 10.5.sp, color = RathoreGreen)
                                            }
                                        }

                                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                            FilledTonalButton(
                                                onClick = {
                                                    updatePhotoPickerLauncher.launch(
                                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                                    )
                                                },
                                                shape = RoundedCornerShape(8.dp),
                                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                                colors = ButtonDefaults.filledTonalButtonColors(containerColor = Color(0xFFE2E8F0))
                                            ) {
                                                Text("फोटो बदलें", fontSize = 11.sp, color = TextPrimary)
                                            }

                                            Button(
                                                onClick = {
                                                    updatePhotoUri = null
                                                    updatePhotos = emptyList()
                                                },
                                                shape = RoundedCornerShape(8.dp),
                                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))
                                            ) {
                                                Text("हटाएं", fontSize = 11.sp, color = Color.White)
                                            }
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // 4. Client-Side Image Compression & Bandwidth Optimization Card
                            AdminImageCompressionCard(
                                currentPreset = uiState.selectedCompressionPreset,
                                onPresetSelected = { viewModel.setCompressionPreset(it) },
                                compressionResult = uiState.lastImageCompressionResult,
                                isCompressing = uiState.isCompressingAndUploadingImage,
                                uploadProgress = uiState.imageUploadProgress,
                                statusText = uiState.imageCompressionStatusText,
                                accentColor = Color(0xFF1877F2),
                                hasAttachedPhoto = updatePhotoUri != null || updatePhotos.isNotEmpty()
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            // 5. Title
                            Text("गतिविधि का मुख्य शीर्षक", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            OutlinedTextField(
                                value = updateTitle,
                                onValueChange = { updateTitle = it },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = { Text("उदा. ग्राम पिपरोली में जनसंवाद चौपाल...") },
                                shape = RoundedCornerShape(8.dp)
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // 6. Detailed Matter
                            Text("दिनभर की गतिविधियों का विस्तृत विवरण (Matter)", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            OutlinedTextField(
                                value = updateContent,
                                onValueChange = { updateContent = it },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(95.dp),
                                placeholder = { Text("विस्तृत विवरण लिखें जिसे समर्थक सीधे कॉपी व शेयर कर सकें...") },
                                shape = RoundedCornerShape(8.dp)
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // 7. Source URL
                            Text("सोशल / फेसबुक स्रोत लिंक (वैकल्पिक)", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            OutlinedTextField(
                                value = updateUrl,
                                onValueChange = { updateUrl = it },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = { Text("https://facebook.com/teamrathore/posts/...") },
                                shape = RoundedCornerShape(8.dp),
                                leadingIcon = {
                                    Icon(Icons.Default.Link, contentDescription = null, tint = Color(0xFF1877F2))
                                }
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = {
                                    if (updateContent.isNotBlank()) {
                                        viewModel.compressAndUploadDailyUpdate(
                                            context = context,
                                            imageUri = updatePhotoUri,
                                            title = updateTitle.ifBlank { "दैनिक गतिविधि अपडेट" },
                                            content = updateContent,
                                            category = updateCategory,
                                            photoCount = 1,
                                            sourceUrl = updateUrl
                                        ) {
                                            updatePublishedNotice = true
                                        }
                                    }
                                },
                                enabled = !uiState.isCompressingAndUploadingImage,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("btn_publish_update"),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1877F2))
                            ) {
                                if (uiState.isCompressingAndUploadingImage) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(18.dp),
                                        color = Color.White,
                                        strokeWidth = 2.dp
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("ऑप्टिमाइज़ व अपलोड हो रहा है...", fontWeight = FontWeight.Bold, color = Color.White)
                                } else {
                                    Icon(Icons.Default.Send, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("प्रतिदिन की अपडेट फ़ीड में पब्लिश करें ✓", fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            }

                            if (updatePublishedNotice) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFFDCFCE7),
                                    border = BorderStroke(1.dp, Color(0xFF86EFAC))
                                ) {
                                    Row(
                                        modifier = Modifier.padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF16A34A), modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("दैनिक अपडेट लाइव हो गई! कार्यकर्ता इसे अपनी जैकेट व नाम के साथ उपयोग कर सकेंगे।", color = Color(0xFF15803D), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }

                AdminUploadMode.VIDEO -> {
                    // Video Content Upload Tab with Aspect Ratio & Jacket Selector
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        color = Color.White,
                        border = BorderStroke(1.dp, BorderStroke)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFF3E8FF)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Videocam, contentDescription = null, tint = Color(0xFF9333EA), modifier = Modifier.size(20.dp))
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text("वीडियो अपलोड एवं जैकेट प्रबंधन", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                                    Text("अनुपात (Ratio), जैकेट टेम्प्लेट व मल्टीपल टैग", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // 1. TOP ASPECT RATIO SELECTOR (9/16, 16/9, 1/1, 4/5)
                            Text("1. वीडियो अनुपात चुनें*", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = Color(0xFF9333EA))
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                listOf(
                                    "9:16" to "9/16\n(रील्स/शॉर्ट्स)",
                                    "16:9" to "16/9\n(लैंडस्केप)",
                                    "1:1" to "1/1\n(स्क्वायर)",
                                    "4/5" to "4/5\n(वर्टिकल)"
                                ).forEach { (ratioKey, ratioLabel) ->
                                    val isSelected = videoAspectRatio == ratioKey
                                    Surface(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable { videoAspectRatio = ratioKey },
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (isSelected) Color(0xFF9333EA) else Color(0xFFFAF5FF),
                                        border = BorderStroke(1.5.dp, if (isSelected) Color(0xFF9333EA) else Color(0xFFE9D5FF))
                                    ) {
                                        Text(
                                            text = ratioLabel,
                                            fontSize = 10.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isSelected) Color.White else Color(0xFF6B21A8),
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.padding(vertical = 8.dp, horizontal = 2.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // 2. JACKET / FRAME SELECTION & ADD JACKET BUTTON
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("2. हेडर-फुटर जैकेट टेम्प्लेट*", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                                TextButton(
                                    onClick = { showAddJacketDialog = true },
                                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null, tint = Color(0xFF9333EA), modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Text("+ नई जैकेट जोड़ें", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF9333EA))
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                uiState.videoJackets.forEach { jacket ->
                                    val isSelected = selectedJacketTemplate == jacket.name
                                    Surface(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { selectedJacketTemplate = jacket.name },
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (isSelected) Color(0xFFFAF5FF) else Color(0xFFF8FAFC),
                                        border = BorderStroke(1.dp, if (isSelected) Color(0xFF9333EA) else Color(0xFFE2E8F0))
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Row(
                                                modifier = Modifier.weight(1f),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                RadioButton(
                                                    selected = isSelected,
                                                    onClick = { selectedJacketTemplate = jacket.name },
                                                    colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF9333EA))
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Column {
                                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                                        Box(
                                                            modifier = Modifier
                                                                .size(10.dp)
                                                                .clip(CircleShape)
                                                                .background(Color(jacket.themeColorHex))
                                                        )
                                                        Spacer(modifier = Modifier.width(6.dp))
                                                        Text(jacket.name, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium)
                                                    }
                                                    Text(
                                                        text = "शीर्ष: ${jacket.headerText.take(28)}... | पाद: ${jacket.footerText.take(24)}...",
                                                        fontSize = 9.sp,
                                                        color = TextSecondary,
                                                        maxLines = 1
                                                    )
                                                }
                                            }

                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Surface(
                                                    modifier = Modifier.clickable {
                                                        editingJacket = jacket
                                                        editJacketName = jacket.name
                                                        editJacketHeaderText = jacket.headerText
                                                        editJacketFooterText = jacket.footerText
                                                        editJacketColorHex = jacket.themeColorHex
                                                    },
                                                    shape = RoundedCornerShape(6.dp),
                                                    color = Color(0xFFEDE9FE),
                                                    border = BorderStroke(1.dp, Color(0xFFDDD6FE))
                                                ) {
                                                    Row(
                                                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp),
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        Icon(Icons.Default.Edit, contentDescription = "संपादित करें", tint = Color(0xFF7C3AED), modifier = Modifier.size(12.dp))
                                                        Spacer(modifier = Modifier.width(3.dp))
                                                        Text("संपादित करें", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF7C3AED))
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // 3. VIDEO FILE SELECTION & 10x COMPRESSION
                            Text("3. वीडियो फ़ाइल एवं 10x कम्प्रेशन*", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = Color(0xFF9333EA))
                            Spacer(modifier = Modifier.height(6.dp))
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFFFAF5FF),
                                border = BorderStroke(1.dp, Color(0xFFD8B4FE))
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.VideoLibrary, contentDescription = null, tint = Color(0xFF9333EA), modifier = Modifier.size(24.dp))
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Column {
                                                Text(selectedVideoFileName, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TextPrimary)
                                                Text("MP4 • 1080p HD • $videoAspectRatio अनुपात • अवधि $videoDuration", fontSize = 10.sp, color = TextSecondary)
                                            }
                                        }
                                        TextButton(
                                            onClick = {
                                                videoPickerLauncher.launch(
                                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)
                                                )
                                            },
                                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                if (selectedVideoUri != null) "वीडियो बदलें" else "गैलरी से वीडियो चुनें",
                                                fontSize = 11.sp,
                                                color = Color(0xFF9333EA),
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    // Video 10x Compression State UI
                                    if (uiState.videoCompressionProgress != null) {
                                        Surface(
                                            modifier = Modifier.fillMaxWidth(),
                                            shape = RoundedCornerShape(8.dp),
                                            color = Color.White,
                                            border = BorderStroke(1.dp, Color(0xFFC084FC))
                                        ) {
                                            Column(modifier = Modifier.padding(10.dp)) {
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween
                                                ) {
                                                    Text("⚡ 10x वीडियो कम्प्रेशन प्रगति...", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF9333EA))
                                                    Text("${uiState.videoCompressionProgress}%", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF9333EA))
                                                }
                                                Spacer(modifier = Modifier.height(6.dp))
                                                LinearProgressIndicator(
                                                    progress = { (uiState.videoCompressionProgress ?: 0) / 100f },
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .height(6.dp)
                                                        .clip(RoundedCornerShape(3.dp)),
                                                    color = Color(0xFF9333EA),
                                                    trackColor = Color(0xFFF3E8FF)
                                                )
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Text("1024 MB → ~98 MB (डेटा बचत व WhatsApp अनुकूलन जारी)", fontSize = 9.sp, color = TextSecondary)
                                            }
                                        }
                                    } else if (uiState.isVideoCompressed) {
                                        Surface(
                                            modifier = Modifier.fillMaxWidth(),
                                            shape = RoundedCornerShape(8.dp),
                                            color = Color(0xFFDCFCE7),
                                            border = BorderStroke(1.dp, Color(0xFF86EFAC))
                                        ) {
                                            Column(modifier = Modifier.padding(10.dp)) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF16A34A), modifier = Modifier.size(16.dp))
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Text("✓ वीडियो 10x कंप्रेस हो चुका है!", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF15803D))
                                                }
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Text(
                                                    text = uiState.compressedVideoInfo ?: "मूल: 1024 MB → कंप्रेस्ड: 98 MB (90% डेटा बचत • WhatsApp अनुकूल)",
                                                    fontSize = 10.sp,
                                                    color = Color(0xFF166534)
                                                )
                                                Spacer(modifier = Modifier.height(6.dp))
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.End
                                                ) {
                                                    TextButton(
                                                        onClick = { viewModel.resetVideoCompression() },
                                                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                                                    ) {
                                                        Text("कम्प्रेशन रीसेट करें", fontSize = 10.sp, color = Color(0xFF15803D), fontWeight = FontWeight.Bold)
                                                    }
                                                }
                                            }
                                        }
                                    } else {
                                        Surface(
                                            modifier = Modifier.fillMaxWidth(),
                                            shape = RoundedCornerShape(8.dp),
                                            color = Color.White,
                                            border = BorderStroke(1.dp, Color(0xFFE9D5FF))
                                        ) {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(10.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Column(modifier = Modifier.weight(1f)) {
                                                    Text("वीडियो आकार: 1024 MB (1.0 GB)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                                    Text("धीमे इंटरनेट व WhatsApp पर भेजने के लिए 10x कंप्रेस करें", fontSize = 9.sp, color = TextSecondary)
                                                }
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Button(
                                                    onClick = { viewModel.compressVideo(1024) },
                                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9333EA)),
                                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                                    shape = RoundedCornerShape(8.dp)
                                                ) {
                                                    Icon(Icons.Default.Bolt, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                                    Spacer(modifier = Modifier.width(3.dp))
                                                    Text("10x कंप्रेस", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Text("वीडियो शीर्षक", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            OutlinedTextField(
                                value = videoTitle,
                                onValueChange = { videoTitle = it },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = { Text("उदा. निवाड़ी विकास संकल्प सभा का मुख्य अंश...") },
                                shape = RoundedCornerShape(8.dp)
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Text("वीडियो विवरण / कैप्शन", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            OutlinedTextField(
                                value = videoCaption,
                                onValueChange = { videoCaption = it },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(80.dp),
                                placeholder = { Text("वीडियो का संक्षिप्त विवरण व मुख्य बिंदु लिखें...") },
                                shape = RoundedCornerShape(8.dp)
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // 4. MULTIPLE CATEGORY TAGS (MAX 3)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("मल्टीपल कैटेगरी टैग्स (अधिकतम 3 चुनें)", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                                Text("${selectedVideoTags.size}/3 चयनित", fontSize = 11.sp, color = Color(0xFF9333EA), fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            FlowRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                allVideoTags.forEach { tag ->
                                    val isSelected = selectedVideoTags.contains(tag)
                                    Surface(
                                        modifier = Modifier.clickable {
                                            if (isSelected) {
                                                selectedVideoTags = selectedVideoTags - tag
                                            } else if (selectedVideoTags.size < 3) {
                                                selectedVideoTags = selectedVideoTags + tag
                                            }
                                        },
                                        shape = RoundedCornerShape(16.dp),
                                        color = if (isSelected) Color(0xFF9333EA) else Color(0xFFFAF5FF),
                                        border = BorderStroke(1.dp, if (isSelected) Color(0xFF9333EA) else Color(0xFFD8B4FE))
                                    ) {
                                        Text(
                                            text = if (isSelected) "✓ $tag" else "+ $tag",
                                            fontSize = 11.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSelected) Color.White else Color(0xFF6B21A8),
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Requires Jacket Toggle
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { videoRequiresJacket = !videoRequiresJacket }
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("जैकेट अनिवार्य", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    Text("कार्यकर्ताओं को साझा करते समय उनका फोटो व पद फ्रेम में जुड़ेगा", fontSize = 10.sp, color = TextSecondary)
                                }
                                Switch(
                                    checked = videoRequiresJacket,
                                    onCheckedChange = { videoRequiresJacket = it },
                                    colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = Color(0xFF9333EA))
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = {
                                    if (videoTitle.isNotBlank()) {
                                        viewModel.addAdminVideo(
                                            title = videoTitle,
                                            caption = videoCaption.ifBlank { videoTitle },
                                            aspectRatio = videoAspectRatio,
                                            jacketTemplateName = selectedJacketTemplate,
                                            categories = selectedVideoTags.toList(),
                                            duration = videoDuration,
                                            requiresJacket = videoRequiresJacket,
                                            videoUri = selectedVideoUri?.toString()
                                        )
                                        videoPublishedNotice = true
                                        videoTitle = ""
                                        videoCaption = ""
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("btn_publish_video"),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9333EA))
                            ) {
                                Icon(Icons.Default.CloudUpload, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("वीडियो ($videoAspectRatio) जैकेट सहित प्रकाशित करें ✓", fontWeight = FontWeight.Bold, color = Color.White)
                            }

                            if (videoPublishedNotice) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("वीडियो सफलतापूर्वक $videoAspectRatio अनुपात व जैकेट के साथ पब्लिश हो गया!", color = Color(0xFF15803D), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }
                }

                // ==================== MODE 4: सूचना (NOTICE & BROADCAST) ====================
                AdminUploadMode.NOTICE -> {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        color = Color.White,
                        border = BorderStroke(1.2.dp, RathoreOrangeDark.copy(alpha = 0.4f)),
                        shadowElevation = 2.dp
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFFFF3E0)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Campaign, contentDescription = null, tint = RathoreOrangeDark, modifier = Modifier.size(18.dp))
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text("आधिकारिक सूचना प्रसारण", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextPrimary)
                                    Text("सभी कार्यकर्ताओं व समर्थकों को तत्काल अलर्ट भेजें", fontSize = 11.sp, color = TextSecondary)
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Text("सूचना का शीर्षक *", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            OutlinedTextField(
                                value = noticeTitle,
                                onValueChange = { noticeTitle = it },
                                placeholder = { Text("उदा. 15 सितंबर को विशाल कार्यकर्ता सम्मेलन...") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_notice_title"),
                                shape = RoundedCornerShape(8.dp),
                                singleLine = true
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Text("सूचना का विस्तृत विवरण / संदेश *", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            OutlinedTextField(
                                value = noticeBody,
                                onValueChange = { noticeBody = it },
                                placeholder = { Text("विस्तृत सूचना, दिशा-निर्देश एवं समय सारणी लिखें...") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(110.dp)
                                    .testTag("input_notice_body"),
                                shape = RoundedCornerShape(8.dp)
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Urgent / Flash Alert Checkbox
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (noticeIsUrgent) Color(0xFFFEE2E2) else Color(0xFFF8FAFC),
                                border = BorderStroke(1.dp, if (noticeIsUrgent) Color(0xFFDC2626) else Color(0xFFCBD5E1)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { noticeIsUrgent = !noticeIsUrgent }
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Checkbox(
                                        checked = noticeIsUrgent,
                                        onCheckedChange = { noticeIsUrgent = it },
                                        colors = CheckboxDefaults.colors(checkedColor = Color(0xFFDC2626))
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Column {
                                        Text("🚨 अति-महत्वपूर्ण फ्लैश अलर्ट", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = if (noticeIsUrgent) Color(0xFFDC2626) else TextPrimary)
                                        Text("ऐप में सबसे ऊपर लाल रंग की प्राथमिकता पट्टी में दिखेगा", fontSize = 10.sp, color = TextSecondary)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // 3-4 Photo Upload Mechanism for Official Letter / Order
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFFF8FAFC),
                                border = BorderStroke(1.dp, if (noticePhotos.isNotEmpty()) RathoreGreen else Color(0xFFCBD5E1)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.AttachFile, contentDescription = null, tint = RathoreGreen, modifier = Modifier.size(18.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "आधिकारिक पत्र / विज्ञप्ति प्रति (${noticePhotos.size}/4 संलग्न)",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp,
                                                color = TextPrimary
                                            )
                                        }
                                        if (noticePhotos.size < 4) {
                                            TextButton(
                                                onClick = {
                                                    noticePhotos = noticePhotos + "aadesh_patra_pg${noticePhotos.size + 1}.jpg"
                                                },
                                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                            ) {
                                                Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, tint = RathoreGreen, modifier = Modifier.size(16.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("+ पत्र जोड़ें", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = RathoreGreen)
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    if (noticePhotos.isEmpty()) {
                                        Surface(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(80.dp)
                                                .clickable {
                                                    noticePhotos = listOf("aadesh_patra_pg1.jpg")
                                                },
                                            shape = RoundedCornerShape(8.dp),
                                            color = Color.White,
                                            border = BorderStroke(1.dp, Color(0xFF94A3B8))
                                        ) {
                                            Column(
                                                modifier = Modifier.fillMaxSize(),
                                                verticalArrangement = Arrangement.Center,
                                                horizontalAlignment = Alignment.CenterHorizontally
                                            ) {
                                                Icon(Icons.Default.CloudUpload, contentDescription = null, tint = RathoreGreen, modifier = Modifier.size(24.dp))
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Text("+ गैलरी / फ़ाइल से सरकारी आदेश या विज्ञप्ति अपलोड करें (अधिकतम 4 फोटो)", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                                Text("PNG, JPG, PDF स्कैन प्रति समर्थित", fontSize = 9.sp, color = TextSecondary)
                                            }
                                        }
                                    } else {
                                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                            noticePhotos.forEachIndexed { index, fileName ->
                                                Surface(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    shape = RoundedCornerShape(8.dp),
                                                    color = Color.White,
                                                    border = BorderStroke(1.dp, Color(0xFFBBF7D0))
                                                ) {
                                                    Row(
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .padding(horizontal = 10.dp, vertical = 6.dp),
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        horizontalArrangement = Arrangement.SpaceBetween
                                                    ) {
                                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                                            Box(
                                                                modifier = Modifier
                                                                    .size(26.dp)
                                                                    .clip(CircleShape)
                                                                    .background(Color(0xFFDCFCE7)),
                                                                contentAlignment = Alignment.Center
                                                            ) {
                                                                Text("${index + 1}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = RathoreGreen)
                                                            }
                                                            Spacer(modifier = Modifier.width(8.dp))
                                                            Column {
                                                                Text(fileName, fontSize = 11.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
                                                                Text("पृष्ठ ${index + 1} • आदेश पत्र स्कैन प्रति", fontSize = 9.sp, color = TextSecondary)
                                                            }
                                                        }
                                                        IconButton(
                                                            onClick = {
                                                                noticePhotos = noticePhotos.filterIndexed { i, _ -> i != index }
                                                            },
                                                            modifier = Modifier.size(26.dp)
                                                        ) {
                                                            Icon(Icons.Default.Close, contentDescription = "हटाएं", tint = Color(0xFFEF4444), modifier = Modifier.size(16.dp))
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = {
                                    if (noticeTitle.isNotBlank()) {
                                        viewModel.broadcastNotice(
                                            title = noticeTitle,
                                            message = noticeBody.ifBlank { noticeTitle },
                                            isUrgent = noticeIsUrgent,
                                            photoAttached = noticePhotos.isNotEmpty(),
                                            photoTitle = if (noticePhotos.isNotEmpty()) "${noticePhotos.size} आधिकारिक पत्र संलग्न" else null,
                                            photos = noticePhotos
                                        )
                                        noticeTitle = ""
                                        noticeBody = ""
                                        noticeIsUrgent = false
                                        noticePhotos = emptyList()
                                        noticePublishedNotice = true
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("btn_publish_notice"),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = RathoreOrangeDark)
                            ) {
                                Icon(Icons.Default.Send, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("सूचना पूरे नेटवर्क में प्रसारित करें ✓", fontWeight = FontWeight.Bold, color = Color.White)
                            }

                            if (noticePublishedNotice) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("सूचना सफलतापूर्वक प्रसारित कर दी गई है!", color = Color(0xFF15803D), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }
                }

                AdminUploadMode.USERS -> {
                    // 👥 Google Sheets Integrated Database & User Hub
                    GoogleSheetsDatabaseHub(
                        viewModel = viewModel,
                        syncStatus = uiState.googleSheetSyncStatus,
                        users = uiState.managedUsers,
                        onSyncRequest = {
                            viewModel.syncUserDatabaseWithGoogleSheets()
                        },
                        onRoleChange = { userId, newRole ->
                            viewModel.updateUserRole(userId, newRole)
                        },
                        onToggleBlock = { userId ->
                            viewModel.toggleUserBlock(userId)
                        },
                        onAddNewUser = { name, phone, area, role, designation, boothNo ->
                            viewModel.addNewManagedUser(name, phone, area, role, designation, boothNo)
                        }
                    )
                }
            }
        }

        // Add Custom Jacket Dialog
        if (showAddJacketDialog) {
            AlertDialog(
                onDismissRequest = { showAddJacketDialog = false },
                title = { Text("नई वीडियो जैकेट / हेडर-फुटर जोड़ें", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("जैकेट का नाम और फ्रेम विवरण लिखें ($videoAspectRatio अनुपात हेतु):", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                        OutlinedTextField(
                            value = newJacketInputName,
                            onValueChange = { newJacketInputName = it },
                            placeholder = { Text("उदा. निवाड़ी जनआशीर्वाद विशेष फ्रेम") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (newJacketInputName.isNotBlank()) {
                                val trimmed = newJacketInputName.trim()
                                availableJackets = availableJackets + trimmed
                                selectedJacketTemplate = trimmed
                                viewModel.addVideoJacket(
                                    name = trimmed,
                                    headerText = "हेमंत राठौर • जनसेवा ही संकल्प",
                                    footerText = "भारतीय राष्ट्रीय कांग्रेस • टीम राठौर",
                                    colorHex = 0xFF9333EA
                                )
                                showAddJacketDialog = false
                                newJacketInputName = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9333EA))
                    ) {
                        Text("जैकेट जोड़ें")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddJacketDialog = false }) {
                        Text("रद्द करें")
                    }
                }
            )
        }

        // Edit Existing Jacket Dialog
        if (editingJacket != null) {
            val jacket = editingJacket!!
            AlertDialog(
                onDismissRequest = { editingJacket = null },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Edit, contentDescription = null, tint = Color(0xFF9333EA), modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("जैकेट टेम्प्लेट संपादित करें", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                    }
                },
                text = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text("जैकेट शीर्षक / नाम", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        OutlinedTextField(
                            value = editJacketName,
                            onValueChange = { editJacketName = it },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        Text("हेडर मैटर (शीर्ष संदेश)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        OutlinedTextField(
                            value = editJacketHeaderText,
                            onValueChange = { editJacketHeaderText = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("शीर्ष संदेश लिखें...") }
                        )

                        Text("फुटर मैटर (निचला संदेश / पद)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        OutlinedTextField(
                            value = editJacketFooterText,
                            onValueChange = { editJacketFooterText = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("निचला संदेश या पद लिखें...") }
                        )

                        Text("थीम रंग चुनें", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            listOf(
                                0xFF9333EA to "बैंगनी",
                                0xFF1877F2 to "नीला",
                                0xFFE11D48 to "लाल",
                                0xFF0D9488 to "टील",
                                0xFFF97316 to "भगवा",
                                0xFF16A34A to "हरा"
                            ).forEach { (hex, name) ->
                                val isColorSelected = editJacketColorHex == hex
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(Color(hex))
                                        .clickable { editJacketColorHex = hex }
                                        .then(
                                            if (isColorSelected) Modifier.border(2.5.dp, Color.Black, CircleShape) else Modifier
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isColorSelected) {
                                        Icon(Icons.Default.Check, contentDescription = name, tint = Color.White, modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text("लाइव जैकेट पूर्वावलोकन:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp),
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF1E293B)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                // Top Header Bar
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color(editJacketColorHex))
                                        .padding(horizontal = 8.dp, vertical = 4.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = editJacketHeaderText.ifBlank { "हेडर संदेश" },
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        maxLines = 1
                                    )
                                }

                                // Middle Video Area
                                Box(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.PlayCircle, contentDescription = null, tint = Color.White.copy(alpha = 0.5f), modifier = Modifier.size(32.dp))
                                }

                                // Bottom Footer Bar
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color(editJacketColorHex))
                                        .padding(horizontal = 8.dp, vertical = 4.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = editJacketFooterText.ifBlank { "फुटर संदेश" },
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color.White,
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.updateVideoJacket(
                                id = jacket.id,
                                newName = editJacketName.ifBlank { jacket.name },
                                newHeaderText = editJacketHeaderText,
                                newFooterText = editJacketFooterText,
                                newColorHex = editJacketColorHex
                            )
                            if (selectedJacketTemplate == jacket.name) {
                                selectedJacketTemplate = editJacketName.ifBlank { jacket.name }
                            }
                            editingJacket = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9333EA))
                    ) {
                        Text("सहेजें")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { editingJacket = null }) {
                        Text("रद्द करें")
                    }
                }
            )
        }

        // Dedicated Category Management Dialog (Opened from the Category card at top)
        if (showCategoryManagerDialog) {
            AlertDialog(
                onDismissRequest = { showCategoryManagerDialog = false },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFEDE9FE)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Category, contentDescription = null, tint = Color(0xFF7C3AED), modifier = Modifier.size(18.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("श्रेणी प्रबंधन", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                    }
                },
                text = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 440.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            "होम स्क्रीन एवं फ़ीड श्रेणियों को यहाँ जोड़ें, नाम बदलें या हटाएं:",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )

                        // Add new category field
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFF8FAFC),
                            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                OutlinedTextField(
                                    value = newCategoryName,
                                    onValueChange = { newCategoryName = it },
                                    placeholder = { Text("नई श्रेणी...", fontSize = 12.sp) },
                                    modifier = Modifier.weight(1f),
                                    singleLine = true,
                                    shape = RoundedCornerShape(6.dp)
                                )
                                Button(
                                    onClick = {
                                        if (newCategoryName.isNotBlank()) {
                                            viewModel.addCategory(newCategoryName)
                                            newCategoryName = ""
                                        }
                                    },
                                    shape = RoundedCornerShape(6.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C3AED)),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text("+ जोड़ें", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        Divider(color = BorderStroke, thickness = 0.8.dp)

                        // List of categories
                        categoryOptions.forEachIndexed { index, cat ->
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFFF8FAFC),
                                border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(
                                            text = "${index + 1}.",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TextSecondary
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = cat,
                                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                                            color = TextPrimary
                                        )
                                    }

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        IconButton(
                                            onClick = {
                                                editingCategory = cat
                                                editCategoryNewName = cat
                                            },
                                            modifier = Modifier.size(28.dp)
                                        ) {
                                            Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color(0xFF2563EB), modifier = Modifier.size(15.dp))
                                        }
                                        IconButton(
                                            onClick = { showDeleteConfirmDialog = cat },
                                            modifier = Modifier.size(28.dp)
                                        ) {
                                            Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = Color(0xFFDC2626), modifier = Modifier.size(15.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { showCategoryManagerDialog = false },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C3AED))
                    ) {
                        Text("संपन्न")
                    }
                }
            )
        }

        // Category Edit Dialog
        if (editingCategory != null) {
            AlertDialog(
                onDismissRequest = { editingCategory = null },
                title = { Text("श्रेणी का नाम बदलें", fontWeight = FontWeight.Bold) },
                text = {
                    Column {
                        Text("श्रेणी का नया नाम दर्ज करें:", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = editCategoryNewName,
                            onValueChange = { editCategoryNewName = it },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (editCategoryNewName.isNotBlank() && editingCategory != null) {
                                viewModel.updateCategory(editingCategory!!, editCategoryNewName)
                                editingCategory = null
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = RathoreGreen)
                    ) {
                        Text("सहेजें")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { editingCategory = null }) {
                        Text("रद्द करें")
                    }
                }
            )
        }

        // Category Delete Confirmation Dialog
        if (showDeleteConfirmDialog != null) {
            AlertDialog(
                onDismissRequest = { showDeleteConfirmDialog = null },
                title = { Text("श्रेणी हटाएं?", fontWeight = FontWeight.Bold, color = Color(0xFFDC2626)) },
                text = {
                    Text("क्या आप श्रेणी '$showDeleteConfirmDialog' को हटाना चाहते हैं? यह होम स्क्रीन और सभी फ़ीड सूचियों से हट जाएगी।")
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showDeleteConfirmDialog?.let { viewModel.deleteCategory(it) }
                            showDeleteConfirmDialog = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
                    ) {
                        Text("हटाएं", color = Color.White)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteConfirmDialog = null }) {
                        Text("रद्द करें")
                    }
                }
            )
        }

        // Interactive Media & Collage Editor Dialog (Supports Device Gallery & Templates)
        if (showImageEditorDialog) {
            val isPrachar = imageEditorTargetMode == AdminUploadMode.PRACHAR
            AdminImageAndCollageEditorDialog(
                isOpen = showImageEditorDialog,
                onDismiss = { showImageEditorDialog = false },
                initialPhotos = if (isPrachar) pracharPhotos else updatePhotos,
                initialCollageCount = if (isPrachar) pracharCollageCount else updateCollageCount,
                initialHeadline = if (isPrachar) pracharMatter else updateTitle,
                onApply = { photos, count, headline, template ->
                    if (isPrachar) {
                        pracharPhotos = photos
                        pracharCollageCount = count
                        if (headline.isNotBlank()) pracharMatter = headline
                    } else {
                        updatePhotos = photos
                        updateCollageCount = count
                        if (headline.isNotBlank()) updateTitle = headline
                    }
                }
            )
        }

        // Interactive Admin Frame Creator Studio Dialog (Upload clean Header & Footer images)
        if (showFrameCreatorStudioDialog) {
            AdminFrameCreatorStudioDialog(
                isOpen = showFrameCreatorStudioDialog,
                onDismiss = { showFrameCreatorStudioDialog = false },
                viewModel = viewModel
            )
        }

        // Interactive Video Jacket & Ratio Editor Dialog (Supports Gallery, Ratio & Jackets)
        if (showVideoEditorDialog) {
            AdminVideoEditorDialog(
                isOpen = showVideoEditorDialog,
                onDismiss = { showVideoEditorDialog = false },
                initialVideoName = selectedVideoFileName,
                initialTitle = videoTitle,
                initialCaption = videoCaption,
                initialAspectRatio = videoAspectRatio,
                initialJacketName = selectedJacketTemplate,
                onApply = { title, caption, aspect, jacket, file, duration ->
                    videoTitle = title
                    videoCaption = caption
                    videoAspectRatio = aspect
                    selectedJacketTemplate = jacket
                    selectedVideoFileName = file
                    videoDuration = duration
                }
            )
        }

        // Actionable Dialog: Scheduled Meetings & Programs Overview
        if (showScheduledMeetingsDialog) {
            AlertDialog(
                onDismissRequest = { showScheduledMeetingsDialog = false },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(RathoreOrange.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.EventAvailable, contentDescription = null, tint = RathoreOrangeDark, modifier = Modifier.size(18.dp))
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("शेड्यूल्ड मीटिंग्स एवं कार्यक्रम", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                },
                text = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 380.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Current Live Meeting Section
                        val isMeetingActive = uiState.meetingInfo.isScheduled && uiState.meetingInfo.status != MeetingStatus.ENDED
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isMeetingActive) Color(0xFFFFFBEB) else Color(0xFFF8FAFC),
                            border = BorderStroke(1.dp, if (isMeetingActive) Color(0xFFFDE68A) else Color(0xFFE2E8F0)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "लाइव मीटिंग स्थिति",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isMeetingActive) RathoreOrangeDark else TextSecondary
                                    )
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = if (isMeetingActive) Color(0xFF22C55E) else Color(0xFF94A3B8)
                                    ) {
                                        Text(
                                            text = if (isMeetingActive) "शेड्यूल सक्रिय ✓" else "कोई बैठक शेड्यूल नहीं",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                if (isMeetingActive) {
                                    Text(
                                        text = uiState.meetingInfo.titleHi,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = "समय: ${uiState.meetingInfo.timeHi} • होस्ट: ${uiState.meetingInfo.hostHi}",
                                        fontSize = 11.sp,
                                        color = TextSecondary
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Button(
                                        onClick = {
                                            showScheduledMeetingsDialog = false
                                            viewModel.navigateTo(AppScreen.Main)
                                            viewModel.selectTab(com.example.viewmodel.MainTab.WAR_ROOM)
                                            viewModel.startMeetingFlow()
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = RathoreOrangeDark),
                                        contentPadding = PaddingValues(vertical = 8.dp)
                                    ) {
                                        Icon(Icons.Default.VideoCall, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("लाइव मीटिंग शुरू करें", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                } else {
                                    Text(
                                        text = "वर्तमान में कोई वर्चुअल बैठक शेड्यूल नहीं है।",
                                        fontSize = 11.5.sp,
                                        color = TextSecondary
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    OutlinedButton(
                                        onClick = {
                                            showScheduledMeetingsDialog = false
                                            viewModel.navigateTo(AppScreen.Main)
                                            viewModel.selectTab(com.example.viewmodel.MainTab.WAR_ROOM)
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(8.dp),
                                        border = BorderStroke(1.dp, Color(0xFF0066DA)),
                                        contentPadding = PaddingValues(vertical = 6.dp)
                                    ) {
                                        Text("वॉर रूम में नई Google Meet शेड्यूल करें", fontSize = 11.sp, color = Color(0xFF0066DA), fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }

                        // Programs List
                        Text(
                            text = "आगामी संगठनात्मक कार्यक्रम (${uiState.programs.size}):",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )

                        uiState.programs.forEach { prog ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFFF8FAFC),
                                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text(text = prog.titleHi, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TextPrimary)
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(text = "दिनांक: ${prog.dateHi} • ${prog.timeHi}", fontSize = 10.5.sp, color = RathoreGreen, fontWeight = FontWeight.SemiBold)
                                    Text(text = "स्थान: ${prog.venueHi}", fontSize = 10.sp, color = TextSecondary)
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showScheduledMeetingsDialog = false
                            viewModel.navigateTo(AppScreen.Main)
                            viewModel.selectTab(com.example.viewmodel.MainTab.WAR_ROOM)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = RathoreGreen)
                    ) {
                        Text("वॉर रूम में जाएं ✓")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showScheduledMeetingsDialog = false }) {
                        Text("बंद करें")
                    }
                }
            )
        }

        // Actionable Dialog: Pending Tasks & Verifications Overview
        if (showPendingTasksDialog) {
            val pendingTasks = uiState.tasks.filter { it.status != TaskStatus.COMPLETED }
            AlertDialog(
                onDismissRequest = { showPendingTasksDialog = false },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFDC2626).copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.AssignmentLate, contentDescription = null, tint = Color(0xFFDC2626), modifier = Modifier.size(18.dp))
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("लंबित कार्य समीक्षा (${pendingTasks.size})", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                },
                text = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 380.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Summary Pill
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFFEF2F2),
                            border = BorderStroke(1.dp, Color(0xFFFECACA)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "कुल ${pendingTasks.size} कार्य प्रगति/लंबित अवस्था में हैं",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFDC2626)
                                )
                                Text(
                                    text = "${uiState.pendingVerifications.size} सत्यापन लंबित",
                                    fontSize = 10.sp,
                                    color = TextSecondary
                                )
                            }
                        }

                        // Task Items
                        pendingTasks.forEach { task ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFFF8FAFC),
                                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = task.titleHi,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = TextPrimary,
                                            modifier = Modifier.weight(1f)
                                        )
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = if (task.status == TaskStatus.IN_PROGRESS) Color(0xFF3B82F6) else Color(0xFFF59E0B)
                                        ) {
                                            Text(
                                                text = if (task.status == TaskStatus.IN_PROGRESS) "प्रगति पर" else "लंबित",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(3.dp))
                                    Text(
                                        text = task.descriptionHi,
                                        fontSize = 10.5.sp,
                                        color = TextSecondary,
                                        maxLines = 2
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "जिम्मेदारी: ${task.assignedBy}",
                                            fontSize = 9.5.sp,
                                            color = TextSecondary
                                        )
                                        Text(
                                            text = "समय सीमा: ${task.dueDateHi}",
                                            fontSize = 9.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFFDC2626)
                                        )
                                    }
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showPendingTasksDialog = false
                            viewModel.navigateTo(AppScreen.Main)
                            viewModel.selectTab(com.example.viewmodel.MainTab.TASKS)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
                    ) {
                        Text("टास्क केंद्र खोलें ✓")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showPendingTasksDialog = false }) {
                        Text("बंद करें")
                    }
                }
            )
        }
    }
}

@Composable
fun AdminActionableMetricCard(
    title: String,
    value: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    bgColor: Color,
    borderColor: Color,
    tag: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .testTag(tag)
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        color = Color.White,
        border = BorderStroke(1.dp, borderColor),
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(bgColor),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(20.dp))
                }
                Icon(
                    Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = TextSecondary.copy(alpha = 0.5f),
                    modifier = Modifier.size(14.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = value,
                fontSize = 22.sp,
                fontWeight = FontWeight.Black,
                color = TextPrimary
            )

            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                maxLines = 1
            )

            Text(
                text = subtitle,
                fontSize = 10.sp,
                color = TextSecondary,
                maxLines = 1
            )
        }
    }
}

@Composable
fun AdminImageCompressionCard(
    currentPreset: ImageCompressionPreset,
    onPresetSelected: (ImageCompressionPreset) -> Unit,
    compressionResult: CompressionResult?,
    isCompressing: Boolean,
    uploadProgress: Float,
    statusText: String?,
    accentColor: Color,
    hasAttachedPhoto: Boolean,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("card_image_compression_utility"),
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFFF0FDF4),
        border = BorderStroke(1.2.dp, Color(0xFF86EFAC))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFDCFCE7)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Speed,
                            contentDescription = null,
                            tint = Color(0xFF15803D),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "⚡ क्लाइंट-साइड इमेज कम्प्रेशन",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.5.sp,
                            color = Color(0xFF14532D)
                        )
                        Text(
                            text = "Firebase Storage अपलोड अनुकूलन",
                            fontSize = 10.sp,
                            color = Color(0xFF166534)
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF16A34A)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Bolt, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = "85-92% बचत",
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Presets row
            Text(
                text = "क्वालिटी व रिज़ॉल्यूशन प्रीसेट चुनें:",
                fontSize = 10.5.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF166534)
            )
            Spacer(modifier = Modifier.height(5.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                ImageCompressionPreset.values().forEach { preset ->
                    val isSelected = currentPreset == preset
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onPresetSelected(preset) }
                            .testTag("preset_${preset.name.lowercase()}"),
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSelected) Color(0xFF16A34A) else Color.White,
                        border = BorderStroke(1.dp, if (isSelected) Color(0xFF15803D) else Color(0xFFBBF7D0))
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 6.dp, horizontal = 4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = when (preset) {
                                    ImageCompressionPreset.BALANCED -> "फास्ट"
                                    ImageCompressionPreset.HD_OPTIMIZED -> "एचडी"
                                    ImageCompressionPreset.MAX_QUALITY -> "अल्ट्रा"
                                },
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.White else TextPrimary
                            )
                            Text(
                                text = "${preset.maxDimension}px",
                                fontSize = 9.sp,
                                color = if (isSelected) Color.White.copy(alpha = 0.9f) else TextSecondary
                            )
                        }
                    }
                }
            }

            // Live metrics & bandwidth savings preview
            Spacer(modifier = Modifier.height(10.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                color = Color.White.copy(alpha = 0.9f),
                border = BorderStroke(1.dp, Color(0xFFD1FAE5))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (compressionResult != null) {
                                "मूल: ${compressionResult.originalSizeFormatted} → कंप्रेस्ड: ${compressionResult.compressedSizeFormatted}"
                            } else if (hasAttachedPhoto) {
                                "अनुमानित: ~4.5 MB → ~350 KB"
                            } else {
                                "फोटो संलग्न होने पर स्वतः कंप्रेस होगी"
                            },
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                        Text(
                            text = if (compressionResult != null) {
                                "${compressionResult.compressedWidth}x${compressionResult.compressedHeight}px • HD गुणवत्ता सुरक्षित"
                            } else {
                                "HD पिक्सल्स व टेक्स्ट स्पष्टता 100% सुरक्षित • तेज़ अपलोड"
                            },
                            fontSize = 9.5.sp,
                            color = Color(0xFF059669)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFFDCFCE7)
                    ) {
                        Text(
                            text = if (compressionResult != null) {
                                "${compressionResult.savedPercentage}% कम डेटा"
                            } else {
                                "~88% कम डेटा"
                            },
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF15803D),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            // Active compression / upload progress bar
            if (isCompressing) {
                Spacer(modifier = Modifier.height(10.dp))
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = statusText ?: "प्रोसेसिंग...",
                            fontSize = 10.sp,
                            color = Color(0xFF15803D),
                            fontWeight = FontWeight.SemiBold
                        )
                        if (uploadProgress > 0f) {
                            Text(
                                text = "${uploadProgress.toInt()}%",
                                fontSize = 10.sp,
                                color = Color(0xFF15803D),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress = { if (uploadProgress > 0f) uploadProgress / 100f else 0.5f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = Color(0xFF16A34A),
                        trackColor = Color(0xFFDCFCE7)
                    )
                }
            } else if (compressionResult != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color(0xFF16A34A),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "कंप्रेशन पूर्ण: ${compressionResult.savedPercentage}% डेटा बचत के साथ Firebase Storage पर तैयार!",
                        fontSize = 10.sp,
                        color = Color(0xFF15803D),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}


@Composable
fun UxStatesShowcaseScreen(
    viewModel: AppViewModel,
    language: Language
) {
    var selectedState by remember { mutableStateOf("FEED_LOADING") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(RathoreBackground)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color.White,
            shadowElevation = 2.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { viewModel.navigateTo(AppScreen.Main) },
                    modifier = Modifier.testTag("btn_back_ux_states")
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = RathoreGreen)
                }
                Spacer(modifier = Modifier.width(6.dp))
                Column {
                    Text("10 UX States & Edge Cases", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = RathoreGreen)
                    Text("Prototype specification compliance review", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                }
            }
        }

        // Horizontal State Selector
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf(
                "NO_NOTIFS" to "1. No Notifications",
                "SUGGESTION_SUBMITTED" to "2. Suggestion Submitted",
                "TASK_UPDATE_SUBMITTED" to "3. Task Submitted",
                "TASK_OVERDUE" to "4. Task Overdue",
                "NO_TASKS_PROGRAMS" to "5. No Tasks/Programs",
                "ROLE_MATRIX" to "6. Role Matrix",
                "ACCOUNT_BLOCKED" to "7. Account Blocked",
                "APPROVAL_PENDING" to "8. Pending Approval",
                "DELETE_ACCOUNT" to "9. Delete Account",
                "DEACTIVATE_ACCOUNT" to "10. Deactivate Account",
                "LOGOUT_CONFIRM" to "11. Logout Confirm",
                "HELP_SUPPORT" to "12. Help & Support",
                "PRIVACY_SECURITY" to "13. Privacy & Security",
                "ATTACHMENT_UPLOAD" to "14. Attachment Upload"
            ).forEach { (key, label) ->
                FilterChip(
                    selected = selectedState == key,
                    onClick = { selectedState = key },
                    label = { Text(label, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = RathoreGreen,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        // Active State Render Box
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                shadowElevation = 2.dp,
                border = BorderStroke(1.dp, BorderStroke)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    when (selectedState) {
                        "NO_NOTIFS" -> {
                            // State 1: No Notifications
                            Icon(Icons.Default.NotificationsNone, contentDescription = null, tint = TextTertiary, modifier = Modifier.size(64.dp))
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("कोई नई सूचना नहीं है", fontWeight = FontWeight.Bold, color = TextPrimary)
                            Text("सभी जरूरी सूचनाएं एवं संगठन से जुड़े अपडेट यहाँ प्रदर्शित होंगे।", style = MaterialTheme.typography.bodySmall, color = TextSecondary, textAlign = TextAlign.Center)
                        }
                        "SUGGESTION_SUBMITTED" -> {
                            // State 2: Suggestion Submitted (War Room)
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = RathoreGreen, modifier = Modifier.size(64.dp))
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("सुझाव सफलतापूर्वक प्रेषित!", fontWeight = FontWeight.Bold, color = RathoreGreen)
                            Text("आपका सुझाव केंद्रीय वार रूम व माननीय विधायक जी तक पहुंचा दिया गया है।", style = MaterialTheme.typography.bodySmall, color = TextSecondary, textAlign = TextAlign.Center)
                        }
                        "TASK_UPDATE_SUBMITTED" -> {
                            // State 3: Task Update Submitted
                            Icon(Icons.Default.TaskAlt, contentDescription = null, tint = RathoreGreen, modifier = Modifier.size(64.dp))
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("टास्क रिपोर्ट प्रेषित", fontWeight = FontWeight.Bold, color = RathoreGreen)
                            Text("आपकी कार्य प्रगति रिपोर्ट व संलग्न प्रमाण जिला नियंत्रण कक्ष को भेज दी गई है।", style = MaterialTheme.typography.bodySmall, color = TextSecondary, textAlign = TextAlign.Center)
                        }
                        "TASK_OVERDUE" -> {
                            // State 4: Task Overdue
                            Icon(Icons.Default.AlarmOff, contentDescription = null, tint = Color(0xFFDC2626), modifier = Modifier.size(64.dp))
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("टास्क समयसीमा समाप्त", fontWeight = FontWeight.Bold, color = Color(0xFFDC2626))
                            Text("कृपया संबंधित मंडल अध्यक्ष से संपर्क करें या तुरंत रिपोर्ट प्रेषित करें।", style = MaterialTheme.typography.bodySmall, color = TextSecondary, textAlign = TextAlign.Center)
                        }
                        "NO_TASKS_PROGRAMS" -> {
                            // State 5: No Tasks / No Programs
                            Icon(Icons.Default.EventBusy, contentDescription = null, tint = TextTertiary, modifier = Modifier.size(64.dp))
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("कोई आगामी टास्क या कार्यक्रम नहीं", fontWeight = FontWeight.Bold, color = TextPrimary)
                            Text("वर्तमान में इस श्रेणी में कोई सक्रिय दायित्व या कार्यक्रम निर्धारित नहीं है।", style = MaterialTheme.typography.bodySmall, color = TextSecondary, textAlign = TextAlign.Center)
                        }
                        "ROLE_MATRIX" -> {
                            // State 6: Role Access Matrix
                            Icon(Icons.Default.Security, contentDescription = null, tint = RathoreGreen, modifier = Modifier.size(54.dp))
                            Spacer(modifier = Modifier.height(10.dp))
                            Text("भूमिका अधिकार मैट्रिक्स", fontWeight = FontWeight.Bold, color = RathoreGreen)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("• समर्थक: प्रचार सामग्री डाउनलोड व शिकायत प्रेषण\n• कार्यकर्ता: वार रूम, सुझाव प्रेषण व बैठकें\n• पदाधिकारी: टास्क प्रबंधन व दायित्व रिपोर्टिंग\n• व्यवस्थापक: पूर्ण नियंत्रण व कार्यकर्ता सत्यापन", style = MaterialTheme.typography.bodySmall, color = TextPrimary, textAlign = TextAlign.Start)
                        }
                        "ACCOUNT_BLOCKED" -> {
                            // State 7: Blocked Account
                            Icon(Icons.Default.Block, contentDescription = null, tint = Color(0xFFDC2626), modifier = Modifier.size(64.dp))
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("खाता अस्थायी रूप से निष्क्रिय है", fontWeight = FontWeight.Bold, color = Color(0xFFDC2626))
                            Text("संदेहास्पद गतिविधि के कारण खाता बंद है। केंद्रीय कार्यालय से संपर्क करें।", style = MaterialTheme.typography.bodySmall, color = TextSecondary, textAlign = TextAlign.Center)
                        }
                        "APPROVAL_PENDING" -> {
                            // State 8: Pending Approval
                            Icon(Icons.Default.PendingActions, contentDescription = null, tint = RathoreOrange, modifier = Modifier.size(64.dp))
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("Admin अनुमोदन लंबित है", fontWeight = FontWeight.Bold, color = RathoreOrangeDark)
                            Text("आपके पहचान दस्तावेजों की जांच चल रही है। 24 घंटे में पुष्टि होगी।", style = MaterialTheme.typography.bodySmall, color = TextSecondary, textAlign = TextAlign.Center)
                        }
                        "DELETE_ACCOUNT" -> {
                            // State 9: Delete Account
                            Icon(Icons.Default.DeleteForever, contentDescription = null, tint = Color(0xFFDC2626), modifier = Modifier.size(64.dp))
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("खाता स्थायी रूप से हटाएं", fontWeight = FontWeight.Bold, color = Color(0xFFDC2626))
                            Text("सावधानी: यह प्रक्रिया अपरिवर्तनीय है। आपका सारा डेटा और संगठनात्मक दायित्व मिटा दिए जाएंगे।", style = MaterialTheme.typography.bodySmall, color = TextSecondary, textAlign = TextAlign.Center)
                        }
                        "DEACTIVATE_ACCOUNT" -> {
                            // State 10: Account Deactivation
                            Icon(Icons.Default.PauseCircleOutline, contentDescription = null, tint = RathoreOrangeDark, modifier = Modifier.size(64.dp))
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("खाता अस्थायी निष्क्रिय करें", fontWeight = FontWeight.Bold, color = RathoreOrangeDark)
                            Text("खाता निष्क्रिय करने पर संगठनात्मक दायित्व रोके जाएंगे। पुनः लॉगिन करके कभी भी सक्रिय कर सकते हैं।", style = MaterialTheme.typography.bodySmall, color = TextSecondary, textAlign = TextAlign.Center)
                        }
                        "LOGOUT_CONFIRM" -> {
                            // State 11: Logout Confirmation
                            Icon(Icons.Default.Logout, contentDescription = null, tint = Color(0xFFDC2626), modifier = Modifier.size(64.dp))
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("लॉगआउट की पुष्टि", fontWeight = FontWeight.Bold, color = TextPrimary)
                            Text("क्या आप सचमुच ऐप से लॉगआउट करना चाहते हैं? दोबारा जुड़ने हेतु ओटीपी चाहिए होगा।", style = MaterialTheme.typography.bodySmall, color = TextSecondary, textAlign = TextAlign.Center)
                        }
                        "HELP_SUPPORT" -> {
                            // State 12: Help & Support
                            Icon(Icons.Default.HelpOutline, contentDescription = null, tint = RathoreGreen, modifier = Modifier.size(64.dp))
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("सहायता एवं समर्थन", fontWeight = FontWeight.Bold, color = RathoreGreen)
                            Text("हेल्पलाइन: +91 94251 00000\nईमेल: support@teamrathore.in\nसमय: प्रातः 9:00 से सायं 8:00 तक", style = MaterialTheme.typography.bodySmall, color = TextSecondary, textAlign = TextAlign.Center)
                        }
                        "PRIVACY_SECURITY" -> {
                            // State 13: Privacy & Security
                            Icon(Icons.Default.Lock, contentDescription = null, tint = RathoreGreen, modifier = Modifier.size(64.dp))
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("गोपनीयता एवं सुरक्षा", fontWeight = FontWeight.Bold, color = RathoreGreen)
                            Text("256-बिट सुरक्षित एन्क्रिप्शन। कोई डेटा तृतीय पक्ष से साझा नहीं किया जाता।", style = MaterialTheme.typography.bodySmall, color = TextSecondary, textAlign = TextAlign.Center)
                        }
                        "ATTACHMENT_UPLOAD" -> {
                            // State 14: Attachment Upload (Direct Connect)
                            Icon(Icons.Default.CloudUpload, contentDescription = null, tint = RathoreOrange, modifier = Modifier.size(64.dp))
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("दस्तावेज / फोटो संलग्नक", fontWeight = FontWeight.Bold, color = RathoreOrangeDark)
                            Text("समस्या अथवा कार्य प्रमाण हेतु कैमरा/गैलरी से फोटो संलग्न करें।", style = MaterialTheme.typography.bodySmall, color = TextSecondary, textAlign = TextAlign.Center)
                        }
                    }
                }
            }
        }
    }
}
