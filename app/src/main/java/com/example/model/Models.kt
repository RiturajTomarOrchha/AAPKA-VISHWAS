package com.example.model

enum class Language(val code: String, val displayName: String, val nativeName: String) {
    HINDI("hi", "Hindi", "हिंदी"),
    ENGLISH("en", "English", "English")
}

enum class UserRole(
    val id: String,
    val titleHi: String,
    val titleEn: String,
    val descHi: String,
    val descEn: String
) {
    SUPPORTER(
        "supporter",
        "समर्थक",
        "Supporter",
        "दैनिक पोस्टर, सुविचार, वीडियो एवं सीधा संवाद",
        "Daily posters, thoughts, videos & direct connect"
    ),
    WORKER(
        "worker",
        "कार्यकर्ता",
        "Worker",
        "प्रचार सामग्री, वॉर रूम एवं संगठनात्मक कार्यक्रम",
        "Campaign material, War Room & organizational events"
    ),
    OFFICE_BEARER(
        "office_bearer",
        "पदाधिकारी",
        "Office Bearer",
        "संगठनात्मक जिम्मेदारियाँ, बूथ समन्वय व समीक्षा",
        "Organizational responsibilities, booth coordination & review"
    ),
    ADMIN(
        "admin",
        "व्यवस्थापक / एडमिन",
        "Admin",
        "केंद्रीय व्यवस्थापक एवं संपूर्ण मंच प्रबंधन",
        "Central Administrator & Full Platform Management"
    )
}

data class UserProfile(
    val fullName: String = "",
    val fatherName: String = "",
    val motherName: String = "",
    val gender: String = "पुरुष",
    val dob: String = "",
    val mobile: String = "",
    val whatsapp: String = "",
    val email: String = "",
    val state: String = "मध्य प्रदेश", // Fixed State
    val district: String = "",
    val assembly: String = "",
    val village: String = "",
    val role: UserRole = UserRole.SUPPORTER,
    val designation: String = "समर्थक",
    val isApproved: Boolean = false,
    val isPendingReview: Boolean = false,
    val reviewReason: String = "",
    val customPhotoUri: String? = null,
    val isBlocked: Boolean = false,
    val profilePhotosCount: Int = 0,
    val userPhotos: List<String> = emptyList(),
    val hasFacebook: Boolean = false,
    val hasInstagram: Boolean = false,
    val hasYoutube: Boolean = false,
    val hasX: Boolean = false,
    val profileCompletion: Int = 0
)

enum class FeedContentType {
    ADMIN_GRAPHIC,
    FACEBOOK_POST,
    VIDEO
}

data class FeedItem(
    val id: String,
    val titleHi: String,
    val titleEn: String,
    val categoryHi: String,
    val categoryEn: String,
    val captionHi: String,
    val captionEn: String,
    val type: FeedContentType,
    val timestampHi: String,
    val timestampEn: String,
    val likesCount: String,
    val commentsCount: String,
    val sharesCount: String,
    val isFeatured: Boolean = false,
    val posterHeadlineHi: String = "",
    val posterSubHeadlineHi: String = "",
    val facebookAuthor: String = "Rahul Gandhi",
    val facebookTimeHi: String = "2 घंटे पहले",
    val facebookContentHi: String = "भारत का भविष्य उसके युवाओं के सपनों में है। न्याय और रोजगार हर युवा का संवैधानिक अधिकार है।",
    val facebookUrl: String = "https://www.facebook.com",
    val bannerThemeIndex: Int = 0,
    val isEnabled: Boolean = true,
    val photoCollageCount: Int = 1,
    val attachedPhotoName: String? = null,
    val imageUrl: String? = null
)

data class VideoFeedItem(
    val id: String,
    val titleHi: String,
    val titleEn: String,
    val captionHi: String,
    val captionEn: String,
    val duration: String,
    val timestampHi: String,
    val timestampEn: String,
    val likesCount: String,
    val commentsCount: String,
    val sharesCount: String,
    val themeColorIndex: Int = 0,
    val requiresJacket: Boolean = false,
    val aspectRatio: String = "9:16",
    val jacketTemplateName: String? = "हेमंत राठौर आधिकारिक जैकेट",
    val categories: List<String> = listOf("वीडियो"),
    val videoUri: String? = null,
    val videoThumbnailUri: String? = null
)

