package com.example.ui.screens

import android.net.Uri
import android.widget.VideoView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.model.Language
import com.example.model.VideoFeedItem
import com.example.ui.components.AdminVideoEditorDialog
import com.example.ui.theme.*
import com.example.util.AppStrings
import com.example.viewmodel.AppViewModel

@Composable
fun VideosScreen(
    viewModel: AppViewModel,
    language: Language,
    videoList: List<VideoFeedItem>,
    isVideoPlaying: Boolean,
    activePlayingVideo: VideoFeedItem?,
    isVideoProcessing: Boolean
) {
    var showVideoEditor by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(RathoreBackground)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            // Header info with Video Editor Trigger
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .width(4.dp)
                            .height(20.dp)
                            .background(RathoreOrange, RoundedCornerShape(2.dp))
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = AppStrings.get("videos_title", language),
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = RathoreGreen
                        )
                        Text(
                            text = "Admin compressed video feed for easy WhatsApp sharing",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                }

                Button(
                    onClick = { showVideoEditor = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9333EA)),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.VideoCall, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("वीडियो एडिटर", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        items(videoList) { video ->
            VideoCard(
                video = video,
                language = language,
                onPlay = { viewModel.playVideo(video) },
                onReuse = { viewModel.reuseVideo(video) }
            )
        }

        item {
            Spacer(modifier = Modifier.height(70.dp))
        }
    }

    // Video Player Modal / Dialog
    if (isVideoPlaying && activePlayingVideo != null) {
        AlertDialog(
            onDismissRequest = { viewModel.stopVideo() },
            confirmButton = {
                Button(
                    onClick = { viewModel.stopVideo() },
                    colors = ButtonDefaults.buttonColors(containerColor = RathoreGreen)
                ) {
                    Text("बंद करें (Close)", color = Color.White)
                }
            },
            title = {
                Text(
                    text = if (language == Language.HINDI) activePlayingVideo.titleHi else activePlayingVideo.titleEn,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (!activePlayingVideo.videoUri.isNullOrBlank()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.Black),
                            contentAlignment = Alignment.Center
                        ) {
                            AndroidView(
                                factory = { ctx ->
                                    VideoView(ctx).apply {
                                        setVideoURI(Uri.parse(activePlayingVideo.videoUri))
                                        setOnPreparedListener { mp ->
                                            mp.isLooping = true
                                            start()
                                        }
                                    }
                                },
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    } else {
                        // Video player canvas simulation for mock feed items
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.Black),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.PauseCircle,
                                    contentDescription = "Playing",
                                    tint = RathoreOrange,
                                    modifier = Modifier.size(54.dp)
                                )
                                Text(
                                    text = "वीडियो चल रहा है (${activePlayingVideo.duration})",
                                    color = Color.White,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { 0.45f },
                        modifier = Modifier.fillMaxWidth(),
                        color = RathoreOrange,
                        trackColor = Color(0xFFE0E0E0)
                    )
                }
            }
        )
    }

    // Admin Video Editor Studio Dialog
    if (showVideoEditor) {
        AdminVideoEditorDialog(
            isOpen = showVideoEditor,
            onDismiss = { showVideoEditor = false },
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
                showVideoEditor = false
            }
        )
    }

    // Video Processing Dialog (from PDF page 12)
    if (isVideoProcessing) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissVideoProcessing() },
            confirmButton = {},
            title = {
                Text(AppStrings.get("video_processing", language), fontWeight = FontWeight.Bold)
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(color = RathoreOrange)
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = AppStrings.get("video_processing_sub", language),
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }
            }
        )
    }
}

@Composable
fun VideoCard(
    video: VideoFeedItem,
    language: Language,
    onPlay: () -> Unit,
    onReuse: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 2.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderStroke)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Video Thumbnail Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(190.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF1E293B),
                                Color(0xFF0F172A)
                            )
                        )
                    )
                    .clickable { onPlay() }
                    .testTag("video_thumb_${video.id}"),
                contentAlignment = Alignment.Center
            ) {
                // Background artistic pattern
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayCircleFilled,
                        contentDescription = "Play Video",
                        tint = Color.White,
                        modifier = Modifier.size(56.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (language == Language.HINDI) video.titleHi else video.titleEn,
                        color = Color.White,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        maxLines = 1
                    )
                }

                // Jacket requirement badge top left if enabled by admin
                if (video.requiresJacket) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = RathoreOrange,
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "जैकेट अनिवार्य",
                                color = Color.White,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                )
                            )
                        }
                    }
                }

                // Duration badge bottom right
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color.Black.copy(alpha = 0.75f),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp)
                ) {
                    Text(
                        text = video.duration,
                        color = Color.White,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = if (language == Language.HINDI) video.titleHi else video.titleEn,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = TextPrimary
            )
            Text(
                text = if (language == Language.HINDI) video.captionHi else video.captionEn,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
            )

            // Buttons: Conditioned on requiresJacket
            if (video.requiresJacket) {
                // Direct download is strictly hidden as requested by user; must be created in user's jacket first!
                Button(
                    onClick = onReuse,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("btn_reuse_video_${video.id}"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = RathoreOrange)
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "अपनी जैकेट में बनाएं (अनिवार्य)",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            } else {
                // If not locked to jacket, both direct download and reuse in jacket are available
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onPlay,
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp),
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BorderStroke)
                    ) {
                        Icon(Icons.Default.Download, contentDescription = null, tint = TextPrimary, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(AppStrings.get("download_video", language), color = TextPrimary, fontWeight = FontWeight.SemiBold)
                    }

                    Button(
                        onClick = onReuse,
                        modifier = Modifier
                            .weight(1.2f)
                            .height(46.dp)
                            .testTag("btn_reuse_video_${video.id}"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = RathoreOrange)
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = AppStrings.get("reuse_video", language),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    }
}
