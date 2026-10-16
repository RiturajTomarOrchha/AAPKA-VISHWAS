package com.example.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun CollagePhotoSlotGrid(
    collageCount: Int,
    photos: List<String>,
    onAddPhoto: (String) -> Unit,
    onRemovePhoto: (Int) -> Unit,
    accentColor: Color,
    label: String,
    modifier: Modifier = Modifier,
    onEditPhoto: ((Int) -> Unit)? = null,
    onOpenEditor: (() -> Unit)? = null
) {
    var showPhotoPickerForSlot by remember { mutableStateOf<Int?>(null) }
    var customPhotoNameInput by remember { mutableStateOf("") }

    // Direct Gallery Picker for the current slot
    val galleryPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            onAddPhoto(uri.toString())
            showPhotoPickerForSlot = null
            customPhotoNameInput = ""
        }
    }

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        color = Color(0xFFF8FAFC),
        border = BorderStroke(1.dp, accentColor.copy(alpha = 0.35f))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Header Info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Collections,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "$label (${photos.size}/$collageCount संलग्न)",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimary
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    if (onOpenEditor != null) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = accentColor.copy(alpha = 0.15f),
                            modifier = Modifier.clickable { onOpenEditor() }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = accentColor, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "एडिटर",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = accentColor
                                )
                            }
                        }
                    }

                    if (photos.size < collageCount) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = accentColor.copy(alpha = 0.12f)
                        ) {
                            Text(
                                text = "${collageCount - photos.size} खाली स्लॉट",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = accentColor,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    } else {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFDCFCE7)
                        ) {
                            Text(
                                text = "पूर्ण ✓",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF15803D),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Grid Slots layout based on collageCount (1, 2, 3, or 4)
            val rows: List<List<Int>> = when (collageCount) {
                1 -> listOf(listOf(0))
                2 -> listOf(listOf(0, 1))
                3 -> listOf(listOf(0), listOf(1, 2))
                4 -> listOf(listOf(0, 1), listOf(2, 3))
                else -> listOf(listOf(0))
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                rows.forEach { rowSlots ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        rowSlots.forEach { slotIndex ->
                            val isFilled = slotIndex < photos.size
                            val photoName = if (isFilled) photos[slotIndex] else null

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(if (collageCount == 1) 94.dp else 84.dp)
                            ) {
                                if (isFilled && photoName != null) {
                                    // Filled Photo Box with Details and Delete Action
                                    Surface(
                                        modifier = Modifier.fillMaxSize(),
                                        shape = RoundedCornerShape(8.dp),
                                        color = Color.White,
                                        border = BorderStroke(1.2.dp, accentColor)
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .padding(horizontal = 8.dp, vertical = 6.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Row(
                                                modifier = Modifier.weight(1f),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(if (collageCount == 1) 40.dp else 34.dp)
                                                        .clip(RoundedCornerShape(6.dp))
                                                        .background(accentColor.copy(alpha = 0.15f)),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    if (photoName.startsWith("content:") || photoName.startsWith("file:") || photoName.startsWith("http")) {
                                                        AsyncImage(
                                                            model = photoName,
                                                            contentDescription = null,
                                                            modifier = Modifier.fillMaxSize(),
                                                            contentScale = ContentScale.Crop
                                                        )
                                                    } else {
                                                        Icon(
                                                            imageVector = Icons.Default.Image,
                                                            contentDescription = null,
                                                            tint = accentColor,
                                                            modifier = Modifier.size(20.dp)
                                                        )
                                                    }
                                                }
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Column(modifier = Modifier.weight(1f)) {
                                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                                        Text(
                                                            text = "फोटो ${slotIndex + 1}",
                                                            fontSize = 11.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = TextPrimary
                                                        )
                                                        Spacer(modifier = Modifier.width(4.dp))
                                                        Text(
                                                            text = "✓ संलग्न",
                                                            fontSize = 9.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = Color(0xFF16A34A)
                                                        )
                                                    }
                                                    Text(
                                                        text = photoName,
                                                        fontSize = 10.sp,
                                                        color = TextSecondary,
                                                        maxLines = 1,
                                                        overflow = TextOverflow.Ellipsis
                                                    )
                                                    Text(
                                                        text = "HD • अनुकूलित",
                                                        fontSize = 8.5.sp,
                                                        color = Color(0xFF64748B)
                                                    )
                                                }
                                            }

                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                if (onEditPhoto != null) {
                                                    IconButton(
                                                        onClick = { onEditPhoto(slotIndex) },
                                                        modifier = Modifier.size(28.dp)
                                                    ) {
                                                        Icon(
                                                            imageVector = Icons.Default.Tune,
                                                            contentDescription = "एडिट करें",
                                                            tint = accentColor,
                                                            modifier = Modifier.size(16.dp)
                                                        )
                                                    }
                                                }

                                                IconButton(
                                                    onClick = { onRemovePhoto(slotIndex) },
                                                    modifier = Modifier.size(28.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Close,
                                                        contentDescription = "हटाएं",
                                                        tint = Color(0xFFEF4444),
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    // Clean Empty Box ready for user-selected photo
                                    Surface(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clickable {
                                                showPhotoPickerForSlot = slotIndex
                                                customPhotoNameInput = ""
                                            },
                                        shape = RoundedCornerShape(8.dp),
                                        color = Color.White,
                                        border = BorderStroke(1.2.dp, accentColor.copy(alpha = 0.45f))
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .padding(6.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.AddPhotoAlternate,
                                                contentDescription = "फोटो अपलोड करें",
                                                tint = accentColor,
                                                modifier = Modifier.size(22.dp)
                                            )
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = "खाली बॉक्स ${slotIndex + 1}",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = TextPrimary
                                            )
                                            Text(
                                                text = "+ फोटो चुनें",
                                                fontSize = 9.5.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = accentColor
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

    // Photo Upload / Selector Dialog for specific slot
    if (showPhotoPickerForSlot != null) {
        val slot = showPhotoPickerForSlot!!
        AlertDialog(
            onDismissRequest = { showPhotoPickerForSlot = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.UploadFile, contentDescription = null, tint = accentColor)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("फोटो ${slot + 1} अपलोड करें", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "खाली बॉक्स ${slot + 1} के लिए डिवाइस की गैलरी अथवा कैमरे से फोटो चुनें:",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )

                    Button(
                        onClick = {
                            galleryPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("📱 डिवाइस गैलरी से फोटो चुनें", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }

                    // Quick Gallery Presets
                    Text(
                        text = "या त्वरित चयन हेतु फोटो नाम दर्ज करें:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )

                    OutlinedTextField(
                        value = customPhotoNameInput,
                        onValueChange = { customPhotoNameInput = it },
                        placeholder = { Text("उदा. samvad_chaupal_${slot + 1}.jpg") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp)
                    )

                    // One-tap presets
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("चौपाल_फोटो_${slot + 1}.jpg", "रैली_फोटो_${slot + 1}.jpg").forEach { preset ->
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = accentColor.copy(alpha = 0.1f),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { customPhotoNameInput = preset }
                            ) {
                                Text(
                                    text = preset,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = accentColor,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(vertical = 6.dp, horizontal = 4.dp)
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val finalPhoto = if (customPhotoNameInput.isNotBlank()) {
                            customPhotoNameInput.trim()
                        } else {
                            "rathore_user_photo_${slot + 1}.jpg"
                        }
                        onAddPhoto(finalPhoto)
                        showPhotoPickerForSlot = null
                        customPhotoNameInput = ""
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = accentColor)
                ) {
                    Text("फोटो संलग्न करें ✓")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPhotoPickerForSlot = null }) {
                    Text("रद्द करें")
                }
            }
        )
    }
}