data class AdminFrameItem(
    val id: String,
    val title: String,
    val headerImageUri: String? = null,
    val footerImageUri: String? = null,
    val fullFrameUri: String? = null,
    val category: String = "सामान्य",
    val aspectRatio: String = "4:5", // "4:5", "1:1", "9:16"
    val templateStyle: String = "TEMPLATE_1", // "TEMPLATE_1", "NAVY_CURVED", "RED_RIBBON", "CUSTOM_PNG"
    val isDefault: Boolean = false,
    // Slot Coordinates & Styling (Normalized 0.0f - 1.0f relative to poster width & height)
    val photoCenterX: Float = 0.20f,
    val photoCenterY: Float = 0.86f,
    val photoRadius: Float = 0.135f,
    val nameX: Float = 0.38f,
    val nameY: Float = 0.89f,
    val nameFontFamily: String = "DEFAULT_BOLD", // "DEFAULT_BOLD", "SERIF", "SANS_SERIF", "MONOSPACE", "CURSIVE"
    val nameFontSizeSp: Float = 36f,
    val nameFontWeight: String = "BOLD", // "BOLD", "NORMAL", "MEDIUM", "BLACK"
    val textColorHex: String = "#FFFFFF",
    val desigX: Float = 0.38f,
    val desigY: Float = 0.94f,
    val desigFontFamily: String = "SANS_SERIF",
    val desigFontSizeSp: Float = 24f,
    val desigFontWeight: String = "MEDIUM",
    val desigColorHex: String = "#E2E8F0",
    val createdAt: Long = System.currentTimeMillis()
)

data class PosterIdentityConfig(
    val showPhoto: Boolean = true,
    val showName: Boolean = true,
    val showDesignation: Boolean = true,
    val showMobile: Boolean = false,
    val userName: String = "रितुराज तोमर",
    val userDesignation: String = "कार्यकर्ता",
    val userMobile: String = "+91 98765 43210",
    val templateIndex: Int = 0,
    val customHeadline: String = "विकास हर गाँव, हर घर हमारी प्रतिबद्धता",
    val customSubHeadline: String = "जनता का साथ, हर समय आपके साथ",
    val userPhotoUri: String? = null,
    val customMainPhotoUri: String? = null,
    val collageMode: Int = 1,
    val collagePhotos: List<String> = emptyList(),
    val jacketTemplateName: String = "हेमंत राठौर आधिकारिक जैकेट",
    val selectedFrameId: String? = null,
    val selectedFrame: AdminFrameItem? = null
)

enum class SuvicharCategory(val titleHi: String, val titleEn: String) {
    TODAY_THOUGHT("आज का सुविचार", "Today's Thought"),
    INSPIRATION("प्रेरणा", "Inspiration"),
    YOUTH("युवा", "Youth"),
    SEVA("सेवा", "Public Service"),
    VIKAS("विकास", "Development"),
    POSITIVE("सकारात्मक संदेश", "Positive Message")
}

data class SuvicharItem(
    val id: String,
    val category: SuvicharCategory,
    val quoteHi: String,
    val quoteEn: String,
    val authorHi: String = "टीम राठौर"
)

enum class RsvpStatus(val titleHi: String, val titleEn: String) {
    NONE("चुनें", "Select"),
    ATTENDING("शामिल होंगे", "Attending"),
    MAYBE("शायद", "Maybe"),
    NOT_ATTENDING("नहीं आ पाएँगे", "Can't Attend")
}

data class ProgramItem(
    val id: String,
    val titleHi: String,
    val titleEn: String,
    val dateHi: String,
    val dateEn: String,
    val timeHi: String,
    val timeEn: String,
    val venueHi: String,
    val venueEn: String,
    val instructionsHi: String,
    val rsvpStatus: RsvpStatus = RsvpStatus.NONE,
    val attendingCount: Int = 148,
    val maybeCount: Int = 24,
    val cantCount: Int = 6,
    val isHighlighted: Boolean = false,
    val notAttendingReason: String? = null
)

data class ImportantNotice(
    val id: String,
    val titleHi: String,
    val titleEn: String,
    val dateHi: String,
    val dateEn: String,
    val bodyHi: String,
    val isUrgent: Boolean = false,
    val viewsCount: Int = 840,
    val likesCount: Int = 320,
    val agreeCount: Int = 145,
    val photoAttached: Boolean = false,
    val photoTitle: String? = null,
    val photos: List<String> = emptyList()
)

data class VideoJacketTemplate(
    val id: String,
    val name: String,
    val headerText: String = "हेमंत राठौर • जनसेवा ही संकल्प",
    val footerText: String = "कार्यकर्ता नाम एवं पद यहाँ जुड़ेगा",
    val themeColorHex: Long = 0xFF9333EAL,
    val isCustomized: Boolean = false,
    val overlayGraphicFileName: String? = null,
    val overlayType: String = "header_footer"
)

data class ManagedUser(
    val id: String,
    val name: String,
    val phone: String,
    val area: String,
    val role: UserRole,
    val designation: String,
    val isBlocked: Boolean = false,
    val joinedDate: String = "10 सितं 2026",
    val photoUrl: String = "",
    val boothNo: String = "बूथ 42",
    val isVerified: Boolean = true,
    val googleSheetSynced: Boolean = true,
    val googleSheetRowIndex: Int = 1
)

