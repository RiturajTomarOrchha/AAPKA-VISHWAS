package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.sheets.GoogleSheetsSyncManager
import com.example.model.GoogleSheetSyncStatus
import com.example.model.ManagedUser
import com.example.model.UserRole
import com.example.ui.theme.*
import com.example.viewmodel.AppViewModel

@Composable
fun GoogleSheetsDatabaseHub(
    viewModel: AppViewModel,
    syncStatus: GoogleSheetSyncStatus,
    users: List<ManagedUser>,
    onSyncRequest: () -> Unit,
    onRoleChange: (userId: String, newRole: UserRole) -> Unit,
    onToggleBlock: (userId: String) -> Unit,
    onAddNewUser: (name: String, phone: String, area: String, role: UserRole, designation: String, boothNo: String) -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    var selectedRoleFilter by remember { mutableStateOf<UserRole?>(null) }
    var selectedAreaFilter by remember { mutableStateOf<String?>(null) }
    var selectedStatusFilter by remember { mutableStateOf("ALL") } // ALL, ACTIVE, BLOCKED, UNVERIFIED
    var searchQuery by remember { mutableStateOf("") }
    var showAddUserDialog by remember { mutableStateOf(false) }
    var showSheetSettingsDialog by remember { mutableStateOf(false) }
    var customSheetUrlInput by remember(syncStatus.spreadsheetUrl) { mutableStateOf(syncStatus.spreadsheetUrl) }
    var customWebhookUrlInput by remember(syncStatus.webhookUrl) { mutableStateOf(syncStatus.webhookUrl) }

    val allAreas = remember(users) {
        listOf("सभी क्षेत्र") + users.map { it.area }.distinct().sorted()
    }

    val filteredUsers = remember(users, selectedRoleFilter, selectedAreaFilter, selectedStatusFilter, searchQuery) {
        users.filter { user ->
            val matchesRole = selectedRoleFilter == null || user.role == selectedRoleFilter
            val matchesArea = selectedAreaFilter == null || selectedAreaFilter == "सभी क्षेत्र" || user.area == selectedAreaFilter
            val matchesStatus = when (selectedStatusFilter) {
                "ACTIVE" -> !user.isBlocked
                "BLOCKED" -> user.isBlocked
                "UNVERIFIED" -> !user.isVerified
                else -> true
            }
            val matchesSearch = searchQuery.isBlank() ||
                    user.name.contains(searchQuery, ignoreCase = true) ||
                    user.phone.contains(searchQuery) ||
                    user.area.contains(searchQuery, ignoreCase = true) ||
                    user.designation.contains(searchQuery, ignoreCase = true) ||
                    user.boothNo.contains(searchQuery, ignoreCase = true)

            matchesRole && matchesArea && matchesStatus && matchesSearch
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Google Sheets Connection Banner
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFFF0FDF4),
            border = BorderStroke(1.5.dp, Color(0xFF86EFAC)),
            shadowElevation = 2.dp
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF16A34A)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.CloudDone, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Google Sheets लाइव सिंक",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = Color(0xFF14532D)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = Color(0xFFDCFCE7),
                                    border = BorderStroke(0.5.dp, Color(0xFF22C55E))
                                ) {
                                    Text(
                                        text = "सक्रिय (Connected)",
                                        color = Color(0xFF15803D),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = "प्रोजेक्ट: ${syncStatus.projectId}",
                                fontSize = 11.sp,
                                color = Color(0xFF166534)
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = { showSheetSettingsDialog = true },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                Icons.Default.Settings,
                                contentDescription = "Sheet Settings",
                                tint = Color(0xFF16A34A),
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        if (syncStatus.isSyncing) {
                            Spacer(modifier = Modifier.width(6.dp))
                            CircularProgressIndicator(
                                modifier = Modifier.size(22.dp),
                                color = Color(0xFF16A34A),
                                strokeWidth = 2.5.dp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "शीट: ${syncStatus.spreadsheetTitle}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF14532D)
                )

                Text(
                    text = "अंतिम सिंक: ${syncStatus.lastSyncTime} • कुल ${users.size} रिकॉर्ड्स",
                    fontSize = 11.sp,
                    color = Color(0xFF166534)
                )

                if (syncStatus.syncMessage.isNotBlank()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFFDCFCE7),
                        border = BorderStroke(0.5.dp, Color(0xFF22C55E))
                    ) {
                        Text(
                            text = "✓ ${syncStatus.syncMessage}",
                            color = Color(0xFF15803D),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Action Buttons for Google Sheets - Row 1
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onSyncRequest,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("btn_sync_google_sheets"),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        Icon(Icons.Default.Sync, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("शीट सिंक करें", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = {
                            try {
                                val intent = GoogleSheetsSyncManager.createOpenSheetIntent(syncStatus.spreadsheetUrl)
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                clipboardManager.setText(AnnotatedString(syncStatus.spreadsheetUrl))
                                Toast.makeText(context, "शीट लिंक कॉपी किया गया: ${syncStatus.spreadsheetUrl}", Toast.LENGTH_LONG).show()
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("btn_open_google_sheet"),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, Color(0xFF16A34A)),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        Icon(Icons.Default.OpenInNew, contentDescription = null, tint = Color(0xFF16A34A), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("शीट खोलें", fontSize = 11.sp, color = Color(0xFF16A34A), fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Action Buttons for Google Sheets - Row 2
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            try {
                                val shareIntent = GoogleSheetsSyncManager.createShareCsvIntent(context, users, selectedRoleFilter)
                                context.startActivity(Intent.createChooser(shareIntent, "Google Sheets / Drive में CSV भेजें"))
                            } catch (e: Exception) {
                                Toast.makeText(context, "फ़ाइल शेयर त्रुटि: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier
                            .weight(1.1f)
                            .testTag("btn_share_csv_file"),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, Color(0xFF15803D)),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, tint = Color(0xFF15803D), modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("CSV फ़ाइल शेयर", fontSize = 11.sp, color = Color(0xFF15803D), fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = {
                            val csv = GoogleSheetsSyncManager.generateCsvData(users, selectedRoleFilter)
                            clipboardManager.setText(AnnotatedString(csv))
                            Toast.makeText(context, "CSV डेटा क्लिपबोर्ड में कॉपी किया गया!", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier
                            .weight(0.9f)
                            .testTag("btn_export_csv"),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, Color(0xFF475569)),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = null, tint = Color(0xFF475569), modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("CSV कॉपी", fontSize = 11.sp, color = Color(0xFF475569), fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = { showSheetSettingsDialog = true },
                        modifier = Modifier
                            .weight(0.9f)
                            .testTag("btn_sheet_settings"),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, Color(0xFF2563EB)),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = null, tint = Color(0xFF2563EB), modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("शीट लिंक", fontSize = 11.sp, color = Color(0xFF2563EB), fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // 2. Add New User Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "यूजर डेटाबेस एवं फ़िल्टर्स",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Button(
                onClick = { showAddUserDialog = true },
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = RathoreOrange),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("नया यूजर जोड़ें", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        // 3. Category Tabs / Role Filter Chips (Crucial requirement: समर्थक, पदाधिकारी, कार्यकर्ता फ़िल्टर)
        Column {
            Text(
                text = "श्रेणी फ़िल्टर (Category Filter):",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedRoleFilter == null,
                    onClick = { selectedRoleFilter = null },
                    label = { Text("सभी (${users.size})", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = RathoreGreen,
                        selectedLabelColor = Color.White
                    )
                )

                listOf(
                    UserRole.SUPPORTER to "समर्थक",
                    UserRole.WORKER to "कार्यकर्ता",
                    UserRole.OFFICE_BEARER to "पदाधिकारी",
                    UserRole.ADMIN to "व्यवस्थापक"
                ).forEach { (role, label) ->
                    val count = users.count { it.role == role }
                    val isSelected = selectedRoleFilter == role
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedRoleFilter = if (isSelected) null else role },
                        label = { Text("$label ($count)", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = when (role) {
                                UserRole.SUPPORTER -> Color(0xFF475569)
                                UserRole.WORKER -> Color(0xFF2563EB)
                                UserRole.OFFICE_BEARER -> Color(0xFF7C3AED)
                                UserRole.ADMIN -> RathoreGreen
                            },
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }
        }

        // 4. Area & Status Multi-Filters
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Area Filter
            var areaExpanded by remember { mutableStateOf(false) }
            Box(modifier = Modifier.weight(1.2f)) {
                OutlinedButton(
                    onClick = { areaExpanded = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = selectedAreaFilter ?: "सभी क्षेत्र",
                        fontSize = 11.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null, modifier = Modifier.size(16.dp))
                }
                DropdownMenu(
                    expanded = areaExpanded,
                    onDismissRequest = { areaExpanded = false }
                ) {
                    allAreas.forEach { area ->
                        DropdownMenuItem(
                            text = { Text(area, fontSize = 12.sp) },
                            onClick = {
                                selectedAreaFilter = if (area == "सभी क्षेत्र") null else area
                                areaExpanded = false
                            }
                        )
                    }
                }
            }

            // Status Filter (Active, Blocked, Unverified)
            var statusExpanded by remember { mutableStateOf(false) }
            Box(modifier = Modifier.weight(1f)) {
                OutlinedButton(
                    onClick = { statusExpanded = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = when (selectedStatusFilter) {
                            "ACTIVE" -> "सक्रिय"
                            "BLOCKED" -> "ब्लॉक"
                            "UNVERIFIED" -> "लंबित"
                            else -> "सभी स्थिति"
                        },
                        fontSize = 11.sp
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null, modifier = Modifier.size(16.dp))
                }
                DropdownMenu(
                    expanded = statusExpanded,
                    onDismissRequest = { statusExpanded = false }
                ) {
                    listOf(
                        "ALL" to "सभी स्थिति",
                        "ACTIVE" to "सक्रिय (Active)",
                        "BLOCKED" to "ब्लॉक (Blocked)",
                        "UNVERIFIED" to "सत्यापन लंबित"
                    ).forEach { (code, label) ->
                        DropdownMenuItem(
                            text = { Text(label, fontSize = 12.sp) },
                            onClick = {
                                selectedStatusFilter = code
                                statusExpanded = false
                            }
                        )
                    }
                }
            }
        }

        // 5. Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("नाम, मोबाइल, पद या बूथ नं. खोजें...", fontSize = 12.sp) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(20.dp)) },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(Icons.Default.Clear, contentDescription = null, modifier = Modifier.size(18.dp))
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("input_search_user_database"),
            shape = RoundedCornerShape(10.dp),
            singleLine = true
        )

        // Matching Users Count
        Text(
            text = "फ़िल्टर अनुसार कुल ${filteredUsers.size} यूजर मिले",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = RathoreGreen
        )

        // 6. User Cards List
        if (filteredUsers.isEmpty()) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFFF8FAFC)
            ) {
                Box(modifier = Modifier.padding(24.dp), contentAlignment = Alignment.Center) {
                    Text("दिए गए फ़िल्टर के अनुसार कोई यूजर नहीं मिला।", color = TextSecondary, fontSize = 12.sp)
                }
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                filteredUsers.forEach { user ->
                    ManagedUserCard(
                        user = user,
                        onRoleChange = { newRole -> onRoleChange(user.id, newRole) },
                        onToggleBlock = { onToggleBlock(user.id) }
                    )
                }
            }
        }
    }

    // Add New User Dialog
    if (showAddUserDialog) {
        AddNewUserDialog(
            isOpen = showAddUserDialog,
            onDismiss = { showAddUserDialog = false },
            onAdd = { name, phone, area, role, desig, booth ->
                onAddNewUser(name, phone, area, role, desig, booth)
                showAddUserDialog = false
            }
        )
    }

    // Google Sheet Configuration Dialog
    if (showSheetSettingsDialog) {
        AlertDialog(
            onDismissRequest = { showSheetSettingsDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Settings, contentDescription = null, tint = Color(0xFF16A34A))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("गूगल शीट लिंक व सिंक सेटिंग्स", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "अपनी असली गूगल शीट का URL यहाँ पेस्ट करें ताकि ऐप का डेटा सीधे आपकी शीट से जुड़ सके:",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )

                    OutlinedTextField(
                        value = customSheetUrlInput,
                        onValueChange = { customSheetUrlInput = it },
                        label = { Text("Google Sheet URL") },
                        placeholder = { Text("https://docs.google.com/spreadsheets/d/...") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "गूगल ऐप्स स्क्रिप्ट वेबहुक URL (वैकल्पिक - लाइव ऑटो-सिंक के लिए):",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )

                    OutlinedTextField(
                        value = customWebhookUrlInput,
                        onValueChange = { customWebhookUrlInput = it },
                        label = { Text("Apps Script Webhook URL") },
                        placeholder = { Text("https://script.google.com/macros/s/...") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    TextButton(
                        onClick = {
                            customSheetUrlInput = GoogleSheetsSyncManager.SPREADSHEET_URL
                            customWebhookUrlInput = ""
                        }
                    ) {
                        Text("डिफ़ॉल्ट लिंक रीसेट करें", fontSize = 11.sp, color = TextSecondary)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updateGoogleSheetConfig(customSheetUrlInput.trim(), customWebhookUrlInput.trim())
                        showSheetSettingsDialog = false
                        onSyncRequest()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A))
                ) {
                    Text("सुरक्षित व सिंक करें ✓", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showSheetSettingsDialog = false }) {
                    Text("रद्द करें")
                }
            }
        )
    }
}

@Composable
private fun ManagedUserCard(
    user: ManagedUser,
    onRoleChange: (UserRole) -> Unit,
    onToggleBlock: () -> Unit
) {
    var roleMenuExpanded by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = if (user.isBlocked) Color(0xFFFEF2F2) else Color.White,
        border = BorderStroke(1.dp, if (user.isBlocked) Color(0xFFFCA5A5) else Color(0xFFE2E8F0)),
        shadowElevation = 1.dp
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(
                                if (user.isBlocked) Color(0xFFEF4444)
                                else when (user.role) {
                                    UserRole.ADMIN -> RathoreGreen
                                    UserRole.OFFICE_BEARER -> Color(0xFF7C3AED)
                                    UserRole.WORKER -> Color(0xFF2563EB)
                                    UserRole.SUPPORTER -> Color(0xFF475569)
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = user.name.take(1),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = user.name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = TextPrimary
                            )
                            if (user.isBlocked) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = Color(0xFFEF4444)
                                ) {
                                    Text(
                                        text = "ब्लॉक",
                                        color = Color.White,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                }
                            }
                        }

                        Text(
                            text = "${user.phone} • ${user.area}",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                        Text(
                            text = "पद: ${user.designation} • ${user.boothNo}",
                            fontSize = 10.sp,
                            color = TextTertiary
                        )
                    }
                }

                // Google Sheets Synced Badge
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = Color(0xFFF0FDF4),
                    border = BorderStroke(0.5.dp, Color(0xFF86EFAC))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF16A34A), modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(2.dp))
                        Text("शीट सिंक", color = Color(0xFF16A34A), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action row: Change Category & Block
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Change Category Dropdown
                Box {
                    AssistChip(
                        onClick = { roleMenuExpanded = true },
                        label = { Text("श्रेणी: ${user.role.titleHi} ▾", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        leadingIcon = {
                            Icon(Icons.Default.SwapHoriz, contentDescription = null, modifier = Modifier.size(14.dp))
                        },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = when (user.role) {
                                UserRole.ADMIN -> RathoreGreen.copy(alpha = 0.1f)
                                UserRole.OFFICE_BEARER -> Color(0xFF7C3AED).copy(alpha = 0.1f)
                                UserRole.WORKER -> Color(0xFF2563EB).copy(alpha = 0.1f)
                                UserRole.SUPPORTER -> Color(0xFF475569).copy(alpha = 0.1f)
                            }
                        )
                    )

                    DropdownMenu(
                        expanded = roleMenuExpanded,
                        onDismissRequest = { roleMenuExpanded = false }
                    ) {
                        UserRole.values().forEach { role ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = "${role.titleHi} (${role.titleEn})",
                                        fontWeight = if (user.role == role) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                onClick = {
                                    onRoleChange(role)
                                    roleMenuExpanded = false
                                }
                            )
                        }
                    }
                }

                // Block / Unblock Toggle
                TextButton(
                    onClick = onToggleBlock,
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Icon(
                        if (user.isBlocked) Icons.Default.LockOpen else Icons.Default.Block,
                        contentDescription = null,
                        tint = if (user.isBlocked) Color(0xFF059669) else Color(0xFFDC2626),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (user.isBlocked) "अनब्लॉक करें" else "ब्लॉक करें",
                        color = if (user.isBlocked) Color(0xFF059669) else Color(0xFFDC2626),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun AddNewUserDialog(
    isOpen: Boolean,
    onDismiss: () -> Unit,
    onAdd: (name: String, phone: String, area: String, role: UserRole, designation: String, boothNo: String) -> Unit
) {
    if (!isOpen) return

    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var area by remember { mutableStateOf("निवाड़ी ग्रामीण") }
    var role by remember { mutableStateOf(UserRole.WORKER) }
    var designation by remember { mutableStateOf("बूथ प्रभारी") }
    var boothNo by remember { mutableStateOf("बूथ नं. 24") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("नया यूजर जोड़ें (गूगल शीट ऑटो-सिंक)", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("पूरा नाम *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("मोबाइल नंबर *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = area,
                    onValueChange = { area = it },
                    label = { Text("विधानसभा / क्षेत्र *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Role selector
                Text("श्रेणी (Category):", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    UserRole.values().forEach { r ->
                        FilterChip(
                            selected = role == r,
                            onClick = {
                                role = r
                                designation = when (r) {
                                    UserRole.SUPPORTER -> "सक्रिय समर्थक"
                                    UserRole.WORKER -> "बूथ कार्यकर्ता"
                                    UserRole.OFFICE_BEARER -> "मंडल संयोजक"
                                    UserRole.ADMIN -> "सह-व्यवस्थापक"
                                }
                            },
                            label = { Text(r.titleHi, fontSize = 11.sp) }
                        )
                    }
                }

                OutlinedTextField(
                    value = designation,
                    onValueChange = { designation = it },
                    label = { Text("पद / दायित्व") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = boothNo,
                    onValueChange = { boothNo = it },
                    label = { Text("बूथ संख्या") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank() && phone.isNotBlank()) {
                        onAdd(name, phone, area, role, designation, boothNo)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = RathoreGreen)
            ) {
                Text("सुरक्षित व सिंक करें ✓", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("रद्द करें")
            }
        }
    )
}
