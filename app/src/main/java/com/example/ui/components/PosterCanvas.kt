package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.model.PosterIdentityConfig
import com.example.ui.theme.RathoreGreen
import com.example.ui.theme.RathoreGreenDark
import com.example.ui.theme.RathoreOrange
import com.example.ui.theme.RathoreOrangeDark
import com.example.util.FrameStyleHelpers

@Composable
fun PosterCanvas(
    config: PosterIdentityConfig,
    modifier: Modifier = Modifier,
    isSquare: Boolean = false
) {
    // 4:5 aspect ratio (0.8f) or 1:1 aspect ratio
    val aspectRatio = if (isSquare) 1.0f else 0.8f
    val selectedFrame = config.selectedFrame
    val hasCustomFrame = selectedFrame != null
    val isTemplate1 = selectedFrame == null || selectedFrame.templateStyle == "TEMPLATE_1" || config.templateIndex == 0
    val isRedRibbon = selectedFrame?.templateStyle == "RED_RIBBON" || config.templateIndex == 1

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(aspectRatio)
            .shadow(8.dp, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        color = Color.White
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val canvasW = maxWidth
            val canvasH = maxHeight

            // 1. Clean Background (Strictly Clean Background as requested)
            Canvas(modifier = Modifier.fillMaxSize()) {
                val width = size.width
                val height = size.height

                // Pure clean white base
                drawRect(
                    color = Color.White,
                    size = androidx.compose.ui.geometry.Size(width, height)
                )

                // If not using Template 1 or custom images, draw subtle gradient
                if (!hasCustomFrame && !isTemplate1) {
                    drawRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFFF8FAFC),
                                Color(0xFFF1F5F9),
                                Color(0xFFE2E8F0)
                            )
                        )
                    )
                }
            }

            // 2. Middle Campaign Graphic / Message Content Layer
            val topHeaderPadding = if (isSquare) 68.dp else 84.dp
            val bottomFooterPadding = if (isSquare) 88.dp else 115.dp

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = topHeaderPadding, bottom = bottomFooterPadding, start = 8.dp, end = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Custom Main Photo from Gallery (if selected)
                    if (config.customMainPhotoUri != null) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f, fill = false)
                                .heightIn(max = if (isSquare) 170.dp else 230.dp)
                                .padding(bottom = 6.dp),
                            shape = RoundedCornerShape(8.dp),
                            shadowElevation = 2.dp,
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
                        ) {
                            AsyncImage(
                                model = config.customMainPhotoUri,
                                contentDescription = "Main Campaign Photo",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                    } else if (config.collagePhotos.isNotEmpty()) {
                        // Collage Grid from Gallery
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f, fill = false)
                                .heightIn(max = if (isSquare) 170.dp else 230.dp)
                                .padding(bottom = 6.dp),
                            shape = RoundedCornerShape(8.dp),
                            shadowElevation = 2.dp
                        ) {
                            Row(
                                modifier = Modifier.fillMaxSize(),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                config.collagePhotos.take(4).forEach { photoUri ->
                                    Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
                                        AsyncImage(
                                            model = photoUri,
                                            contentDescription = "Collage Photo",
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Crop
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Prominent Campaign Headline
                    if (config.customHeadline.isNotBlank()) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = RathoreGreen.copy(alpha = 0.08f),
                            modifier = Modifier.padding(bottom = 4.dp)
                        ) {
                            Text(
                                text = config.customHeadline,
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Black,
                                    fontSize = if (isSquare) 16.sp else 19.sp,
                                    lineHeight = 23.sp
                                ),
                                color = RathoreGreenDark,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    // Subtitle / Campaign Slogan
                    if (config.customSubHeadline.isNotBlank()) {
                        Text(
                            text = config.customSubHeadline,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp,
                                lineHeight = 16.sp
                            ),
                            color = Color(0xFF334155),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 10.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Leadership Unity Ribbon
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .width(32.dp)
                                .height(2.dp)
                                .background(RathoreOrange)
                        )
                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = null,
                            tint = RathoreGreen,
                            modifier = Modifier
                                .padding(horizontal = 5.dp)
                                .size(14.dp)
                        )
                        Box(
                            modifier = Modifier
                                .width(32.dp)
                                .height(2.dp)
                                .background(RathoreGreen)
                        )
                    }
                }
            }

            // 3. Top Header Layer (Edge to edge, 0 padding)
            if (selectedFrame?.headerImageUri != null) {
                AsyncImage(
                    model = selectedFrame.headerImageUri,
                    contentDescription = "Frame Header Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .align(Alignment.TopCenter),
                    contentScale = ContentScale.FillWidth
                )
            } else if (isTemplate1) {
                Template1HeaderView(modifier = Modifier.align(Alignment.TopCenter))
            } else {
                DefaultClassicHeaderView(modifier = Modifier.align(Alignment.TopCenter))
            }

            // 4. Bottom Footer Layer (Edge to edge, 0 padding)
            if (selectedFrame != null) {
                // Render Footer Background Layer
                if (selectedFrame.footerImageUri != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter)
                    ) {
                        AsyncImage(
                            model = selectedFrame.footerImageUri,
                            contentDescription = "Frame Footer Image",
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight(),
                            contentScale = ContentScale.FillWidth
                        )
                    }
                } else if (selectedFrame.templateStyle == "RED_RIBBON" || isRedRibbon) {
                    RedRibbonFooterBackground(modifier = Modifier.align(Alignment.BottomCenter))
                } else {
                    Template1FooterBackground(modifier = Modifier.align(Alignment.BottomCenter))
                }

                // 1. Admin Assigned Photo Slot
                if (config.showPhoto) {
                    val circleDiameter = canvasW * (selectedFrame.photoRadius * 2f)
                    val circleLeft = (canvasW * selectedFrame.photoCenterX) - (circleDiameter / 2f)
                    val circleTop = (canvasH * selectedFrame.photoCenterY) - (circleDiameter / 2f)

                    Box(
                        modifier = Modifier
                            .offset(x = circleLeft, y = circleTop)
                            .size(circleDiameter)
                            .clip(CircleShape)
                            .background(Color.White)
                            .border(2.5.dp, Color.White, CircleShape)
                            .border(4.dp, Color(0xFF162750), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        if (!config.userPhotoUri.isNullOrBlank()) {
                            AsyncImage(
                                model = config.userPhotoUri,
                                contentDescription = "User Photo",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "User Photo",
                                tint = Color(0xFF0C2340),
                                modifier = Modifier.size(circleDiameter * 0.65f)
                            )
                        }
                    }
                }

                // 2. Admin Assigned Name Slot
                if (config.showName) {
                    val nameColor = try {
                        Color(android.graphics.Color.parseColor(selectedFrame.textColorHex))
                    } catch (_: Exception) {
                        Color.White
                    }
                    val nameFamily = FrameStyleHelpers.getComposeFontFamily(selectedFrame.nameFontFamily)
                    val nameWeight = FrameStyleHelpers.getComposeFontWeight(selectedFrame.nameFontWeight)
                    val nameSize = (selectedFrame.nameFontSizeSp * (canvasW.value / 360f)).coerceIn(12f, 22f).sp

                    Text(
                        text = config.userName.ifBlank { "पूरा नाम..................." },
                        color = nameColor,
                        fontFamily = nameFamily,
                        fontWeight = nameWeight,
                        fontSize = nameSize,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .offset(
                                x = canvasW * selectedFrame.nameX,
                                y = canvasH * selectedFrame.nameY - 8.dp
                            )
                            .fillMaxWidth(1f - selectedFrame.nameX - 0.04f)
                    )
                }

                // 3. Admin Assigned Designation Slot
                if (config.showDesignation) {
                    val desigColor = try {
                        Color(android.graphics.Color.parseColor(selectedFrame.desigColorHex))
                    } catch (_: Exception) {
                        Color(0xFFE2E8F0)
                    }
                    val desigFamily = FrameStyleHelpers.getComposeFontFamily(selectedFrame.desigFontFamily)
                    val desigWeight = FrameStyleHelpers.getComposeFontWeight(selectedFrame.desigFontWeight)
                    val desigSize = (selectedFrame.desigFontSizeSp * (canvasW.value / 360f)).coerceIn(8.5f, 16f).sp

                    val desigLabel = buildString {
                        append(config.userDesignation.ifBlank { "पद..................." })
                        if (config.showMobile && config.userMobile.isNotBlank()) {
                            append(" | ")
                            append(config.userMobile)
                        }
                    }
                    Text(
                        text = desigLabel,
                        color = desigColor,
                        fontFamily = desigFamily,
                        fontWeight = desigWeight,
                        fontSize = desigSize,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .offset(
                                x = canvasW * selectedFrame.desigX,
                                y = canvasH * selectedFrame.desigY - 8.dp
                            )
                            .fillMaxWidth(1f - selectedFrame.desigX - 0.04f)
                    )
                }
            } else if (isTemplate1) {
                // Official Template 1 (Navy Curved wave, Congress hand symbol, circular worker photo, name & designation)
                Template1FooterView(
                    config = config,
                    canvasW = canvasW,
                    canvasH = canvasH,
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
            } else if (isRedRibbon) {
                // Template 2: Red Ribbon Footer
                RedRibbonFooterView(
                    config = config,
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
            } else {
                // Default Classic Navy Footer
                Template1FooterView(
                    config = config,
                    canvasW = canvasW,
                    canvasH = canvasH,
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
            }

            // 5. Optional Full Transparent PNG Frame Overlay
            if (selectedFrame?.fullFrameUri != null) {
                AsyncImage(
                    model = selectedFrame.fullFrameUri,
                    contentDescription = "Full Frame Overlay",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.FillBounds
                )
            }
        }
    }
}