data class GoogleSheetSyncStatus(
    val projectId: String = "breaking-news-wa-1741984378101",
    val spreadsheetId: String = "1Xy4Z-hemant-rathore-user-db-2026",
    val spreadsheetTitle: String = "हेमंत राठौर - विधानसभा जनसंपर्क एवं कार्यकर्ता डेटाबेस 2026",
    val spreadsheetUrl: String = "https://docs.google.com/spreadsheets/d/1Xy4Z-hemant-rathore-user-db-2026/edit",
    val webhookUrl: String = "",
    val isConnected: Boolean = true,
    val isSyncing: Boolean = false,
    val lastSyncTime: String = "आज, 10 सितंबर 2026 • 16:45",
    val supporterCount: Int = 840,
    val workerCount: Int = 320,
    val officeBearerCount: Int = 45,
    val adminCount: Int = 3,
    val totalSyncedCount: Int = 1208,
    val syncMessage: String = ""
)

data class PendingWorkerVerification(
    val id: String,
    val name: String,
    val phone: String,
    val area: String,
    val roleRequested: String,
    val appliedDate: String,
    val photoUrl: String = "",
    val boothNo: String = "बूथ नं. 42",
    val status: String = "PENDING"
)

data class WorkerSuggestion(
    val id: String,
    val workerName: String,
    val area: String,
    val category: String,
    val message: String,
    val date: String,
    val status: String = "प्राप्त",
    val subject: String = "",
    val photoUri: String? = null
)

data class FieldReport(
    val id: String,
    val authorName: String,
    val area: String,
    val subject: String,
    val message: String,
    val photoUri: String? = null,
    val videoUri: String? = null,
    val date: String,
    val status: String = "प्राप्त"
)

data class OfficialDocument(
    val id: String,
    val title: String,
    val letterNumber: String,
    val category: String,
    val date: String,
    val summary: String,
    val issuedBy: String = "केंद्रीय कार्यालय • टीम राठौर",
    val documentUrl: String = ""
)

data class TourSchedule(
    val id: String,
    val leaderNameHi: String,
    val dateHi: String,
    val locationHi: String,
    val detailsHi: String
)

enum class MeetingStatus {
    UPCOMING,
    PERMISSIONS_REQUIRED,
    WAITING_ROOM,
    LIVE,
    ENDED
}

data class MeetingInfo(
    val id: String,
    val titleHi: String,
    val titleEn: String,
    val dateHi: String,
    val timeHi: String,
    val hostHi: String,
    val agendaHi: String,
    val participantCount: Int = 42,
    val status: MeetingStatus = MeetingStatus.UPCOMING,
    val scheduledTimeMillis: Long = System.currentTimeMillis() + (14 * 60 + 50) * 1000L,
    val meetingUrl: String? = null,
    val isScheduled: Boolean = false
)

enum class TaskPriority(val titleHi: String, val titleEn: String) {
    HIGH("उच्च प्राथमिकता", "High Priority"),
    MEDIUM("मध्यम", "Medium"),
    LOW("सामान्य", "Normal")
}

enum class TaskStatus(val titleHi: String, val titleEn: String) {
    PENDING("स्वीकृति प्रतीक्षित", "Pending"),
    ACCEPTED("स्वीकृत", "Accepted"),
    IN_PROGRESS("प्रगति पर", "In Progress"),
    COMPLETED("पूर्ण", "Completed")
}

data class OfficeBearerTask(
    val id: String,
    val titleHi: String,
    val titleEn: String,
    val descriptionHi: String,
    val descriptionEn: String,
    val assignedBy: String,
    val dueDateHi: String,
    val priority: TaskPriority,
    val status: TaskStatus,
    val evidenceSubmitted: Boolean = false,
    val evidenceNote: String = "",
    val isOverdue: Boolean = false
)

enum class RequestStatus(val titleHi: String, val titleEn: String) {
    SUBMITTED("दर्ज", "Submitted"),
    IN_REVIEW("समीक्षाधीन", "In Review"),
    ACTION_TAKEN("कार्यवाही जारी", "Action Taken"),
    RESOLVED("निराकरण", "Resolved")
}

data class CitizenRequest(
    val id: String,
    val categoryHi: String,
    val categoryEn: String,
    val message: String,
    val dateHi: String,
    val status: RequestStatus,
    val adminResponseHi: String? = null,
    val attachmentName: String? = null,
    val citizenName: String = "विधानसभा नागरिक",
    val citizenPhone: String? = null,
    val location: String = "निवाड़ी / पृथ्वीपुर",
    val adminResolutionPhotoUri: String? = null,
    val resolutionDateHi: String? = null,
    val adminOfficerName: String = "विधायक जनसेवा कार्यालय"
)

data class NotificationItem(
    val id: String,
    val titleHi: String,
    val titleEn: String,
    val messageHi: String,
    val messageEn: String,
    val timeAgoHi: String,
    val isUnread: Boolean = true,
    val type: String = "general"
)

data class ReferralSummary(
    val code: String = "TR-8K4P2",
    val invitedCount: Int = 24,
    val registeredCount: Int = 18,
    val activeCount: Int = 15
) {
    val totalReferrals: Int get() = invitedCount
    val referralCode: String get() = code
}

data class UserActivitySummary(
    val graphicsReused: Int = 42,
    val videosReused: Int = 11,
    val suvicharCreated: Int = 19,
    val requestsSubmitted: Int = 3,
    val meetingsJoined: Int = 8,
    val tasksCompleted: Int = 5
)
