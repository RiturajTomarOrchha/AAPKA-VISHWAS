package com.example.ui.screens

import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.model.FeedContentType
import com.example.model.FeedItem
import com.example.model.Language
import com.example.model.MeetingStatus
import com.example.model.PosterIdentityConfig
import com.example.ui.components.PosterCanvas
import com.example.ui.components.TeamRathoreEmblem
import com.example.ui.components.TeamRathoreLogoIcon
import com.example.ui.components.TricolorRibbon
import com.example.ui.theme.*
import com.example.util.AppStrings
import com.example.viewmodel.AppScreen
import com.example.viewmodel.AppViewModel
import com.example.viewmodel.MainTab

@Composable
fun HomeScreen(
    viewModel: AppViewModel,
    language: Language,
    feedItems: List<FeedItem>,
    activeFilter: String,
    posterConfig: PosterIdentityConfig
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var showAllCategoriesDialog by remember { mutableStateOf(false) }

    val dynamicCategories = uiState.feedCategories
    val categoriesList = remember(dynamicCategories, language) {
        listOf("ALL" to (if (language == Language.HINDI) "सभी" else "All")) +
            dynamicCategories.map { it to it }
    }

    val filteredItems = when (activeFilter) {
        "ALL" -> feedItems
        "OFFICIAL" -> feedItems.filter { it.type == FeedContentType.ADMIN_GRAPHIC }
        "SOCIAL" -> feedItems.filter { it.type == FeedContentType.FACEBOOK_POST }
        "VIDEOS" -> feedItems.filter { it.type == FeedContentType.VIDEO }
        else -> feedItems.filter {
            it.categoryHi.contains(activeFilter, ignoreCase = true) ||
            it.titleHi.contains(activeFilter, ignoreCase = true) ||
            it.captionHi.contains(activeFilter, ignoreCase = true) ||
            it.facebookAuthor.contains(activeFilter, ignoreCase = true)
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(RathoreBackground)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Special Meeting Reminder / Countdown Header (15-min countdown + War Room direct join & invite link)
        // बैठक की सूचना तभी दिखानी है जब बैठक शेड्यूल हो
        if (uiState.meetingInfo.isScheduled && uiState.meetingInfo.status != MeetingStatus.ENDED) {
            item {
                val meeting = uiState.meetingInfo
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = Color.White,
                border = BorderStroke(1.2.dp, if (meeting.status == MeetingStatus.LIVE) Color(0xFFDC2626) else RathoreGreen.copy(alpha = 0.5f)),
                shadowElevation = 2.dp
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(if (meeting.status == MeetingStatus.LIVE) Color(0xFFDC2626) else RathoreOrange)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (meeting.status == MeetingStatus.LIVE)
                                    "🔴 लाइव वर्चुअल बैठक जारी है"
                                else
                                    "⏰ बैठक सूचना • 15 मिनट पूर्व अलर्ट",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (meeting.status == MeetingStatus.LIVE) Color(0xFFDC2626) else RathoreOrangeDark
                                )
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = RathoreGreen.copy(alpha = 0.12f)
                        ) {
                            Text(
                                text = "${meeting.dateHi} • ${meeting.timeHi}",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = RathoreGreen,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = if (language == Language.HINDI) meeting.titleHi else meeting.titleEn,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                viewModel.selectTab(MainTab.WAR_ROOM)
                                viewModel.startMeetingFlow()
                            },
                            modifier = Modifier.weight(1.3f).height(38.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = if (meeting.status == MeetingStatus.LIVE) Color(0xFFDC2626) else RathoreGreen),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                        ) {
                            Icon(Icons.Default.VideoCall, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (meeting.status == MeetingStatus.LIVE) "सीधे जुड़ें (Live)" else "वॉर रूम बैठक में जुड़ें",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        OutlinedButton(
                            onClick = {
                                val sendIntent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(
                                        Intent.EXTRA_TEXT,
                                        "टीम राठौर विशेष वर्चुअल बैठक\nविषय: ${meeting.titleHi}\nसमय: ${meeting.dateHi} • ${meeting.timeHi}\n\nमाननीय पृथ्वीपुर विधायक नितेंद्र बृजेंद्र सिंह राठौर जी से सीधे संवाद व संगठनात्मक चर्चा हेतु अभी टीम राठौर ऐप डाउनलोड कर जुड़ें:\nhttps://teamrathore.in/meeting/join"
                                    )
                                }
                                context.startActivity(Intent.createChooser(sendIntent, "मीटिंग आमंत्रण लिंक शेयर करें"))
                            },
                            modifier = Modifier.weight(1f).height(38.dp),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, RathoreOrange),
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp)
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, tint = RathoreOrangeDark, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("लिंक भेजें", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = RathoreOrangeDark)
                        }
                    }
                }
            }
        }
        }

        // Rotating Emergency / Important Notification Ticker (Left-to-Right Animated Transition)
        item {
            HomeNotificationTicker(
                language = language,
                onDirectConnectClick = { viewModel.navigateTo(AppScreen.DirectConnect) },
                onAlertClick = { viewModel.navigateTo(AppScreen.Notifications) },
                onCampaignClick = { viewModel.selectTab(MainTab.WAR_ROOM) }
            )
        }

        // Horizontal Category Scrolling Filter (with expandable "सभी" modal)
        item {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                item {
                    Surface(
                        modifier = Modifier
                            .clickable { showAllCategoriesDialog = true }
                            .testTag("btn_all_categories_more"),
                        shape = RoundedCornerShape(20.dp),
                        color = if (activeFilter == "ALL") RathoreGreen else Color.White,
                        border = BorderStroke(1.dp, if (activeFilter == "ALL") RathoreGreen else BorderStroke),
                        shadowElevation = 1.dp
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Category,
                                contentDescription = null,
                                tint = if (activeFilter == "ALL") Color.White else RathoreGreen,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (language == Language.HINDI) "सभी श्रेणियां ▾" else "All Categories ▾",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                color = if (activeFilter == "ALL") Color.White else TextPrimary
                            )
                        }
                    }
                }

                items(categoriesList.filter { it.first != "ALL" }) { (catId, catName) ->
                    val isSelected = activeFilter == catId
                    Surface(
                        modifier = Modifier
                            .clickable { viewModel.setFeedFilter(catId) }
                            .testTag("category_chip_$catId"),
                        shape = RoundedCornerShape(20.dp),
                        color = if (isSelected) RathoreGreen else Color.White,
                        border = BorderStroke(1.dp, if (isSelected) RathoreGreen else BorderStroke),
                        shadowElevation = if (isSelected) 2.dp else 0.dp
                    ) {
                        Text(
                            text = catName,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            ),
                            color = if (isSelected) Color.White else TextPrimary,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
                        )
                    }
                }
            }
        }

        // Empty state when filter matches nothing
        if (filteredItems.isEmpty()) {
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, BorderStroke)
                ) {
                    Column(
                        modifier = Modifier.padding(26.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.Feed, contentDescription = null, tint = RathoreGreen, modifier = Modifier.size(36.dp))
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "इस श्रेणी में अभी कोई पोस्ट उपलब्ध नहीं है",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        TextButton(onClick = { viewModel.setFeedFilter("ALL") }) {
                            Text("सभी पोस्ट देखें", color = RathoreGreen, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Feed Items
        items(filteredItems.filter { it.isEnabled }) { item ->
            when (item.type) {
                FeedContentType.ADMIN_GRAPHIC -> {
                    AdminGraphicPostCard(
                        item = item,
                        language = language,
                        posterConfig = posterConfig,
                        onShare = {
                            viewModel.shareContent("टीम राठौर: ${item.posterHeadlineHi}\n${item.posterSubHeadlineHi}\nडाउनलोड करें: https://teamrathore.in")
                        },
                        onCopy = {
                            val copyMatter = buildString {
                                if (item.posterHeadlineHi.isNotBlank()) appendLine(item.posterHeadlineHi)
                                if (item.captionHi.isNotBlank()) appendLine(item.captionHi)
                                append("#टीम_राठौर #हेमंत_राठौर #निवाड़ी")
                            }
                            viewModel.copyPostText(copyMatter)
                        },
                        onDownload = {
                            viewModel.downloadFeedGraphic(item)
                        },
                        onReuse = { viewModel.reuseFeedItem(item) }
                    )
                }
                FeedContentType.FACEBOOK_POST -> {
                    FacebookPostCard(
                        item = item,
                        language = language,
                        onViewOnFb = {
                            viewModel.shareContent("Facebook पर देखें: ${item.facebookUrl}")
                        },
                        onCopy = {
                            val copyContent = "${item.facebookAuthor}:\n${item.facebookContentHi}\n\n#टीम_राठौर #निवाड़ी"
                            viewModel.copyPostText(copyContent)
                        },
                        onDownload = {
                            viewModel.downloadFeedGraphic(item)
                        },
                        onReshare = {
                            // Transfer post into user's personal jacket format
                            viewModel.reuseFeedItem(item)
                        }
                    )
                }
                FeedContentType.VIDEO -> {
                    // Video preview in feed
                    AdminGraphicPostCard(
                        item = item,
                        language = language,
                        posterConfig = posterConfig,
                        onShare = {
                            viewModel.shareContent("टीम राठौर वीडियो: ${item.titleHi}\nदेखें: https://teamrathore.in/videos")
                        },
                        onCopy = {
                            val copyMatter = "${item.titleHi}\n${item.captionHi}\n#टीम_राठौर #वीडियो"
                            viewModel.copyPostText(copyMatter)
                        },
                        onDownload = {
                            viewModel.downloadFeedGraphic(item)
                        },
                        onReuse = { viewModel.reuseFeedItem(item) }
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(60.dp))
        }
    }

    // Full Categories Selection Dialog
    if (showAllCategoriesDialog) {
        AlertDialog(
            onDismissRequest = { showAllCategoriesDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Category, contentDescription = null, tint = RathoreGreen)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (language == Language.HINDI) "श्रेणी चुनें" else "Select Category",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categoriesList.forEach { (catId, catName) ->
                        val isCurrent = activeFilter == catId
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.setFeedFilter(catId)
                                    showAllCategoriesDialog = false
                                },
                            shape = RoundedCornerShape(10.dp),
                            color = if (isCurrent) Color(0xFFF0FDF4) else Color(0xFFF8FAFC),
                            border = BorderStroke(1.dp, if (isCurrent) RathoreGreen else Color(0xFFE2E8F0))
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = catName,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium
                                    ),
                                    color = if (isCurrent) RathoreGreen else TextPrimary
                                )
                                if (isCurrent) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = RathoreGreen, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showAllCategoriesDialog = false }) {
                    Text(if (language == Language.HINDI) "बंद करें" else "Close", color = RathoreGreen)
                }
            }
        )
    }
}

