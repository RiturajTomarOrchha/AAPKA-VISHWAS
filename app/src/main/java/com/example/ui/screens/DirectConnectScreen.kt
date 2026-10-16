package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.*
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.model.CitizenRequest
import com.example.model.Language
import com.example.model.RequestStatus
import com.example.model.UserRole
import com.example.ui.components.AdminGrievanceEditorDialog
import com.example.ui.theme.*
import com.example.util.AppStrings
import com.example.viewmodel.AppScreen
import com.example.viewmodel.AppViewModel

@Composable
fun DirectConnectScreen(
    viewModel: AppViewModel,
    language: Language,
    requests: List<CitizenRequest>
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isAdmin = uiState.userProfile.role == UserRole.ADMIN

    var selectedCategory by remember { mutableStateOf("सड़क एवं नाली मरम्मत") }
    var grievanceMessage by remember { mutableStateOf("") }
    var photoAttached by remember { mutableStateOf(false) }
    var attachedFileName by remember { mutableStateOf("site_inspection_photo.jpg") }
    var showRequestSubmittedNotice by remember { mutableStateOf(false) }
    var editingRequest by remember { mutableStateOf<CitizenRequest?>(null) }
    var selectedStatusFilter by remember { mutableStateOf<RequestStatus?>(null) }

    val categories = listOf(
        "सड़क एवं नाली मरम्मत",
        "बिजली व ट्रांसफार्मर",
        "पेयजल व हैंडपंप",
        "स्वास्थ्य व अस्पताल",
        "शिक्षा व स्कूल",
        "अन्य क्षेत्रीय मांग"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(RathoreBackground)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // App Bar
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
                    modifier = Modifier.testTag("btn_back_connect")
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = RathoreGreen)
                }
                Spacer(modifier = Modifier.width(6.dp))
                Column {
                    Text(
                        text = if (language == Language.HINDI) "सीधा संवाद" else "Direct Connect",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = RathoreGreen
                    )
                    Text(
                        text = if (language == Language.HINDI) "विधायक आपके पास • जनसमस्या निवारण" else "Citizen Grievance & Connect",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary
                    )
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Hero Intro Banner
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = RathoreGreen,
                    shadowElevation = 2.dp
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(RathoreOrange),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.VolunteerActivism, contentDescription = null, tint = Color.White)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = AppStrings.get("vidhayak_title", language),
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Black,
                                        fontSize = 17.sp
                                    ),
                                    color = Color.White
                                )
                                Text(
                                    text = AppStrings.get("vidhayak_sub", language),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White.copy(alpha = 0.9f)
                                )
                            }
                        }
                    }
                }
            }

            // Section 1: Submit New Request Card
            item {
                if (isAdmin) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xFFF0FDF4),
                        shadowElevation = 2.dp,
                        border = BorderStroke(1.dp, Color(0xFFBBF7D0))
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(RathoreGreen),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "जनसमस्या निवारण एवं समीक्षा केंद्र",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = RathoreGreen
                                )
                                Text(
                                    text = "नागरिकों एवं कार्यकर्ताओं द्वारा दर्ज कुल ${requests.size} शिकायतों की स्थिति यहाँ देखें।",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary
                                )
                            }
                        }
                    }
                } else {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    color = Color.White,
                    shadowElevation = 2.dp,
                    border = BorderStroke(1.dp, BorderStroke)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = AppStrings.get("submit_request", language),
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = RathoreGreen
                        )
                        Text(
                            text = AppStrings.get("request_category", language),
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                            modifier = Modifier.padding(top = 4.dp, bottom = 8.dp)
                        )

                        // Category Pills Horizontal Flow
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            categories.forEach { cat ->
                                val isSelected = selectedCategory == cat
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { selectedCategory = cat },
                                    label = { Text(cat, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = RathoreGreen,
                                        selectedLabelColor = Color.White
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Grievance message box
                        OutlinedTextField(
                            value = grievanceMessage,
                            onValueChange = { grievanceMessage = it },
                            label = { Text(AppStrings.get("request_desc", language)) },
                            placeholder = { Text("अपनी समस्या, स्थान या मांग का पूरा विवरण दें...") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .testTag("input_grievance_desc"),
                            shape = RoundedCornerShape(10.dp)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Photo attachment area with upload / remove capabilities
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { photoAttached = !photoAttached },
                            shape = RoundedCornerShape(8.dp),
                            color = if (photoAttached) Color(0xFFE8F5E9) else Color(0xFFF8FAFC),
                            border = BorderStroke(1.dp, if (photoAttached) RathoreGreen else BorderStroke)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        if (photoAttached) Icons.Default.CheckCircle else Icons.Default.AddPhotoAlternate,
                                        contentDescription = null,
                                        tint = if (photoAttached) RathoreGreen else RathoreOrange
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = if (photoAttached) "दस्तावेज / फोटो संलग्न है" else AppStrings.get("attach_photo_doc", language),
                                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                                            color = if (photoAttached) RathoreGreen else TextPrimary
                                        )
                                        if (photoAttached) {
                                            Text(
                                                text = attachedFileName,
                                                style = MaterialTheme.typography.labelSmall,
                                                color = TextSecondary
                                            )
                                        }
                                    }
                                }
                                if (photoAttached) {
                                    Text(
                                        text = "हटाएं",
                                        color = Color(0xFFDC2626),
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                if (grievanceMessage.isNotBlank()) {
                                    viewModel.submitCitizenRequest(
                                        categoryHi = selectedCategory,
                                        message = grievanceMessage,
                                        attachment = if (photoAttached) attachedFileName else null
                                    )
                                    grievanceMessage = ""
                                    photoAttached = false
                                    showRequestSubmittedNotice = true
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("btn_submit_request"),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = RathoreOrange)
                        ) {
                            Icon(Icons.Default.Send, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (language == Language.HINDI) "अनुरोध प्रेषित करें ✓" else "Submit Request ✓",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                        }

                        if (showRequestSubmittedNotice) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFFE8F5E9),
                                border = BorderStroke(1.dp, RathoreGreen)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = RathoreGreen, modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "समस्या सफलतापूर्वक दर्ज हो गई है। ट्रैकिंग आईडी जनरेट कर दी गई है।",
                                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                        color = RathoreGreen
                                    )
                                }
                            }
                        }
                    }
                }
                }
            }

            // Section 2: Previous Requests Status Tracking
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = AppStrings.get("track_requests", language),
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = TextPrimary
                        )
                        Text(
                            text = "कुल ${requests.size} शिकायतें",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = RathoreGreen
                        )
                    }

                    // Status Filters
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        FilterChip(
                            selected = selectedStatusFilter == null,
                            onClick = { selectedStatusFilter = null },
                            label = { Text("सभी (${requests.size})", fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = RathoreGreen,
                                selectedLabelColor = Color.White
                            )
                        )

                        listOf(
                            RequestStatus.SUBMITTED to "दर्ज",
                            RequestStatus.IN_REVIEW to "समीक्षाधीन",
                            RequestStatus.ACTION_TAKEN to "कार्यवाही जारी",
                            RequestStatus.RESOLVED to "समाधान हो गया ✓"
                        ).forEach { (status, label) ->
                            val count = requests.count { it.status == status }
                            val isSelected = selectedStatusFilter == status
                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedStatusFilter = if (isSelected) null else status },
                                label = { Text("$label ($count)", fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = if (status == RequestStatus.RESOLVED) Color(0xFF16A34A) else RathoreOrange,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                }
            }

            val filteredRequests = if (selectedStatusFilter != null) {
                requests.filter { it.status == selectedStatusFilter }
            } else {
                requests
            }

            if (filteredRequests.isEmpty()) {
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = Color.White
                    ) {
                        Box(modifier = Modifier.padding(24.dp), contentAlignment = Alignment.Center) {
                            Text("इस स्थिति में कोई शिकायत नहीं है।", color = TextSecondary, fontSize = 12.sp)
                        }
                    }
                }
            } else {
                items(filteredRequests) { req ->
                    RequestStatusCard(
                        req = req,
                        language = language,
                        onEdit = { editingRequest = req }
                    )
                }
            }

            // Section 3: Emergency Helpline
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFFFFBEB),
                    border = BorderStroke(1.dp, Color(0xFFFDE68A))
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.SupportAgent, contentDescription = null, tint = RathoreOrange, modifier = Modifier.size(32.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("कार्यालय हेल्पलाइन संपर्क", fontWeight = FontWeight.Bold, color = TextPrimary)
                            Text("हेल्पलाइन: +91 751-240-5678 • प्रातः 9 से शाम 7 बजे तक", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(60.dp))
            }
        }

        // Admin Grievance Editor Dialog
        if (editingRequest != null) {
            AdminGrievanceEditorDialog(
                isOpen = true,
                request = editingRequest!!,
                onDismiss = { editingRequest = null },
                onSave = { newStatus, reply, photoUri, officerName ->
                    viewModel.updateCitizenGrievanceResponse(
                        requestId = editingRequest!!.id,
                        newStatus = newStatus,
                        reply = reply,
                        resolutionPhotoUri = photoUri,
                        officerName = officerName
                    )
                    editingRequest = null
                }
            )
        }
    }
}