/**
 * Template 1 Header View (Matches DESIGN IDEA 1)
 * Left: Framed floral garland leader tribute
 * Center: 5 Party leaders in circular badges with tricolor ribbon
 * Right: Hemant Rathore portrait
 */
@Composable
fun Template1HeaderView(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = Color.White
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Saffron & Green subtle top trim lines
            Row(modifier = Modifier.fillMaxWidth().height(3.dp)) {
                Box(modifier = Modifier.weight(1f).fillMaxHeight().background(RathoreOrange))
                Box(modifier = Modifier.weight(1f).fillMaxHeight().background(Color.White))
                Box(modifier = Modifier.weight(1f).fillMaxHeight().background(RathoreGreen))
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Left: Floral garland elder / leader tribute frame
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFFFEF3C7))
                            .border(1.5.dp, Color(0xFFD97706), RoundedCornerShape(6.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = Color(0xFFB45309),
                                modifier = Modifier.size(24.dp)
                            )
                            Text(
                                text = "सादर नमन",
                                fontSize = 7.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF92400E)
                            )
                        }
                    }
                }

                // Center: 5 Circular Leader portrait badges with tricolor rings
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    listOf("सोनिया जी", "राहुल जी", "खड़गे जी", "कमलनाथ जी", "दिग्विजय जी").forEach { leaderName ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(30.dp)
                                    .clip(CircleShape)
                                    .background(Color.White)
                                    .border(1.dp, RathoreOrange, CircleShape)
                                    .border(2.dp, Color.White, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = leaderName,
                                    tint = Color(0xFF1E3A8A),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Text(
                                text = leaderName,
                                fontSize = 6.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E293B),
                                maxLines = 1
                            )
                        }
                    }
                }

                // Right: Hemant Rathore portrait badge
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "हेमंत राठौर",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF1B5E20)
                        )
                        Text(
                            text = "पृथ्वीपुर (म.प्र.)",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF64748B)
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFDCFCE7))
                            .border(1.5.dp, RathoreGreen, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "हेमंत राठौर",
                            tint = RathoreGreenDark,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }

            // Separator line
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color(0xFFE2E8F0))
            )
        }
    }
}