data class TickerItem(
    val title: String,
    val subtitle: String,
    val gradientColors: List<Color>,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val onClickType: Int
)

@Composable
fun HomeNotificationTicker(
    language: Language,
    onDirectConnectClick: () -> Unit,
    onAlertClick: () -> Unit,
    onCampaignClick: () -> Unit
) {
    val items = remember {
        listOf(
            TickerItem(
                title = "आपका विधायक आपके पास",
                subtitle = "सीधी बात, सीधे समाधान • समस्या या सुझाव सीधे विधायक कार्यालय भेजें",
                gradientColors = listOf(RathoreGreen, Color(0xFF1B5E20)),
                icon = Icons.Default.ConnectWithoutContact,
                onClickType = 1
            ),
            TickerItem(
                title = "एडमिन विशेष सूचना",
                subtitle = "12 सितंबर को जिला मुख्यालय पर विशाल संगठनात्मक बैठक • अनिवार्य उपस्थिति",
                gradientColors = listOf(Color(0xFFC62828), Color(0xFF880E4F)),
                icon = Icons.Default.Emergency,
                onClickType = 2
            ),
            TickerItem(
                title = "विशेष सदस्यता महा-अभियान",
                subtitle = "प्रत्येक बूथ पर 10 नए युवा व महिला साथियों को संगठन से जोड़ने का लक्ष्य",
                gradientColors = listOf(RathoreOrange, Color(0xFFD84315)),
                icon = Icons.Default.Campaign,
                onClickType = 3
            ),
            TickerItem(
                title = "जनसंवाद चौपाल कार्यक्रम",
                subtitle = "निवाड़ी एवं पृथ्वीपुर में विधायक हेमंत राठौर जी का सघन जनसंवाद — 14 सितंबर",
                gradientColors = listOf(Color(0xFF1565C0), Color(0xFF0D47A1)),
                icon = Icons.Default.Event,
                onClickType = 1
            )
        )
    }

    var currentIndex by remember { mutableIntStateOf(0) }

    // Left-to-Right auto-scrolling cycle every 4.5 seconds
    LaunchedEffect(Unit) {
        while (true) {
            kotlinx.coroutines.delay(4500)
            currentIndex = (currentIndex + 1) % items.size
        }
    }

    val currentItem = items[currentIndex]

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                when (currentItem.onClickType) {
                    1 -> onDirectConnectClick()
                    2 -> onAlertClick()
                    3 -> onCampaignClick()
                }
            }
            .testTag("banner_notification_ticker"),
        shape = RoundedCornerShape(14.dp),
        shadowElevation = 3.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(currentItem.gradientColors)
                )
                .padding(14.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.22f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = currentItem.icon,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = currentItem.title,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                ),
                                color = Color.White,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                            Spacer(modifier = Modifier.height(3.dp))

                            Text(
                                text = currentItem.subtitle,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 12.sp,
                                    lineHeight = 16.sp
                                ),
                                color = Color.White.copy(alpha = 0.95f),
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = "Open",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Progress Indicator Dots
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    items.indices.forEach { index ->
                        val isCurrent = index == currentIndex
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 3.dp)
                                .size(if (isCurrent) 16.dp else 6.dp, 4.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(
                                    if (isCurrent) Color.White else Color.White.copy(alpha = 0.35f)
                                )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FilterChipItem(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = if (isSelected) RathoreGreen else Color.White,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isSelected) RathoreGreen else BorderStroke
        ),
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                fontSize = 12.sp
            ),
            color = if (isSelected) Color.White else TextPrimary,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
        )
    }
}

