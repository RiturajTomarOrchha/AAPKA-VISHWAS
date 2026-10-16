package com.example.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.model.CitizenRequest
import com.example.model.RequestStatus
import com.example.ui.theme.BorderStroke
import com.example.ui.theme.RathoreGreen
import com.example.ui.theme.RathoreOrange
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun AdminGrievanceEditorDialog(
    isOpen: Boolean,
    request: CitizenRequest,
    onDismiss: () -> Unit,
    onSave: (newStatus: RequestStatus, reply: String, photoUri: String?, officerName: String) -> Unit
) {
    if (!isOpen) return

    var selectedStatus by remember(request) { mutableStateOf(request.status) }
    var replyText by remember(request) { mutableStateOf(request.adminResponseHi ?: "") }
    var resolutionPhotoUri by remember(request) { mutableStateOf(request.adminResolutionPhotoUri) }
    var officerName by remember(request) { mutableStateOf(request.adminOfficerName) }

    // Zero-permission Photo Picker for Resolution Photo proof
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            resolutionPhotoUri = uri.toString()
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.90f)
                .clip(RoundedCornerShape(20.dp)),
            color = Color.White,
            shadowElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Header
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
                                .background(RathoreGreen.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.EditNote,
                                contentDescription = null,
                                tint = RathoreGreen,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "शिकायत संपादन एवं समाधान उत्तर",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium,
                                color = TextPrimary
                            )
                            Text(
                                text = "ट्रैकिंग ID: ${request.id} • ${request.categoryHi}",
                                style = MaterialTheme.typography.labelSmall,
                                color = RathoreGreen,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = BorderStroke)

                // Scrollable Body
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // 1. Citizen's Original Complaint Preview
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFF8FAFC),
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "नागरिक: ${request.citizenName}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = TextPrimary
                                )
                                Text(
                                    text = request.dateHi,
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            }
                            if (!request.citizenPhone.isNullOrBlank()) {
                                Text(
                                    text = "संपर्क: ${request.citizenPhone} • ${request.location}",
                                    fontSize = 11.sp,
                                    color = TextSecondary,
                                    modifier = Modifier.padding(vertical = 2.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "समस्या: \"${request.message}\"",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF334155),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    // 2. Change Status Section
                    Column {
                        Text(
                            text = "शिकायत की वर्तमान स्थिति (Status):",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleSmall,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(
                                RequestStatus.SUBMITTED to "दर्ज",
                                RequestStatus.IN_REVIEW to "समीक्षाधीन",
                                RequestStatus.ACTION_TAKEN to "कार्यवाही जारी",
                                RequestStatus.RESOLVED to "समाधान हो गया ✓"
                            ).forEach { (status, label) ->
                                val isSelected = selectedStatus == status
                                FilterChip(
                                    selected = isSelected,
                                    onClick = {
                                        selectedStatus = status
                                        if (status == RequestStatus.RESOLVED && replyText.isBlank()) {
                                            replyText = "आपकी समस्या का पूर्ण समाधान कर दिया गया है। संबंधित कार्य की स्थिति सत्यापित है।"
                                        }
                                    },
                                    label = {
                                        Text(
                                            text = label,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            fontSize = 12.sp
                                        )
                                    },
                                    leadingIcon = if (isSelected) {
                                        { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                                    } else null,
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = if (status == RequestStatus.RESOLVED) RathoreGreen else RathoreOrange,
                                        selectedLabelColor = Color.White
                                    )
                                )
                            }
                        }
                    }

                    // 3. Admin / Office Reply Text Input
                    Column {
                        Text(
                            text = "कार्यालय का उत्तर / समाधान विवरण *:",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleSmall,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = replyText,
                            onValueChange = { replyText = it },
                            placeholder = { Text("नागरिक को समाधान की पूरी जानकारी लिखें (उदा. PWD विभाग द्वारा सड़क की मरम्मत पूर्ण कराई गई)...") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(110.dp)
                                .testTag("input_grievance_reply"),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }

                    // 4. Resolution Photo Attachment (Critical User Requirement)
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "समाधान प्रमाण फोटो (Proof Photo):",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleSmall,
                                color = TextPrimary
                            )
                            if (resolutionPhotoUri != null) {
                                TextButton(
                                    onClick = { resolutionPhotoUri = null },
                                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp)
                                ) {
                                    Text("फोटो हटाएं", color = Color(0xFFDC2626), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        if (resolutionPhotoUri != null) {
                            // Live Preview of selected resolution photo
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(160.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .border(1.dp, RathoreGreen, RoundedCornerShape(10.dp)),
                                color = Color(0xFFF1F5F9)
                            ) {
                                Box(modifier = Modifier.fillMaxSize()) {
                                    AsyncImage(
                                        model = resolutionPhotoUri,
                                        contentDescription = "Resolution Proof Photo",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )

                                    Surface(
                                        modifier = Modifier
                                            .align(Alignment.BottomStart)
                                            .padding(8.dp),
                                        shape = RoundedCornerShape(6.dp),
                                        color = Color.Black.copy(alpha = 0.65f)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(Icons.Default.Verified, contentDescription = null, tint = Color(0xFF4ADE80), modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("समाधान फोटो संलग्न ✓", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        } else {
                            // Attachment Picker button
                            OutlinedButton(
                                onClick = {
                                    photoPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp)
                                    .testTag("btn_pick_resolution_photo"),
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(1.dp, RathoreGreen)
                            ) {
                                Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, tint = RathoreGreen)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "गैलरी से समाधान फोटो चुनें",
                                    color = RathoreGreen,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Preset quick demo sample photos
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp)
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf(
                                "सड़क मरम्मत प्रमाण" to "https://picsum.photos/seed/road_done/600/400",
                                "बिजली आपूर्ति प्रमाण" to "https://picsum.photos/seed/power_done/600/400",
                                "पेयजल कार्य प्रमाण" to "https://picsum.photos/seed/water_done/600/400"
                            ).forEach { (label, uri) ->
                                AssistChip(
                                    onClick = { resolutionPhotoUri = uri },
                                    label = { Text(label, fontSize = 10.sp) },
                                    leadingIcon = { Icon(Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.size(12.dp)) }
                                )
                            }
                        }
                    }

                    // 5. Officer Designation / Department Name
                    Column {
                        Text(
                            text = "उत्तरदाता / समीक्षा अधिकारी:",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleSmall,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = officerName,
                            onValueChange = { officerName = it },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            singleLine = true
                        )
                    }

                    // Notice explaining the notification push
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFFEFF6FF),
                        border = BorderStroke(1.dp, Color(0xFFBFDBFE))
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = Color(0xFF2563EB), modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "सुरक्षित करने पर नागरिक को तुरंत ऐप में नोटिफिकेशन प्राप्त होगा: 'आपकी समस्या का समाधान हो गया है'",
                                fontSize = 11.sp,
                                color = Color(0xFF1E40AF),
                                lineHeight = 16.sp
                            )
                        }
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = BorderStroke)

                // Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("रद्द करें", color = TextSecondary)
                    }

                    Button(
                        onClick = {
                            onSave(
                                selectedStatus,
                                replyText.ifBlank { "आपकी समस्या पर यथोचित कार्यवाही पूर्ण की गई है।" },
                                resolutionPhotoUri,
                                officerName
                            )
                        },
                        modifier = Modifier
                            .weight(2f)
                            .testTag("btn_save_grievance_reply"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = RathoreGreen)
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "उत्तर व फोटो सुरक्षित करें ✓",
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}