@Composable
fun RequestStatusCard(
    req: CitizenRequest,
    language: Language,
    onEdit: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = Color.White,
        shadowElevation = 2.dp,
        border = BorderStroke(1.dp, BorderStroke)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Tracking ID, Citizen Name & Status Chip
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFFF1F5F9)
                    ) {
                        Text(
                            text = "ID: ${req.id}",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = TextPrimary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = req.dateHi,
                        style = MaterialTheme.typography.labelSmall,
                        color = TextTertiary
                    )
                }

                // Status Badge
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = when (req.status) {
                        RequestStatus.RESOLVED -> Color(0xFFDCFCE7)
                        RequestStatus.ACTION_TAKEN -> Color(0xFFFFEDD5)
                        RequestStatus.IN_REVIEW -> Color(0xFFDBEAFE)
                        RequestStatus.SUBMITTED -> Color(0xFFF1F5F9)
                    },
                    border = BorderStroke(
                        0.5.dp,
                        when (req.status) {
                            RequestStatus.RESOLVED -> Color(0xFF16A34A)
                            RequestStatus.ACTION_TAKEN -> RathoreOrange
                            RequestStatus.IN_REVIEW -> Color(0xFF2563EB)
                            RequestStatus.SUBMITTED -> Color(0xFF94A3B8)
                        }
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (req.status == RequestStatus.RESOLVED) {
                            Icon(Icons.Default.Verified, contentDescription = null, tint = Color(0xFF16A34A), modifier = Modifier.size(13.dp))
                            Spacer(modifier = Modifier.width(3.dp))
                        }
                        Text(
                            text = if (language == Language.HINDI) req.status.titleHi else req.status.titleEn,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = when (req.status) {
                                RequestStatus.RESOLVED -> Color(0xFF15803D)
                                RequestStatus.ACTION_TAKEN -> Color(0xFFC2410C)
                                RequestStatus.IN_REVIEW -> Color(0xFF1D4ED8)
                                RequestStatus.SUBMITTED -> Color(0xFF475569)
                            }
                        )
                    }
                }
            }

            // Citizen Info
            Text(
                text = "नागरिक: ${req.citizenName} • ${req.location}",
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = TextSecondary,
                modifier = Modifier.padding(top = 6.dp)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = if (language == Language.HINDI) req.categoryHi else req.categoryEn,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = RathoreGreen
            )

            Text(
                text = req.message,
                style = MaterialTheme.typography.bodySmall,
                color = TextPrimary,
                modifier = Modifier.padding(vertical = 4.dp)
            )

            if (!req.attachmentName.isNullOrBlank()) {
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = Color(0xFFF8FAFC),
                    border = BorderStroke(0.5.dp, Color(0xFFCBD5E1)),
                    modifier = Modifier.padding(bottom = 6.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.AttachFile, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(req.attachmentName, fontSize = 10.sp, color = TextSecondary)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Stepper
            RequestProgressStepper(currentStatus = req.status)

            // Admin response section
            if (!req.adminResponseHi.isNullOrBlank()) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFFF0FDF4),
                    border = BorderStroke(1.dp, Color(0xFFBBF7D0))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "कार्यालय द्वारा समाधान व उत्तर:",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = Color(0xFF15803D)
                            )
                            if (!req.resolutionDateHi.isNullOrBlank()) {
                                Text(
                                    text = req.resolutionDateHi,
                                    fontSize = 10.sp,
                                    color = Color(0xFF166534)
                                )
                            }
                        }

                        Text(
                            text = req.adminResponseHi,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF14532D),
                            modifier = Modifier.padding(top = 4.dp),
                            lineHeight = 18.sp
                        )

                        Text(
                            text = "द्वारा: ${req.adminOfficerName}",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF15803D),
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }

            // Resolution Proof Photo (User Requirement: Proof photo display)
            if (!req.adminResolutionPhotoUri.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(170.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .border(1.dp, Color(0xFF86EFAC), RoundedCornerShape(10.dp)),
                    color = Color(0xFFF1F5F9)
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        AsyncImage(
                            model = req.adminResolutionPhotoUri,
                            contentDescription = "Resolution Proof Photo",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = androidx.compose.ui.layout.ContentScale.Crop
                        )

                        Surface(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(8.dp),
                            shape = RoundedCornerShape(6.dp),
                            color = Color.Black.copy(alpha = 0.7f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Verified, contentDescription = null, tint = Color(0xFF4ADE80), modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("समाधान प्रमाण फोटो (Proof Attached) ✓", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Admin Edit & Reply Action Button
            OutlinedButton(
                onClick = onEdit,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(42.dp)
                    .testTag("btn_edit_grievance_${req.id}"),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, RathoreGreen),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Icon(Icons.Default.EditNote, contentDescription = null, tint = RathoreGreen, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "संपादित करें एवं समाधान उत्तर/फोटो जोड़ें",
                    color = RathoreGreen,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun RequestProgressStepper(currentStatus: RequestStatus) {
    val statuses = listOf(
        RequestStatus.SUBMITTED,
        RequestStatus.IN_REVIEW,
        RequestStatus.ACTION_TAKEN,
        RequestStatus.RESOLVED
    )
    val currentIndex = statuses.indexOf(currentStatus)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        statuses.forEachIndexed { index, status ->
            val isDone = index <= currentIndex
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(if (isDone) RathoreGreen else Color(0xFFE2E8F0)),
                    contentAlignment = Alignment.Center
                ) {
                    if (isDone) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                    }
                }
                Text(
                    text = status.titleHi,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 9.sp,
                        fontWeight = if (isDone) FontWeight.Bold else FontWeight.Normal
                    ),
                    color = if (isDone) RathoreGreen else TextTertiary,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    maxLines = 1
                )
            }
        }
    }
}
