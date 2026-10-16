package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.model.AdminFrameItem
import com.example.ui.theme.RathoreGreen
import com.example.ui.theme.RathoreOrange
import com.example.ui.theme.RathoreOrangeDark
import com.example.util.FrameStyleHelpers

@Composable
fun AdminFrameSlotCustomizerDialog(
    frame: AdminFrameItem,
    onDismiss: () -> Unit,
    onSave: (AdminFrameItem) -> Unit
) {
    // 1. फोटो (Photo Circle) State
    var photoCenterX by remember { mutableFloatStateOf(frame.photoCenterX) }
    var photoCenterY by remember { mutableFloatStateOf(frame.photoCenterY) }
    var photoRadius by remember { mutableFloatStateOf(frame.photoRadius) }

    // 2. नाम (Name) State
    var nameX by remember { mutableFloatStateOf(frame.nameX) }
    var nameY by remember { mutableFloatStateOf(frame.nameY) }
    var nameFontFamily by remember { mutableStateOf(frame.nameFontFamily) }
    var nameFontSizeSp by remember { mutableFloatStateOf(frame.nameFontSizeSp) }
    var nameFontWeight by remember { mutableStateOf(frame.nameFontWeight) }
    var textColorHex by remember { mutableStateOf(frame.textColorHex) }

    // 3. पद (Designation) State
    var desigX by remember { mutableFloatStateOf(frame.desigX) }
    var desigY by remember { mutableFloatStateOf(frame.desigY) }
    var desigFontFamily by remember { mutableStateOf(frame.desigFontFamily) }
    var desigFontSizeSp by remember { mutableFloatStateOf(frame.desigFontSizeSp) }
    var desigFontWeight by remember { mutableStateOf(frame.desigFontWeight) }
    var desigColorHex by remember { mutableStateOf(frame.desigColorHex) }

    // Active Control Tab: 0 = Photo, 1 = Name, 2 = Designation
    var activeTab by remember { mutableIntStateOf(0) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFF0F172A)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(14.dp)
            ) {
                // Top App Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Tune, contentDescription = null, tint = RathoreOrange, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "फ्रेम कस्टमाइज़र: ${frame.title}",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                        }
                        Text(
                            text = "एडमिन कंट्रोल: फोटो, नाम और पद स्लॉट असाइन करें (यह सभी के लिए लागू होगा)",
                            fontSize = 10.5.sp,
                            color = Color(0xFF94A3B8)
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "बंद करें", tint = Color(0xFF94A3B8))
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // LIVE PREVIEW CANVAS
                    item {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF1E293B),
                            border = BorderStroke(1.dp, Color(0xFF334155)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "लाइव प्रीव्यू (सभी उपयोगकर्ताओं को यही दिखेगा):",
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Surface(shape = RoundedCornerShape(4.dp), color = RathoreGreen.copy(alpha = 0.2f)) {
                                        Text("रियल-टाइम", color = RathoreGreen, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                BoxWithConstraints(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .aspectRatio(1.15f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color.White),
                                    contentAlignment = Alignment.Center
                                ) {
                                    val canvasW = maxWidth
                                    val canvasH = maxHeight

                                    // Sample Background Poster Content
                                    Column(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(
                                                Brush.verticalGradient(
                                                    listOf(Color(0xFFF8FAFC), Color(0xFFF1F5F9))
                                                )
                                            ),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Icon(Icons.Default.Campaign, contentDescription = null, tint = RathoreOrange, modifier = Modifier.size(34.dp))
                                        Text("हेमंत राठौर प्रचार पोस्टर", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF334155))
                                        Text("कार्यकर्ता का आधिकारिक पोस्टर", fontSize = 9.5.sp, color = Color(0xFF64748B))
                                    }

                                    // Render Frame Header if exists
                                    if (frame.headerImageUri != null) {
                                        AsyncImage(
                                            model = frame.headerImageUri,
                                            contentDescription = null,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .wrapContentHeight()
                                                .align(Alignment.TopCenter),
                                            contentScale = ContentScale.FillWidth
                                        )
                                    }

                                    // Render Frame Footer Background Layer
                                    if (frame.footerImageUri != null) {
                                        AsyncImage(
                                            model = frame.footerImageUri,
                                            contentDescription = null,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .wrapContentHeight()
                                                .align(Alignment.BottomCenter),
                                            contentScale = ContentScale.FillWidth
                                        )
                                    } else {
                                        // Built-in Navy Wave style
                                        Surface(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(50.dp)
                                                .align(Alignment.BottomCenter),
                                            color = Color(0xFF162750)
                                        ) {
                                            Box(modifier = Modifier.fillMaxSize()) {
                                                // Congress Hand symbol at right
                                                Surface(
                                                    shape = CircleShape,
                                                    color = Color.White,
                                                    modifier = Modifier
                                                        .size(32.dp)
                                                        .align(Alignment.CenterEnd)
                                                        .padding(end = 8.dp)
                                                ) {
                                                    Icon(Icons.Default.PanTool, contentDescription = null, tint = Color(0xFF162750), modifier = Modifier.padding(4.dp))
                                                }
                                            }
                                        }
                                    }

                                    // 1. LIVE PHOTO CIRCLE OVERLAY
                                    val circleDiam = canvasW * (photoRadius * 2f)
                                    val circleOffsetLeft = canvasW * photoCenterX - (circleDiam / 2f)
                                    val circleOffsetTop = canvasH * photoCenterY - (circleDiam / 2f)

                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.TopStart)
                                            .offset(x = circleOffsetLeft, y = circleOffsetTop)
                                            .size(circleDiam)
                                            .clip(CircleShape)
                                            .background(Color.White)
                                            .border(2.5.dp, Color.White, CircleShape)
                                            .border(4.dp, Color(0xFF162750), CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Person,
                                            contentDescription = "फोटो गोला",
                                            tint = Color(0xFF162750),
                                            modifier = Modifier.size(circleDiam * 0.62f)
                                        )
                                    }

                                    // 2. LIVE NAME OVERLAY
                                    val activeNameColor = try {
                                        Color(android.graphics.Color.parseColor(textColorHex))
                                    } catch (_: Exception) {
                                        Color.White
                                    }
                                    val activeNameFamily = FrameStyleHelpers.getComposeFontFamily(nameFontFamily)
                                    val activeNameWeight = FrameStyleHelpers.getComposeFontWeight(nameFontWeight)
                                    val responsiveNameSize = (nameFontSizeSp * (canvasW.value / 360f)).coerceIn(11f, 20f).sp

                                    Text(
                                        text = "रितुराज तोमर",
                                        color = activeNameColor,
                                        fontSize = responsiveNameSize,
                                        fontFamily = activeNameFamily,
                                        fontWeight = activeNameWeight,
                                        maxLines = 1,
                                        modifier = Modifier
                                            .align(Alignment.TopStart)
                                            .offset(
                                                x = canvasW * nameX,
                                                y = canvasH * nameY - 8.dp
                                            )
                                    )

                                    // 3. LIVE DESIGNATION OVERLAY
                                    val activeDesigColor = try {
                                        Color(android.graphics.Color.parseColor(desigColorHex))
                                    } catch (_: Exception) {
                                        Color(0xFFE2E8F0)
                                    }
                                    val activeDesigFamily = FrameStyleHelpers.getComposeFontFamily(desigFontFamily)
                                    val activeDesigWeight = FrameStyleHelpers.getComposeFontWeight(desigFontWeight)
                                    val responsiveDesigSize = (desigFontSizeSp * (canvasW.value / 360f)).coerceIn(8.5f, 15f).sp

                                    Text(
                                        text = "कार्यकर्ता | +91 98765 43210",
                                        color = activeDesigColor,
                                        fontSize = responsiveDesigSize,
                                        fontFamily = activeDesigFamily,
                                        fontWeight = activeDesigWeight,
                                        maxLines = 1,
                                        modifier = Modifier
                                            .align(Alignment.TopStart)
                                            .offset(
                                                x = canvasW * desigX,
                                                y = canvasH * desigY - 8.dp
                                            )
                                    )
                                }
                            }
                        }
                    }

                    // QUICK PRESET BUTTONS
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Button(
                                onClick = {
                                    photoCenterX = 0.20f
                                    photoCenterY = 0.86f
                                    photoRadius = 0.135f
                                    nameX = 0.38f
                                    nameY = 0.89f
                                    desigX = 0.38f
                                    desigY = 0.94f
                                    textColorHex = "#FFFFFF"
                                    desigColorHex = "#E2E8F0"
                                },
                                shape = RoundedCornerShape(6.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                                border = BorderStroke(1.dp, Color(0xFF334155)),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("टेम्पलेट 1 (बायां गोला)", fontSize = 10.sp, color = Color.White)
                            }

                            Button(
                                onClick = {
                                    photoCenterX = 0.82f
                                    photoCenterY = 0.86f
                                    photoRadius = 0.135f
                                    nameX = 0.08f
                                    nameY = 0.89f
                                    desigX = 0.08f
                                    desigY = 0.94f
                                },
                                shape = RoundedCornerShape(6.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                                border = BorderStroke(1.dp, Color(0xFF334155)),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("दायां गोला", fontSize = 10.sp, color = Color.White)
                            }

                            Button(
                                onClick = {
                                    photoCenterX = 0.50f
                                    photoCenterY = 0.82f
                                    photoRadius = 0.12f
                                    nameX = 0.25f
                                    nameY = 0.94f
                                    desigX = 0.25f
                                    desigY = 0.97f
                                },
                                shape = RoundedCornerShape(6.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                                border = BorderStroke(1.dp, Color(0xFF334155)),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("मध्यम गोला", fontSize = 10.sp, color = Color.White)
                            }
                        }
                    }

                    // 3 TAB SELECTORS (PHOTO / NAME / DESIGNATION)
                    item {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
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
                                    0 to "📸 1. फोटो (Photo)",
                                    1 to "👤 2. नाम (Name)",
                                    2 to "🎖️ 3. पद (Desig)"
                                ).forEach { (index, title) ->
                                    val isSelected = activeTab == index
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = if (isSelected) RathoreOrangeDark else Color.Transparent,
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable { activeTab = index }
                                    ) {
                                        Text(
                                            text = title,
                                            color = Color.White,
                                            fontSize = 11.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.padding(vertical = 8.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // TAB 0: PHOTO CIRCLE CONTROLS
                    if (activeTab == 0) {
                        item {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFF1E293B),
                                border = BorderStroke(1.dp, Color(0xFF334155)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                    Text("📸 फोटो गोला नियंत्रण (Photo Circle Adjustments):", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)

                                    // Radius (Size)
                                    Column {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text("गोला साइज़ (बड़ा / छोटा):", fontSize = 11.sp, color = Color(0xFF94A3B8))
                                            Text("${(photoRadius * 200).toInt()}%", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = RathoreGreen)
                                        }
                                        Slider(
                                            value = photoRadius,
                                            onValueChange = { photoRadius = it },
                                            valueRange = 0.07f..0.22f,
                                            colors = SliderDefaults.colors(
                                                thumbColor = RathoreOrange,
                                                activeTrackColor = RathoreOrange
                                            )
                                        )
                                    }

                                    // Position X
                                    Column {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text("क्षैतिज स्थिति X (बाएं ↔ दायें):", fontSize = 11.sp, color = Color(0xFF94A3B8))
                                            Text("${(photoCenterX * 100).toInt()}%", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF38BDF8))
                                        }
                                        Slider(
                                            value = photoCenterX,
                                            onValueChange = { photoCenterX = it },
                                            valueRange = 0.08f..0.92f,
                                            colors = SliderDefaults.colors(
                                                thumbColor = Color(0xFF38BDF8),
                                                activeTrackColor = Color(0xFF38BDF8)
                                            )
                                        )
                                    }

                                    // Position Y
                                    Column {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text("लंबवत स्थिति Y (ऊपर ↕ नीचे):", fontSize = 11.sp, color = Color(0xFF94A3B8))
                                            Text("${(photoCenterY * 100).toInt()}%", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF38BDF8))
                                        }
                                        Slider(
                                            value = photoCenterY,
                                            onValueChange = { photoCenterY = it },
                                            valueRange = 0.65f..0.98f,
                                            colors = SliderDefaults.colors(
                                                thumbColor = Color(0xFF38BDF8),
                                                activeTrackColor = Color(0xFF38BDF8)
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // TAB 1: NAME CONTROLS
                    if (activeTab == 1) {
                        item {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFF1E293B),
                                border = BorderStroke(1.dp, Color(0xFF334155)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                    Text("👤 नाम नियंत्रण व फॉन्ट (User Name Styling):", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)

                                    // Name X
                                    Column {
                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                            Text("नाम स्थिति X (बाएं ↔ दायें):", fontSize = 11.sp, color = Color(0xFF94A3B8))
                                            Text("${(nameX * 100).toInt()}%", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF38BDF8))
                                        }
                                        Slider(
                                            value = nameX,
                                            onValueChange = { nameX = it },
                                            valueRange = 0.05f..0.90f
                                        )
                                    }

                                    // Name Y
                                    Column {
                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                            Text("नाम स्थिति Y (ऊपर ↕ नीचे):", fontSize = 11.sp, color = Color(0xFF94A3B8))
                                            Text("${(nameY * 100).toInt()}%", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF38BDF8))
                                        }
                                        Slider(
                                            value = nameY,
                                            onValueChange = { nameY = it },
                                            valueRange = 0.65f..0.98f
                                        )
                                    }

                                    // Font Family Selection
                                    Text("फॉन्ट प्रकार (Font Family):", fontSize = 11.sp, color = Color(0xFF94A3B8))
                                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        items(FrameStyleHelpers.FONT_FAMILY_OPTIONS) { (key, label) ->
                                            val isSelected = nameFontFamily.equals(key, ignoreCase = true)
                                            Surface(
                                                shape = RoundedCornerShape(6.dp),
                                                color = if (isSelected) RathoreGreen else Color(0xFF0F172A),
                                                border = BorderStroke(1.dp, if (isSelected) RathoreGreen else Color(0xFF334155)),
                                                modifier = Modifier.clickable { nameFontFamily = key }
                                            ) {
                                                Text(
                                                    text = label,
                                                    color = Color.White,
                                                    fontSize = 10.sp,
                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                                                )
                                            }
                                        }
                                    }

                                    // Font Size
                                    Column {
                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                            Text("फॉन्ट साइज़ (Font Size):", fontSize = 11.sp, color = Color(0xFF94A3B8))
                                            Text("${nameFontSizeSp.toInt()} sp", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = RathoreOrange)
                                        }
                                        Slider(
                                            value = nameFontSizeSp,
                                            onValueChange = { nameFontSizeSp = it },
                                            valueRange = 24f..52f
                                        )
                                    }

                                    // Font Weight Selection
                                    Text("फॉन्ट मोटाई (Font Weight):", fontSize = 11.sp, color = Color(0xFF94A3B8))
                                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        items(FrameStyleHelpers.FONT_WEIGHT_OPTIONS) { (key, label) ->
                                            val isSelected = nameFontWeight.equals(key, ignoreCase = true)
                                            Surface(
                                                shape = RoundedCornerShape(6.dp),
                                                color = if (isSelected) RathoreOrange else Color(0xFF0F172A),
                                                border = BorderStroke(1.dp, if (isSelected) RathoreOrange else Color(0xFF334155)),
                                                modifier = Modifier.clickable { nameFontWeight = key }
                                            ) {
                                                Text(
                                                    text = label,
                                                    color = Color.White,
                                                    fontSize = 10.sp,
                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                                                )
                                            }
                                        }
                                    }

                                    // Text Color Selection
                                    Text("नाम का रंग (Text Color):", fontSize = 11.sp, color = Color(0xFF94A3B8))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        FrameStyleHelpers.TEXT_COLOR_OPTIONS.forEach { (hex, title) ->
                                            val isSelected = textColorHex.equals(hex, ignoreCase = true)
                                            Surface(
                                                shape = RoundedCornerShape(6.dp),
                                                color = if (isSelected) Color(android.graphics.Color.parseColor(hex)).copy(alpha = 0.25f) else Color(0xFF0F172A),
                                                border = BorderStroke(if (isSelected) 2.dp else 1.dp, if (isSelected) RathoreGreen else Color(0xFF334155)),
                                                modifier = Modifier.clickable { textColorHex = hex }
                                            ) {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                                                ) {
                                                    Box(
                                                        modifier = Modifier
                                                            .size(12.dp)
                                                            .clip(CircleShape)
                                                            .background(Color(android.graphics.Color.parseColor(hex)))
                                                    )
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text(title, color = Color.White, fontSize = 9.sp)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // TAB 2: DESIGNATION CONTROLS
                    if (activeTab == 2) {
                        item {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFF1E293B),
                                border = BorderStroke(1.dp, Color(0xFF334155)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                    Text("🎖️ पद नियंत्रण व फॉन्ट (Designation Styling):", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)

                                    // Designation X
                                    Column {
                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                            Text("पद स्थिति X (बाएं ↔ दायें):", fontSize = 11.sp, color = Color(0xFF94A3B8))
                                            Text("${(desigX * 100).toInt()}%", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF38BDF8))
                                        }
                                        Slider(
                                            value = desigX,
                                            onValueChange = { desigX = it },
                                            valueRange = 0.05f..0.90f
                                        )
                                    }

                                    // Designation Y
                                    Column {
                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                            Text("पद स्थिति Y (ऊपर ↕ नीचे):", fontSize = 11.sp, color = Color(0xFF94A3B8))
                                            Text("${(desigY * 100).toInt()}%", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF38BDF8))
                                        }
                                        Slider(
                                            value = desigY,
                                            onValueChange = { desigY = it },
                                            valueRange = 0.65f..0.98f
                                        )
                                    }

                                    // Designation Font Family
                                    Text("फॉन्ट प्रकार (Font Family):", fontSize = 11.sp, color = Color(0xFF94A3B8))
                                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        items(FrameStyleHelpers.FONT_FAMILY_OPTIONS) { (key, label) ->
                                            val isSelected = desigFontFamily.equals(key, ignoreCase = true)
                                            Surface(
                                                shape = RoundedCornerShape(6.dp),
                                                color = if (isSelected) RathoreGreen else Color(0xFF0F172A),
                                                border = BorderStroke(1.dp, if (isSelected) RathoreGreen else Color(0xFF334155)),
                                                modifier = Modifier.clickable { desigFontFamily = key }
                                            ) {
                                                Text(
                                                    text = label,
                                                    color = Color.White,
                                                    fontSize = 10.sp,
                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                                                )
                                            }
                                        }
                                    }

                                    // Designation Font Size
                                    Column {
                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                            Text("फॉन्ट साइज़ (Font Size):", fontSize = 11.sp, color = Color(0xFF94A3B8))
                                            Text("${desigFontSizeSp.toInt()} sp", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = RathoreOrange)
                                        }
                                        Slider(
                                            value = desigFontSizeSp,
                                            onValueChange = { desigFontSizeSp = it },
                                            valueRange = 16f..36f
                                        )
                                    }

                                    // Designation Text Color
                                    Text("पद का रंग (Text Color):", fontSize = 11.sp, color = Color(0xFF94A3B8))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        FrameStyleHelpers.TEXT_COLOR_OPTIONS.forEach { (hex, title) ->
                                            val isSelected = desigColorHex.equals(hex, ignoreCase = true)
                                            Surface(
                                                shape = RoundedCornerShape(6.dp),
                                                color = if (isSelected) Color(android.graphics.Color.parseColor(hex)).copy(alpha = 0.25f) else Color(0xFF0F172A),
                                                border = BorderStroke(if (isSelected) 2.dp else 1.dp, if (isSelected) RathoreGreen else Color(0xFF334155)),
                                                modifier = Modifier.clickable { desigColorHex = hex }
                                            ) {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                                                ) {
                                                    Box(
                                                        modifier = Modifier
                                                            .size(12.dp)
                                                            .clip(CircleShape)
                                                            .background(Color(android.graphics.Color.parseColor(hex)))
                                                    )
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text(title, color = Color.White, fontSize = 9.sp)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // BOTTOM ACTION BAR: DONE & SAVE FOR EVERYONE
                Button(
                    onClick = {
                        val updated = frame.copy(
                            photoCenterX = photoCenterX,
                            photoCenterY = photoCenterY,
                            photoRadius = photoRadius,
                            nameX = nameX,
                            nameY = nameY,
                            nameFontFamily = nameFontFamily,
                            nameFontSizeSp = nameFontSizeSp,
                            nameFontWeight = nameFontWeight,
                            textColorHex = textColorHex,
                            desigX = desigX,
                            desigY = desigY,
                            desigFontFamily = desigFontFamily,
                            desigFontSizeSp = desigFontSizeSp,
                            desigFontWeight = desigFontWeight,
                            desigColorHex = desigColorHex
                        )
                        onSave(updated)
                        onDismiss()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = RathoreGreen)
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "✅ डन करें और सभी के लिए सेव करें (Done & Save for Everyone)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = Color.White
                    )
                }
            }
        }
    }
}