@Composable
fun AdminGraphicPostCard(
    item: FeedItem,
    language: Language,
    posterConfig: PosterIdentityConfig,
    onShare: () -> Unit,
    onCopy: () -> Unit,
    onDownload: () -> Unit,
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
            // Post Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .width(4.dp)
                            .height(18.dp)
                            .background(RathoreOrange, RoundedCornerShape(2.dp))
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = if (language == Language.HINDI) item.titleHi else item.titleEn,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = TextPrimary
                        )
                        Text(
                            text = if (language == Language.HINDI) item.categoryHi else item.categoryEn,
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary
                        )
                    }
                }

                Text(
                    text = if (language == Language.HINDI) item.timestampHi else item.timestampEn,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextTertiary
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 1. Short Caption on TOP of photo
            val shortCaption = if (language == Language.HINDI) {
                if (item.captionHi.isNotBlank()) item.captionHi else item.titleHi
            } else {
                if (item.captionEn.isNotBlank()) item.captionEn else item.titleEn
            }
            if (shortCaption.isNotBlank()) {
                Text(
                    text = shortCaption,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.5.sp
                    ),
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            // 2. The Photo in the middle (4:5 Aspect Ratio)
            val displayImageUrl = item.imageUrl ?: ""
            if (displayImageUrl.isNotBlank()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(4f / 5f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF0F172A)),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = displayImageUrl,
                        contentDescription = shortCaption,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    if (item.type == FeedContentType.VIDEO) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .background(Color.Black.copy(alpha = 0.65f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.PlayArrow,
                                contentDescription = "Play Video",
                                tint = Color.White,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                    }
                }
            } else if (item.type == FeedContentType.VIDEO) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(4f / 5f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF1E1B4B)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .background(Color(0xFF9333EA), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.White, modifier = Modifier.size(36.dp))
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = item.titleHi,
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "🎬 वीडियो • टीम राठौर",
                            color = Color(0xFFC084FC),
                            fontSize = 12.sp
                        )
                    }
                }
            } else {
                // Fallback to Canvas preview
                PosterCanvas(
                    config = posterConfig.copy(
                        customHeadline = if (item.posterHeadlineHi.isNotBlank()) item.posterHeadlineHi else item.titleHi,
                        customSubHeadline = if (item.posterSubHeadlineHi.isNotBlank()) item.posterSubHeadlineHi else item.captionHi
                    ),
                    isSquare = false,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(4f / 5f)
                        .clip(RoundedCornerShape(12.dp))
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 3. Message / Content below the photo
            val messageText = if (language == Language.HINDI) {
                if (item.posterHeadlineHi.isNotBlank()) item.posterHeadlineHi else item.titleHi
            } else {
                item.titleEn
            }
            if (messageText.isNotBlank()) {
                Text(
                    text = messageText,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 12.5.sp,
                        lineHeight = 17.sp
                    ),
                    color = Color(0xFF334155)
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            // Social Engagement Counts
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Favorite, contentDescription = null, tint = Color(0xFFE53935), modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(item.likesCount, style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.ChatBubbleOutline, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(item.commentsCount, style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Share, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(item.sharesCount, style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Primary Action: Apply own photo and name with frame jacket
            Button(
                onClick = onReuse,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
                    .testTag("btn_reuse_post_${item.id}"),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = RathoreOrange)
            ) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "सीधा अपना फोटो व नाम लगाएं (फ्रेम जैकेट के साथ)",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
        }
    }
}

@Composable
fun FacebookPostCard(
    item: FeedItem,
    language: Language,
    onViewOnFb: () -> Unit,
    onCopy: () -> Unit,
    onDownload: () -> Unit,
    onReshare: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 2.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderStroke)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // FB Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .width(4.dp)
                            .height(18.dp)
                            .background(Color(0xFF1877F2), RoundedCornerShape(2.dp))
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = AppStrings.get("facebook_post", language),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimary
                    )
                }
                Icon(Icons.Default.MoreHoriz, contentDescription = null, tint = TextTertiary)
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Author preview
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF1877F2)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(item.facebookAuthor, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                    Text("${item.facebookTimeHi} • 🌐", style = MaterialTheme.typography.labelSmall, color = TextTertiary)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // FB Post Content
            Text(
                text = item.facebookContentHi,
                style = MaterialTheme.typography.bodyMedium,
                color = TextPrimary
            )

            val updatePhoto = item.imageUrl
            if (!updatePhoto.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(4f / 5f)
                        .clip(RoundedCornerShape(12.dp)),
                    color = Color(0xFFF1F5F9),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
                ) {
                    AsyncImage(
                        model = updatePhoto,
                        contentDescription = item.titleHi,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // FB Embed Visual Quote Banner
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFFF0F4F8),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFD0D7DE))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "“${item.facebookContentHi}”",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp
                        ),
                        color = Color(0xFF1E293B)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "— ${item.facebookAuthor}",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = RathoreGreen
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action Buttons: Copy, FB Open, Reshare with Jacket
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onCopy,
                    modifier = Modifier
                        .weight(0.9f)
                        .height(40.dp)
                        .testTag("btn_copy_fb_${item.id}"),
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderStroke),
                    contentPadding = PaddingValues(horizontal = 6.dp)
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = null, tint = TextPrimary, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("कॉपी", fontSize = 12.sp, color = TextPrimary, fontWeight = FontWeight.Medium)
                }

                OutlinedButton(
                    onClick = onViewOnFb,
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp),
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1877F2).copy(alpha = 0.5f)),
                    contentPadding = PaddingValues(horizontal = 6.dp)
                ) {
                    Text(
                        text = AppStrings.get("view_on_fb", language),
                        color = Color(0xFF1877F2),
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                        fontSize = 11.sp
                    )
                }

                Button(
                    onClick = onReshare,
                    modifier = Modifier
                        .weight(1.4f)
                        .height(40.dp)
                        .testTag("btn_fb_reshare"),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1877F2)),
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.White, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "फ्रेम सहित शेयर",
                        color = Color.White,
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}
