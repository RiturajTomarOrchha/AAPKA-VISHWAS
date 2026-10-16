package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.*
import kotlinx.coroutines.delay
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.model.*
import com.example.ui.theme.*
import com.example.util.AppStrings
import com.example.viewmodel.AppViewModel

@Composable
fun WarRoomScreen(
    viewModel: AppViewModel,
    language: Language,
    meetingInfo: MeetingInfo,
    programs: List<ProgramItem>,
    notices: List<ImportantNotice>,
    isMicMuted: Boolean,
    isCameraOff: Boolean
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isAdmin = uiState.userProfile.role == UserRole.ADMIN
    val isOfficeBearerOrAdmin = uiState.userProfile.role == UserRole.OFFICE_BEARER || uiState.userProfile.role == UserRole.ADMIN

    var selectedSection by remember { mutableStateOf<String?>(null) }
    var suggestionText by remember { mutableStateOf("") }
    var suggestionCategory by remember { mutableStateOf("क्षेत्रीय समस्या") }
    var suggestionSubmitted by remember { mutableStateOf(false) }

    // Worker Information submission state
    var workerInfoSubject by remember { mutableStateOf("") }
    var workerInfoLocation by remember { mutableStateOf("") }
    var workerInfoDetails by remember { mutableStateOf("") }
    var workerInfoSubmitted by remember { mutableStateOf(false) }

    // Admin Dialog states
    var showCreateMeetingDialog by remember { mutableStateOf(false) }
    var showCreateProgramDialog by remember { mutableStateOf(false) }
    var showBroadcastNoticeDialog by remember { mutableStateOf(false) }

    var adminMeetingTitle by remember { mutableStateOf("") }
    var adminMeetingDate by remember { mutableStateOf("आज") }
    var adminMeetingTime by remember { mutableStateOf("15 मिनट बाद") }
    var adminMeetingAgenda by remember { mutableStateOf("") }
    var adminMeetingUrl by remember { mutableStateOf("") }
    var adminMeetingMinutesAhead by remember { mutableIntStateOf(15) }

    var adminProgTitle by remember { mutableStateOf("") }
    var adminProgDate by remember { mutableStateOf("रविवार") }
    var adminProgTime by remember { mutableStateOf("सुबह 10:00 बजे") }
    var adminProgVenue by remember { mutableStateOf("विधानसभा कार्यालय") }
    var adminProgDesc by remember { mutableStateOf("") }
    var adminProgHighlighted by remember { mutableStateOf(false) }

    // Edit program state for Admin
    var editingProgram by remember { mutableStateOf<ProgramItem?>(null) }
    var editProgTitle by remember { mutableStateOf("") }
    var editProgDate by remember { mutableStateOf("") }
    var editProgTime by remember { mutableStateOf("") }
    var editProgVenue by remember { mutableStateOf("") }
    var editProgDesc by remember { mutableStateOf("") }
    var editProgHighlighted by remember { mutableStateOf(false) }

    var adminNoticeTitle by remember { mutableStateOf("") }
    var adminNoticeBody by remember { mutableStateOf("") }
    var adminNoticeUrgent by remember { mutableStateOf(false) }
    var adminNoticeAttachPhoto by remember { mutableStateOf(false) }
    var adminNoticePhotoTitle by remember { mutableStateOf("आधिकारिक_विज्ञप्ति_आदेश.jpg") }

    // Live Ticking Clock for Scheduled Meeting Countdown (updates every second)
    var currentTimeMillis by remember { mutableLongStateOf(System.currentTimeMillis()) }
    LaunchedEffect(Unit) {
        while (true) {
            currentTimeMillis = System.currentTimeMillis()
            delay(1000L)
        }
    }

    val scheduledTime = meetingInfo.scheduledTimeMillis
    val diffMillis = scheduledTime - currentTimeMillis
    val fifteenMinutesMillis = 15 * 60 * 1000L
    // Countdown timer appears 15 minutes before scheduled meeting starts (only if meeting is scheduled)
    val isMeetingUpcoming = meetingInfo.status == MeetingStatus.UPCOMING && meetingInfo.isScheduled
    val isWithinFifteenMinutes = isMeetingUpcoming && diffMillis <= fifteenMinutesMillis && diffMillis >= -3600_000L

    val totalSeconds = (diffMillis / 1000L).coerceAtLeast(0L)
    val countdownMinutes = totalSeconds / 60
    val countdownSeconds = totalSeconds % 60
    val formattedCountdown = String.format("%02d:%02d", countdownMinutes, countdownSeconds)
    val isStartingNow = diffMillis <= 0L

    // If meeting is in interactive flow (permissions, waiting, live, ended), show dedicated meeting view
    if (meetingInfo.status != MeetingStatus.UPCOMING) {
        WarRoomMeetingView(
            viewModel = viewModel,
            language = language,
            meetingInfo = meetingInfo,
            isMicMuted = isMicMuted,
            isCameraOff = isCameraOff
        )
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(RathoreBackground)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // War Room Header
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = RathoreGreen,
                shadowElevation = 3.dp
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isAdmin) "वॉर रूम (एडमिन कंट्रोल)" else AppStrings.get("war_room_title", language),
                                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Black),
                                color = Color.White
                            )
                            Text(
                                text = if (isAdmin) "मीटिंग, कार्यक्रम एवं आधिकारिक सूचना प्रबंधन केंद्र" else AppStrings.get("war_room_sub", language),
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(RathoreOrange),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Shield, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
                        }
                    }

                    // 15-Minute Live Meeting Countdown & Join Meeting Action (Appears 15 min before scheduled start)
                    AnimatedVisibility(
                        visible = isWithinFifteenMinutes,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        Column {
                            Spacer(modifier = Modifier.height(14.dp))
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("war_room_header_countdown_card"),
                                shape = RoundedCornerShape(14.dp),
                                color = Color(0xFF0F172A), // Dark slate canvas with high contrast against RathoreGreen
                                shadowElevation = 4.dp,
                                border = BorderStroke(1.5.dp, if (isStartingNow) Color(0xFF22C55E) else Color(0xFFF59E0B))
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    // Status pill & Digital Countdown Display
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.weight(1f, fill = false)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(10.dp)
                                                    .clip(CircleShape)
                                                    .background(if (isStartingNow) Color(0xFF22C55E) else Color(0xFFEF4444))
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = if (isStartingNow) "मीटिंग शुरू हो चुकी है!" else "लाइव मीटिंग शुरू होने में शेष समय",
                                                color = if (isStartingNow) Color(0xFF86EFAC) else Color(0xFFFDE68A),
                                                fontSize = 11.5.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }

                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = Color(0xFF1E293B),
                                            border = BorderStroke(1.dp, if (isStartingNow) Color(0xFF22C55E).copy(alpha = 0.6f) else Color(0xFFF59E0B).copy(alpha = 0.6f))
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    Icons.Default.HourglassTop,
                                                    contentDescription = null,
                                                    tint = if (isStartingNow) Color(0xFF22C55E) else Color(0xFFF59E0B),
                                                    modifier = Modifier.size(14.dp)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    text = if (isStartingNow) "LIVE" else formattedCountdown,
                                                    color = Color.White,
                                                    fontSize = 14.sp,
                                                    fontWeight = FontWeight.Black,
                                                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    // Meeting Title & Schedule
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(34.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(Color(0xFF1E293B)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                Icons.Default.VideoCameraFront,
                                                contentDescription = null,
                                                tint = Color(0xFF60A5FA),
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = meetingInfo.titleHi,
                                                color = Color.White,
                                                fontSize = 12.5.sp,
                                                fontWeight = FontWeight.Bold,
                                                maxLines = 1
                                            )
                                            Text(
                                                text = "${meetingInfo.timeHi} • होस्ट: ${meetingInfo.hostHi}",
                                                color = Color(0xFF94A3B8),
                                                fontSize = 10.5.sp,
                                                maxLines = 1
                                            )
                                        }
                                    }

                                     Spacer(modifier = Modifier.height(10.dp))

                                    // External Meeting Link banner (if URL configured)
                                    if (!meetingInfo.meetingUrl.isNullOrBlank()) {
                                        Surface(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable {
                                                    try {
                                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(meetingInfo.meetingUrl))
                                                        context.startActivity(intent)
                                                    } catch (e: Exception) {
                                                        Toast.makeText(context, "लिंक खोलने में असमर्थ", Toast.LENGTH_SHORT).show()
                                                    }
                                                }
                                                .testTag("banner_countdown_external_url"),
                                            shape = RoundedCornerShape(8.dp),
                                            color = Color(0xFF1E293B),
                                            border = BorderStroke(1.dp, Color(0xFF3B82F6).copy(alpha = 0.5f))
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    modifier = Modifier.weight(1f)
                                                ) {
                                                    Icon(
                                                        Icons.Default.Link,
                                                        contentDescription = null,
                                                        tint = Color(0xFF60A5FA),
                                                        modifier = Modifier.size(15.dp)
                                                    )
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Column {
                                                        Text(
                                                            text = "मीटिंग URL उपलब्ध है (Google Meet / Zoom)",
                                                            color = Color(0xFF93C5FD),
                                                            fontSize = 9.5.sp,
                                                            fontWeight = FontWeight.Bold
                                                        )
                                                        Text(
                                                            text = meetingInfo.meetingUrl,
                                                            color = Color(0xFFE2E8F0),
                                                            fontSize = 9.5.sp,
                                                            maxLines = 1,
                                                            overflow = TextOverflow.Ellipsis
                                                        )
                                                    }
                                                }
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    IconButton(
                                                        onClick = {
                                                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                                            val clip = ClipData.newPlainText("Meeting Link", meetingInfo.meetingUrl)
                                                            clipboard.setPrimaryClip(clip)
                                                            Toast.makeText(context, "मीटिंग URL कॉपी किया गया!", Toast.LENGTH_SHORT).show()
                                                        },
                                                        modifier = Modifier.size(24.dp)
                                                    ) {
                                                        Icon(
                                                            Icons.Default.ContentCopy,
                                                            contentDescription = "कॉपी करें",
                                                            tint = Color(0xFF60A5FA),
                                                            modifier = Modifier.size(14.dp)
                                                        )
                                                    }
                                                    Spacer(modifier = Modifier.width(2.dp))
                                                    Icon(
                                                        Icons.Default.OpenInNew,
                                                        contentDescription = "खोलें",
                                                        tint = Color(0xFF60A5FA),
                                                        modifier = Modifier.size(15.dp)
                                                    )
                                                }
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(10.dp))
                                    }

                                    // 15-Minute Countdown Progress Bar
                                    val progressFraction = (totalSeconds.toFloat() / 900f).coerceIn(0f, 1f)
                                    LinearProgressIndicator(
                                        progress = { 1f - progressFraction },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(4.dp)
                                            .clip(RoundedCornerShape(2.dp)),
                                        color = if (isStartingNow) Color(0xFF22C55E) else Color(0xFFF59E0B),
                                        trackColor = Color(0xFF334155)
                                    )

                                    Spacer(modifier = Modifier.height(12.dp))

                                    // Meeting Join Buttons: In-App and/or External URL
                                    if (!meetingInfo.meetingUrl.isNullOrBlank()) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Button(
                                                onClick = { viewModel.startMeetingFlow() },
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .height(44.dp)
                                                    .testTag("btn_join_meeting_countdown"),
                                                shape = RoundedCornerShape(10.dp),
                                                colors = ButtonDefaults.buttonColors(
                                                    containerColor = if (isStartingNow) Color(0xFF16A34A) else RathoreOrange
                                                ),
                                                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 4.dp)
                                            ) {
                                                Icon(
                                                    Icons.Default.VideoCall,
                                                    contentDescription = null,
                                                    tint = Color.White,
                                                    modifier = Modifier.size(17.dp)
                                                )
                                                Spacer(modifier = Modifier.width(5.dp))
                                                Text(
                                                    text = if (isStartingNow) "लाइव जुड़ें" else "इन-ऐप मीटिंग",
                                                    fontSize = 11.5.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color.White
                                                )
                                            }

                                            OutlinedButton(
                                                onClick = {
                                                    try {
                                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(meetingInfo.meetingUrl))
                                                        context.startActivity(intent)
                                                    } catch (e: Exception) {
                                                        Toast.makeText(context, "लिंक खोलने में असमर्थ", Toast.LENGTH_SHORT).show()
                                                    }
                                                },
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .height(44.dp)
                                                    .testTag("btn_open_external_url_countdown"),
                                                shape = RoundedCornerShape(10.dp),
                                                border = BorderStroke(1.dp, Color(0xFF60A5FA)),
                                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF93C5FD)),
                                                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 4.dp)
                                            ) {
                                                Icon(
                                                    Icons.Default.OpenInNew,
                                                    contentDescription = null,
                                                    tint = Color(0xFF60A5FA),
                                                    modifier = Modifier.size(15.dp)
                                                )
                                                Spacer(modifier = Modifier.width(5.dp))
                                                Text(
                                                    text = "बाहरी लिंक खोलें",
                                                    fontSize = 11.5.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    } else {
                                        Button(
                                            onClick = { viewModel.startMeetingFlow() },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(48.dp)
                                                .testTag("btn_join_meeting_countdown"),
                                            shape = RoundedCornerShape(10.dp),
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = if (isStartingNow) Color(0xFF16A34A) else RathoreOrange
                                            )
                                        ) {
                                            Icon(
                                                Icons.Default.VideoCall,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(20.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = if (isStartingNow) "लाइव मीटिंग में अभी शामिल हों ✓" else "मीटिंग में शामिल हों",
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                        }
                                    }

                                    // Quick Admin Management & Test Switchers
                                    if (isAdmin) {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Surface(
                                                modifier = Modifier
                                                    .clickable {
                                                        adminMeetingTitle = meetingInfo.titleHi
                                                        adminMeetingDate = meetingInfo.dateHi
                                                        adminMeetingTime = meetingInfo.timeHi
                                                        adminMeetingAgenda = meetingInfo.agendaHi
                                                        adminMeetingUrl = meetingInfo.meetingUrl ?: ""
                                                        showCreateMeetingDialog = true
                                                    }
                                                    .testTag("btn_admin_edit_meeting_countdown"),
                                                shape = RoundedCornerShape(6.dp),
                                                color = Color(0xFF2563EB)
                                            ) {
                                                Row(
                                                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Icon(
                                                        Icons.Default.EditCalendar,
                                                        contentDescription = null,
                                                        tint = Color.White,
                                                        modifier = Modifier.size(11.dp)
                                                    )
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text(
                                                        text = "मीटिंग व URL सेट करें",
                                                        fontSize = 9.sp,
                                                        color = Color.White,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }
                                            }

                                            Row(
                                                horizontalArrangement = Arrangement.spacedBy(3.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = "टेस्ट:",
                                                    fontSize = 9.sp,
                                                    color = Color(0xFF94A3B8)
                                                )
                                                listOf(15 to "15m", 5 to "5m", 1 to "1m", 0 to "LIVE").forEach { (mins, label) ->
                                                    Surface(
                                                        modifier = Modifier.clickable { viewModel.setMeetingCountdown(mins) },
                                                        shape = RoundedCornerShape(4.dp),
                                                        color = Color(0xFF334155)
                                                    ) {
                                                        Text(
                                                            text = label,
                                                            fontSize = 8.5.sp,
                                                            color = Color.White,
                                                            fontWeight = FontWeight.Bold,
                                                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
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
            }
        }

        // Hub Tiles Grid: Tailored for Admin (Meeting, Program, Notice) vs Worker (6 Tiles)
        item {
            if (isAdmin) {
                // Admin: सूचना, कार्यक्रम, मीटिंग & कार्यकर्ता सुझाव इनबॉक्स
                // (सुझाव फॉर्म, जानकारी शेयर, दस्तावेज एडमिन में नहीं होने चाहिए)
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        WarRoomTile(
                            title = "📢 सूचना प्रबंधन",
                            icon = Icons.Default.Campaign,
                            iconTint = Color(0xFFE53935),
                            badge = "${notices.size}",
                            onClick = { selectedSection = if (selectedSection == "NOTICES") null else "NOTICES" },
                            modifier = Modifier.weight(1f),
                            tag = "tile_notices"
                        )
                        WarRoomTile(
                            title = "📅 कार्यक्रम प्रबंधन",
                            icon = Icons.Default.Event,
                            iconTint = RathoreGreen,
                            badge = "${programs.size}",
                            onClick = { selectedSection = if (selectedSection == "PROGRAMS") null else "PROGRAMS" },
                            modifier = Modifier.weight(1f),
                            tag = "tile_programs"
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        WarRoomTile(
                            title = "📹 मीटिंग प्रबंधन व URL",
                            icon = Icons.Default.VideoCameraFront,
                            iconTint = Color(0xFF1E88E5),
                            badge = if (!meetingInfo.meetingUrl.isNullOrBlank()) "URL Set" else "Live",
                            onClick = {
                                adminMeetingTitle = meetingInfo.titleHi
                                adminMeetingDate = meetingInfo.dateHi
                                adminMeetingTime = meetingInfo.timeHi
                                adminMeetingAgenda = meetingInfo.agendaHi
                                adminMeetingUrl = meetingInfo.meetingUrl ?: ""
                                showCreateMeetingDialog = true
                            },
                            modifier = Modifier.weight(1f),
                            tag = "tile_live_meeting"
                        )
                        WarRoomTile(
                            title = "📥 कार्यकर्ता सुझाव इनबॉक्स",
                            icon = Icons.Default.Inbox,
                            iconTint = RathoreOrange,
                            badge = "${uiState.workerSuggestions.size}",
                            onClick = { selectedSection = if (selectedSection == "WORKER_SUGGESTIONS") null else "WORKER_SUGGESTIONS" },
                            modifier = Modifier.weight(1f),
                            tag = "tile_suggestion_inbox"
                        )
                    }
                }
            } else {
                // Worker: Full 6 Hub Tiles Grid (2x3)
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        WarRoomTile(
                            title = AppStrings.get("tile_important_info", language),
                            icon = Icons.Default.Campaign,
                            iconTint = Color(0xFFE53935),
                            badge = "${notices.size}",
                            onClick = { selectedSection = if (selectedSection == "NOTICES") null else "NOTICES" },
                            modifier = Modifier.weight(1f),
                            tag = "tile_notices"
                        )
                        WarRoomTile(
                            title = AppStrings.get("tile_upcoming_programs", language),
                            icon = Icons.Default.Event,
                            iconTint = RathoreGreen,
                            badge = "${programs.size}",
                            onClick = { selectedSection = if (selectedSection == "PROGRAMS") null else "PROGRAMS" },
                            modifier = Modifier.weight(1f),
                            tag = "tile_programs"
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        WarRoomTile(
                            title = AppStrings.get("tile_live_meeting", language),
                            icon = Icons.Default.VideoCameraFront,
                            iconTint = Color(0xFF1E88E5),
                            badge = "Live",
                            onClick = { viewModel.startMeetingFlow() },
                            modifier = Modifier.weight(1f),
                            tag = "tile_live_meeting"
                        )
                        WarRoomTile(
                            title = AppStrings.get("tile_send_suggestion", language),
                            icon = Icons.Default.EditNote,
                            iconTint = RathoreOrange,
                            onClick = { selectedSection = if (selectedSection == "SUGGESTION") null else "SUGGESTION" },
                            modifier = Modifier.weight(1f),
                            tag = "tile_suggestion"
                        )
                    }

                    if (isOfficeBearerOrAdmin) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            WarRoomTile(
                                title = AppStrings.get("tile_share_info", language),
                                icon = Icons.Default.ForwardToInbox,
                                iconTint = Color(0xFF8E24AA),
                                onClick = { selectedSection = if (selectedSection == "TOUR") null else "TOUR" },
                                modifier = Modifier.weight(1f),
                                tag = "tile_tour"
                            )
                            WarRoomTile(
                                title = AppStrings.get("tile_documents", language),
                                icon = Icons.Default.Description,
                                iconTint = Color(0xFF00897B),
                                onClick = { selectedSection = if (selectedSection == "DOCS") null else "DOCS" },
                                modifier = Modifier.weight(1f),
                                tag = "tile_docs"
                            )
                        }
                    }
                }
            }
        }

        // --- EXPANDABLE BOX (Right below the clicked tiles) ---
        if (selectedSection != null) {
            when (selectedSection) {
                "NOTICES" -> {
                    item {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFFEF2F2),
                            border = BorderStroke(1.dp, Color(0xFFFECACA))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Campaign, contentDescription = null, tint = Color(0xFFDC2626), modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "📢 सूचना प्रबंधन",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = TextPrimary
                                    )
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    if (isAdmin) {
                                        Button(
                                            onClick = { showBroadcastNoticeDialog = true },
                                            shape = RoundedCornerShape(8.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                        ) {
                                            Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("नई सूचना", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        }
                                        Spacer(modifier = Modifier.width(6.dp))
                                    }
                                    IconButton(
                                        onClick = { selectedSection = null },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(Icons.Default.Close, contentDescription = "बंद करें", tint = TextSecondary)
                                    }
                                }
                            }
                        }
                    }

                    if (notices.isEmpty()) {
                        item {
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                color = Color.White,
                                border = BorderStroke(1.dp, BorderStroke)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(24.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(Icons.Default.NotificationsOff, contentDescription = null, tint = TextTertiary, modifier = Modifier.size(36.dp))
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text("कोई सक्रिय सूचना नहीं है।", color = TextSecondary, style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    } else {
                        items(notices) { notice ->
                            NoticeCard(
                                notice = notice,
                                language = language,
                                isAdmin = isAdmin,
                                onDelete = { viewModel.deleteNotice(notice.id) }
                            )
                        }
                    }
                }
                "PROGRAMS" -> {
                    item {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFF0FDF4),
                            border = BorderStroke(1.dp, Color(0xFFBBF7D0))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Event, contentDescription = null, tint = RathoreGreen, modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "📅 कार्यक्रम प्रबंधन",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = TextPrimary
                                    )
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    if (isAdmin) {
                                        Button(
                                            onClick = { showCreateProgramDialog = true },
                                            shape = RoundedCornerShape(8.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = RathoreGreen),
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                        ) {
                                            Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("नया कार्यक्रम", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        }
                                        Spacer(modifier = Modifier.width(6.dp))
                                    }
                                    IconButton(
                                        onClick = { selectedSection = null },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(Icons.Default.Close, contentDescription = "बंद करें", tint = TextSecondary)
                                    }
                                }
                            }
                        }
                    }

                    if (programs.isEmpty()) {
                        item {
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                color = Color.White,
                                border = BorderStroke(1.dp, BorderStroke)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(24.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(Icons.Default.EventBusy, contentDescription = null, tint = TextTertiary, modifier = Modifier.size(36.dp))
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text("कोई आगामी कार्यक्रम निर्धारित नहीं है।", color = TextSecondary, style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    } else {
                        items(programs) { prog ->
                            ProgramCard(
                                prog = prog,
                                language = language,
                                isAdmin = isAdmin,
                                onRsvp = { status -> viewModel.rsvpProgram(prog.id, status) },
                                onEdit = {
                                    editingProgram = prog
                                    editProgTitle = prog.titleHi
                                    editProgDate = prog.dateHi
                                    editProgTime = prog.timeHi
                                    editProgVenue = prog.venueHi
                                    editProgDesc = prog.instructionsHi
                                    editProgHighlighted = prog.isHighlighted
                                },
                                onDelete = { viewModel.deleteProgram(prog.id) }
                            )
                        }
                    }
                }
                "WORKER_SUGGESTIONS" -> {
                    item {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFFFF7ED),
                            border = BorderStroke(1.dp, Color(0xFFFED7AA))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Inbox, contentDescription = null, tint = RathoreOrange, modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "📥 प्राप्त कार्यकर्ता सुझाव",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = TextPrimary
                                    )
                                }
                                IconButton(
                                    onClick = { selectedSection = null },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(Icons.Default.Close, contentDescription = "बंद करें", tint = TextSecondary)
                                }
                            }
                        }
                    }

                    if (uiState.workerSuggestions.isEmpty()) {
                        item {
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                color = Color.White,
                                border = BorderStroke(1.dp, BorderStroke)
                            ) {
                                Column(
                                    modifier = Modifier.padding(24.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(Icons.Default.Inbox, contentDescription = null, tint = TextTertiary, modifier = Modifier.size(36.dp))
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text("कोई नया सुझाव प्राप्त नहीं हुआ है।", color = TextSecondary, style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    } else {
                        items(uiState.workerSuggestions) { sug ->
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                color = Color.White,
                                border = BorderStroke(1.dp, BorderStroke)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(sug.workerName, fontWeight = FontWeight.Bold, color = RathoreGreen, style = MaterialTheme.typography.bodyMedium)
                                        Surface(shape = RoundedCornerShape(4.dp), color = Color(0xFFEDE9FE)) {
                                            Text(sug.category, color = Color(0xFF7C3AED), fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                        }
                                    }
                                    Text("क्षेत्र: ${sug.area}", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(sug.message, style = MaterialTheme.typography.bodySmall, color = TextPrimary)
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(sug.date, style = MaterialTheme.typography.labelSmall, color = TextTertiary)
                                }
                            }
                        }
                    }
                }
                "SUGGESTION" -> {
                    item {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            color = Color.White,
                            border = BorderStroke(1.dp, BorderStroke)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("सुझाव भेजें", fontWeight = FontWeight.Bold, color = RathoreGreen, style = MaterialTheme.typography.titleSmall)
                                    IconButton(onClick = { selectedSection = null }, modifier = Modifier.size(28.dp)) {
                                        Icon(Icons.Default.Close, contentDescription = "बंद करें", tint = TextSecondary)
                                    }
                                }
                                Text("संगठन व क्षेत्र के विकास हेतु अपने विचार नेतृत्व तक सीधे पहुंचाएं।", style = MaterialTheme.typography.bodySmall, color = TextSecondary)

                                Spacer(modifier = Modifier.height(12.dp))

                                OutlinedTextField(
                                    value = suggestionText,
                                    onValueChange = { suggestionText = it },
                                    label = { Text("अपना सुझाव विस्तार से लिखें...") },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(120.dp),
                                    shape = RoundedCornerShape(10.dp)
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                Button(
                                    onClick = {
                                        if (suggestionText.isNotBlank()) {
                                            viewModel.addWorkerSuggestion(suggestionText, suggestionCategory)
                                            suggestionSubmitted = true
                                            suggestionText = ""
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(containerColor = RathoreGreen)
                                ) {
                                    Text("सुझाव प्रेषित करें ✓", color = Color.White, fontWeight = FontWeight.Bold)
                                }

                                if (suggestionSubmitted) {
                                    Spacer(modifier = Modifier.height(14.dp))
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = Color(0xFFE8F5E9),
                                        border = BorderStroke(1.dp, RathoreGreen)
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(12.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                Icons.Default.CheckCircle,
                                                contentDescription = null,
                                                tint = RathoreGreen,
                                                modifier = Modifier.size(24.dp)
                                            )
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Column {
                                                Text(
                                                    "सुझाव सफलतापूर्वक प्रेषित!",
                                                    color = RathoreGreen,
                                                    fontWeight = FontWeight.Bold,
                                                    style = MaterialTheme.typography.bodyMedium
                                                )
                                                Text(
                                                    "आपका सुझाव केंद्रीय वार रूम व विधायक जी तक पहुंचा दिया गया है।",
                                                    color = TextSecondary,
                                                    style = MaterialTheme.typography.bodySmall
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                "TOUR" -> {
                    item {
                        if (isOfficeBearerOrAdmin) {
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                color = Color.White,
                                border = BorderStroke(1.dp, BorderStroke)
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
                                                .background(Color(0xFF8E24AA).copy(alpha = 0.12f), CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(Icons.Default.ForwardToInbox, contentDescription = null, tint = Color(0xFF8E24AA), modifier = Modifier.size(18.dp))
                                        }
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text("वॉर रूम में जानकारी दें", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall, color = TextPrimary)
                                            Text("ग्राउंड रिपोर्ट, क्षेत्र की जानकारी व दिशा-निर्देश", fontSize = 11.sp, color = TextSecondary)
                                        }
                                    }
                                    IconButton(onClick = { selectedSection = null }, modifier = Modifier.size(28.dp)) {
                                        Icon(Icons.Default.Close, contentDescription = "बंद करें", tint = TextSecondary)
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                // Part 1: Submit Ground Information to War Room
                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(10.dp),
                                    color = Color(0xFFFAF5FF),
                                    border = BorderStroke(1.dp, Color(0xFFE9D5FF))
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text(
                                            text = "✍️ वॉर रूम को अपने क्षेत्र/बूथ की जानकारी भेजें",
                                            fontSize = 12.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF7E22CE)
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))

                                        OutlinedTextField(
                                            value = workerInfoLocation,
                                            onValueChange = { workerInfoLocation = it },
                                            modifier = Modifier.fillMaxWidth(),
                                            placeholder = { Text("क्षेत्र / वार्ड / बूथ संख्या (उदा. बूथ क्र. 45, निवाड़ी)") },
                                            singleLine = true,
                                            shape = RoundedCornerShape(8.dp)
                                        )

                                        Spacer(modifier = Modifier.height(8.dp))

                                        OutlinedTextField(
                                            value = workerInfoSubject,
                                            onValueChange = { workerInfoSubject = it },
                                            modifier = Modifier.fillMaxWidth(),
                                            placeholder = { Text("विषय (उदा. मतदाता संपर्क रिपोर्ट / स्थानीय समस्या)") },
                                            singleLine = true,
                                            shape = RoundedCornerShape(8.dp)
                                        )

                                        Spacer(modifier = Modifier.height(8.dp))

                                        OutlinedTextField(
                                            value = workerInfoDetails,
                                            onValueChange = { workerInfoDetails = it },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(76.dp),
                                            placeholder = { Text("विस्तृत विवरण व जानकारी यहाँ लिखें...") },
                                            shape = RoundedCornerShape(8.dp)
                                        )

                                        Spacer(modifier = Modifier.height(10.dp))

                                        Button(
                                            onClick = {
                                                if (workerInfoDetails.isNotBlank() || workerInfoSubject.isNotBlank()) {
                                                    val subject = workerInfoSubject.ifBlank { "ग्राउंड रिपोर्ट" }
                                                    val location = if (workerInfoLocation.isNotBlank()) " [${workerInfoLocation}]" else ""
                                                    viewModel.addWorkerSuggestion(
                                                        subject = subject,
                                                        message = workerInfoDetails.ifBlank { subject },
                                                        category = "कार्यकर्ता जानकारी$location"
                                                    )
                                                    workerInfoSubmitted = true
                                                    workerInfoSubject = ""
                                                    workerInfoLocation = ""
                                                    workerInfoDetails = ""
                                                    Toast.makeText(context, "आपकी जानकारी वॉर रूम तक सफलतापूर्वक प्रेषित कर दी गई!", Toast.LENGTH_SHORT).show()
                                                } else {
                                                    Toast.makeText(context, "कृपया जानकारी का विवरण लिखें", Toast.LENGTH_SHORT).show()
                                                }
                                            },
                                            shape = RoundedCornerShape(8.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8E24AA)),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Icon(Icons.Default.Send, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("वॉर रूम में जानकारी भेजें", fontWeight = FontWeight.Bold, color = Color.White)
                                        }

                                        if (workerInfoSubmitted) {
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Text(
                                                text = "✓ जानकारी प्राप्त हुई। वॉर रूम टीम समीक्षा कर रही है।",
                                                color = Color(0xFF16A34A),
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                // Part 2: Official Bulletins & Directives from War Room
                                Text("📋 वॉर रूम आधिकारिक जानकारी एवं दिशा-निर्देश", fontSize = 12.5.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                Spacer(modifier = Modifier.height(8.dp))

                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    listOf(
                                        "📌 बूथ प्रबंधन व संपर्क" to "प्रत्येक मतदान केंद्र पर 10 सक्रिय कार्यकर्ताओं की सूची सत्यापित करें और दैनिक मतदाता जनसंपर्क सुनिश्चित करें।",
                                        "📌 प्रचार सामग्री प्रसार" to "ऐप के होम फ़ीड से आधिकारिक 4:5 पोस्टर्स प्रतिदिन अपने स्थानीय व्हाट्सएप ग्रुपों व स्टेटस पर साझा करें।",
                                        "📌 आगामी कार्यक्रम सूची" to "14 मार्च: निवाड़ी ग्रामीण जनसंवाद चौपाल • 16 मार्च: पृथ्वीपुर नगर वार्ड समीक्षा बैठक • 18 मार्च: युवा सम्मेलन।",
                                        "📞 वॉर रूम हेल्पलाइन" to "किसी भी संगठनात्मक सहायता या आपात स्थिति हेतु वॉर रूम कंट्रोल डेस्क: 9876543210 पर संपर्क करें।"
                                    ).forEach { (heading, desc) ->
                                        Surface(
                                            modifier = Modifier.fillMaxWidth(),
                                            shape = RoundedCornerShape(8.dp),
                                            color = Color(0xFFF8FAFC),
                                            border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                                        ) {
                                            Column(modifier = Modifier.padding(10.dp)) {
                                                Text(heading, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                                Spacer(modifier = Modifier.height(2.dp))
                                                Text(desc, fontSize = 11.sp, color = TextSecondary, lineHeight = 15.sp)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                "DOCS" -> {
                    item {
                        if (isOfficeBearerOrAdmin) {
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                color = Color.White,
                                border = BorderStroke(1.dp, BorderStroke)
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
                                                .background(Color(0xFF00897B).copy(alpha = 0.12f), CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(Icons.Default.Description, contentDescription = null, tint = Color(0xFF00897B), modifier = Modifier.size(18.dp))
                                        }
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text("संगठनात्मक दस्तावेज", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall, color = TextPrimary)
                                            Text("मार्गदर्शिकाएं, प्रपत्र व आधिकारिक संकल्प पत्र", fontSize = 11.sp, color = TextSecondary)
                                        }
                                    }
                                    IconButton(onClick = { selectedSection = null }, modifier = Modifier.size(28.dp)) {
                                        Icon(Icons.Default.Close, contentDescription = "बंद करें", tint = TextSecondary)
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    listOf(
                                        Triple("संगठनात्मक नियमावली एवं दिशानिर्देश 2026.pdf", "1.2 MB", "आधिकारिक दिशा-निर्देश एवं प्रोटोकॉल"),
                                        Triple("बूथ स्तरीय कार्यकर्ता दायित्व व प्रबंधन पत्रक.pdf", "850 KB", "बूथ स्तर पर कार्यप्रणाली एवं पन्ना प्रमुख हैंडबुक"),
                                        Triple("विधानसभा क्षेत्र विकास संकल्प पत्र 2026.pdf", "2.4 MB", "जनहित मुद्दे, विज़न डॉक्यूमेंट एवं संकल्प"),
                                        Triple("सदस्यता अभियान व मतदाता सत्यापन फॉर्म.pdf", "620 KB", "नवीन सदस्यता प्रपत्र व वोटर लिस्ट क्रॉस-चेक"),
                                        Triple("सोशल मीडिया व प्रचार मार्गदर्शिका 2026.pdf", "1.1 MB", "डिजिटल प्रचार, पोस्टर शेयरिंग व आईटी सेल नियम")
                                    ).forEach { (docName, size, desc) ->
                                        Surface(
                                            modifier = Modifier.fillMaxWidth(),
                                            shape = RoundedCornerShape(10.dp),
                                            color = Color(0xFFF8FAFC),
                                            border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                                        ) {
                                            Column(modifier = Modifier.padding(12.dp)) {
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.SpaceBetween
                                                ) {
                                                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                                        Box(
                                                            modifier = Modifier
                                                                .size(36.dp)
                                                                .background(Color(0xFFFEF2F2), CircleShape),
                                                            contentAlignment = Alignment.Center
                                                        ) {
                                                            Icon(Icons.Default.PictureAsPdf, contentDescription = null, tint = Color(0xFFDC2626), modifier = Modifier.size(20.dp))
                                                        }
                                                        Spacer(modifier = Modifier.width(10.dp))
                                                        Column {
                                                            Text(docName, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                                            Text("$size • $desc", fontSize = 10.sp, color = TextSecondary)
                                                        }
                                                    }
                                                }

                                                Spacer(modifier = Modifier.height(8.dp))

                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                                ) {
                                                    FilledTonalButton(
                                                        onClick = {
                                                            Toast.makeText(context, "$docName डाउनलोड हो रहा है...", Toast.LENGTH_SHORT).show()
                                                        },
                                                        shape = RoundedCornerShape(6.dp),
                                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                                        colors = ButtonDefaults.filledTonalButtonColors(containerColor = Color(0xFFE8F5E9)),
                                                        modifier = Modifier.weight(1f)
                                                    ) {
                                                        Icon(Icons.Default.Download, contentDescription = null, tint = RathoreGreen, modifier = Modifier.size(15.dp))
                                                        Spacer(modifier = Modifier.width(4.dp))
                                                        Text("डाउनलोड", fontSize = 11.sp, color = RathoreGreen, fontWeight = FontWeight.Bold)
                                                    }

                                                    Button(
                                                        onClick = {
                                                            try {
                                                                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                                                    type = "text/plain"
                                                                    putExtra(Intent.EXTRA_TEXT, "हेमंत राठौर वॉर रूम आधिकारिक दस्तावेज: $docName\n\nकृपया संगठन के दिशानिर्देशानुसार अध्ययन करें।")
                                                                }
                                                                context.startActivity(Intent.createChooser(shareIntent, "दस्तावेज साझा करें"))
                                                            } catch (e: Exception) {
                                                                Toast.makeText(context, "साझा करने में असमर्थ", Toast.LENGTH_SHORT).show()
                                                            }
                                                        },
                                                        shape = RoundedCornerShape(6.dp),
                                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00897B)),
                                                        modifier = Modifier.weight(1f)
                                                    ) {
                                                        Icon(Icons.Default.Share, contentDescription = null, tint = Color.White, modifier = Modifier.size(15.dp))
                                                        Spacer(modifier = Modifier.width(4.dp))
                                                        Text("शेयर करें", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
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
            }
        }
        }

        // Live Meeting Invitation Banner (बैठक की सूचना तभी दिखानी है जब बैठक शेड्यूल हो)
        if (meetingInfo.isScheduled && meetingInfo.status != MeetingStatus.ENDED) {
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("banner_meeting_join"),
                shape = RoundedCornerShape(14.dp),
                color = Color(0xFFE8F5E9),
                border = BorderStroke(1.5.dp, RathoreGreen)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(RathoreGreen),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.VideoCall, contentDescription = null, tint = Color.White)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = if (language == Language.HINDI) meetingInfo.titleHi else meetingInfo.titleEn,
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = TextPrimary
                                )
                                Text(
                                    text = "${meetingInfo.dateHi} • ${meetingInfo.timeHi} • ${meetingInfo.hostHi}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = RathoreGreen
                                )
                            }
                        }

                        if (isAdmin) {
                            FilledTonalButton(
                                onClick = {
                                    adminMeetingTitle = meetingInfo.titleHi
                                    adminMeetingDate = meetingInfo.dateHi
                                    adminMeetingTime = meetingInfo.timeHi
                                    adminMeetingAgenda = meetingInfo.agendaHi
                                    adminMeetingUrl = meetingInfo.meetingUrl ?: ""
                                    showCreateMeetingDialog = true
                                },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.filledTonalButtonColors(
                                    containerColor = RathoreGreen.copy(alpha = 0.15f),
                                    contentColor = RathoreGreen
                                ),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                modifier = Modifier.testTag("btn_admin_edit_meeting_banner")
                            ) {
                                Icon(Icons.Default.EditCalendar, contentDescription = null, modifier = Modifier.size(13.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("एडिट / लिंक", fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    if (!meetingInfo.meetingUrl.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    try {
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(meetingInfo.meetingUrl))
                                        context.startActivity(intent)
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "लिंक खोलने में असमर्थ", Toast.LENGTH_SHORT).show()
                                    }
                                }
                                .testTag("banner_meeting_external_url_card"),
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFFF0FDF4),
                            border = BorderStroke(1.dp, Color(0xFF86EFAC))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        Icons.Default.Link,
                                        contentDescription = null,
                                        tint = RathoreGreen,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = "बाहरी मीटिंग लिंक (Google Meet / Zoom)",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF15803D)
                                        )
                                        Text(
                                            text = meetingInfo.meetingUrl,
                                            fontSize = 10.sp,
                                            color = TextSecondary,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(
                                        onClick = {
                                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                            val clip = ClipData.newPlainText("Meeting Link", meetingInfo.meetingUrl)
                                            clipboard.setPrimaryClip(clip)
                                            Toast.makeText(context, "मीटिंग लिंक कॉपी किया गया!", Toast.LENGTH_SHORT).show()
                                        },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.ContentCopy,
                                            contentDescription = "लिंक कॉपी करें",
                                            tint = RathoreGreen,
                                            modifier = Modifier.size(15.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Icon(
                                        Icons.Default.OpenInNew,
                                        contentDescription = "लिंक खोलें",
                                        tint = RathoreGreen,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                val urlText = if (!meetingInfo.meetingUrl.isNullOrBlank()) {
                                    "\nसीधे जुड़ने का लिंक: ${meetingInfo.meetingUrl}\n"
                                } else ""
                                val shareText = "टीम राठौर विशेष वर्चुअल बैठक\nविषय: ${meetingInfo.titleHi}\nसमय: ${meetingInfo.dateHi} • ${meetingInfo.timeHi}$urlText\nबैठक में शामिल होने हेतु टीम राठौर ऐप से जुड़ें:\nhttps://teamrathore.in/meeting/join"
                                val sendIntent = Intent(Intent.ACTION_SEND).apply {
                                    putExtra(Intent.EXTRA_TEXT, shareText)
                                    type = "text/plain"
                                }
                                context.startActivity(Intent.createChooser(sendIntent, "मीटिंग आमंत्रण लिंक शेयर करें"))
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, RathoreGreen),
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, tint = RathoreGreen, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("लिंक शेयर करें", fontSize = 11.sp, color = RathoreGreen, fontWeight = FontWeight.Bold)
                        }

                        if (!meetingInfo.meetingUrl.isNullOrBlank()) {
                            OutlinedButton(
                                onClick = {
                                    try {
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(meetingInfo.meetingUrl))
                                        context.startActivity(intent)
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "लिंक खोलने में असमर्थ", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, Color(0xFF2563EB)),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF2563EB)),
                                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 6.dp)
                            ) {
                                Icon(Icons.Default.OpenInNew, contentDescription = null, tint = Color(0xFF2563EB), modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("बाहरी लिंक खोलें", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Button(
                            onClick = { viewModel.startMeetingFlow() },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = RathoreGreen),
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.VideoCall, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(AppStrings.get("join_meeting", language), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
        } else if (isAdmin) {
            // Admin quick card to schedule Google Meet when no meeting is scheduled
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            adminMeetingTitle = "विधानसभा स्तरीय कार्यकर्ता डिजिटल मीटिंग"
                            adminMeetingDate = "आज"
                            adminMeetingTime = "शाम 7:30 बजे"
                            adminMeetingAgenda = "आगामी जनसंवाद यात्रा की कार्ययोजना व रणनीति"
                            adminMeetingUrl = "https://meet.google.com/team-rathore-assembly"
                            showCreateMeetingDialog = true
                        }
                        .testTag("admin_quick_schedule_meeting_card"),
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    shadowElevation = 1.dp
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFEFF6FF)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_google_meet),
                                    contentDescription = "Google Meet",
                                    tint = Color.Unspecified,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "कोई लाइव बैठक अभी शेड्यूल नहीं है",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "Google Meet द्वारा नई बैठक शेड्यूल करें",
                                    fontSize = 10.5.sp,
                                    color = TextSecondary
                                )
                            }
                        }

                        Button(
                            onClick = {
                                adminMeetingTitle = "विधानसभा स्तरीय कार्यकर्ता डिजिटल मीटिंग"
                                adminMeetingDate = "आज"
                                adminMeetingTime = "शाम 7:30 बजे"
                                adminMeetingAgenda = "आगामी जनसंवाद यात्रा की कार्ययोजना व रणनीति"
                                adminMeetingUrl = "https://meet.google.com/team-rathore-assembly"
                                showCreateMeetingDialog = true
                            },
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0066DA))
                        ) {
                            Text("+ शेड्यूल करें", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Section details (Default feed when no section is explicitly opened)
        if (selectedSection == null) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "📌 ${AppStrings.get("tile_important_info", language)}",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimary
                    )
                    if (isAdmin) {
                        Button(
                            onClick = { showBroadcastNoticeDialog = true },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("नई सूचना", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            items(notices) { notice ->
                NoticeCard(
                    notice = notice,
                    language = language,
                    isAdmin = isAdmin,
                    onDelete = { viewModel.deleteNotice(notice.id) }
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "📅 ${AppStrings.get("tile_upcoming_programs", language)}",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimary
                    )
                    if (isAdmin) {
                        Button(
                            onClick = { showCreateProgramDialog = true },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = RathoreGreen),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("नया कार्यक्रम", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            if (programs.isEmpty()) {
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = Color.White,
                        border = BorderStroke(1.dp, BorderStroke)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.EventBusy, contentDescription = null, tint = TextTertiary, modifier = Modifier.size(48.dp))
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = if (language == Language.HINDI) "कोई आगामी कार्यक्रम नहीं है" else "No Upcoming Programs",
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (language == Language.HINDI)
                                    "नए संगठनात्मक बैठकों एवं दौरों की घोषणा जल्द की जाएगी।"
                                else
                                    "New organizational meetings and schedules will be announced shortly.",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                items(programs) { prog ->
                    ProgramCard(
                        prog = prog,
                        language = language,
                        isAdmin = isAdmin,
                        onRsvp = { status -> viewModel.rsvpProgram(prog.id, status) },
                        onEdit = {
                            editingProgram = prog
                            editProgTitle = prog.titleHi
                            editProgDate = prog.dateHi
                            editProgTime = prog.timeHi
                            editProgVenue = prog.venueHi
                            editProgDesc = prog.instructionsHi
                            editProgHighlighted = prog.isHighlighted
                        },
                        onDelete = { viewModel.deleteProgram(prog.id) }
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(70.dp))
        }
    }

    // Admin Dialog: Broadcast Notice
    if (showBroadcastNoticeDialog) {
        AlertDialog(
            onDismissRequest = { showBroadcastNoticeDialog = false },
            title = { Text("नई सूचना प्रसारित करें", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = adminNoticeTitle,
                        onValueChange = { adminNoticeTitle = it },
                        label = { Text("सूचना का शीर्षक...") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = adminNoticeBody,
                        onValueChange = { adminNoticeBody = it },
                        label = { Text("सूचना का पूर्ण विवरण...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = adminNoticeUrgent, onCheckedChange = { adminNoticeUrgent = it })
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            "अति-महत्वपूर्ण फ्लैश अलर्ट",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (adminNoticeUrgent) Color(0xFFDC2626) else TextPrimary
                        )
                    }

                    // Notice Photo Upload / Official Document Attachment Option
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (adminNoticeAttachPhoto) Color(0xFFE8F5E9) else Color(0xFFF8FAFC),
                        border = BorderStroke(1.dp, if (adminNoticeAttachPhoto) RathoreGreen else Color(0xFFCBD5E1)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { adminNoticeAttachPhoto = !adminNoticeAttachPhoto }
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = adminNoticeAttachPhoto,
                                onCheckedChange = { adminNoticeAttachPhoto = it },
                                colors = CheckboxDefaults.colors(checkedColor = RathoreGreen)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Text("आधिकारिक पत्र / फोटो संलग्न करें", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = if (adminNoticeAttachPhoto) RathoreGreen else TextPrimary)
                                Text("विज्ञप्ति या सरकारी आदेश की प्रतिलिपि जोड़ें", fontSize = 10.sp, color = TextSecondary)
                            }
                        }
                    }

                    if (adminNoticeAttachPhoto) {
                        OutlinedTextField(
                            value = adminNoticePhotoTitle,
                            onValueChange = { adminNoticePhotoTitle = it },
                            label = { Text("संलग्न पत्र / फोटो का नाम...") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (adminNoticeTitle.isNotBlank()) {
                            viewModel.broadcastNotice(
                                title = adminNoticeTitle,
                                message = adminNoticeBody,
                                isUrgent = adminNoticeUrgent,
                                photoAttached = adminNoticeAttachPhoto,
                                photoTitle = if (adminNoticeAttachPhoto) adminNoticePhotoTitle else null
                            )
                            adminNoticeTitle = ""
                            adminNoticeBody = ""
                            adminNoticeUrgent = false
                            adminNoticeAttachPhoto = false
                            showBroadcastNoticeDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RathoreGreen)
                ) {
                    Text("प्रसारित करें ✓")
                }
            },
            dismissButton = {
                TextButton(onClick = { showBroadcastNoticeDialog = false }) { Text("रद्द करें") }
            }
        )
    }

    // Admin Dialog: Add Program
    if (showCreateProgramDialog) {
        AlertDialog(
            onDismissRequest = { showCreateProgramDialog = false },
            title = { Text("नया कार्यक्रम जोड़ें", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = adminProgTitle,
                        onValueChange = { adminProgTitle = it },
                        label = { Text("कार्यक्रम का नाम...") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = adminProgDate,
                            onValueChange = { adminProgDate = it },
                            label = { Text("दिनांक") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = adminProgTime,
                            onValueChange = { adminProgTime = it },
                            label = { Text("समय") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    OutlinedTextField(
                        value = adminProgVenue,
                        onValueChange = { adminProgVenue = it },
                        label = { Text("स्थान / वेन्यू") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = adminProgDesc,
                        onValueChange = { adminProgDesc = it },
                        label = { Text("संक्षिप्त विवरण व निर्देश") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Flash Alert / Highlight Option for Program
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { adminProgHighlighted = !adminProgHighlighted }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = adminProgHighlighted,
                            onCheckedChange = { adminProgHighlighted = it },
                            colors = CheckboxDefaults.colors(checkedColor = Color(0xFFF59E0B))
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text("⚡ फ्लैश अलर्ट / मुख्य कार्यक्रम", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = if (adminProgHighlighted) Color(0xFFB45309) else TextPrimary)
                            Text("सूची में सबसे ऊपर विशेष रंग व फ्लैश बैज के साथ दिखेगा", fontSize = 10.sp, color = TextSecondary)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (adminProgTitle.isNotBlank()) {
                            viewModel.createProgram(
                                title = adminProgTitle,
                                date = adminProgDate,
                                time = adminProgTime,
                                venue = adminProgVenue,
                                instructions = adminProgDesc,
                                isHighlighted = adminProgHighlighted
                            )
                            adminProgTitle = ""
                            adminProgDesc = ""
                            adminProgHighlighted = false
                            showCreateProgramDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RathoreGreen)
                ) {
                    Text("जोड़ें ✓")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateProgramDialog = false }) { Text("रद्द करें") }
            }
        )
    }

    // Admin Dialog: Edit Program
    editingProgram?.let { prog ->
        AlertDialog(
            onDismissRequest = { editingProgram = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Edit, contentDescription = null, tint = RathoreOrangeDark)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("कार्यक्रम संपादित करें", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = editProgTitle,
                        onValueChange = { editProgTitle = it },
                        label = { Text("कार्यक्रम का नाम...") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = editProgDate,
                            onValueChange = { editProgDate = it },
                            label = { Text("दिनांक") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = editProgTime,
                            onValueChange = { editProgTime = it },
                            label = { Text("समय") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    OutlinedTextField(
                        value = editProgVenue,
                        onValueChange = { editProgVenue = it },
                        label = { Text("स्थान / वेन्यू") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editProgDesc,
                        onValueChange = { editProgDesc = it },
                        label = { Text("संक्षिप्त विवरण व निर्देश") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { editProgHighlighted = !editProgHighlighted }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = editProgHighlighted,
                            onCheckedChange = { editProgHighlighted = it },
                            colors = CheckboxDefaults.colors(checkedColor = Color(0xFFF59E0B))
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("⚡ फ्लैश अलर्ट / मुख्य कार्यक्रम", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = if (editProgHighlighted) Color(0xFFB45309) else TextPrimary)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (editProgTitle.isNotBlank()) {
                            viewModel.updateProgram(
                                id = prog.id,
                                title = editProgTitle,
                                date = editProgDate,
                                time = editProgTime,
                                venue = editProgVenue,
                                isHighlighted = editProgHighlighted
                            )
                            editingProgram = null
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RathoreGreen)
                ) {
                    Text("अपडेट सहेजें ✓")
                }
            },
            dismissButton = {
                TextButton(onClick = { editingProgram = null }) { Text("रद्द करें") }
            }
        )
    }

    // Admin Dialog: Schedule Live Meeting
    if (showCreateMeetingDialog) {
        AlertDialog(
            onDismissRequest = { showCreateMeetingDialog = false },
            title = { Text("नई लाइव मीटिंग निर्धारित करें", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = adminMeetingTitle,
                        onValueChange = { adminMeetingTitle = it },
                        label = { Text("मीटिंग का विषय...") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = adminMeetingDate,
                            onValueChange = { adminMeetingDate = it },
                            label = { Text("दिनांक") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = adminMeetingTime,
                            onValueChange = { adminMeetingTime = it },
                            label = { Text("समय") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Text("शेड्यूल प्रीसेट (काउंटडाउन):", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(
                            15 to "15 मिनट में (काउंटडाउन शुरू)",
                            30 to "30 मिनट में",
                            60 to "1 घंटे में"
                        ).forEach { (mins, label) ->
                            val isSelected = adminMeetingMinutesAhead == mins
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {
                                        adminMeetingMinutesAhead = mins
                                        adminMeetingDate = "आज"
                                        adminMeetingTime = "$mins मिनट बाद"
                                    },
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected) RathoreGreen else Color(0xFFF1F5F9),
                                border = BorderStroke(1.dp, if (isSelected) RathoreGreen else Color(0xFFCBD5E1))
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 10.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) Color.White else TextPrimary,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(vertical = 6.dp, horizontal = 2.dp)
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = adminMeetingAgenda,
                        onValueChange = { adminMeetingAgenda = it },
                        label = { Text("एजेंडा / मुख्य बिंदु...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp)
                    )

                    // Dedicated Google Meet In-App Integration
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFFF0FDF4),
                        border = BorderStroke(1.dp, Color(0xFFBBF7D0))
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_google_meet),
                                        contentDescription = "Google Meet",
                                        tint = Color.Unspecified,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = "Google Meet इन-ऐप एकीकरण",
                                            fontSize = 11.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF166534)
                                        )
                                        Text(
                                            text = "मीटिंग ऐप के अंदर ही लाइव चलेगी • Google Meet पावर्ड",
                                            fontSize = 10.sp,
                                            color = Color(0xFF15803D)
                                        )
                                    }
                                }

                                TextButton(
                                    onClick = {
                                        val chars = "abcdefghijklmnopqrstuvwxyz"
                                        val seg1 = (1..3).map { chars.random() }.joinToString("")
                                        val seg2 = (1..4).map { chars.random() }.joinToString("")
                                        val seg3 = (1..3).map { chars.random() }.joinToString("")
                                        adminMeetingUrl = "https://meet.google.com/$seg1-$seg2-$seg3"
                                    },
                                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(12.dp), tint = Color(0xFF15803D))
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text("नया कोड", fontSize = 10.5.sp, color = Color(0xFF15803D), fontWeight = FontWeight.Bold)
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            OutlinedTextField(
                                value = adminMeetingUrl.ifEmpty { "https://meet.google.com/team-rathore-assembly" },
                                onValueChange = { adminMeetingUrl = it },
                                label = { Text("Google Meet रूम URL / कोड") },
                                placeholder = { Text("https://meet.google.com/xxx-yyyy-zzz") },
                                leadingIcon = {
                                    Icon(Icons.Default.Link, contentDescription = null, tint = Color(0xFF00832D))
                                },
                                trailingIcon = {
                                    if (adminMeetingUrl.isNotBlank()) {
                                        IconButton(onClick = { adminMeetingUrl = "" }) {
                                            Icon(Icons.Default.Clear, contentDescription = "हटाएं", tint = TextTertiary)
                                        }
                                    }
                                },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_admin_meeting_url")
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = "ℹ️ कार्यकर्ताओं को अलग से Google Meet ऐप खोलने की जरूरत नहीं है। बैठक शुरू होते ही यह ऐप के अंदर पूरी तरह सुरक्षित रूप से लाइव होगी।",
                                fontSize = 9.5.sp,
                                color = Color(0xFF166534),
                                lineHeight = 13.sp
                            )
                        }
                    }

                    // Option to Cancel / End Current Scheduled Meeting if active
                    if (meetingInfo.isScheduled && meetingInfo.status != MeetingStatus.ENDED) {
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedButton(
                            onClick = {
                                viewModel.cancelOrEndMeeting()
                                showCreateMeetingDialog = false
                                Toast.makeText(context, "बैठक रद्द की गई और सूचना हटा दी गई", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFDC2626)),
                            border = BorderStroke(1.dp, Color(0xFFDC2626).copy(alpha = 0.5f)),
                            modifier = Modifier.fillMaxWidth().testTag("btn_cancel_scheduled_meeting")
                        ) {
                            Icon(Icons.Default.DeleteOutline, contentDescription = null, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("वर्तमान बैठक रद्द करें (नोटिफिकेशन हटाएं)", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (adminMeetingTitle.isNotBlank()) {
                            val scheduledMillis = System.currentTimeMillis() + adminMeetingMinutesAhead * 60 * 1000L
                            val finalUrl = adminMeetingUrl.trim().ifEmpty { "https://meet.google.com/team-rathore-assembly" }
                            viewModel.createLiveMeeting(
                                title = adminMeetingTitle,
                                date = adminMeetingDate,
                                time = adminMeetingTime,
                                agenda = adminMeetingAgenda,
                                scheduledTimeMillis = scheduledMillis,
                                meetingUrl = finalUrl
                            )
                            adminMeetingTitle = ""
                            adminMeetingAgenda = ""
                            adminMeetingUrl = ""
                            showCreateMeetingDialog = false
                            Toast.makeText(context, "Google Meet बैठक सफलतापूर्वक शेड्यूल की गई!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00832D))
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_google_meet),
                        contentDescription = null,
                        tint = Color.Unspecified,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Google Meet शेड्यूल करें ✓", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateMeetingDialog = false }) { Text("बंद करें") }
            }
        )
    }
}

@Composable
fun WarRoomMeetingView(
    viewModel: AppViewModel,
    language: Language,
    meetingInfo: MeetingInfo,
    isMicMuted: Boolean,
    isCameraOff: Boolean
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isAdmin = uiState.userProfile.role == UserRole.ADMIN

    var isHandRaised by remember { mutableStateOf(false) }
    var showInAppChatSheet by remember { mutableStateOf(false) }
    var showEndMeetingConfirmDialog by remember { mutableStateOf(false) }
    val meetingUrl = meetingInfo.meetingUrl?.ifBlank { "https://meet.google.com/team-rathore-assembly" } ?: "https://meet.google.com/team-rathore-assembly"

    val chatMessages = remember {
        mutableStateListOf(
            "रामअवतार (मुरैना): जय कांग्रेस! ✋",
            "दिनेश शर्मा (अम्बाह): सभी कार्यकर्ता साथी तैयार हैं।",
            "राजेश गुर्जर (पोरसा): आवाज़ स्पष्ट आ रही है राठौर साहब।"
        )
    }
    var newChatText by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(RathoreBackground)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Google Meet In-App Top Bar
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = Color.White,
            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
            shadowElevation = 2.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    IconButton(
                        onClick = { viewModel.resetMeeting() },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = RathoreGreen)
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        painter = painterResource(id = R.drawable.ic_google_meet),
                        contentDescription = "Google Meet",
                        tint = Color.Unspecified,
                        modifier = Modifier.size(26.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Google Meet इन-ऐप",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = Color(0xFF1E293B)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = Color(0xFFDCFCE7)
                            ) {
                                Text(
                                    text = "लाइव सुरक्षित",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF166534),
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = if (language == Language.HINDI) meetingInfo.titleHi else meetingInfo.titleEn,
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Copy URL Button
                    IconButton(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("Google Meet URL", meetingUrl)
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(context, "Google Meet लिंक कॉपी किया गया!", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "कॉपी करें", tint = Color(0xFF00832D), modifier = Modifier.size(16.dp))
                    }

                    // Open in External Google Meet App
                    IconButton(
                        onClick = {
                            try {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(meetingUrl))
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                Toast.makeText(context, "लिंक खोलने में असमर्थ", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(Icons.Default.OpenInNew, contentDescription = "Google Meet में खोलें", tint = Color(0xFF2563EB), modifier = Modifier.size(17.dp))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        when (meetingInfo.status) {
            MeetingStatus.PERMISSIONS_REQUIRED -> {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    shadowElevation = 3.dp
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFDCFCE7)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Videocam, contentDescription = null, tint = Color(0xFF166534), modifier = Modifier.size(30.dp))
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "माइक्रोफ़ोन और कैमरा अनुमति",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = TextPrimary
                        )
                        Text(
                            text = "Google Meet इन-ऐप सत्र में भाग लेने और बोलने के लिए कृपया अनुमति प्रदान करें।",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Button(
                            onClick = { viewModel.grantMeetingPermissions() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("btn_grant_permissions"),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00832D)),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("अनुमति दें और जुड़ें", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedButton(
                            onClick = { viewModel.grantMeetingPermissions() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("केवल सुनने के लिए जुड़ें (कैमरा बंद)", color = TextPrimary, fontSize = 12.sp)
                        }
                    }
                }
            }

            MeetingStatus.WAITING_ROOM -> {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    shadowElevation = 3.dp
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(color = Color(0xFF00832D))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Google Meet प्रतीक्षालय (Waiting Room)",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color(0xFF00832D)
                        )
                        Text(
                            text = "होस्ट माननीय नितेंद्र सिंह राठौर सत्र प्रारंभ कर रहे हैं। कृपया कुछ क्षण प्रतीक्षा करें...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Button(
                            onClick = { viewModel.enterLiveMeeting() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("btn_enter_live_meeting"),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00832D)),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.VideoCall, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("लाइव मीटिंग में प्रवेश करें", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            MeetingStatus.LIVE -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    // Host Video Feed Box (Main Stage)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(210.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFF0F172A)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF00832D)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Person, contentDescription = null, tint = Color.White, modifier = Modifier.size(38.dp))
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("माननीय नितेंद्र सिंह राठौर", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(7.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF22C55E))
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                Text("मुख्य वक्ता एवं संयोजक (संबोधन जारी)", color = Color.White.copy(alpha = 0.85f), fontSize = 11.sp)
                            }
                        }

                        // Top Left Live Badge
                        Surface(
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(10.dp),
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFFDC2626)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(Color.White)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("LIVE", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        // Top Right Room Code Pill
                        Surface(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(10.dp),
                            shape = RoundedCornerShape(6.dp),
                            color = Color.Black.copy(alpha = 0.5f)
                        ) {
                            Text(
                                text = meetingUrl.substringAfterLast("/"),
                                color = Color.White,
                                fontSize = 10.sp,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // 4 Participant Tiles Grid
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ParticipantTile(
                            name = "आप",
                            isYou = true,
                            isMicMuted = isMicMuted,
                            isCameraOff = isCameraOff,
                            isHandRaised = isHandRaised,
                            modifier = Modifier.weight(1f)
                        )
                        ParticipantTile(
                            name = "कार्यकर्ता (ग्वालियर)",
                            isYou = false,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ParticipantTile(
                            name = "कार्यकर्ता (मुरैना)",
                            isYou = false,
                            modifier = Modifier.weight(1f)
                        )
                        ParticipantTile(
                            name = "कार्यकर्ता (भिण्ड)",
                            isYou = false,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    // In-App Conferencing Bottom Controls Bar
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFF1E293B),
                        shadowElevation = 6.dp
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Mic Toggle
                            IconButton(
                                onClick = { viewModel.toggleMic() },
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(if (isMicMuted) Color(0xFFDC2626) else Color(0xFF334155))
                            ) {
                                Icon(
                                    if (isMicMuted) Icons.Default.MicOff else Icons.Default.Mic,
                                    contentDescription = "Mic",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            // Camera Toggle
                            IconButton(
                                onClick = { viewModel.toggleCamera() },
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(if (isCameraOff) Color(0xFFDC2626) else Color(0xFF334155))
                            ) {
                                Icon(
                                    if (isCameraOff) Icons.Default.VideocamOff else Icons.Default.Videocam,
                                    contentDescription = "Camera",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            // Raise Hand Button (✋)
                            IconButton(
                                onClick = {
                                    isHandRaised = !isHandRaised
                                    val msg = if (isHandRaised) "आपने बोलने के लिए हाथ उठाया ✋" else "हाथ नीचे किया गया"
                                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(if (isHandRaised) Color(0xFFF59E0B) else Color(0xFF334155))
                            ) {
                                Icon(
                                    Icons.Default.PanTool,
                                    contentDescription = "हाथ उठाएं",
                                    tint = if (isHandRaised) Color.Black else Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            // Live Chat Sheet Toggle (💬)
                            IconButton(
                                onClick = { showInAppChatSheet = true },
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF334155))
                            ) {
                                Icon(
                                    Icons.Default.Chat,
                                    contentDescription = "चैट",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            // Leave / End Meeting Button
                            Button(
                                onClick = { showEndMeetingConfirmDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                            ) {
                                Icon(Icons.Default.CallEnd, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(if (isAdmin) "समाप्त" else "छोड़ें", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            MeetingStatus.ENDED -> {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    shadowElevation = 3.dp
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFF1F5F9)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.MeetingRoom, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(32.dp))
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "बैठक समाप्त हो चुकी है",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = TextPrimary
                        )
                        Text(
                            text = "Google Meet इन-ऐप सत्र समाप्त हो गया है। बैठक में सक्रिय सहभागिता के लिए धन्यवाद।",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 6.dp)
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Button(
                            onClick = { viewModel.resetMeeting() },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = RathoreGreen)
                        ) {
                            Text("वॉर रूम में वापस जाएं", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
            else -> {}
        }
    }

    // In-App Live Meeting Chat Dialog
    if (showInAppChatSheet) {
        AlertDialog(
            onDismissRequest = { showInAppChatSheet = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Forum, contentDescription = null, tint = Color(0xFF00832D))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("लाइव बैठक संवाद (इन-ऐप चैट)", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                    ) {
                        items(chatMessages) { msg ->
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp),
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFFF1F5F9)
                            ) {
                                Text(text = msg, fontSize = 11.5.sp, modifier = Modifier.padding(8.dp), color = TextPrimary)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Quick reactions
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("जय कांग्रेस! ✋", "राठौर साहब जिंदाबाद!", "मुरैना तैयार है!").forEach { reaction ->
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0xFFDCFCE7),
                                modifier = Modifier.clickable {
                                    chatMessages.add("आप: $reaction")
                                }
                            ) {
                                Text(text = reaction, fontSize = 9.5.sp, color = Color(0xFF166534), fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = newChatText,
                            onValueChange = { newChatText = it },
                            placeholder = { Text("संदेश लिखें...", fontSize = 12.sp) },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        IconButton(
                            onClick = {
                                if (newChatText.isNotBlank()) {
                                    chatMessages.add("आप: ${newChatText.trim()}")
                                    newChatText = ""
                                }
                            }
                        ) {
                            Icon(Icons.Default.Send, contentDescription = "भेजें", tint = Color(0xFF00832D))
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showInAppChatSheet = false }) {
                    Text("बंद करें")
                }
            }
        )
    }

    // Leave / End Meeting Confirm Dialog
    if (showEndMeetingConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showEndMeetingConfirmDialog = false },
            title = {
                Text(if (isAdmin) "मीटिंग समाप्त करें / छोड़ें" else "मीटिंग छोड़ें?", fontWeight = FontWeight.Bold)
            },
            text = {
                Text(
                    if (isAdmin)
                        "क्या आप केवल स्वयं मीटिंग छोड़ना चाहते हैं, या सभी कार्यकर्ताओं के लिए यह बैठक समाप्त कर सूचना हटाना चाहते हैं?"
                    else
                        "क्या आप इस लाइव Google Meet सत्र से बाहर जाना चाहते हैं?"
                )
            },
            confirmButton = {
                if (isAdmin) {
                    Button(
                        onClick = {
                            showEndMeetingConfirmDialog = false
                            viewModel.cancelOrEndMeeting()
                            Toast.makeText(context, "बैठक सभी के लिए समाप्त कर दी गई और सूचना हटा दी गई।", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
                    ) {
                        Text("सभी के लिए समाप्त करें", fontWeight = FontWeight.Bold)
                    }
                } else {
                    Button(
                        onClick = {
                            showEndMeetingConfirmDialog = false
                            viewModel.leaveMeeting()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
                    ) {
                        Text("मीटिंग छोड़ें", fontWeight = FontWeight.Bold)
                    }
                }
            },
            dismissButton = {
                Row {
                    if (isAdmin) {
                        OutlinedButton(
                            onClick = {
                                showEndMeetingConfirmDialog = false
                                viewModel.leaveMeeting()
                            }
                        ) {
                            Text("केवल मैं छोड़ूं")
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                    }
                    TextButton(onClick = { showEndMeetingConfirmDialog = false }) {
                        Text("रद्द करें")
                    }
                }
            }
        )
    }
}

@Composable
private fun ParticipantTile(
    name: String,
    isYou: Boolean,
    isMicMuted: Boolean = false,
    isCameraOff: Boolean = false,
    isHandRaised: Boolean = false,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(95.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFF1E293B)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(if (isYou) Color(0xFF00832D) else Color(0xFF475569)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Person, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(name, color = Color.White, style = MaterialTheme.typography.labelSmall)
        }

        // Top Right Status Badges
        Row(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isHandRaised) {
                Surface(
                    shape = CircleShape,
                    color = Color(0xFFF59E0B),
                    modifier = Modifier.size(16.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text("✋", fontSize = 9.sp)
                    }
                }
                Spacer(modifier = Modifier.width(2.dp))
            }
            if (isMicMuted) {
                Icon(
                    Icons.Default.MicOff,
                    contentDescription = null,
                    tint = Color(0xFFEF4444),
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}

@Composable
private fun WarRoomTile(
    title: String,
    icon: ImageVector,
    iconTint: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    badge: String? = null,
    tag: String = ""
) {
    Surface(
        modifier = modifier
            .height(100.dp)
            .clickable { onClick() }
            .testTag(tag),
        shape = RoundedCornerShape(14.dp),
        color = Color.White,
        border = BorderStroke(1.dp, BorderStroke),
        shadowElevation = 1.dp
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(28.dp))
                if (badge != null) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = iconTint.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = badge,
                            color = iconTint,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, fontSize = 13.sp),
                color = TextPrimary
            )
        }
    }
}

@Composable
private fun RsvpButton(
    text: String,
    isSelected: Boolean,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(38.dp),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) color else Color(0xFFF1F5F9),
            contentColor = if (isSelected) Color.White else TextPrimary
        ),
        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp)
    ) {
        Text(text, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium)
    }
}

@Composable
private fun NoticeCard(
    notice: ImportantNotice,
    language: Language,
    isAdmin: Boolean,
    onDelete: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = if (notice.isUrgent) Color(0xFFFFF3E0) else Color.White,
        border = BorderStroke(1.dp, if (notice.isUrgent) RathoreOrange else BorderStroke)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (language == Language.HINDI) notice.titleHi else notice.titleEn,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = if (notice.isUrgent) RathoreOrangeDark else TextPrimary,
                    modifier = Modifier.weight(1f)
                )
                if (notice.isUrgent) {
                    Surface(shape = RoundedCornerShape(4.dp), color = RathoreOrange) {
                        Text(
                            "जरूरी",
                            color = Color.White,
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
            Text(
                text = notice.bodyHi,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                modifier = Modifier.padding(vertical = 4.dp)
            )

            if (notice.photoAttached) {
                Spacer(modifier = Modifier.height(4.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFF1F5F9),
                    border = BorderStroke(1.dp, Color(0xFFCBD5E1))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.AttachFile, contentDescription = null, tint = RathoreGreen, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = notice.photoTitle ?: "आधिकारिक_विज्ञप्ति_आदेश.jpg",
                            fontSize = 11.sp,
                            color = TextPrimary,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Text("संलग्न पत्र ✓", fontSize = 10.sp, color = RathoreGreen, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(notice.dateHi, style = MaterialTheme.typography.labelSmall, color = TextTertiary)
                if (isAdmin) {
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = Color(0xFFDC2626), modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun ProgramCard(
    prog: ProgramItem,
    language: Language,
    isAdmin: Boolean,
    onRsvp: (RsvpStatus) -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = if (prog.isHighlighted) Color(0xFFFFFBEB) else Color.White,
        border = BorderStroke(if (prog.isHighlighted) 1.5.dp else 1.dp, if (prog.isHighlighted) Color(0xFFF59E0B) else BorderStroke)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (language == Language.HINDI) prog.titleHi else prog.titleEn,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = if (prog.isHighlighted) Color(0xFFB45309) else RathoreGreen,
                    modifier = Modifier.weight(1f)
                )
                if (prog.isHighlighted) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = Color(0xFFF59E0B)
                    ) {
                        Text(
                            text = "⚡ फ्लैश अलर्ट",
                            color = Color.White,
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
            Row(
                modifier = Modifier.padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Schedule, contentDescription = null, tint = TextTertiary, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("${prog.dateHi} • ${prog.timeHi}", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LocationOn, contentDescription = null, tint = RathoreOrange, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(prog.venueHi, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            }

            if (!isAdmin) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    RsvpButton(
                        text = AppStrings.get("rsvp_attending", language),
                        isSelected = prog.rsvpStatus == RsvpStatus.ATTENDING,
                        color = RathoreGreen,
                        onClick = { onRsvp(RsvpStatus.ATTENDING) },
                        modifier = Modifier.weight(1f)
                    )
                    RsvpButton(
                        text = AppStrings.get("rsvp_maybe", language),
                        isSelected = prog.rsvpStatus == RsvpStatus.MAYBE,
                        color = RathoreOrange,
                        onClick = { onRsvp(RsvpStatus.MAYBE) },
                        modifier = Modifier.weight(1f)
                    )
                    RsvpButton(
                        text = AppStrings.get("rsvp_cant", language),
                        isSelected = prog.rsvpStatus == RsvpStatus.NOT_ATTENDING,
                        color = TextTertiary,
                        onClick = { onRsvp(RsvpStatus.NOT_ATTENDING) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            if (isAdmin) {
                Spacer(modifier = Modifier.height(6.dp))
                Divider(color = BorderStroke, thickness = 0.6.dp)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = onEdit,
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = RathoreOrangeDark, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("संपादित करें", fontSize = 11.sp, color = RathoreOrangeDark, fontWeight = FontWeight.Bold)
                    }
                    TextButton(
                        onClick = onDelete,
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = Color(0xFFDC2626), modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("हटाएं", fontSize = 11.sp, color = Color(0xFFDC2626))
                    }
                }
            }
        }
    }
}
