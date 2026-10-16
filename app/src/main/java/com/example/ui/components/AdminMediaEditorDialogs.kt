package com.example.ui.components

import android.net.Uri
import android.widget.VideoView
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.*
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.ui.theme.*

// ==============================================================================
// 1. ADMIN IMAGE & COLLAGE EDITOR DIALOG WITH TEMPLATES & GALLERY PICKER
// ==============================================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminImageAndCollageEditorDialog(
    isOpen: Boolean,
    onDismiss: () -> Unit,
    initialPhotos: List<String> = emptyList(),
    initialCollageCount: Int = 1,
    initialHeadline: String = "",
    onApply: (photos: List<String>, collageCount: Int, headline: String, appliedTemplate: String) -> Unit
) {
    if (!isOpen) return

    var selectedPhotos by remember(initialPhotos) {
        mutableStateOf(if (initialPhotos.isNotEmpty()) initialPhotos else listOf("samvad_chaupal_1.jpg"))
    }
    var collageCount by remember(initialCollageCount) {
        mutableIntStateOf(if (initialCollageCount in 1..4) initialCollageCount else 1)
    }
    var aspectRatio by remember { mutableStateOf("1:1") } // "1:1", "4:5", "16:9", "9:16"
    var activeEditorTab by remember { mutableIntStateOf(0) } // 0: लेआउट, 1: टेम्पलेट्स, 2: फिल्टर, 3: टेक्स्ट व बैज

    // Filter & Adjustments
    var selectedFilter by remember { mutableStateOf("मूल") } // "मूल", "ऊर्जावान", "तिरंगा आभा", "ब्लैक & व्हाइट", "स्वर्णिम"
    var brightness by remember { mutableFloatStateOf(0f) } // -50f to 50f
    var contrast by remember { mutableFloatStateOf(1f) } // 0.5f to 1.5f
    var saturation by remember { mutableFloatStateOf(1f) } // 0f to 2f
    var rotationDegrees by remember { mutableIntStateOf(0) } // 0, 90, 180, 270
    var isFlippedHorizontally by remember { mutableStateOf(false) }

    // Templates & Text
    val imageTemplates = listOf(
        "हेमंत राठौर आधिकारिक फ्रेम",
        "जनसंवाद चौपाल पोस्टर",
        "युवा शक्ति एवं विकास संकल्प",
        "दैनिक सुविचार एवं सेवा संदेश",
        "स्वच्छ मिनिमल कांग्रेस बॉर्डर"
    )
    var selectedTemplate by remember { mutableStateOf(imageTemplates[0]) }
    var headlineText by remember(initialHeadline) {
        mutableStateOf(if (initialHeadline.isNotBlank()) initialHeadline else "जनसंवाद चौपाल • आपके द्वार")
    }
    var subHeadlineText by remember { mutableStateOf("हेमंत राठौर • जनसेवा ही संकल्प") }
    val badgesList = listOf("जय कांग्रेस", "युवा शक्ति", "जनसेवा ही संकल्प", "चौपाल संवाद", "सत्यमेव जयते")
    var selectedBadge by remember { mutableStateOf(badgesList[0]) }

    // Custom Uploaded Header & Footer State (Top & Lower Space Overlay for all graphics)
    var customHeaderUri by remember { mutableStateOf<Uri?>(null) }
    var customHeaderFileName by remember { mutableStateOf<String?>(null) }
    var customHeaderText by remember { mutableStateOf("हेमंत राठौर • जनसेवा ही संकल्प") }
    var isHeaderActive by remember { mutableStateOf(false) }

    var customFooterUri by remember { mutableStateOf<Uri?>(null) }
    var customFooterFileName by remember { mutableStateOf<String?>(null) }
    var customFooterText by remember { mutableStateOf("भारतीय राष्ट्रीय कांग्रेस • निवाड़ी विधानसभा") }
    var isFooterActive by remember { mutableStateOf(false) }

    val headerPhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            customHeaderUri = uri
            customHeaderFileName = "header_${uri.lastPathSegment?.takeLast(8) ?: "top"}.png"
            isHeaderActive = true
        }
    }

    val footerPhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            customFooterUri = uri
            customFooterFileName = "footer_${uri.lastPathSegment?.takeLast(8) ?: "bottom"}.png"
            isFooterActive = true
        }
    }

    // Device Gallery Pickers (Zero-permission Android Photo Picker)
    val multiplePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = 4)
    ) { uris: List<Uri> ->
        if (uris.isNotEmpty()) {
            selectedPhotos = uris.map { it.toString() }
            collageCount = uris.size.coerceIn(1, 4)
        }
    }

    val singlePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            val newName = uri.toString()
            if (selectedPhotos.size < collageCount) {
                selectedPhotos = selectedPhotos + newName
            } else {
                selectedPhotos = listOf(newName)
            }
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF0F172A))
                .statusBarsPadding()
                .navigationBarsPadding(),
            color = Color(0xFF0F172A)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Top Action Bar
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color(0xFF1E293B),
                    shadowElevation = 4.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = onDismiss,
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "बंद करें", tint = Color.White)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "🎨 फोटो व कोलाज एडिटर",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                                Text(
                                    text = "गैलरी फोटो • कोलाज • पार्टी टेम्पलेट फ्रेम",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(0xFF94A3B8)
                                )
                            }
                        }

                        Button(
                            onClick = {
                                onApply(selectedPhotos, collageCount, headlineText, selectedTemplate)
                                onDismiss()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = RathoreGreen),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("लागू करें ✓", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }

                // Main Scrollable Body
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Quick Gallery Selector Row
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF1E293B),
                        border = BorderStroke(1.dp, Color(0xFF334155))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(RathoreGreen.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, tint = RathoreGreen, modifier = Modifier.size(20.dp))
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "डिवाइस गैलरी से फोटो चुनें",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = Color.White
                                    )
                                    Text(
                                        text = "${selectedPhotos.size} फोटो चयनित (कोलाज: $collageCount)",
                                        fontSize = 11.sp,
                                        color = Color(0xFF94A3B8)
                                    )
                                }
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Button(
                                    onClick = {
                                        multiplePhotoPickerLauncher.launch(
                                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                        )
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = RathoreGreen),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Icon(Icons.Default.Collections, contentDescription = null, tint = Color.White, modifier = Modifier.size(15.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("गैलरी खोलें", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    // =========================================================================
                    // LIVE PREVIEW CANVAS WITH SELECTED TEMPLATE & COLLAGE
                    // =========================================================================
                    val aspectValue = when (aspectRatio) {
                        "1:1" -> 1.0f
                        "4:5" -> 0.8f
                        "16:9" -> 1.77f
                        "9:16" -> 0.56f
                        else -> 1.0f
                    }

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth(if (aspectRatio == "9:16") 0.65f else 0.95f)
                            .aspectRatio(aspectValue)
                            .shadow(12.dp, RoundedCornerShape(12.dp)),
                        shape = RoundedCornerShape(12.dp),
                        color = Color.White,
                        border = BorderStroke(
                            2.dp,
                            when (selectedTemplate) {
                                "जनसंवाद चौपाल पोस्टर" -> RathoreOrangeDark
                                "युवा शक्ति एवं विकास संकल्प" -> Color(0xFF2563EB)
                                "दैनिक सुविचार एवं सेवा संदेश" -> Color(0xFFF59E0B)
                                else -> RathoreGreen
                            }
                        )
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .scale(scaleX = if (isFlippedHorizontally) -1f else 1f, scaleY = 1f)
                                .rotate(rotationDegrees.toFloat())
                        ) {
                            // Base Content Layer (Single Photo or Collage Grid)
                            LiveCollageGridCanvas(
                                photos = selectedPhotos,
                                collageCount = collageCount,
                                filterName = selectedFilter,
                                brightness = brightness,
                                contrast = contrast,
                                saturation = saturation,
                                modifier = Modifier.fillMaxSize()
                            )

                            // Custom Header Overlay (Top Space) - Clean Image ONLY, NO Text
                            if (isHeaderActive && customHeaderUri != null) {
                                AsyncImage(
                                    model = customHeaderUri,
                                    contentDescription = "Custom Header",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .wrapContentHeight()
                                        .align(Alignment.TopCenter),
                                    contentScale = ContentScale.FillWidth
                                )
                            }

                            // Custom Footer Overlay (Lower Space) - Clean Image ONLY, NO Text
                            if (isFooterActive && customFooterUri != null) {
                                AsyncImage(
                                    model = customFooterUri,
                                    contentDescription = "Custom Footer",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .wrapContentHeight()
                                        .align(Alignment.BottomCenter),
                                    contentScale = ContentScale.FillWidth
                                )
                            }

                            // Template Overlay Layer
                            LiveTemplateGraphicOverlay(
                                templateName = selectedTemplate,
                                headline = headlineText,
                                subHeadline = subHeadlineText,
                                badge = selectedBadge,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }

                    // Tool Category Selector Tabs (5 Tabs)
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFF1E293B),
                        border = BorderStroke(1.dp, Color(0xFF334155))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(4.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            listOf(
                                0 to "📐 लेआउट",
                                1 to "🎨 फ्रेम",
                                2 to "🖼️ हेडर-फुटर",
                                3 to "✨ फिल्टर",
                                4 to "✍️ टेक्स्ट"
                            ).forEach { (tabIndex, title) ->
                                val isSelected = activeEditorTab == tabIndex
                                Surface(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { activeEditorTab = tabIndex },
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isSelected) RathoreGreen else Color.Transparent
                                ) {
                                    Text(
                                        text = title,
                                        fontSize = 10.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) Color.White else Color(0xFF94A3B8),
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(vertical = 8.dp)
                                    )
                                }
                            }
                        }
                    }

                    // TAB CONTENT CONTROLS
                    when (activeEditorTab) {
                        0 -> {
                            // 📐 Aspect Ratio & Collage Count Layout
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text("अनुपात (Aspect Ratio) चुनें:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    listOf(
                                        "1:1" to "1:1 (फीड/वर्गाकार)",
                                        "4:5" to "4:5 (पोस्टर/FB)",
                                        "16:9" to "16:9 (लैंडस्केप)",
                                        "9:16" to "9:16 (स्टोरी)"
                                    ).forEach { (ratio, label) ->
                                        val isSel = aspectRatio == ratio
                                        Surface(
                                            modifier = Modifier
                                                .weight(1f)
                                                .clickable { aspectRatio = ratio },
                                            shape = RoundedCornerShape(8.dp),
                                            color = if (isSel) RathoreGreen else Color(0xFF1E293B),
                                            border = BorderStroke(1.dp, if (isSel) RathoreGreen else Color(0xFF334155))
                                        ) {
                                            Column(
                                                modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
                                                horizontalAlignment = Alignment.CenterHorizontally
                                            ) {
                                                Text(ratio, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = if (isSel) Color.White else Color(0xFFE2E8F0))
                                                Text(label.substringAfter("(").removeSuffix(")"), fontSize = 8.5.sp, color = if (isSel) Color.White.copy(alpha = 0.85f) else Color(0xFF94A3B8))
                                            }
                                        }
                                    }
                                }

                                Text("कोलाज फोटो संख्या चुनें:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    listOf(
                                        1 to "1 फोटो (सिंगल)",
                                        2 to "2 फोटो कोलाज",
                                        3 to "3 फोटो कोलाज",
                                        4 to "4 फोटो कोलाज"
                                    ).forEach { (count, label) ->
                                        val isSel = collageCount == count
                                        Surface(
                                            modifier = Modifier
                                                .weight(1f)
                                                .clickable {
                                                    collageCount = count
                                                    if (selectedPhotos.size < count) {
                                                        val needed = count - selectedPhotos.size
                                                        val newItems = (1..needed).map { "samvad_pic_${selectedPhotos.size + it}.jpg" }
                                                        selectedPhotos = selectedPhotos + newItems
                                                    }
                                                },
                                            shape = RoundedCornerShape(8.dp),
                                            color = if (isSel) Color(0xFF1877F2) else Color(0xFF1E293B),
                                            border = BorderStroke(1.dp, if (isSel) Color(0xFF1877F2) else Color(0xFF334155))
                                        ) {
                                            Text(
                                                text = label,
                                                fontSize = 10.sp,
                                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                                                color = if (isSel) Color.White else Color(0xFFCBD5E1),
                                                textAlign = TextAlign.Center,
                                                modifier = Modifier.padding(vertical = 8.dp, horizontal = 2.dp)
                                            )
                                        }
                                    }
                                }

                                // Rotate & Flip Quick Actions
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    OutlinedButton(
                                        onClick = { rotationDegrees = (rotationDegrees + 90) % 360 },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(8.dp),
                                        border = BorderStroke(1.dp, Color(0xFF475569))
                                    ) {
                                        Icon(Icons.Default.RotateRight, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("घुमाएं ($rotationDegrees°)", color = Color.White, fontSize = 11.sp)
                                    }

                                    OutlinedButton(
                                        onClick = { isFlippedHorizontally = !isFlippedHorizontally },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(8.dp),
                                        border = BorderStroke(1.dp, Color(0xFF475569))
                                    ) {
                                        Icon(Icons.Default.Flip, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(if (isFlippedHorizontally) "फ्लिप: हाँ" else "फ्लिप: सामान्य", color = Color.White, fontSize = 11.sp)
                                    }
                                }
                            }
                        }

                        1 -> {
                            // 🎨 Templates & Party Frames Selector
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Text("पार्टी एवं नेताजी टेम्पलेट फ्रेम चुनें:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                imageTemplates.forEach { template ->
                                    val isSel = selectedTemplate == template
                                    Surface(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { selectedTemplate = template },
                                        shape = RoundedCornerShape(10.dp),
                                        color = if (isSel) Color(0xFF1E293B) else Color(0xFF0F172A),
                                        border = BorderStroke(1.5.dp, if (isSel) RathoreGreen else Color(0xFF334155))
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
                                                        .size(32.dp)
                                                        .clip(CircleShape)
                                                        .background(
                                                            when (template) {
                                                                "जनसंवाद चौपाल पोस्टर" -> RathoreOrange
                                                                "युवा शक्ति एवं विकास संकल्प" -> Color(0xFF2563EB)
                                                                "दैनिक सुविचार एवं सेवा संदेश" -> Color(0xFFF59E0B)
                                                                else -> RathoreGreen
                                                            }
                                                        ),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Icon(Icons.Default.Layers, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                                                }
                                                Spacer(modifier = Modifier.width(10.dp))
                                                Column {
                                                    Text(template, fontWeight = FontWeight.Bold, fontSize = 12.5.sp, color = Color.White)
                                                    Text("तिरंगा बॉर्डर • कांग्रेस हैंड लोगो • नेताजी का नाम", fontSize = 10.sp, color = Color(0xFF94A3B8))
                                                }
                                            }
                                            if (isSel) {
                                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = RathoreGreen, modifier = Modifier.size(20.dp))
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        2 -> {
                            // 🖼️ Header & Footer Upload Section (Top & Lower Space)
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text("हेडर व फुटर प्रबंधन (सभी ग्राफिक्स व फोटो हेतु):", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)

                                // 1. Top Header Upload Card
                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(10.dp),
                                    color = Color(0xFF1E293B),
                                    border = BorderStroke(1.dp, if (isHeaderActive) RathoreGreen else Color(0xFF334155))
                                ) {
                                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(Icons.Default.VerticalAlignTop, contentDescription = null, tint = RathoreGreen, modifier = Modifier.size(18.dp))
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text("शीर्ष हेडर (Top Space Header)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                            }
                                            Switch(
                                                checked = isHeaderActive,
                                                onCheckedChange = { isHeaderActive = it },
                                                colors = SwitchDefaults.colors(
                                                    checkedThumbColor = Color.White,
                                                    checkedTrackColor = RathoreGreen
                                                ),
                                                modifier = Modifier.scale(0.8f)
                                            )
                                        }

                                        Text("इमेज के सबसे ऊपरी हिस्से (Header) में कस्टम ब्रांडिंग या फोटो अपलोड करें:", fontSize = 10.sp, color = Color(0xFF94A3B8))

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Button(
                                                onClick = {
                                                    headerPhotoPickerLauncher.launch(
                                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                                    )
                                                },
                                                modifier = Modifier.weight(1f).height(36.dp),
                                                shape = RoundedCornerShape(8.dp),
                                                colors = ButtonDefaults.buttonColors(containerColor = RathoreGreen),
                                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                            ) {
                                                Icon(Icons.Default.CloudUpload, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text("गैलरी से हेडर अपलोड करें", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                            }

                                            if (customHeaderFileName != null) {
                                                IconButton(
                                                    onClick = {
                                                        customHeaderUri = null
                                                        customHeaderFileName = null
                                                    },
                                                    modifier = Modifier.size(36.dp)
                                                ) {
                                                    Icon(Icons.Default.Delete, contentDescription = "हटाएं", tint = Color(0xFFEF4444))
                                                }
                                            }
                                        }

                                        if (customHeaderFileName != null) {
                                            Surface(
                                                shape = RoundedCornerShape(6.dp),
                                                color = Color(0xFF0F172A),
                                                border = BorderStroke(1.dp, Color(0xFF10B981))
                                            ) {
                                                Row(
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(14.dp))
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Text("सक्रिय हेडर इमेज: $customHeaderFileName (केवल इमेज, कोई टेक्स्ट नहीं)", fontSize = 10.sp, color = Color(0xFF10B981), fontWeight = FontWeight.Bold)
                                                }
                                            }
                                        }
                                    }
                                }

                                // 2. Bottom Footer Upload Card
                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(10.dp),
                                    color = Color(0xFF1E293B),
                                    border = BorderStroke(1.dp, if (isFooterActive) RathoreOrangeDark else Color(0xFF334155))
                                ) {
                                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(Icons.Default.VerticalAlignBottom, contentDescription = null, tint = RathoreOrangeDark, modifier = Modifier.size(18.dp))
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text("निचला फुटर (Lower Space Footer)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                            }
                                            Switch(
                                                checked = isFooterActive,
                                                onCheckedChange = { isFooterActive = it },
                                                colors = SwitchDefaults.colors(
                                                    checkedThumbColor = Color.White,
                                                    checkedTrackColor = RathoreOrangeDark
                                                ),
                                                modifier = Modifier.scale(0.8f)
                                            )
                                        }

                                        Text("इमेज के सबसे निचले हिस्से (Footer) में कस्टम ब्रांडिंग, नाम या स्ट्रैप अपलोड करें:", fontSize = 10.sp, color = Color(0xFF94A3B8))

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Button(
                                                onClick = {
                                                    footerPhotoPickerLauncher.launch(
                                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                                    )
                                                },
                                                modifier = Modifier.weight(1f).height(36.dp),
                                                shape = RoundedCornerShape(8.dp),
                                                colors = ButtonDefaults.buttonColors(containerColor = RathoreOrangeDark),
                                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                            ) {
                                                Icon(Icons.Default.CloudUpload, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text("गैलरी से फुटर अपलोड करें", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                            }

                                            if (customFooterFileName != null) {
                                                IconButton(
                                                    onClick = {
                                                        customFooterUri = null
                                                        customFooterFileName = null
                                                    },
                                                    modifier = Modifier.size(36.dp)
                                                ) {
                                                    Icon(Icons.Default.Delete, contentDescription = "हटाएं", tint = Color(0xFFEF4444))
                                                }
                                            }
                                        }

                                        if (customFooterFileName != null) {
                                            Surface(
                                                shape = RoundedCornerShape(6.dp),
                                                color = Color(0xFF0F172A),
                                                border = BorderStroke(1.dp, Color(0xFFF59E0B))
                                            ) {
                                                Row(
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFFF59E0B), modifier = Modifier.size(14.dp))
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Text("सक्रिय फुटर इमेज: $customFooterFileName (केवल इमेज, कोई टेक्स्ट नहीं)", fontSize = 10.sp, color = Color(0xFFF59E0B), fontWeight = FontWeight.Bold)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        3 -> {
                            // ✨ Filters & Adjustments (Sliders)
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text("फिल्टर प्रीसेट चुनें:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    listOf("मूल", "ऊर्जावान", "तिरंगा आभा", "ब्लैक & व्हाइट", "स्वर्णिम").forEach { filter ->
                                        val isSel = selectedFilter == filter
                                        Surface(
                                            modifier = Modifier
                                                .weight(1f)
                                                .clickable { selectedFilter = filter },
                                            shape = RoundedCornerShape(8.dp),
                                            color = if (isSel) Color(0xFF9333EA) else Color(0xFF1E293B),
                                            border = BorderStroke(1.dp, if (isSel) Color(0xFF9333EA) else Color(0xFF334155))
                                        ) {
                                            Text(
                                                text = filter,
                                                fontSize = 10.sp,
                                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                                                color = Color.White,
                                                textAlign = TextAlign.Center,
                                                modifier = Modifier.padding(vertical = 8.dp, horizontal = 2.dp)
                                            )
                                        }
                                    }
                                }

                                // Brightness Slider
                                Column {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("चमक (Brightness):", fontSize = 11.sp, color = Color(0xFFE2E8F0))
                                        Text("${brightness.toInt()}%", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    }
                                    Slider(
                                        value = brightness,
                                        onValueChange = { brightness = it },
                                        valueRange = -50f..50f,
                                        colors = SliderDefaults.colors(
                                            thumbColor = RathoreGreen,
                                            activeTrackColor = RathoreGreen,
                                            inactiveTrackColor = Color(0xFF334155)
                                        )
                                    )
                                }

                                // Contrast Slider
                                Column {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("कंट्रास्ट (Contrast):", fontSize = 11.sp, color = Color(0xFFE2E8F0))
                                        Text(String.format("%.1fx", contrast), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    }
                                    Slider(
                                        value = contrast,
                                        onValueChange = { contrast = it },
                                        valueRange = 0.5f..1.5f,
                                        colors = SliderDefaults.colors(
                                            thumbColor = Color(0xFF1877F2),
                                            activeTrackColor = Color(0xFF1877F2),
                                            inactiveTrackColor = Color(0xFF334155)
                                        )
                                    )
                                }
                            }
                        }

                        4 -> {
                            // ✍️ Text Overlay & Badges
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Text("मुख्य शीर्षक (Headline):", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                OutlinedTextField(
                                    value = headlineText,
                                    onValueChange = { headlineText = it },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White,
                                        focusedContainerColor = Color(0xFF1E293B),
                                        unfocusedContainerColor = Color(0xFF1E293B),
                                        focusedBorderColor = RathoreGreen,
                                        unfocusedBorderColor = Color(0xFF475569)
                                    ),
                                    shape = RoundedCornerShape(8.dp)
                                )

                                Text("उप-शीर्षक / संदेश:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                OutlinedTextField(
                                    value = subHeadlineText,
                                    onValueChange = { subHeadlineText = it },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White,
                                        focusedContainerColor = Color(0xFF1E293B),
                                        unfocusedContainerColor = Color(0xFF1E293B),
                                        focusedBorderColor = RathoreGreen,
                                        unfocusedBorderColor = Color(0xFF475569)
                                    ),
                                    shape = RoundedCornerShape(8.dp)
                                )

                                Text("बैज (Badge) चुनें:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    badgesList.forEach { badge ->
                                        val isSel = selectedBadge == badge
                                        Surface(
                                            modifier = Modifier
                                                .weight(1f)
                                                .clickable { selectedBadge = badge },
                                            shape = RoundedCornerShape(8.dp),
                                            color = if (isSel) RathoreOrange else Color(0xFF1E293B),
                                            border = BorderStroke(1.dp, if (isSel) RathoreOrange else Color(0xFF334155))
                                        ) {
                                            Text(
                                                text = badge,
                                                fontSize = 9.5.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White,
                                                textAlign = TextAlign.Center,
                                                modifier = Modifier.padding(vertical = 7.dp, horizontal = 2.dp)
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

// ==============================================================================
// 2. ADMIN VIDEO JACKET & TEMPLATE EDITOR DIALOG WITH GALLERY PICKER & SIMULATOR
// ==============================================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminVideoEditorDialog(
    isOpen: Boolean,
    onDismiss: () -> Unit,
    initialVideoName: String = "samvad_rally_2026.mp4",
    initialTitle: String = "",
    initialCaption: String = "",
    initialAspectRatio: String = "9:16",
    initialJacketName: String = "हेमंत राठौर आधिकारिक वीडियो जैकेट",
    onApply: (title: String, caption: String, aspectRatio: String, jacketName: String, videoFile: String, duration: String) -> Unit
) {
    if (!isOpen) return

    var selectedVideoName by remember(initialVideoName) { mutableStateOf(initialVideoName) }
    var videoTitle by remember(initialTitle) {
        mutableStateOf(if (initialTitle.isNotBlank()) initialTitle else "जनसंवाद चौपाल विशाल जनसभा")
    }
    var videoCaption by remember(initialCaption) {
        mutableStateOf(if (initialCaption.isNotBlank()) initialCaption else "क्षेत्र के विकास और जनसमस्याओं के समाधान हेतु संवाद")
    }
    var aspectRatio by remember(initialAspectRatio) { mutableStateOf(initialAspectRatio) }
    val videoJackets = listOf(
        "हेमंत राठौर आधिकारिक वीडियो जैकेट",
        "जनसंवाद चौपाल लाइव जैकेट",
        "युवा क्रांति एवं भाषण जैकेट",
        "विकास कार्य रिपोर्ट जैकेट"
    )
    var selectedJacket by remember(initialJacketName) { mutableStateOf(initialJacketName) }
    var headerText by remember { mutableStateOf("हेमंत राठौर • जनसेवा ही संकल्प") }
    var footerText by remember { mutableStateOf("कार्यकर्ता नाम व पद यहाँ जुड़ेगा") }
    var themeColorHex by remember { mutableLongStateOf(0xFF9333EAL) }
    var watermarkTag by remember { mutableStateOf("HD 1080p") }
    var videoFilter by remember { mutableStateOf("सामान्य") }

    // Video Player Simulator State
    var isPlaying by remember { mutableStateOf(true) }
    var progressPosition by remember { mutableFloatStateOf(0.42f) }
    var isMuted by remember { mutableStateOf(false) }

    // Custom Uploaded Video Header & Footer State (Top & Lower Space Overlay for video/reels)
    var customVideoHeaderUri by remember { mutableStateOf<Uri?>(null) }
    var customVideoHeaderFileName by remember { mutableStateOf<String?>(null) }
    var isVideoHeaderActive by remember { mutableStateOf(false) }

    var customVideoFooterUri by remember { mutableStateOf<Uri?>(null) }
    var customVideoFooterFileName by remember { mutableStateOf<String?>(null) }
    var isVideoFooterActive by remember { mutableStateOf(false) }

    val videoHeaderPhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            customVideoHeaderUri = uri
            customVideoHeaderFileName = "vid_header_${uri.lastPathSegment?.takeLast(8) ?: "top"}.png"
            isVideoHeaderActive = true
        }
    }

    val videoFooterPhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            customVideoFooterUri = uri
            customVideoFooterFileName = "vid_footer_${uri.lastPathSegment?.takeLast(8) ?: "bottom"}.png"
            isVideoFooterActive = true
        }
    }

    // Video Gallery Picker launcher
    var selectedVideoUri by remember { mutableStateOf<Uri?>(null) }
    val videoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedVideoUri = uri
            selectedVideoName = uri.lastPathSegment ?: "gallery_video.mp4"
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF090D16))
                .statusBarsPadding()
                .navigationBarsPadding(),
            color = Color(0xFF090D16)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Top Header Bar
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color(0xFF111827),
                    shadowElevation = 4.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = onDismiss,
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "बंद करें", tint = Color.White)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "🎬 वीडियो जैकेट व एडिटर",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                                Text(
                                    text = "अनुपात चयन • लाइव जैकेट पूर्वावलोकन • ब्रांडिंग",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(0xFF9CA3AF)
                                )
                            }
                        }

                        Button(
                            onClick = {
                                onApply(
                                    videoTitle,
                                    videoCaption,
                                    aspectRatio,
                                    selectedJacket,
                                    selectedVideoUri?.toString() ?: selectedVideoName,
                                    "2:30"
                                )
                                onDismiss()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9333EA)),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("फॉर्म में लागू करें ✓", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }

                // Scrollable Body
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Pick Video Source Row
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF1F2937),
                        border = BorderStroke(1.dp, Color(0xFF374151))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF9333EA).copy(alpha = 0.25f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.VideoLibrary, contentDescription = null, tint = Color(0xFFC084FC), modifier = Modifier.size(20.dp))
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = selectedVideoName,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.5.sp,
                                        color = Color.White,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = "1080p HD • 2:30 मिनट • 48 MB",
                                        fontSize = 10.sp,
                                        color = Color(0xFF9CA3AF)
                                    )
                                }
                            }

                            Button(
                                onClick = {
                                    videoPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)
                                    )
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9333EA)),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Icon(Icons.Default.UploadFile, contentDescription = null, tint = Color.White, modifier = Modifier.size(15.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("गैलरी से वीडियो", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    // =========================================================================
                    // INTERACTIVE LIVE VIDEO PLAYER WITH JACKET OVERLAY
                    // =========================================================================
                    val videoAspect = when (aspectRatio) {
                        "9:16" -> 0.56f
                        "16:9" -> 1.77f
                        "1:1" -> 1.0f
                        "4:5" -> 0.8f
                        else -> 0.56f
                    }

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth(if (aspectRatio == "9:16") 0.68f else 0.95f)
                            .aspectRatio(videoAspect)
                            .shadow(16.dp, RoundedCornerShape(12.dp)),
                        shape = RoundedCornerShape(12.dp),
                        color = Color.Black,
                        border = BorderStroke(2.dp, Color(themeColorHex))
                    ) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            // Video Footage or Simulated Footage Background
                            if (selectedVideoUri != null) {
                                AndroidView(
                                    factory = { ctx ->
                                        VideoView(ctx).apply {
                                            setVideoURI(selectedVideoUri)
                                            setOnPreparedListener { mp ->
                                                mp.isLooping = true
                                                if (isPlaying) start()
                                            }
                                        }
                                    },
                                    update = { view ->
                                        if (isPlaying) {
                                            if (!view.isPlaying) view.start()
                                        } else {
                                            if (view.isPlaying) view.pause()
                                        }
                                    },
                                    modifier = Modifier.fillMaxSize()
                                )

                                // Floating Play/Pause Control Button over video
                                IconButton(
                                    onClick = { isPlaying = !isPlaying },
                                    modifier = Modifier
                                        .align(Alignment.Center)
                                        .size(54.dp)
                                        .clip(CircleShape)
                                        .background(Color.Black.copy(alpha = 0.5f))
                                ) {
                                    Icon(
                                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(32.dp)
                                    )
                                }
                            } else {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(
                                            Brush.verticalGradient(
                                                colors = listOf(
                                                    Color(0xFF1E1B4B),
                                                    Color(0xFF0F172A),
                                                    Color(0xFF1E293B)
                                                )
                                            )
                                        )
                                        .clickable {
                                            videoPickerLauncher.launch(
                                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)
                                            )
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center,
                                        modifier = Modifier.padding(16.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(56.dp)
                                                .clip(CircleShape)
                                                .background(Color(0xFF9333EA).copy(alpha = 0.3f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.AddPhotoAlternate,
                                                contentDescription = null,
                                                tint = Color(0xFFC084FC),
                                                modifier = Modifier.size(32.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = "📱 गैलरी से वीडियो चुनें",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                        Spacer(modifier = Modifier.height(3.dp))
                                        Text(
                                            text = "टैप करके फ़ोन से कोई भी वीडियो अपलोड करें",
                                            fontSize = 10.sp,
                                            color = Color(0xFF94A3B8)
                                        )
                                    }
                                }
                            }

                            // TOP JACKET BANNER
                            if (customVideoHeaderUri != null) {
                                AsyncImage(
                                    model = customVideoHeaderUri,
                                    contentDescription = "Custom Video Header",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .wrapContentHeight()
                                        .align(Alignment.TopCenter),
                                    contentScale = ContentScale.FillWidth
                                )
                            } else {
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .align(Alignment.TopCenter),
                                    color = Color(themeColorHex),
                                    shadowElevation = 4.dp
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 8.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                            Box(
                                                modifier = Modifier
                                                    .size(26.dp)
                                                    .clip(CircleShape)
                                                    .background(Color.White),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(Icons.Default.Person, contentDescription = null, tint = RathoreGreen, modifier = Modifier.size(18.dp))
                                            }
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = headerText,
                                                color = Color.White,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 11.sp,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }

                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = RathoreGreen
                                        ) {
                                            Text(
                                                text = watermarkTag,
                                                color = Color.White,
                                                fontSize = 8.5.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }
                            }

                            // BOTTOM JACKET FOOTER STRAP & TIMELINE
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .align(Alignment.BottomCenter)
                            ) {
                                // Scrubber Slider
                                Slider(
                                    value = progressPosition,
                                    onValueChange = { progressPosition = it },
                                    colors = SliderDefaults.colors(
                                        thumbColor = Color.White,
                                        activeTrackColor = Color(0xFFC084FC),
                                        inactiveTrackColor = Color.White.copy(alpha = 0.3f)
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(18.dp)
                                        .padding(horizontal = 6.dp)
                                    )

                                if (customVideoFooterUri != null) {
                                    AsyncImage(
                                        model = customVideoFooterUri,
                                        contentDescription = "Custom Video Footer",
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .wrapContentHeight(),
                                        contentScale = ContentScale.FillWidth
                                    )
                                } else {
                                    Surface(
                                        modifier = Modifier.fillMaxWidth(),
                                        color = Color(themeColorHex)
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 8.dp, vertical = 6.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = footerText,
                                                color = Color(0xFFFFD54F),
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 10.sp,
                                                maxLines = 1,
                                                modifier = Modifier.weight(1f),
                                                overflow = TextOverflow.Ellipsis
                                            )

                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(
                                                    text = "01:05 / 02:30",
                                                    color = Color.White,
                                                    fontSize = 9.sp
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                IconButton(
                                                    onClick = { isMuted = !isMuted },
                                                    modifier = Modifier.size(20.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = if (isMuted) Icons.Default.VolumeOff else Icons.Default.VolumeUp,
                                                        contentDescription = null,
                                                        tint = Color.White,
                                                        modifier = Modifier.size(14.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Aspect Ratio Selector
                    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("वीडियो अनुपात चुनें:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf(
                                "9:16" to "9:16 (रील्स)",
                                "16:9" to "16:9 (लैंडस्केप)",
                                "1:1" to "1:1 (फीड)",
                                "4:5" to "4:5 (इंस्टा)"
                            ).forEach { (ratio, label) ->
                                val isSel = aspectRatio == ratio
                                Surface(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { aspectRatio = ratio },
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isSel) Color(0xFF9333EA) else Color(0xFF1F2937),
                                    border = BorderStroke(1.dp, if (isSel) Color(0xFF9333EA) else Color(0xFF374151))
                                ) {
                                    Text(
                                        text = label,
                                        fontSize = 10.sp,
                                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                                        color = Color.White,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(vertical = 8.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Jacket Template Selector
                    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("जैकेट टेम्प्लेट चुनें:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        videoJackets.forEach { jacket ->
                            val isSel = selectedJacket == jacket
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedJacket = jacket
                                        when (jacket) {
                                            "जनसंवाद चौपाल लाइव जैकेट" -> {
                                                themeColorHex = 0xFFDC2626L
                                                watermarkTag = "🔴 LIVE"
                                                headerText = "जनसंवाद चौपाल • सीधा प्रसारण"
                                            }
                                            "युवा क्रांति एवं भाषण जैकेट" -> {
                                                themeColorHex = 0xFF7C3AEDL
                                                watermarkTag = "युवा शक्ति"
                                                headerText = "युवा क्रांति संवाद • हेमंत राठौर"
                                            }
                                            "विकास कार्य रिपोर्ट जैकेट" -> {
                                                themeColorHex = 0xFF15803DL
                                                watermarkTag = "विकास 2026"
                                                headerText = "विकास संकल्प रिपोर्ट • क्षेत्र प्रगति"
                                            }
                                            else -> {
                                                themeColorHex = 0xFF9333EAL
                                                watermarkTag = "HD 1080p"
                                                headerText = "हेमंत राठौर • जनसेवा ही संकल्प"
                                            }
                                        }
                                    },
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSel) Color(0xFF1F2937) else Color(0xFF111827),
                                border = BorderStroke(1.5.dp, if (isSel) Color(0xFFC084FC) else Color(0xFF374151))
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(jacket, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    if (isSel) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFFC084FC), modifier = Modifier.size(18.dp))
                                    }
                                }
                            }
                        }
                    }

                    // Header & Footer Upload Section for Videos and Reels (Top Space & Lower Space)
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFF1E293B),
                        border = BorderStroke(1.dp, Color(0xFF475569))
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = "🖼️ वीडियो व रील्स हेडर/फुटर अपलोड:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )

                            // 1. Top Header Upload for Video
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFF0F172A),
                                border = BorderStroke(1.dp, if (customVideoHeaderFileName != null) RathoreGreen else Color(0xFF334155))
                            ) {
                                Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.VerticalAlignTop, contentDescription = null, tint = RathoreGreen, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("शीर्ष हेडर (Top Space)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                        }
                                        if (customVideoHeaderFileName != null) {
                                            IconButton(
                                                onClick = {
                                                    customVideoHeaderUri = null
                                                    customVideoHeaderFileName = null
                                                },
                                                modifier = Modifier.size(24.dp)
                                            ) {
                                                Icon(Icons.Default.Delete, contentDescription = "हटाएं", tint = Color(0xFFEF4444), modifier = Modifier.size(16.dp))
                                            }
                                        }
                                    }

                                    Button(
                                        onClick = {
                                            videoHeaderPhotoPickerLauncher.launch(
                                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                            )
                                        },
                                        modifier = Modifier.fillMaxWidth().height(36.dp),
                                        shape = RoundedCornerShape(6.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = RathoreGreen),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Icon(Icons.Default.CloudUpload, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = if (customVideoHeaderFileName != null) "हेडर बदलें ($customVideoHeaderFileName)" else "गैलरी से वीडियो हेडर अपलोड करें",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }
                                }
                            }

                            // 2. Lower Footer Upload for Video
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFF0F172A),
                                border = BorderStroke(1.dp, if (customVideoFooterFileName != null) Color(0xFFC084FC) else Color(0xFF334155))
                            ) {
                                Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.VerticalAlignBottom, contentDescription = null, tint = Color(0xFFC084FC), modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("निचला फुटर (Lower Space)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                        }
                                        if (customVideoFooterFileName != null) {
                                            IconButton(
                                                onClick = {
                                                    customVideoFooterUri = null
                                                    customVideoFooterFileName = null
                                                },
                                                modifier = Modifier.size(24.dp)
                                            ) {
                                                Icon(Icons.Default.Delete, contentDescription = "हटाएं", tint = Color(0xFFEF4444), modifier = Modifier.size(16.dp))
                                            }
                                        }
                                    }

                                    Button(
                                        onClick = {
                                            videoFooterPhotoPickerLauncher.launch(
                                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                            )
                                        },
                                        modifier = Modifier.fillMaxWidth().height(36.dp),
                                        shape = RoundedCornerShape(6.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C3AED)),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Icon(Icons.Default.CloudUpload, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = if (customVideoFooterFileName != null) "फुटर बदलें ($customVideoFooterFileName)" else "गैलरी से वीडियो फुटर अपलोड करें",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Jacket Text Inputs
                    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("हेडर टेक्स्ट (Header):", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        OutlinedTextField(
                            value = headerText,
                            onValueChange = { headerText = it },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedContainerColor = Color(0xFF1F2937),
                                unfocusedContainerColor = Color(0xFF1F2937)
                            ),
                            shape = RoundedCornerShape(8.dp)
                        )

                        Text("फुटर टेक्स्ट (Footer):", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        OutlinedTextField(
                            value = footerText,
                            onValueChange = { footerText = it },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedContainerColor = Color(0xFF1F2937),
                                unfocusedContainerColor = Color(0xFF1F2937)
                            ),
                            shape = RoundedCornerShape(8.dp)
                        )
                    }
                }
            }
        }
    }
}