/**
 * Template 1 Footer View (Exact match to DESIGN IDEA 1)
 * Features:
 * - Deep navy curved wave footer
 * - Congress Party Logo in circular tricolor badge on the right
 * - Circular worker photo placeholder at bottom-left
 * - Worker name in bold white text
 * - Designation & mobile number in clean white text
 */
@Composable
fun Template1FooterView(
    config: PosterIdentityConfig,
    canvasW: androidx.compose.ui.unit.Dp,
    canvasH: androidx.compose.ui.unit.Dp,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(118.dp)
    ) {
        // Deep Navy curved wave backdrop
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            val footerPath = Path().apply {
                moveTo(0f, height * 0.28f)
                cubicTo(
                    width * 0.35f, height * 0.12f,
                    width * 0.70f, height * 0.38f,
                    width, height * 0.22f
                )
                lineTo(width, height)
                lineTo(0f, height)
                close()
            }

            // Navy background fill
            drawPath(
                path = footerPath,
                color = Color(0xFF162750) // Deep Royal Congress Navy
            )

            // Saffron/Orange top accent curve line
            drawPath(
                path = footerPath,
                color = RathoreOrange,
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3.dp.toPx())
            )
        }

        // Content Row over Navy Wave
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 12.dp, end = 12.dp, top = 22.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left: Circular Worker Photo + Name & Designation
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // Circle Photo Cutout (as in DESIGN IDEA 1)
                if (config.showPhoto) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .border(3.dp, Color.White, CircleShape)
                            .border(5.dp, Color(0xFF162750), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        if (!config.userPhotoUri.isNullOrBlank()) {
                            AsyncImage(
                                model = config.userPhotoUri,
                                contentDescription = "User Photo",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "User Photo",
                                tint = Color(0xFF162750),
                                modifier = Modifier.size(46.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                }

                // Name, Designation & Mobile Number
                Column(modifier = Modifier.weight(1f)) {
                    if (config.showName) {
                        Text(
                            text = config.userName.ifBlank { "पूरा नाम..................." },
                            color = Color.White,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Black,
                                fontSize = 15.sp,
                                letterSpacing = 0.3.sp
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    if (config.showDesignation) {
                        val desigText = buildString {
                            append(config.userDesignation.ifBlank { "पद..................." })
                            if (config.showMobile) {
                                append(" | ")
                                append(config.userMobile.ifBlank { "मोबाइल नंबर" })
                            }
                        }
                        Text(
                            text = desigText,
                            color = Color(0xFFE2E8F0),
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 10.5.sp
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            // Right: Congress Party Hand Symbol (Panja)
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .border(2.dp, RathoreGreen, CircleShape)
                    .shadow(4.dp, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_congress_logo),
                    contentDescription = "Congress Party Logo",
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Fit
                )
            }
        }
    }
}

/**
 * Background-only Navy Wave & Congress Symbol for Template 1
 */
@Composable
fun Template1FooterBackground(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(118.dp)
    ) {
        // Deep Navy curved wave backdrop
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            val footerPath = Path().apply {
                moveTo(0f, height * 0.28f)
                cubicTo(
                    width * 0.35f, height * 0.12f,
                    width * 0.70f, height * 0.38f,
                    width, height * 0.22f
                )
                lineTo(width, height)
                lineTo(0f, height)
                close()
            }

            drawPath(
                path = footerPath,
                color = Color(0xFF162750)
            )

            drawPath(
                path = footerPath,
                color = RathoreOrange,
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3.dp.toPx())
            )
        }

        // Right: Congress Party Hand Symbol (Panja)
        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 12.dp, top = 20.dp)
                .size(48.dp)
                .clip(CircleShape)
                .background(Color.White)
                .border(2.dp, RathoreGreen, CircleShape)
                .shadow(4.dp, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_congress_logo),
                contentDescription = "Congress Party Logo",
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Fit
            )
        }
    }
}

