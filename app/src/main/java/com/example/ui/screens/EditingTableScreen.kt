package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.model.Language
import com.example.model.PosterIdentityConfig
import com.example.model.SuvicharCategory
import com.example.ui.components.AdminImageAndCollageEditorDialog
import com.example.ui.components.AdminVideoEditorDialog
import com.example.ui.components.PosterCanvas
import com.example.ui.theme.*
import com.example.util.AppStrings
import com.example.util.GraphicExportManager
import com.example.viewmodel.AppScreen
import com.example.viewmodel.AppViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditingTableScreen(
    viewModel: AppViewModel,
    language: Language,
    posterConfig: PosterIdentityConfig,
    isDownloadSuccessShown: Boolean
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var isExportingGraphic by remember { mutableStateOf(false) }

    var activeTab by remember { mutableStateOf(0) } // 0: Personalize Graphic, 1: Daily Suvichar
    var customHeadline by remember(posterConfig.customHeadline) { mutableStateOf(posterConfig.customHeadline) }
    var customSubHeadline by remember(posterConfig.customSubHeadline) { mutableStateOf(posterConfig.customSubHeadline) }
    var customName by remember(posterConfig.userName) { mutableStateOf(posterConfig.userName) }
    var customDesig by remember(posterConfig.userDesignation) { mutableStateOf(posterConfig.userDesignation) }
    var customMobile by remember(posterConfig.userMobile) { mutableStateOf(posterConfig.userMobile) }

    // Gallery Photo Launchers (Zero-permission Android Photo Picker)
    var showFullPhotoEditorDialog by remember { mutableStateOf(false) }
    var showFullVideoEditorDialog by remember { mutableStateOf(false) }

    val userPhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.updateUserPhoto(uri.toString())
        }
    }

    val mainPhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.updatePosterMainPhoto(uri.toString())
        }
    }

    val collagePhotosPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = 4)
    ) { uris: List<Uri> ->
        if (uris.isNotEmpty()) {
            viewModel.updatePosterCollagePhotos(uris.map { it.toString() })
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(RathoreBackground)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // Studio Top Bar
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color.White,
            shadowElevation = 2.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { viewModel.navigateTo(AppScreen.Main) },
                        modifier = Modifier.testTag("btn_back_editing")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = RathoreGreen)
                    }
                    IconButton(
                        onClick = { viewModel.navigateTo(AppScreen.Main) },
                        modifier = Modifier.testTag("btn_home_editing")
                    ) {
                        Icon(Icons.Default.Home, contentDescription = "होम (Home)", tint = RathoreOrange)
                    }
                    Spacer(modifier = Modifier.width(2.dp))
                    Column {
                        Text(
                            text = AppStrings.get("editing_table_title", language),
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = RathoreGreen
                        )
                    }
                }

                // Quick Suvichar / Template Switcher
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFF1F5F9),
                    border = BorderStroke(1.dp, BorderStroke)
                ) {
                    Row(modifier = Modifier.padding(4.dp)) {
                        FilterChip(
                            selected = activeTab == 0,
                            onClick = { activeTab = 0 },
                            label = { Text("पोस्टर", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = RathoreGreen,
                                selectedLabelColor = Color.White
                            )
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        FilterChip(
                            selected = activeTab == 1,
                            onClick = { activeTab = 1 },
                            label = { Text("सुविचार", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = RathoreOrange,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }
        }

        // Main Scrollable Editor
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Live 4:5 Poster Canvas
            PosterCanvas(
                config = posterConfig.copy(
                    customHeadline = customHeadline,
                    customSubHeadline = customSubHeadline,
                    userName = customName,
                    userDesignation = customDesig
                ),
                isSquare = false,
                modifier = Modifier
                    .fillMaxWidth(0.88f)
                    .testTag("poster_canvas_preview")
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Daily Suvichar Quick Generator (if tab == 1)
            AnimatedVisibility(visible = activeTab == 1) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    shape = RoundedCornerShape(14.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, BorderStroke)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = AppStrings.get("daily_suvichar", language),
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = RathoreGreen
                        )
                        Text(
                            text = AppStrings.get("suvichar_subtitle", language),
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )

                        // Suvichar category selector
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            SuvicharCategory.values().forEach { cat ->
                                OutlinedButton(
                                    onClick = {
                                        viewModel.selectSuvicharCategory(cat)
                                        viewModel.generateNewSuvichar()
                                        customHeadline = cat.titleHi
                                        customSubHeadline = viewModel.uiState.value.currentSuvichar.quoteHi
                                    },
                                    shape = RoundedCornerShape(20.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        containerColor = if (viewModel.uiState.value.selectedSuvicharCategory == cat) RathoreGreenLight else Color.Transparent
                                    ),
                                    border = BorderStroke(
                                        1.dp,
                                        if (viewModel.uiState.value.selectedSuvicharCategory == cat) RathoreGreen else BorderStroke
                                    ),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = if (language == Language.HINDI) cat.titleHi else cat.titleEn,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (viewModel.uiState.value.selectedSuvicharCategory == cat) RathoreGreen else TextPrimary
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Button(
                            onClick = {
                                viewModel.generateNewSuvichar()
                                customHeadline = viewModel.uiState.value.selectedSuvicharCategory.titleHi
                                customSubHeadline = viewModel.uiState.value.currentSuvichar.quoteHi
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = RathoreOrange)
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(AppStrings.get("new_suvichar", language), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // 🎨 Official Admin Frames Selection Carousel (Worker/Supporter selection)
            val adminFrames = viewModel.uiState.value.adminFrames
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = Color.White,
                shadowElevation = 2.dp,
                border = BorderStroke(1.dp, BorderStroke)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.FilterFrames, contentDescription = null, tint = RathoreOrange, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "आधिकारिक फ्रेम्स (Admin Frames)",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = RathoreGreen
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = RathoreOrange.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "${adminFrames.size} फ्रेम्स",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = RathoreOrangeDark,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Text(
                        text = "एडमिन द्वारा जारी आधिकारिक फ्रेम चुनें (पोस्टर पर तुरंत लागू होगा):",
                        fontSize = 11.5.sp,
                        color = TextSecondary,
                        modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                    )

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(vertical = 4.dp)
                    ) {
                        items(adminFrames) { frame ->
                            val isSelected = posterConfig.selectedFrameId == frame.id
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(
                                    if (isSelected) 2.dp else 1.dp,
                                    if (isSelected) RathoreGreen else BorderStroke
                                ),
                                color = if (isSelected) RathoreGreenLight.copy(alpha = 0.12f) else Color(0xFFFAFAFA),
                                modifier = Modifier
                                    .width(135.dp)
                                    .clickable { viewModel.selectPosterFrame(frame) }
                            ) {
                                Column(
                                    modifier = Modifier.padding(8.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    // Mini frame graphic preview box
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(65.dp)
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(
                                                Brush.verticalGradient(
                                                    listOf(RathoreOrange, Color.White, RathoreGreen)
                                                )
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (frame.headerImageUri != null) {
                                            AsyncImage(
                                                model = frame.headerImageUri,
                                                contentDescription = null,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(20.dp)
                                                    .align(Alignment.TopCenter),
                                                contentScale = ContentScale.FillWidth
                                            )
                                        }
                                        if (frame.footerImageUri != null) {
                                            AsyncImage(
                                                model = frame.footerImageUri,
                                                contentDescription = null,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(20.dp)
                                                    .align(Alignment.BottomCenter),
                                                contentScale = ContentScale.FillWidth
                                            )
                                        }
                                        if (isSelected) {
                                            Box(
                                                modifier = Modifier
                                                    .size(24.dp)
                                                    .clip(CircleShape)
                                                    .background(RathoreGreen),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    Icons.Default.Check,
                                                    contentDescription = "Selected",
                                                    tint = Color.White,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))

                                    Text(
                                        text = frame.title,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) RathoreGreen else TextPrimary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        textAlign = TextAlign.Center
                                    )

                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = if (isSelected) RathoreGreen.copy(alpha = 0.15f) else Color(0xFFE2E8F0),
                                        modifier = Modifier.padding(top = 3.dp)
                                    ) {
                                        Text(
                                            text = frame.category,
                                            fontSize = 9.sp,
                                            color = if (isSelected) RathoreGreen else TextSecondary,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Identity Options Card (The Core requirement)
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = Color.White,
                shadowElevation = 2.dp,
                border = BorderStroke(1.dp, BorderStroke)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = AppStrings.get("identity_options", language),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = RathoreGreen
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Toggle switches: Photo, Name, Designation
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(AppStrings.get("option_photo", language), style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
                        Switch(
                            checked = posterConfig.showPhoto,
                            onCheckedChange = { viewModel.updatePosterConfig { cfg -> cfg.copy(showPhoto = it) } },
                            colors = SwitchDefaults.colors(checkedThumbColor = RathoreGreen, checkedTrackColor = RathoreGreenLight)
                        )
                    }

                    // Photo Selection Sub-Section
                    if (posterConfig.showPhoto) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            shape = RoundedCornerShape(10.dp),
                            color = RathoreBackground,
                            border = BorderStroke(1.dp, RathoreGreenLight.copy(alpha = 0.5f))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(50.dp)
                                        .clip(CircleShape)
                                        .background(Color.White)
                                        .border(2.dp, RathoreOrange, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (!posterConfig.userPhotoUri.isNullOrBlank()) {
                                        AsyncImage(
                                            model = posterConfig.userPhotoUri,
                                            contentDescription = "User Photo",
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Crop
                                        )
                                    } else {
                                        Icon(
                                            Icons.Default.Person,
                                            contentDescription = null,
                                            tint = RathoreGreen,
                                            modifier = Modifier.size(30.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = if (!posterConfig.userPhotoUri.isNullOrBlank()) "✓ फोन गैलरी से फोटो सेट है" else "आपकी कार्यकर्ता फोटो",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = if (!posterConfig.userPhotoUri.isNullOrBlank()) RathoreGreen else TextPrimary
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Button(
                                            onClick = {
                                                userPhotoPickerLauncher.launch(
                                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                                )
                                            },
                                            modifier = Modifier.testTag("btn_pick_user_photo"),
                                            colors = ButtonDefaults.buttonColors(containerColor = RathoreGreen),
                                            shape = RoundedCornerShape(8.dp),
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                        ) {
                                            Icon(Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = if (!posterConfig.userPhotoUri.isNullOrBlank()) "फोटो बदलें" else "गैलरी से फोटो चुनें",
                                                fontSize = 11.sp
                                            )
                                        }

                                        if (!posterConfig.userPhotoUri.isNullOrBlank()) {
                                            OutlinedButton(
                                                onClick = { viewModel.updateUserPhoto(null) },
                                                shape = RoundedCornerShape(8.dp),
                                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFDC2626))
                                            ) {
                                                Text("हटाएं", fontSize = 11.sp)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Divider(color = BorderStroke, thickness = 0.6.dp, modifier = Modifier.padding(vertical = 8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(AppStrings.get("option_name", language), style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
                        Switch(
                            checked = posterConfig.showName,
                            onCheckedChange = { viewModel.updatePosterConfig { cfg -> cfg.copy(showName = it) } },
                            colors = SwitchDefaults.colors(checkedThumbColor = RathoreGreen, checkedTrackColor = RathoreGreenLight)
                        )
                    }

                    Divider(color = BorderStroke, thickness = 0.6.dp, modifier = Modifier.padding(vertical = 8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(AppStrings.get("option_designation", language), style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
                        Switch(
                            checked = posterConfig.showDesignation,
                            onCheckedChange = { viewModel.updatePosterConfig { cfg -> cfg.copy(showDesignation = it) } },
                            colors = SwitchDefaults.colors(checkedThumbColor = RathoreGreen, checkedTrackColor = RathoreGreenLight)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Manual Rename inputs
                    OutlinedTextField(
                        value = customName,
                        onValueChange = {
                            customName = it
                            viewModel.updatePosterConfig { cfg -> cfg.copy(userName = it) }
                        },
                        label = { Text(AppStrings.get("edit_name", language)) },
                        leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null, tint = RathoreGreen) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = customDesig,
                        onValueChange = {
                            customDesig = it
                            viewModel.updatePosterConfig { cfg -> cfg.copy(userDesignation = it) }
                        },
                        label = { Text(AppStrings.get("edit_designation", language)) },
                        leadingIcon = { Icon(Icons.Default.MilitaryTech, contentDescription = null, tint = RathoreOrange) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action Card: Download, Copy Caption & Share
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = Color.White,
                shadowElevation = 2.dp,
                border = BorderStroke(1.dp, BorderStroke)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "पोस्टर तैयार करें व साझा करें",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = RathoreGreen
                    )
                    Text(
                        text = "फ्रेम जैकेट और आपकी पहचान के साथ 1080×1350 HD पोस्टर तैयार है",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        modifier = Modifier.padding(top = 2.dp, bottom = 14.dp)
                    )

                    // 1. Download Button (Prominent)
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                isExportingGraphic = true
                                val savedUri = GraphicExportManager.renderAndSavePoster(
                                    context = context,
                                    config = posterConfig.copy(
                                        customHeadline = customHeadline,
                                        customSubHeadline = customSubHeadline,
                                        userName = customName,
                                        userDesignation = customDesig,
                                        userMobile = customMobile
                                    ),
                                    customHeadline = customHeadline,
                                    customSubHeadline = customSubHeadline,
                                    userName = customName,
                                    userDesignation = customDesig,
                                    userMobile = customMobile
                                )
                                isExportingGraphic = false
                                if (savedUri != null) {
                                    viewModel.onGraphicExportedSuccessfully()
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("btn_save_graphic"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = RathoreGreen),
                        enabled = !isExportingGraphic
                    ) {
                        if (isExportingGraphic) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp, color = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("पोस्टर सेव हो रहा है...", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        } else {
                            Icon(Icons.Default.Download, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "डाउनलोड करें (1080×1350 HD)",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Row: Copy Caption and Share
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // 2. Copy Caption Button
                        OutlinedButton(
                            onClick = {
                                val copyMatter = buildString {
                                    if (customHeadline.isNotBlank()) appendLine(customHeadline)
                                    if (customSubHeadline.isNotBlank()) appendLine(customSubHeadline)
                                    append("#टीम_राठौर #हेमंत_राठौर #निवाड़ी")
                                }
                                viewModel.copyPostText(copyMatter)
                                Toast.makeText(context, "कैप्शन कॉपी किया गया ✓", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("btn_copy_caption"),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.5.dp, RathoreOrange),
                            colors = ButtonDefaults.outlinedButtonColors(containerColor = RathoreOrange.copy(alpha = 0.06f)),
                            contentPadding = PaddingValues(horizontal = 8.dp)
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = null, tint = RathoreOrange, modifier = Modifier.size(17.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "कैप्शन कॉपी करें",
                                color = RathoreOrangeDark,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }

                        // 3. Direct Share Button
                        Button(
                            onClick = {
                                coroutineScope.launch {
                                    isExportingGraphic = true
                                    GraphicExportManager.sharePoster(
                                        context = context,
                                        config = posterConfig.copy(
                                            customHeadline = customHeadline,
                                            customSubHeadline = customSubHeadline,
                                            userName = customName,
                                            userDesignation = customDesig,
                                            userMobile = customMobile
                                        ),
                                        customHeadline = customHeadline,
                                        customSubHeadline = customSubHeadline,
                                        userName = customName,
                                        userDesignation = customDesig,
                                        userMobile = customMobile
                                    )
                                    isExportingGraphic = false
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("btn_share_graphic"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = RathoreOrange),
                            enabled = !isExportingGraphic,
                            contentPadding = PaddingValues(horizontal = 8.dp)
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, tint = Color.White, modifier = Modifier.size(17.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "शेयर करें",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(60.dp))
        }

        // Download Success Dialog / Modal (as shown in PDF page 10)
        // Full Admin Photo & Collage Studio Dialog
        if (showFullPhotoEditorDialog) {
            AdminImageAndCollageEditorDialog(
                isOpen = showFullPhotoEditorDialog,
                onDismiss = { showFullPhotoEditorDialog = false },
                initialPhotos = posterConfig.collagePhotos.ifEmpty {
                    if (posterConfig.customMainPhotoUri != null) listOf(posterConfig.customMainPhotoUri!!) else emptyList()
                },
                initialCollageCount = if (posterConfig.collagePhotos.isNotEmpty()) posterConfig.collagePhotos.size else 1,
                initialHeadline = customHeadline,
                onApply = { photos, count, headline, template ->
                    viewModel.updatePosterCollagePhotos(photos)
                    if (photos.isNotEmpty()) {
                        viewModel.updatePosterMainPhoto(photos.first())
                    }
                    if (headline.isNotBlank()) {
                        customHeadline = headline
                    }
                    showFullPhotoEditorDialog = false
                }
            )
        }

        // Full Admin Video Jacket & Reels Studio Dialog
        if (showFullVideoEditorDialog) {
            AdminVideoEditorDialog(
                isOpen = showFullVideoEditorDialog,
                onDismiss = { showFullVideoEditorDialog = false },
                onApply = { title, caption, aspect, jacket, file, duration ->
                    viewModel.addAdminVideo(
                        title = title,
                        caption = caption,
                        aspectRatio = aspect,
                        jacketTemplateName = jacket,
                        categories = listOf("वीडियो", "रील्स"),
                        duration = duration,
                        videoUri = file
                    )
                    showFullVideoEditorDialog = false
                }
            )
        }

        if (isDownloadSuccessShown) {
            AlertDialog(
                onDismissRequest = { viewModel.dismissDownloadSuccess() },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = RathoreGreen)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(AppStrings.get("graphic_ready", language), fontWeight = FontWeight.Bold)
                    }
                },
                text = {
                    Text(AppStrings.get("graphic_ready_sub", language))
                },
                confirmButton = {
                    Button(
                        onClick = { viewModel.dismissDownloadSuccess() },
                        colors = ButtonDefaults.buttonColors(containerColor = RathoreGreen)
                    ) {
                        Text("ठीक है (Done)", color = Color.White)
                    }
                }
            )
        }
    }
}