// ==============================================================================
// 3. CANVAS HELPER COMPONENTS FOR LIVE EDITING PREVIEW
// ==============================================================================

@Composable
private fun LivePhotoSlot(
    photo: String?,
    fallbackText: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFE2E8F0)),
        contentAlignment = Alignment.Center
    ) {
        if (photo != null && (photo.startsWith("content:") || photo.startsWith("file:") || photo.startsWith("http"))) {
            AsyncImage(
                model = photo,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.Image, contentDescription = null, tint = RathoreGreen, modifier = Modifier.size(36.dp))
                Spacer(modifier = Modifier.height(4.dp))
                Text(photo ?: fallbackText, fontSize = 11.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
            }
        }
    }
}

@Composable
fun LiveCollageGridCanvas(
    photos: List<String>,
    collageCount: Int,
    filterName: String,
    brightness: Float,
    contrast: Float,
    saturation: Float,
    modifier: Modifier = Modifier
) {
    val filterColorOverlay = when (filterName) {
        "ऊर्जावान" -> Color(0xFFFF9800).copy(alpha = 0.12f)
        "तिरंगा आभा" -> Color(0xFF10B981).copy(alpha = 0.12f)
        "ब्लैक & व्हाइट" -> Color(0xFF000000).copy(alpha = 0.35f)
        "स्वर्णिम" -> Color(0xFFFFD54F).copy(alpha = 0.15f)
        else -> Color.Transparent
    }

    Box(modifier = modifier) {
        when (collageCount) {
            1 -> {
                // 1 Single Photo
                LivePhotoSlot(photo = photos.firstOrNull(), fallbackText = "फोटो 1")
            }
            2 -> {
                // 2 Photos Split
                Row(modifier = Modifier.fillMaxSize()) {
                    LivePhotoSlot(
                        photo = photos.getOrNull(0),
                        fallbackText = "फोटो 1",
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .border(1.dp, Color.White)
                    )
                    LivePhotoSlot(
                        photo = photos.getOrNull(1),
                        fallbackText = "फोटो 2",
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .border(1.dp, Color.White)
                    )
                }
            }
            3 -> {
                // 3 Photos
                Column(modifier = Modifier.fillMaxSize()) {
                    LivePhotoSlot(
                        photo = photos.getOrNull(0),
                        fallbackText = "मुख्य फोटो 1",
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .border(1.dp, Color.White)
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        LivePhotoSlot(
                            photo = photos.getOrNull(1),
                            fallbackText = "फोटो 2",
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .border(1.dp, Color.White)
                        )
                        LivePhotoSlot(
                            photo = photos.getOrNull(2),
                            fallbackText = "फोटो 3",
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .border(1.dp, Color.White)
                        )
                    }
                }
            }
            4 -> {
                // 4 Photos 2x2 Grid
                Column(modifier = Modifier.fillMaxSize()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        LivePhotoSlot(
                            photo = photos.getOrNull(0),
                            fallbackText = "फोटो 1",
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .border(1.dp, Color.White)
                        )
                        LivePhotoSlot(
                            photo = photos.getOrNull(1),
                            fallbackText = "फोटो 2",
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .border(1.dp, Color.White)
                        )
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        LivePhotoSlot(
                            photo = photos.getOrNull(2),
                            fallbackText = "फोटो 3",
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .border(1.dp, Color.White)
                        )
                        LivePhotoSlot(
                            photo = photos.getOrNull(3),
                            fallbackText = "फोटो 4",
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .border(1.dp, Color.White)
                        )
                    }
                }
            }
        }

        // Color & Brightness Overlay Filter
        if (filterColorOverlay != Color.Transparent || brightness != 0f) {
            val brightnessColor = if (brightness > 0) Color.White.copy(alpha = (brightness / 100f).coerceIn(0f, 0.4f))
            else Color.Black.copy(alpha = (-brightness / 100f).coerceIn(0f, 0.4f))

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(filterColorOverlay)
                    .background(brightnessColor)
            )
        }
    }
}