/**
 * Background-only Red Ribbon for Template 2
 */
@Composable
fun RedRibbonFooterBackground(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(110.dp)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(36.dp),
            color = Color(0xFFDC2626)
        ) {}
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            color = Color(0xFF0F172A)
        ) {}
    }
}

/**
 * Template 2: Red Ribbon Footer View
 */
@Composable
fun RedRibbonFooterView(
    config: PosterIdentityConfig,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        // Red Ribbon strip
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color(0xFFDC2626)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (config.showName) {
                    Text(
                        text = config.userName.ifBlank { "पूरा नाम" },
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        maxLines = 1
                    )
                }
                if (config.showDesignation) {
                    Text(
                        text = config.userDesignation.ifBlank { "कार्यकर्ता" },
                        color = Color(0xFFFEF3C7),
                        fontSize = 10.sp,
                        maxLines = 1
                    )
                }
            }
        }

        // Dark base
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color(0xFF0F172A)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (config.showPhoto) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        if (!config.userPhotoUri.isNullOrBlank()) {
                            AsyncImage(
                                model = config.userPhotoUri,
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFF0F172A))
                        }
                    }
                }
                Text("टीम राठौर • विधानसभा पृथ्वीपुर", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

/**
 * Default Classic Header
 */
@Composable
fun DefaultClassicHeaderView(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = Color(0xFF0C2340)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "आपका विश्वास",
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Black,
                        fontSize = 15.sp,
                        letterSpacing = 0.8.sp
                    )
                )
                Text(
                    text = "सदैव आपके साथ",
                    color = Color.White.copy(alpha = 0.85f),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.Medium
                    )
                )
            }
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color.White.copy(alpha = 0.2f)
            ) {
                Text(
                    text = "जनता का साथ",
                    color = Color.White,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 8.5.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }
    }
}