@Composable
fun LiveTemplateGraphicOverlay(
    templateName: String,
    headline: String,
    subHeadline: String,
    badge: String,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        when (templateName) {
            "जनसंवाद चौपाल पोस्टर" -> {
                // Earthy orange chaupal style
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter),
                    color = RathoreOrangeDark
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("जनसंवाद चौपाल", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                        Surface(shape = RoundedCornerShape(4.dp), color = Color.White) {
                            Text(badge, color = RathoreOrangeDark, fontWeight = FontWeight.Bold, fontSize = 8.sp, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                        }
                    }
                }

                // Bottom Headline Bar
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter),
                    color = Color.Black.copy(alpha = 0.85f)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(headline, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp, textAlign = TextAlign.Center)
                        Text(subHeadline, color = Color(0xFFFFD54F), fontSize = 10.sp, textAlign = TextAlign.Center)
                    }
                }
            }

            "युवा शक्ति एवं विकास संकल्प" -> {
                // Modern Navy/Blue Gradient
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter),
                    color = Color(0xFF1E3A8A)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("युवा शक्ति • विकास संकल्प", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                        Surface(shape = RoundedCornerShape(4.dp), color = Color(0xFF38BDF8)) {
                            Text(badge, color = Color(0xFF0F172A), fontWeight = FontWeight.Bold, fontSize = 8.sp, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                        }
                    }
                }

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter),
                    color = Color(0xFF1E3A8A).copy(alpha = 0.9f)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(headline, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp, textAlign = TextAlign.Center)
                        Text(subHeadline, color = Color(0xFF7DD3FC), fontSize = 10.sp, textAlign = TextAlign.Center)
                    }
                }
            }

            "दैनिक सुविचार एवं सेवा संदेश" -> {
                // Golden Sunrise Style
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter),
                    color = Color(0xFFD97706)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("दैनिक सेवा संदेश", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                        Text(badge, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 8.sp)
                    }
                }

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter),
                    color = Color(0xFF78350F).copy(alpha = 0.9f)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(headline, color = Color(0xFFFEF3C7), fontWeight = FontWeight.Bold, fontSize = 12.sp, textAlign = TextAlign.Center)
                        Text(subHeadline, color = Color.White, fontSize = 10.sp, textAlign = TextAlign.Center)
                    }
                }
            }

            else -> {
                // Official Hemant Rathore Frame - Clean top banner without hardcoded text
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter),
                    color = RathoreOrange
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Verified, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("TEAM RATHORE", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 9.5.sp)
                        }
                        Surface(shape = RoundedCornerShape(3.dp), color = Color.White) {
                            Text(badge, color = RathoreOrange, fontWeight = FontWeight.Bold, fontSize = 8.sp, modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
                        }
                    }
                }

                // Bottom Green Banner
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter),
                    color = RathoreGreen
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(headline, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.5.sp, textAlign = TextAlign.Center)
                        Text(subHeadline, color = Color(0xFFFFD54F), fontSize = 9.5.sp, textAlign = TextAlign.Center)
                    }
                }
            }
        }
    }
}
