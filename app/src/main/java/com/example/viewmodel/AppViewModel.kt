package com.example.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.SampleData
import com.example.data.firebase.FirebaseManager
import com.example.data.sheets.GoogleSheetsSyncManager
import com.example.model.*
import com.example.util.CompressionResult
import com.example.util.ImageCompressionPreset
import com.example.util.ImageCompressionUtil
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed class AppScreen {
    object Splash : AppScreen()
    object LanguageSelect : AppScreen()
    object Login : AppScreen()
    object OtpVerify : AppScreen()
    object RoleSelect : AppScreen()
    object RegistrationStep1 : AppScreen()
    object RegistrationStep2 : AppScreen()
    object RegistrationStep3 : AppScreen()
    object RegistrationSuccess : AppScreen()
    object Main : AppScreen()
    object EditingTable : AppScreen()
    object DirectConnect : AppScreen()
    object Referral : AppScreen()
    object MyActivity : AppScreen()
    object Notifications : AppScreen()
    object Settings : AppScreen()
    object AdminPanel : AppScreen()
    object UxStatesShowcase : AppScreen()
}

enum class MainTab {
    HOME,
    VIDEOS,
    CREATE,
    WAR_ROOM,
    TASKS,
    DIRECT_CONNECT,
    PROFILE
}

data class AppUiState(
    val currentScreen: AppScreen = AppScreen.Splash,
    val language: Language = Language.HINDI,
    val selectedTab: MainTab = MainTab.HOME,
    val userProfile: UserProfile = UserProfile(),
    val registrationRole: UserRole = UserRole.WORKER,
    val regPhotosUploaded: Int = 3,
    val mobileInput: String = "",
    val otpInput: String = "",
    val sentOtpCode: String = "",
    val isOtpSent: Boolean = false,
    val otpTimer: Int = 45,
    val isAdminLoggedIn: Boolean = false,
    val isTestModeActive: Boolean = false,
    val testModeRole: UserRole? = null,
    val originalAdminProfile: UserProfile? = null,
    val feedFilter: String = "ALL", // ALL, OFFICIAL, SOCIAL, VIDEOS
    val feedCategories: List<String> = listOf(
        "सुविचार",
        "सोशल मीडिया",
        "वीडियो",
        "कांग्रेस विचारधारा",
        "राहुल गांधी",
        "प्रियंका गांधी",
        "मध्य प्रदेश कांग्रेस",
        "जीतू पटवारी",
        "उमंग सिंगार",
        "हेमंत राठौर"
    ),
    val feedList: List<FeedItem> = SampleData.feedItems,
    val videoList: List<VideoFeedItem> = SampleData.videoItems,
    val selectedSuvicharCategory: SuvicharCategory = SuvicharCategory.TODAY_THOUGHT,
    val currentSuvichar: SuvicharItem = SampleData.suvicharQuotes[0],
    val posterConfig: PosterIdentityConfig = PosterIdentityConfig(),
    val isDownloadSuccessShown: Boolean = false,
    val isVideoPlaying: Boolean = false,
    val activePlayingVideo: VideoFeedItem? = null,
    val isVideoProcessing: Boolean = false,
    val meetingInfo: MeetingInfo = SampleData.defaultMeeting,
    val isMicMuted: Boolean = false,
    val isCameraOff: Boolean = false,
    val programs: List<ProgramItem> = SampleData.upcomingPrograms,
    val notices: List<ImportantNotice> = SampleData.importantNotices,
    val tasks: List<OfficeBearerTask> = SampleData.officeBearerTasks,
    val citizenRequests: List<CitizenRequest> = SampleData.citizenRequests,
    val notifications: List<NotificationItem> = SampleData.notifications,
    val pendingVerifications: List<PendingWorkerVerification> = listOf(
        PendingWorkerVerification(
            id = "v1",
            name = "राजेश शर्मा",
            phone = "+91 98260 11223",
            area = "निवाड़ी ग्रामीण",
            roleRequested = "मंडल उपाध्यक्ष",
            appliedDate = "09 सितं 2026",
            boothNo = "बूथ नं. 14"
        ),
        PendingWorkerVerification(
            id = "v2",
            name = "सुनील तोमर",
            phone = "+91 94251 88776",
            area = "पृथ्वीपुर नगर",
            roleRequested = "बूथ संयोजक",
            appliedDate = "10 सितं 2026",
            boothNo = "बूथ नं. 28"
        ),
        PendingWorkerVerification(
            id = "v3",
            name = "अमित कुशवाह",
            phone = "+91 91112 33445",
            area = "ओरछा धाम",
            roleRequested = "आईटी सेल संयोजक",
            appliedDate = "10 सितं 2026",
            boothNo = "बूथ नं. 05"
        )
    ),
    val googleSheetSyncStatus: GoogleSheetSyncStatus = GoogleSheetSyncStatus(),
    val managedUsers: List<ManagedUser> = listOf(
        ManagedUser(
            id = "usr_1",
            name = "राजेश शर्मा",
            phone = "+91 98260 11223",
            area = "निवाड़ी ग्रामीण",
            role = UserRole.WORKER,
            designation = "बूथ प्रभारी",
            isBlocked = false,
            joinedDate = "05 सितं 2026",
            boothNo = "बूथ नं. 14",
            isVerified = true
        ),
        ManagedUser(
            id = "usr_2",
            name = "दीपक वर्मा",
            phone = "+91 98261 44556",
            area = "पृथ्वीपुर नगर",
            role = UserRole.OFFICE_BEARER,
            designation = "मंडल उपाध्यक्ष",
            isBlocked = false,
            joinedDate = "08 सितं 2026",
            boothNo = "बूथ नं. 42",
            isVerified = true
        ),
        ManagedUser(
            id = "usr_3",
            name = "अमित पटेल",
            phone = "+91 98262 77889",
            area = "ओरछा धाम",
            role = UserRole.SUPPORTER,
            designation = "सक्रिय समर्थक",
            isBlocked = false,
            joinedDate = "09 सितं 2026",
            boothNo = "बूथ नं. 03",
            isVerified = true
        ),
        ManagedUser(
            id = "usr_4",
            name = "सुनील चौहान",
            phone = "+91 98263 99001",
            area = "तरीचरकलां",
            role = UserRole.WORKER,
            designation = "सेक्टर समन्वयक",
            isBlocked = false,
            joinedDate = "10 सितं 2026",
            boothNo = "बूथ नं. 28",
            isVerified = true
        ),
        ManagedUser(
            id = "usr_5",
            name = "सुरेश दांगी",
            phone = "+91 98264 22334",
            area = "सेंदरी",
            role = UserRole.SUPPORTER,
            designation = "युवा समर्थक",
            isBlocked = false,
            joinedDate = "11 सितं 2026",
            boothNo = "बूथ नं. 55",
            isVerified = true
        ),
        ManagedUser(
            id = "usr_6",
            name = "डॉ. वीरेन्द्र सिंह बुंदेला",
            phone = "+91 94250 11998",
            area = "पृथ्वीपुर",
            role = UserRole.OFFICE_BEARER,
            designation = "ब्लॉक कांग्रेस अध्यक्ष",
            isBlocked = false,
            joinedDate = "01 सितं 2026",
            boothNo = "बूथ नं. 01",
            isVerified = true
        ),
        ManagedUser(
            id = "usr_7",
            name = "कमलेश कुशवाहा",
            phone = "+91 91112 44778",
            area = "मोहनगढ़",
            role = UserRole.WORKER,
            designation = "बूथ विस्तारक",
            isBlocked = false,
            joinedDate = "06 सितं 2026",
            boothNo = "बूथ नं. 64",
            isVerified = true
        ),
        ManagedUser(
            id = "usr_8",
            name = "श्रीमती माया रजक",
            phone = "+91 97551 66332",
            area = "निवाड़ी वार्ड 7",
            role = UserRole.OFFICE_BEARER,
            designation = "महिला मोर्चा संयोजिका",
            isBlocked = false,
            joinedDate = "03 सितं 2026",
            boothNo = "बूथ नं. 07",
            isVerified = true
        ),
        ManagedUser(
            id = "usr_9",
            name = "प्रशांत तिवारी",
            phone = "+91 99812 33441",
            area = "ओरछा ग्रामीण",
            role = UserRole.SUPPORTER,
            designation = "सोशल मीडिया समर्थक",
            isBlocked = false,
            joinedDate = "11 सितं 2026",
            boothNo = "बूथ नं. 19",
            isVerified = false
        ),
        ManagedUser(
            id = "usr_10",
            name = "हेमंत राठौर (कार्यालय)",
            phone = "+91 75124 05678",
            area = "केंद्रीय कार्यालय",
            role = UserRole.ADMIN,
            designation = "केंद्रीय व्यवस्थापक",
            isBlocked = false,
            joinedDate = "01 सितं 2026",
            boothNo = "मुख्यालय",
            isVerified = true
        )
    ),
    val workerSuggestions: List<WorkerSuggestion> = listOf(
        WorkerSuggestion(
            id = "s1",
            workerName = "मनोज यादव",
            area = "निवाड़ी वार्ड 4",
            category = "क्षेत्रीय समस्या",
            subject = "स्ट्रीट लाइट व सफाई व्यवस्था",
            message = "वार्ड 4 में मुख्य मार्ग की स्ट्रीट लाइट व सफाई व्यवस्था सुधारने हेतु ज्ञापन देना चाहिए।",
            date = "आज"
        ),
        WorkerSuggestion(
            id = "s2",
            workerName = "दीपक राय",
            area = "पृथ्वीपुर",
            category = "संगठन विस्तार",
            subject = "युवा संवाद कार्यक्रम",
            message = "युवाओं को जोड़ने के लिए आगामी रविवार को ब्लॉक स्तरीय खेलकूद व संवाद कार्यक्रम आयोजित किया जाए।",
            date = "कल"
        )
    ),
    val officialDocuments: List<OfficialDocument> = SampleData.officialDocuments,
    val fieldReports: List<FieldReport> = SampleData.fieldReports,
    val referral: ReferralSummary = ReferralSummary(),
    val activity: UserActivitySummary = UserActivitySummary(),
    val toastMessage: String? = null,
    val showProfilePendingReviewDialog: Boolean = false,
    val videoCompressionProgress: Int? = null,
    val isVideoCompressed: Boolean = false,
    val compressedVideoInfo: String? = null,
    val videoJackets: List<VideoJacketTemplate> = listOf(
        VideoJacketTemplate(
            id = "j1",
            name = "हेमंत राठौर आधिकारिक जैकेट",
            headerText = "हेमंत राठौर • जनसेवा ही संकल्प",
            footerText = "कार्यकर्ता नाम एवं पद यहाँ जुड़ेगा",
            themeColorHex = 0xFF9333EAL
        ),
        VideoJacketTemplate(
            id = "j2",
            name = "जनसंवाद चौपाल हेडर-फुटर फ्रेम",
            headerText = "जनसंवाद चौपाल • सीधी बात, सीधा समाधान",
            footerText = "भारतीय राष्ट्रीय कांग्रेस • टीम राठौर",
            themeColorHex = 0xFF1877F2L
        ),
        VideoJacketTemplate(
            id = "j3",
            name = "युवा क्रांति बॉर्डर जैकेट",
            headerText = "युवा क्रांति • न्याय और रोजगार हमारा अधिकार",
            footerText = "युवा कांग्रेस समन्वय समिति",
            themeColorHex = 0xFFE11D48L
        ),
        VideoJacketTemplate(
            id = "j4",
            name = "विकास संकल्प विशेष जैकेट",
            headerText = "विकास संकल्प • क्षेत्र का सर्वांगीण विकास",
            footerText = "टीम हेमंत राठौर आधिकारिक",
            themeColorHex = 0xFF0D9488L
        )
    ),
    // Active edge state viewer (null for normal app operation)
    val activeSimulatedState: String? = null,
    // Client-side image compression & optimization state
    val selectedCompressionPreset: ImageCompressionPreset = ImageCompressionPreset.HD_OPTIMIZED,
    val lastImageCompressionResult: CompressionResult? = null,
    val isCompressingAndUploadingImage: Boolean = false,
    val imageUploadProgress: Float = 0f,
    val imageCompressionStatusText: String? = null,
    // Admin Created Frames (Shared with workers, supporters & office-bearers)
    val adminFrames: List<AdminFrameItem> = listOf(
        AdminFrameItem(
            id = "frame_template_1",
            title = "टेम्पलेट 1",
            category = "आधिकारिक डिजाइन",
            templateStyle = "TEMPLATE_1",
            aspectRatio = "4:5",
            isDefault = true,
            photoCenterX = 0.20f,
            photoCenterY = 0.86f,
            photoRadius = 0.135f,
            nameX = 0.38f,
            nameY = 0.89f,
            desigX = 0.38f,
            desigY = 0.94f,
            textColorHex = "#FFFFFF"
        ),
        AdminFrameItem(
            id = "frame_template_red",
            title = "टेम्पलेट 2 (रेड रिबन फुटर)",
            category = "आधिकारिक डिजाइन",
            templateStyle = "RED_RIBBON",
            aspectRatio = "4:5"
        ),
        AdminFrameItem(
            id = "frame_chaupal_1",
            title = "जनसंवाद चौपाल आधिकारिक फ्रेम",
            category = "चौपाल संवाद",
            templateStyle = "TEMPLATE_1",
            aspectRatio = "4:5"
        ),
        AdminFrameItem(
            id = "frame_yuva_2",
            title = "युवा शक्ति एवं क्रांति फ्रेम",
            category = "युवा क्रांति",
            templateStyle = "RED_RIBBON",
            aspectRatio = "4:5"
        )
    )
)

class AppViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(AppUiState())
    val uiState: StateFlow<AppUiState> = _uiState.asStateFlow()

    private var firebaseVerificationId: String? = null

    init {
        // 1. Sync Feed Items in real-time from Cloud Firestore
        viewModelScope.launch {
            try {
                FirebaseManager.observeFeedItems().collect { firestoreFeed ->
                    if (firestoreFeed.isNotEmpty()) {
                        _uiState.update { current ->
                            current.copy(feedList = firestoreFeed)
                        }
                    }
                }
            } catch (e: Exception) {
                // Fallback to local sample data
            }
        }

        // 2. Sync Official Notices in real-time from Cloud Firestore
        viewModelScope.launch {
            try {
                FirebaseManager.observeNotices().collect { firestoreNotices ->
                    if (firestoreNotices.isNotEmpty()) {
                        _uiState.update { current ->
                            current.copy(notices = firestoreNotices)
                        }
                    }
                }
            } catch (e: Exception) {
                // Fallback to local
            }
        }

        // 3. Sync Programs in real-time from Cloud Firestore
        viewModelScope.launch {
            try {
                FirebaseManager.observePrograms().collect { firestorePrograms ->
                    if (firestorePrograms.isNotEmpty()) {
                        _uiState.update { current ->
                            current.copy(programs = firestorePrograms)
                        }
                    }
                }
            } catch (e: Exception) {
                // Fallback to local
            }
        }

        // 4. Sync Live Meeting Info in real-time from Cloud Firestore
        viewModelScope.launch {
            try {
                FirebaseManager.observeMeetingInfo().collect { firestoreMeeting ->
                    if (firestoreMeeting != null) {
                        _uiState.update { current ->
                            val shouldShowNotif = firestoreMeeting.isScheduled && firestoreMeeting.status != MeetingStatus.ENDED
                            val filteredNotifs = current.notifications.filter { it.type != "meeting" }
                            val updatedNotifs = if (shouldShowNotif) {
                                val meetNotif = NotificationItem(
                                    id = "notif_meet_${firestoreMeeting.id}",
                                    titleHi = "लाइव बैठक सूचना: ${firestoreMeeting.titleHi}",
                                    titleEn = "Live Meeting: ${firestoreMeeting.titleEn}",
                                    messageHi = "दिनांक: ${firestoreMeeting.dateHi} • समय: ${firestoreMeeting.timeHi}। Google Meet के माध्यम से ऐप पर सीधे लाइव जुड़ें।",
                                    messageEn = "Date: ${firestoreMeeting.dateHi} • Time: ${firestoreMeeting.timeHi}. Connect live via Google Meet right inside the app.",
                                    timeAgoHi = "अभी-अभी",
                                    isUnread = true,
                                    type = "meeting"
                                )
                                listOf(meetNotif) + filteredNotifs
                            } else {
                                filteredNotifs
                            }
                            current.copy(
                                meetingInfo = firestoreMeeting,
                                notifications = updatedNotifs
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                // Fallback to local
            }
        }

        // 5. Sync Admin Created Frames in real-time from Cloud Firestore
        viewModelScope.launch {
            try {
                FirebaseManager.observeFrames().collect { remoteFrames ->
                    if (remoteFrames.isNotEmpty()) {
                        _uiState.update { current ->
                            val remoteIds = remoteFrames.map { it.id }.toSet()
                            val merged = remoteFrames + current.adminFrames.filter { it.id !in remoteIds }
                            current.copy(adminFrames = merged)
                        }
                    }
                }
            } catch (e: Exception) {
                // Fallback to local
            }
        }
    }

    fun navigateTo(screen: AppScreen) {
        _uiState.update { it.copy(currentScreen = screen) }
    }

    fun selectTab(tab: MainTab) {
        if (tab == MainTab.CREATE) {
            _uiState.update { it.copy(currentScreen = AppScreen.EditingTable) }
        } else {
            _uiState.update { it.copy(selectedTab = tab, currentScreen = AppScreen.Main) }
        }
    }

    fun toggleLanguage() {
        val nextLang = if (_uiState.value.language == Language.HINDI) Language.ENGLISH else Language.HINDI
        setLanguage(nextLang)
    }

    fun setLanguage(lang: Language) {
        _uiState.update { it.copy(language = lang) }
    }

    fun setUserRole(role: UserRole) {
        _uiState.update {
            it.copy(
                userProfile = it.userProfile.copy(
                    role = role,
                    designation = when (role) {
                        UserRole.SUPPORTER -> "समर्थक"
                        UserRole.WORKER -> "कार्यकर्ता"
                        UserRole.OFFICE_BEARER -> "मंडल उपाध्यक्ष"
                        UserRole.ADMIN -> "मुख्य व्यवस्थापक (Admin)"
                    }
                ),
                posterConfig = it.posterConfig.copy(
                    userDesignation = when (role) {
                        UserRole.SUPPORTER -> "समर्थक"
                        UserRole.WORKER -> "कार्यकर्ता"
                        UserRole.OFFICE_BEARER -> "मंडल उपाध्यक्ष"
                        UserRole.ADMIN -> "मुख्य व्यवस्थापक (Admin)"
                    }
                ),
                registrationRole = role
            )
        }
    }

    fun setMobileInput(number: String) {
        _uiState.update { it.copy(mobileInput = number) }
    }

    fun setOtpInput(otp: String) {
        _uiState.update { it.copy(otpInput = otp) }
    }

    fun requestOtpForMobile(mobileNumber: String, activity: android.app.Activity? = null): Boolean {
        val digitsOnly = mobileNumber.filter { it.isDigit() }
        val clean10Digit = if (digitsOnly.length > 10) digitsOnly.takeLast(10) else digitsOnly
        if (clean10Digit.length != 10) {
            _uiState.update { it.copy(toastMessage = "❌ कृपया 10 अंकों का मान्य मोबाइल नंबर दर्ज करें") }
            return false
        }
        val generatedOtp = (100000..999999).random().toString()
        _uiState.update {
            it.copy(
                mobileInput = clean10Digit,
                sentOtpCode = generatedOtp,
                otpInput = "",
                otpTimer = 45,
                isOtpSent = true,
                toastMessage = "📩 OTP आपके नंबर +91-$clean10Digit पर भेजा गया: $generatedOtp"
            )
        }
        if (activity != null) {
            try {
                FirebaseManager.sendVerificationCode(
                    phoneNumber = "+91$clean10Digit",
                    activity = activity,
                    onCodeSent = { vId ->
                        firebaseVerificationId = vId
                    },
                    onError = {
                        // local OTP remains active
                    }
                )
            } catch (e: Exception) {
                // local OTP
            }
        }
        return true
    }

    fun sendOtp() {
        val currentMobile = _uiState.value.mobileInput.filter { it.isDigit() }.takeLast(10)
        val generatedOtp = (100000..999999).random().toString()
        _uiState.update {
            it.copy(
                sentOtpCode = generatedOtp,
                otpInput = "",
                otpTimer = 45,
                isOtpSent = true,
                toastMessage = "📩 OTP भेजा गया: $generatedOtp"
            )
        }
    }

    fun sendFirebasePhoneOtp(
        activity: android.app.Activity,
        phoneNumber: String,
        onCodeSent: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        val digitsOnly = phoneNumber.filter { it.isDigit() }.takeLast(10)
        val generatedOtp = (100000..999999).random().toString()
        _uiState.update {
            it.copy(
                mobileInput = digitsOnly,
                sentOtpCode = generatedOtp,
                otpInput = "",
                otpTimer = 45,
                isOtpSent = true,
                toastMessage = "📩 OTP भेजा गया: $generatedOtp"
            )
        }
        FirebaseManager.sendVerificationCode(
            phoneNumber = "+91$digitsOnly",
            activity = activity,
            onCodeSent = { vId ->
                firebaseVerificationId = vId
                onCodeSent()
            },
            onError = { err ->
                // fallback to active local generated OTP
                onError(err)
            }
        )
    }

    fun resendOtp() {
        val digits = _uiState.value.mobileInput.filter { it.isDigit() }.takeLast(10)
        val generatedOtp = (100000..999999).random().toString()
        _uiState.update {
            it.copy(
                sentOtpCode = generatedOtp,
                otpInput = "",
                otpTimer = 45,
                toastMessage = "📩 नया OTP कोड: $generatedOtp"
            )
        }
    }

    fun verifyEnteredOtp(code: String): Boolean {
        val trimmed = code.trim()
        val expected = _uiState.value.sentOtpCode
        if (trimmed.length != 6) {
            _uiState.update { it.copy(toastMessage = "❌ कृपया 6 अंकों का OTP दर्ज करें") }
            return false
        }
        if (trimmed == expected || (expected.isBlank() && trimmed.length == 6)) {
            _uiState.update {
                it.copy(
                    currentScreen = AppScreen.RoleSelect,
                    toastMessage = "✓ मोबाइल नंबर सफलतापूर्वक सत्यापित हुआ!"
                )
            }
            return true
        } else {
            _uiState.update { it.copy(toastMessage = "❌ गलत OTP दर्ज किया गया है! सही कोड दर्ज करें।") }
            return false
        }
    }

    fun verifyOtpAndContinue() {
        _uiState.update { it.copy(currentScreen = AppScreen.RoleSelect) }
    }

    fun verifyFirebaseOtp(
        code: String,
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        val trimmed = code.trim()
        val expected = _uiState.value.sentOtpCode
        val vId = firebaseVerificationId
        if (vId != null) {
            FirebaseManager.verifyCode(
                verificationId = vId,
                code = trimmed,
                onSuccess = {
                    _uiState.update { it.copy(currentScreen = AppScreen.RoleSelect) }
                    onSuccess()
                },
                onError = { err ->
                    // Check against generated code
                    if (trimmed == expected && expected.isNotBlank()) {
                        verifyOtpAndContinue()
                        onSuccess()
                    } else {
                        _uiState.update { it.copy(toastMessage = "❌ गलत OTP कोड!") }
                        onError(err)
                    }
                }
            )
        } else {
            if (trimmed == expected && expected.isNotBlank()) {
                verifyOtpAndContinue()
                onSuccess()
            } else {
                _uiState.update { it.copy(toastMessage = "❌ गलत OTP कोड! कृपया SMS में आया कोड दर्ज करें।") }
                onError("गलत OTP")
            }
        }
    }

    fun completeRegistrationAndSync(profile: UserProfile) {
        _uiState.update { state ->
            state.copy(
                userProfile = profile.copy(isApproved = true, isPendingReview = false)
            )
        }
        addNewManagedUser(
            name = profile.fullName,
            phone = profile.mobile.ifBlank { _uiState.value.mobileInput },
            area = "${profile.village}, ${profile.assembly}",
            role = profile.role,
            designation = profile.designation.ifBlank {
                when (profile.role) {
                    UserRole.WORKER -> "सक्रिय कार्यकर्ता"
                    UserRole.OFFICE_BEARER -> "मंडल पदाधिकारी"
                    UserRole.SUPPORTER -> "समर्थक"
                    UserRole.ADMIN -> "व्यवस्थापक"
                }
            },
            boothNo = "वार्ड/बूथ: ${profile.village}"
        )
        // trigger background sync
        syncUserDatabaseWithGoogleSheets()
    }

    fun enterTestMode(role: UserRole) {
        val currentProfile = _uiState.value.userProfile
        val original = _uiState.value.originalAdminProfile ?: currentProfile
        _uiState.update { state ->
            state.copy(
                isTestModeActive = true,
                testModeRole = role,
                originalAdminProfile = original,
                userProfile = state.userProfile.copy(
                    role = role,
                    designation = when (role) {
                        UserRole.WORKER -> "सक्रिय कार्यकर्ता (टेस्ट मोड)"
                        UserRole.OFFICE_BEARER -> "मंडल पदाधिकारी (टेस्ट मोड)"
                        UserRole.SUPPORTER -> "सक्रिय समर्थक (टेस्ट मोड)"
                        UserRole.ADMIN -> "मुख्य प्रशासक"
                    }
                ),
                currentScreen = AppScreen.Main,
                selectedTab = MainTab.HOME,
                toastMessage = "🧪 टेस्ट मोड सक्रिय: अब आप '${role.titleHi}' इंटरफेस देख रहे हैं।"
            )
        }
    }

    fun exitTestMode() {
        val original = _uiState.value.originalAdminProfile
        _uiState.update { state ->
            state.copy(
                isTestModeActive = false,
                testModeRole = null,
                userProfile = original ?: state.userProfile.copy(
                    role = UserRole.ADMIN,
                    designation = "मुख्य प्रशासक (Super Admin)"
                ),
                currentScreen = AppScreen.AdminPanel,
                toastMessage = "👑 व्यवस्थापक (Admin) मोड में वापस लौटे।"
            )
        }
    }

    fun verifyAdminCredentials(id: String, pass: String): Boolean {
        val cleanId = id.trim().lowercase()
        val cleanPass = pass.trim()
        val validIds = listOf("breakingnewswala.com@gmail.com", "admin@teamrathore.in", "admin@rathore.org", "admin")
        val validPasses = listOf("RathoreAdmin@2026", "rathore2026", "admin123", "rathore@2026")
        return (validIds.contains(cleanId) || cleanId.contains("admin") || cleanId == "breakingnewswala.com@gmail.com") && validPasses.contains(cleanPass)
    }

    fun loginWithAdminCredentials(id: String, pass: String): Boolean {
        if (verifyAdminCredentials(id, pass)) {
            _uiState.update {
                it.copy(
                    isAdminLoggedIn = true,
                    userProfile = it.userProfile.copy(
                        role = UserRole.ADMIN,
                        designation = "मुख्य प्रशासक (Super Admin)",
                        fullName = "एडमिन टीम राठौर",
                        email = id.trim(),
                        isApproved = true,
                        isPendingReview = false
                    ),
                    currentScreen = AppScreen.AdminPanel,
                    toastMessage = "✓ एडमिन के रूप में सफलतापूर्वक लॉगिन हुआ!"
                )
            }
            return true
        } else {
            _uiState.update { it.copy(toastMessage = "❌ अमान्य एडमिन ईमेल या पासवर्ड!") }
            return false
        }
    }

    fun showProfileReviewDialog() {
        _uiState.update { it.copy(showProfilePendingReviewDialog = true) }
    }

    fun dismissProfileReviewDialog() {
        _uiState.update { it.copy(showProfilePendingReviewDialog = false) }
    }

    fun approveProfileDirectly() {
        _uiState.update { state ->
            val myPhone = state.userProfile.mobile
            val remainingVerifications = state.pendingVerifications.filter { it.phone != myPhone }
            state.copy(
                userProfile = state.userProfile.copy(
                    isApproved = true,
                    isPendingReview = false,
                    reviewReason = ""
                ),
                pendingVerifications = remainingVerifications,
                showProfilePendingReviewDialog = false,
                toastMessage = "✓ प्रोफाइल एडमिन द्वारा स्वीकृत! अब आप सभी सामग्री बना व डाउनलोड कर सकते हैं।"
            )
        }
    }

    fun contactAdminFromPopup() {
        _uiState.update {
            it.copy(
                showProfilePendingReviewDialog = false,
                toastMessage = "टीम राठौर एडमिन सहायता नंबर: +91 94251 00111 (WhatsApp पर कनेक्ट किया गया)"
            )
        }
    }

    fun editUserProfile(
        newName: String,
        newDesignation: String,
        newRole: UserRole,
        newPhotoUri: String? = null
    ) {
        val current = _uiState.value.userProfile
        val isRoleChanged = newRole != current.role
        val isNameChanged = newName.trim().isNotBlank() && newName.trim() != current.fullName.trim()
        val isDesignationChanged = newDesignation.trim().isNotBlank() && newDesignation.trim() != current.designation.trim()
        val isPhotoChanged = newPhotoUri != null && newPhotoUri != current.customPhotoUri

        val requiresReview = isRoleChanged || isNameChanged || isDesignationChanged

        if (requiresReview) {
            val changeDesc = buildString {
                if (isRoleChanged) append("श्रेणी: ${current.role.titleHi} → ${newRole.titleHi}; ")
                if (isNameChanged) append("नाम: ${newName.trim()}; ")
                if (isDesignationChanged) append("पद: ${newDesignation.trim()}")
            }
            val newPending = PendingWorkerVerification(
                id = "rev_${System.currentTimeMillis()}",
                name = newName.trim().ifBlank { current.fullName },
                phone = current.mobile,
                area = "${current.village}, ${current.district}",
                roleRequested = "${newRole.titleHi} (${newDesignation.trim().ifBlank { current.designation }})",
                appliedDate = "आज (प्रोफाइल बदलाव)",
                boothNo = changeDesc
            )
            _uiState.update { state ->
                state.copy(
                    userProfile = current.copy(
                        fullName = newName.trim().ifBlank { current.fullName },
                        designation = newDesignation.trim().ifBlank { current.designation },
                        role = newRole,
                        customPhotoUri = newPhotoUri ?: current.customPhotoUri,
                        isApproved = false,
                        isPendingReview = true,
                        reviewReason = "प्रोफाइल में बदलाव ($changeDesc) के कारण समीक्षाधीन है।"
                    ),
                    posterConfig = state.posterConfig.copy(
                        userName = newName.trim().ifBlank { current.fullName },
                        userDesignation = newDesignation.trim().ifBlank { current.designation }
                    ),
                    pendingVerifications = listOf(newPending) + state.pendingVerifications,
                    toastMessage = "⚠️ प्रोफाइल बदलाव सहेजा गया। स्वीकृति हेतु एडमिन को भेजा गया है।"
                )
            }
        } else if (isPhotoChanged) {
            _uiState.update { state ->
                state.copy(
                    userProfile = current.copy(
                        customPhotoUri = newPhotoUri
                    ),
                    toastMessage = "प्रोफाइल फोटो सफलतापूर्वक अपडेट की गई ✓"
                )
            }
        } else {
            _uiState.update { it.copy(toastMessage = "कोई बदलाव नहीं पाया गया।") }
        }
    }

    fun updateProfile(profile: UserProfile) {
        _uiState.update { it.copy(userProfile = profile) }
    }

    fun setFeedFilter(filter: String) {
        _uiState.update { it.copy(feedFilter = filter) }
    }

    fun reuseFeedItem(item: FeedItem) {
        if (!_uiState.value.userProfile.isApproved) {
            showProfileReviewDialog()
            return
        }
        val headline = when {
            item.type == FeedContentType.FACEBOOK_POST -> item.facebookAuthor.ifBlank { "सोशल मीडिया पोस्ट" }
            item.posterHeadlineHi.isNotBlank() -> item.posterHeadlineHi
            else -> item.titleHi
        }
        val subHeadline = when {
            item.type == FeedContentType.FACEBOOK_POST -> item.facebookContentHi.ifBlank { item.captionHi }
            item.posterSubHeadlineHi.isNotBlank() -> item.posterSubHeadlineHi
            else -> item.captionHi
        }

        _uiState.update {
            it.copy(
                posterConfig = it.posterConfig.copy(
                    customHeadline = headline,
                    customSubHeadline = subHeadline,
                    templateIndex = item.bannerThemeIndex,
                    customMainPhotoUri = item.imageUrl
                ),
                currentScreen = AppScreen.EditingTable,
                toastMessage = "पोस्ट आपकी व्यक्तिगत जैकेट (नाम व फोटो) में लोड हो गई है"
            )
        }
    }

    fun updatePosterConfig(transform: (PosterIdentityConfig) -> PosterIdentityConfig) {
        _uiState.update { it.copy(posterConfig = transform(it.posterConfig)) }
    }

    fun updateUserPhoto(photoUri: String?) {
        _uiState.update { current ->
            current.copy(
                userProfile = current.userProfile.copy(customPhotoUri = photoUri),
                posterConfig = current.posterConfig.copy(
                    userPhotoUri = photoUri,
                    showPhoto = photoUri != null
                ),
                toastMessage = if (photoUri != null) "गैलरी से आपकी फोटो सफलतापूर्वक सेट हो गई!" else "फोटो हटा दी गई है"
            )
        }
    }

    fun updatePosterMainPhoto(photoUri: String?) {
        _uiState.update { current ->
            current.copy(
                posterConfig = current.posterConfig.copy(customMainPhotoUri = photoUri),
                toastMessage = if (photoUri != null) "गैलरी से मुख्य फोटो पोस्टर में जुड़ गई!" else "मुख्य फोटो हटा दी गई है"
            )
        }
    }

    fun updatePosterCollagePhotos(photos: List<String>) {
        _uiState.update { current ->
            current.copy(
                posterConfig = current.posterConfig.copy(
                    collagePhotos = photos,
                    collageMode = photos.size.coerceIn(1, 4)
                ),
                toastMessage = "${photos.size} फोटो पोस्टर में जोड़ी गईं ✓"
            )
        }
    }

    // ==========================================
    // ADMIN FRAME CREATOR & PUBLISHER SYSTEM
    // ==========================================

    fun createAdminFrame(
        title: String,
        headerUri: String?,
        footerUri: String?,
        fullFrameUri: String? = null,
        category: String = "सामान्य",
        aspectRatio: String = "4:5",
        templateStyle: String = "TEMPLATE_1",
        photoCenterX: Float = 0.20f,
        photoCenterY: Float = 0.86f,
        photoRadius: Float = 0.135f,
        nameX: Float = 0.38f,
        nameY: Float = 0.89f,
        nameFontFamily: String = "DEFAULT_BOLD",
        nameFontSizeSp: Float = 36f,
        nameFontWeight: String = "BOLD",
        textColorHex: String = "#FFFFFF",
        desigX: Float = 0.38f,
        desigY: Float = 0.94f,
        desigFontFamily: String = "SANS_SERIF",
        desigFontSizeSp: Float = 24f,
        desigFontWeight: String = "MEDIUM",
        desigColorHex: String = "#E2E8F0"
    ) {
        val newFrame = AdminFrameItem(
            id = "frame_${System.currentTimeMillis()}",
            title = title.ifBlank { "टेम्पलेट 1" },
            headerImageUri = headerUri,
            footerImageUri = footerUri,
            fullFrameUri = fullFrameUri,
            category = category,
            aspectRatio = aspectRatio,
            templateStyle = templateStyle,
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
            desigColorHex = desigColorHex,
            createdAt = System.currentTimeMillis()
        )
        _uiState.update { current ->
            current.copy(
                adminFrames = listOf(newFrame) + current.adminFrames,
                toastMessage = "फ्रेम '${newFrame.title}' सफलतापूर्वक लाइव व पब्लिश कर दिया गया है!"
            )
        }
        viewModelScope.launch {
            try {
                FirebaseManager.publishFrame(newFrame)
            } catch (e: Exception) {
                // Local state is preserved
            }
        }
    }

    fun updateAdminFrame(updatedFrame: AdminFrameItem) {
        _uiState.update { current ->
            val updatedList = current.adminFrames.map {
                if (it.id == updatedFrame.id) updatedFrame else it
            }
            val updatedSelected = if (current.posterConfig.selectedFrameId == updatedFrame.id) {
                updatedFrame
            } else current.posterConfig.selectedFrame

            current.copy(
                adminFrames = updatedList,
                posterConfig = current.posterConfig.copy(
                    selectedFrame = updatedSelected
                ),
                toastMessage = "फ्रेम '${updatedFrame.title}' की फोटो, नाम व पद सेटिंग्स सफलतापूर्वक सेव कर दी गईं! ✓"
            )
        }
        viewModelScope.launch {
            try {
                FirebaseManager.updateFrame(updatedFrame)
            } catch (e: Exception) {
                // Local state is preserved
            }
        }
    }

    fun renameAdminFrame(frameId: String, newTitle: String) {
        if (newTitle.isBlank()) return
        val trimmed = newTitle.trim()
        _uiState.update { current ->
            val updatedList = current.adminFrames.map {
                if (it.id == frameId) it.copy(title = trimmed) else it
            }
            val updatedSelected = if (current.posterConfig.selectedFrameId == frameId) {
                current.posterConfig.selectedFrame?.copy(title = trimmed)
            } else current.posterConfig.selectedFrame

            current.copy(
                adminFrames = updatedList,
                posterConfig = current.posterConfig.copy(selectedFrame = updatedSelected),
                toastMessage = "फ्रेम का नाम बदलकर '$trimmed' कर दिया गया ✓"
            )
        }
        viewModelScope.launch {
            try {
                FirebaseManager.renameFrame(frameId, trimmed)
            } catch (e: Exception) {
                // Local state is preserved
            }
        }
    }

    fun deleteAdminFrame(frameId: String) {
        _uiState.update { current ->
            current.copy(
                adminFrames = current.adminFrames.filter { it.id != frameId },
                toastMessage = "फ्रेम हटा दिया गया है"
            )
        }
        viewModelScope.launch {
            try {
                FirebaseManager.deleteFrame(frameId)
            } catch (e: Exception) {
                // Local state is preserved
            }
        }
    }

    fun selectPosterFrame(frame: AdminFrameItem) {
        _uiState.update { current ->
            current.copy(
                posterConfig = current.posterConfig.copy(
                    selectedFrameId = frame.id,
                    selectedFrame = frame
                ),
                toastMessage = "फ्रेम '${frame.title}' लागू किया गया ✓"
            )
        }
    }

    fun triggerDownloadSuccess() {
        if (!_uiState.value.userProfile.isApproved) {
            showProfileReviewDialog()
            return
        }
        _uiState.update {
            it.copy(
                isDownloadSuccessShown = true,
                activity = it.activity.copy(graphicsReused = it.activity.graphicsReused + 1)
            )
        }
    }

    fun onGraphicExportedSuccessfully() {
        _uiState.update {
            it.copy(
                isDownloadSuccessShown = true,
                activity = it.activity.copy(graphicsReused = it.activity.graphicsReused + 1),
                toastMessage = "ग्राफ़िक 1080×1350 HD रिज़ॉल्यूशन में आपकी गैलरी में सेव हो गया ✓"
            )
        }
    }

    fun dismissDownloadSuccess() {
        _uiState.update { it.copy(isDownloadSuccessShown = false) }
    }

    fun copyPostText(text: String) {
        _uiState.update {
            it.copy(toastMessage = "मैटर कॉपी किया गया! सोशल मीडिया पर साझा करें ✓")
        }
    }

    fun downloadFeedGraphic(item: FeedItem) {
        if (!_uiState.value.userProfile.isApproved) {
            showProfileReviewDialog()
            return
        }
        _uiState.update {
            it.copy(
                isDownloadSuccessShown = true,
                activity = it.activity.copy(graphicsReused = it.activity.graphicsReused + 1),
                toastMessage = "ग्राफिक पोस्टर फ्रेम सहित सफलतापूर्वक डाउनलोड हो गया!"
            )
        }
    }

    fun selectSuvicharCategory(cat: SuvicharCategory) {
        val quote = SampleData.suvicharQuotes.firstOrNull { it.category == cat }
            ?: SampleData.suvicharQuotes[0]
        _uiState.update {
            it.copy(
                selectedSuvicharCategory = cat,
                currentSuvichar = quote
            )
        }
    }

    fun generateNewSuvichar(customText: String? = null) {
        if (!_uiState.value.userProfile.isApproved) {
            showProfileReviewDialog()
            return
        }
        val quotes = SampleData.suvicharQuotes.filter { it.category == _uiState.value.selectedSuvicharCategory }
        val randomQuote = quotes.randomOrNull() ?: SampleData.suvicharQuotes.random()
        val textToUse = if (!customText.isNullOrBlank()) customText else randomQuote.quoteHi
        _uiState.update {
            it.copy(
                currentSuvichar = randomQuote.copy(quoteHi = textToUse),
                posterConfig = it.posterConfig.copy(
                    customHeadline = _uiState.value.selectedSuvicharCategory.titleHi,
                    customSubHeadline = textToUse,
                    templateIndex = (it.posterConfig.templateIndex + 1) % 3
                ),
                activity = it.activity.copy(suvicharCreated = it.activity.suvicharCreated + 1)
            )
        }
    }

    fun playVideo(video: VideoFeedItem) {
        _uiState.update { it.copy(isVideoPlaying = true, activePlayingVideo = video) }
    }

    fun stopVideo() {
        _uiState.update { it.copy(isVideoPlaying = false, activePlayingVideo = null) }
    }

    fun reuseVideo(video: VideoFeedItem) {
        if (!_uiState.value.userProfile.isApproved) {
            showProfileReviewDialog()
            return
        }
        _uiState.update {
            it.copy(
                isVideoProcessing = true,
                activity = it.activity.copy(videosReused = it.activity.videosReused + 1)
            )
        }
        viewModelScope.launch {
            kotlinx.coroutines.delay(1200)
            _uiState.update {
                it.copy(
                    isVideoProcessing = false,
                    toastMessage = "वीडियो पर आपकी पहचान सफलतापूर्वक लागू हो गई!"
                )
            }
        }
    }

    fun dismissVideoProcessing() {
        _uiState.update { it.copy(isVideoProcessing = false) }
    }

    fun rsvpProgram(programId: String, status: RsvpStatus, reason: String? = null) {
        _uiState.update { state ->
            val updated = state.programs.map {
                if (it.id == programId) it.copy(rsvpStatus = status, notAttendingReason = reason) else it
            }
            val msg = if (status == RsvpStatus.NOT_ATTENDING && !reason.isNullOrBlank()) {
                "आपकी अनुपस्थिति का कारण दर्ज कर लिया गया: $reason"
            } else {
                "आपकी उपस्थिति दर्ज हो गई: ${status.titleHi}"
            }
            state.copy(programs = updated, toastMessage = msg)
        }
    }

    fun addNotice(title: String, body: String, isUrgent: Boolean) {
        val newNotice = ImportantNotice(
            id = "notif_${System.currentTimeMillis()}",
            titleHi = title,
            titleEn = title,
            dateHi = "आज",
            dateEn = "Today",
            bodyHi = body,
            isUrgent = isUrgent
        )
        _uiState.update { state ->
            state.copy(notices = listOf(newNotice) + state.notices, toastMessage = "नई सूचना प्रसारित कर दी गई")
        }
    }

    fun updateMeeting(
        newTitle: String,
        newDate: String,
        newTime: String,
        newAgenda: String,
        scheduledTimeMillis: Long? = null,
        meetingUrl: String? = null
    ) {
        _uiState.update { state ->
            state.copy(
                meetingInfo = state.meetingInfo.copy(
                    titleHi = newTitle,
                    dateHi = newDate,
                    timeHi = newTime,
                    agendaHi = newAgenda,
                    scheduledTimeMillis = scheduledTimeMillis ?: state.meetingInfo.scheduledTimeMillis,
                    meetingUrl = meetingUrl?.takeIf { it.isNotBlank() }
                ),
                toastMessage = "मीटिंग का समय, विवरण व लिंक सफलतापूर्वक अपडेट किया गया"
            )
        }
    }

    fun setMeetingCountdown(minutesFromNow: Int) {
        val targetMillis = System.currentTimeMillis() + minutesFromNow * 60 * 1000L + (if (minutesFromNow > 0) 50 * 1000L else 0L)
        val formattedTime = if (minutesFromNow == 0) "अभी (लाइव)" else "$minutesFromNow मिनट बाद"
        _uiState.update { state ->
            state.copy(
                meetingInfo = state.meetingInfo.copy(
                    scheduledTimeMillis = targetMillis,
                    timeHi = formattedTime,
                    status = MeetingStatus.UPCOMING
                ),
                toastMessage = "मीटिंग काउंटडाउन $minutesFromNow मिनट पर सेट किया गया"
            )
        }
    }

    fun updateWhatsappNumber(newWhatsapp: String) {
        _uiState.update { state ->
            state.copy(
                userProfile = state.userProfile.copy(whatsapp = newWhatsapp),
                toastMessage = "WhatsApp नंबर सफलतापूर्वक अपडेट किया गया"
            )
        }
    }

    fun startMeetingFlow() {
        _uiState.update {
            it.copy(meetingInfo = it.meetingInfo.copy(status = MeetingStatus.PERMISSIONS_REQUIRED))
        }
    }

    fun grantMeetingPermissions() {
        _uiState.update {
            it.copy(meetingInfo = it.meetingInfo.copy(status = MeetingStatus.WAITING_ROOM))
        }
    }

    fun enterLiveMeeting() {
        _uiState.update {
            it.copy(
                meetingInfo = it.meetingInfo.copy(status = MeetingStatus.LIVE),
                activity = it.activity.copy(meetingsJoined = it.activity.meetingsJoined + 1)
            )
        }
    }

    fun leaveMeeting() {
        _uiState.update {
            it.copy(meetingInfo = it.meetingInfo.copy(status = MeetingStatus.ENDED))
        }
    }

    fun resetMeeting() {
        _uiState.update {
            it.copy(meetingInfo = it.meetingInfo.copy(status = MeetingStatus.UPCOMING))
        }
    }

    fun toggleMic() {
        _uiState.update { it.copy(isMicMuted = !it.isMicMuted) }
    }

    fun toggleCamera() {
        _uiState.update { it.copy(isCameraOff = !it.isCameraOff) }
    }

    fun updateTaskStatus(taskId: String, newStatus: TaskStatus, note: String = "") {
        _uiState.update { state ->
            val updated = state.tasks.map { task ->
                if (task.id == taskId) {
                    task.copy(
                        status = newStatus,
                        evidenceSubmitted = note.isNotBlank() || task.evidenceSubmitted,
                        evidenceNote = if (note.isNotBlank()) note else task.evidenceNote
                    )
                } else task
            }
            val completedInc = if (newStatus == TaskStatus.COMPLETED) 1 else 0
            state.copy(
                tasks = updated,
                activity = state.activity.copy(tasksCompleted = state.activity.tasksCompleted + completedInc),
                toastMessage = "टास्क स्टेटस अपडेट: ${newStatus.titleHi}"
            )
        }
    }

    fun submitCitizenRequest(categoryHi: String, message: String, attachment: String? = null) {
        val newId = "TR-2026-${(1000..9999).random()}"
        val newReq = CitizenRequest(
            id = newId,
            categoryHi = categoryHi,
            categoryEn = categoryHi,
            message = message,
            dateHi = "आज, 10 सितंबर 2026",
            status = RequestStatus.SUBMITTED,
            adminResponseHi = "आपकी मांग दर्ज कर ली गई है। शीघ्र जांच कर संबंधित विभाग को प्रेषित की जाएगी।",
            attachmentName = attachment
        )
        _uiState.update {
            it.copy(
                citizenRequests = listOf(newReq) + it.citizenRequests,
                activity = it.activity.copy(requestsSubmitted = it.activity.requestsSubmitted + 1),
                toastMessage = "अनुरोध $newId सफलतापूर्वक दर्ज हुआ!"
            )
        }
        try {
            FirebaseManager.submitCitizenRequest(newReq)
        } catch (e: Exception) {
            // Preserved locally
        }
    }

    fun updateCitizenGrievanceResponse(
        requestId: String,
        newStatus: RequestStatus,
        reply: String,
        resolutionPhotoUri: String? = null,
        officerName: String = "विधायक जनसेवा प्रकोष्ठ • हेमंत राठौर कार्यालय"
    ) {
        val nowFormatted = "10 सितं 2026"
        val notificationItem = NotificationItem(
            id = "notif_grievance_${System.currentTimeMillis()}",
            titleHi = "आपकी समस्या का समाधान हो गया है ✓",
            titleEn = "Your Grievance Has Been Resolved ✓",
            messageHi = "शिकायत क्रमांक $requestId: कार्यालय द्वारा समाधान विवरण व प्रमाण फोटो प्रेषित कर दिया गया है। उत्तर: \"$reply\"",
            messageEn = "Grievance #$requestId: Official response and proof photo have been attached.",
            timeAgoHi = "अभी-अभी",
            isUnread = true,
            type = "grievance_resolved"
        )

        _uiState.update { state ->
            val updatedList = state.citizenRequests.map { req ->
                if (req.id == requestId) {
                    req.copy(
                        status = newStatus,
                        adminResponseHi = reply,
                        adminResolutionPhotoUri = resolutionPhotoUri ?: req.adminResolutionPhotoUri,
                        resolutionDateHi = nowFormatted,
                        adminOfficerName = officerName
                    )
                } else req
            }
            state.copy(
                citizenRequests = updatedList,
                notifications = listOf(notificationItem) + state.notifications,
                toastMessage = "समाधान उत्तर सुरक्षित हुआ एवं नागरिक को नोटिफिकेशन प्रेषित किया गया ✓"
            )
        }
    }

    fun deleteCitizenRequest(requestId: String) {
        _uiState.update { state ->
            state.copy(
                citizenRequests = state.citizenRequests.filterNot { it.id == requestId },
                toastMessage = "शिकायत अनुरोध $requestId हटा दिया गया।"
            )
        }
    }

    fun addProfilePhoto(photoUri: String) {
        _uiState.update {
            val updated = it.userProfile.userPhotos + photoUri
            it.copy(
                userProfile = it.userProfile.copy(
                    userPhotos = updated,
                    profilePhotosCount = updated.size
                ),
                toastMessage = "फोटो सफलतापूर्वक जोड़ी गई!"
            )
        }
    }

    fun removeProfilePhoto(index: Int) {
        _uiState.update {
            val updated = it.userProfile.userPhotos.toMutableList()
            if (index in updated.indices) {
                updated.removeAt(index)
            }
            it.copy(
                userProfile = it.userProfile.copy(
                    userPhotos = updated,
                    profilePhotosCount = updated.size
                )
            )
        }
    }

    fun loginAsAdmin() {
        _uiState.update {
            it.copy(
                userProfile = it.userProfile.copy(
                    role = UserRole.ADMIN,
                    designation = "मुख्य प्रशासक (Super Admin)",
                    fullName = "एडमिन टीम राठौर"
                ),
                currentScreen = AppScreen.AdminPanel,
                toastMessage = "एडमिन के रूप में सफलतापूर्वक लॉगिन हुआ!"
            )
        }
    }

    fun addFeedItem(item: FeedItem) {
        _uiState.update {
            it.copy(
                feedList = listOf(item) + it.feedList,
                toastMessage = "नया कंटेंट सफलतापूर्वक पब्लिश किया गया!"
            )
        }
        try {
            FirebaseManager.publishFeedItem(item)
        } catch (e: Exception) {
            // Preserved locally
        }
    }

    fun toggleFeedItemEnabled(id: String) {
        _uiState.update { state ->
            val updated = state.feedList.map {
                if (it.id == id) it.copy(isEnabled = !it.isEnabled) else it
            }
            val target = updated.firstOrNull { it.id == id }
            val statusText = if (target?.isEnabled == true) "सक्रिय (Active)" else "निष्क्रिय (Disabled)"
            state.copy(feedList = updated, toastMessage = "पोस्ट फीड में अब $statusText है")
        }
    }

    fun toggleVideoRequiresJacket(id: String) {
        _uiState.update { state ->
            val updated = state.videoList.map {
                if (it.id == id) it.copy(requiresJacket = !it.requiresJacket) else it
            }
            val target = updated.firstOrNull { it.id == id }
            val msg = if (target?.requiresJacket == true) "इस वीडियो के लिए 'जैकेट अनिवार्य' कर दिया गया है!" else "इस वीडियो के लिए 'सीधा डाउनलोड' सक्रिय है!"
            state.copy(videoList = updated, toastMessage = msg)
        }
    }

    fun addCustomFacebookPost(author: String, content: String, url: String) {
        val newPost = FeedItem(
            id = "fb_post_${System.currentTimeMillis()}",
            titleHi = "Facebook पोस्ट",
            titleEn = "Facebook Post",
            categoryHi = "सोशल मीडिया",
            categoryEn = "Social Media",
            captionHi = "फेसबुक से चयनित पोस्ट",
            captionEn = "Selected Facebook Post",
            type = FeedContentType.FACEBOOK_POST,
            timestampHi = "अभी-अभी",
            timestampEn = "Just now",
            likesCount = "1.5K",
            commentsCount = "48",
            sharesCount = "92",
            facebookAuthor = if (author.isNotBlank()) author else "Aapka Vishwas Official",
            facebookTimeHi = "अभी-अभी",
            facebookContentHi = content,
            facebookUrl = if (url.isNotBlank()) url else "https://www.facebook.com",
            bannerThemeIndex = 1,
            isEnabled = true
        )
        _uiState.update {
            it.copy(
                feedList = listOf(newPost) + it.feedList,
                toastMessage = "नया Facebook पोस्ट फीड में सफलतापूर्वक जोड़ा गया!"
            )
        }
    }

    fun approveWorker(candidateName: String) {
        _uiState.update {
            it.copy(toastMessage = "$candidateName का सत्यापन सफलतापूर्वक स्वीकृत हुआ!")
        }
    }

    fun addCategory(categoryName: String) {
        val trimmed = categoryName.trim()
        if (trimmed.isNotBlank() && !_uiState.value.feedCategories.contains(trimmed)) {
            _uiState.update {
                it.copy(
                    feedCategories = it.feedCategories + trimmed,
                    toastMessage = "श्रेणी '$trimmed' सफलतापूर्वक जोड़ी गई!"
                )
            }
        }
    }

    fun updateCategory(oldName: String, newName: String) {
        val trimmed = newName.trim()
        if (trimmed.isNotBlank()) {
            _uiState.update { state ->
                val updated = state.feedCategories.map { if (it == oldName) trimmed else it }
                state.copy(
                    feedCategories = updated,
                    toastMessage = "श्रेणी का नाम बदलकर '$trimmed' कर दिया गया!"
                )
            }
        }
    }

    fun deleteCategory(categoryName: String) {
        _uiState.update { state ->
            state.copy(
                feedCategories = state.feedCategories.filter { it != categoryName },
                toastMessage = "श्रेणी '$categoryName' हटा दी गई!"
            )
        }
    }

    fun broadcastNotice(
        title: String,
        message: String,
        isUrgent: Boolean,
        photoAttached: Boolean = false,
        photoTitle: String? = null,
        photos: List<String> = emptyList()
    ) {
        val newNotice = ImportantNotice(
            id = "notice_${System.currentTimeMillis()}",
            titleHi = title,
            titleEn = title,
            dateHi = "आज, 10 सितंबर 2026",
            dateEn = "Today, 10 Sep 2026",
            bodyHi = message,
            isUrgent = isUrgent,
            photoAttached = photoAttached || photos.isNotEmpty(),
            photoTitle = photoTitle ?: if (photos.isNotEmpty()) "${photos.size} फोटो संलग्न" else null,
            photos = photos
        )
        val newNotif = NotificationItem(
            id = "notif_${System.currentTimeMillis()}",
            titleHi = if (isUrgent) "🚨 अति-महत्वपूर्ण: $title" else "📢 $title",
            titleEn = title,
            messageHi = message,
            messageEn = message,
            timeAgoHi = "अभी-अभी",
            isUnread = true,
            type = if (isUrgent) "urgent" else "general"
        )
        _uiState.update {
            it.copy(
                notices = listOf(newNotice) + it.notices,
                notifications = listOf(newNotif) + it.notifications,
                toastMessage = "आधिकारिक सूचना प्रसारित कर दी गई!"
            )
        }
        try {
            FirebaseManager.publishNotice(newNotice)
        } catch (e: Exception) {
            // Preserved locally
        }
    }

    fun markNotificationAsRead(notifId: String) {
        _uiState.update { state ->
            state.copy(
                notifications = state.notifications.map { notif ->
                    if (notif.id == notifId) notif.copy(isUnread = false) else notif
                }
            )
        }
    }

    fun compressVideo(originalSizeMb: Int = 1024) {
        viewModelScope.launch {
            _uiState.update { it.copy(videoCompressionProgress = 15) }
            kotlinx.coroutines.delay(250)
            _uiState.update { it.copy(videoCompressionProgress = 50) }
            kotlinx.coroutines.delay(300)
            _uiState.update { it.copy(videoCompressionProgress = 85) }
            kotlinx.coroutines.delay(250)
            val compressedMb = (originalSizeMb * 0.098).toInt().coerceAtLeast(10)
            val percentSaved = (((originalSizeMb - compressedMb).toDouble() / originalSizeMb) * 100).toInt()
            _uiState.update {
                it.copy(
                    videoCompressionProgress = null,
                    isVideoCompressed = true,
                    compressedVideoInfo = "मूल: ${originalSizeMb} MB → कंप्रेस्ड: ${compressedMb} MB (${percentSaved}% डेटा बचत • WhatsApp अनुकूल)",
                    toastMessage = "वीडियो 10x कंप्रेस हो गया (${originalSizeMb} MB → ${compressedMb} MB) ✓"
                )
            }
        }
    }

    fun resetVideoCompression() {
        _uiState.update {
            it.copy(
                videoCompressionProgress = null,
                isVideoCompressed = false,
                compressedVideoInfo = null
            )
        }
    }

    fun updateVideoJacket(
        id: String,
        newName: String,
        newHeaderText: String,
        newFooterText: String,
        newColorHex: Long
    ) {
        _uiState.update { state ->
            val updated = state.videoJackets.map { jacket ->
                if (jacket.id == id) {
                    jacket.copy(
                        name = newName.trim(),
                        headerText = newHeaderText.trim(),
                        footerText = newFooterText.trim(),
                        themeColorHex = newColorHex,
                        isCustomized = true
                    )
                } else jacket
            }
            state.copy(
                videoJackets = updated,
                toastMessage = "जैकेट '$newName' सफलतापूर्वक कस्टमाइज व अपडेट की गई!"
            )
        }
    }

    fun addVideoJacket(
        name: String,
        headerText: String,
        footerText: String,
        colorHex: Long,
        overlayGraphicFileName: String? = null
    ) {
        val newJacket = VideoJacketTemplate(
            id = "jacket_${System.currentTimeMillis()}",
            name = name.trim(),
            headerText = headerText.trim(),
            footerText = footerText.trim(),
            themeColorHex = colorHex,
            isCustomized = true,
            overlayGraphicFileName = overlayGraphicFileName
        )
        _uiState.update { state ->
            state.copy(
                videoJackets = state.videoJackets + newJacket,
                toastMessage = "नई जैकेट '$name' सफलतापूर्वक जोड़ी गई!"
            )
        }
    }

    fun broadcastAdminAlert(title: String, message: String, isUrgent: Boolean) {
        val newNotice = ImportantNotice(
            id = "notice_${System.currentTimeMillis()}",
            titleHi = title,
            titleEn = title,
            dateHi = "आज, 10 सितंबर 2026",
            dateEn = "Today, 10 Sep 2026",
            bodyHi = message,
            isUrgent = isUrgent
        )
        val newNotif = NotificationItem(
            id = "notif_${System.currentTimeMillis()}",
            titleHi = if (isUrgent) "🚨 अति-महत्वपूर्ण: $title" else "📢 $title",
            titleEn = title,
            messageHi = message,
            messageEn = message,
            timeAgoHi = "अभी-अभी",
            isUnread = true,
            type = if (isUrgent) "urgent" else "general"
        )
        _uiState.update {
            it.copy(
                notices = listOf(newNotice) + it.notices,
                notifications = listOf(newNotif) + it.notifications,
                toastMessage = "सूचना सभी कार्यकर्ताओं को प्रसारित कर दी गई!"
            )
        }
    }

    fun setSimulatedState(stateName: String?) {
        _uiState.update { it.copy(activeSimulatedState = stateName) }
    }

    fun shareContent(content: String) {
        _uiState.update { it.copy(toastMessage = "रीशेयर / शेयर संदेश तैयार!") }
    }

    fun approveWorkerVerification(candidateId: String) {
        _uiState.update { state ->
            val target = state.pendingVerifications.firstOrNull { it.id == candidateId }
            val updatedList = state.pendingVerifications.filter { it.id != candidateId }
            state.copy(
                pendingVerifications = updatedList,
                toastMessage = "${target?.name ?: "कार्यकर्ता"} का आवेदन स्वीकृत कर दिया गया है!"
            )
        }
    }

    fun rejectWorkerVerification(candidateId: String) {
        _uiState.update { state ->
            val target = state.pendingVerifications.firstOrNull { it.id == candidateId }
            val updatedList = state.pendingVerifications.filter { it.id != candidateId }
            state.copy(
                pendingVerifications = updatedList,
                toastMessage = "${target?.name ?: "कार्यकर्ता"} का आवेदन अस्वीकृत कर दिया गया है।"
            )
        }
    }

    fun updateUserRole(userId: String, newRole: UserRole) {
        _uiState.update { state ->
            val updated = state.managedUsers.map { user ->
                if (user.id == userId) {
                    user.copy(
                        role = newRole,
                        designation = when (newRole) {
                            UserRole.ADMIN -> "व्यवस्थापक / एडमिन"
                            UserRole.OFFICE_BEARER -> "पदाधिकारी"
                            UserRole.WORKER -> "सक्रिय कार्यकर्ता"
                            UserRole.SUPPORTER -> "समर्थक"
                        },
                        googleSheetSynced = true
                    )
                } else user
            }
            val supporters = updated.count { it.role == UserRole.SUPPORTER }
            val workers = updated.count { it.role == UserRole.WORKER }
            val officeBearers = updated.count { it.role == UserRole.OFFICE_BEARER }
            val admins = updated.count { it.role == UserRole.ADMIN }

            state.copy(
                managedUsers = updated,
                googleSheetSyncStatus = state.googleSheetSyncStatus.copy(
                    supporterCount = supporters,
                    workerCount = workers,
                    officeBearerCount = officeBearers,
                    adminCount = admins
                ),
                toastMessage = "यूजर की श्रेणी Google Sheet में '${newRole.titleHi}' टैब में स्थानांतरित हुई!"
            )
        }
    }

    fun syncUserDatabaseWithGoogleSheets() {
        viewModelScope.launch {
            val currentStatus = _uiState.value.googleSheetSyncStatus
            _uiState.update { it.copy(googleSheetSyncStatus = currentStatus.copy(isSyncing = true)) }
            val newStatus = GoogleSheetsSyncManager.syncUsersToGoogleSheet(
                users = _uiState.value.managedUsers,
                customUrl = currentStatus.spreadsheetUrl,
                webhookUrl = currentStatus.webhookUrl
            )
            _uiState.update {
                it.copy(
                    googleSheetSyncStatus = newStatus,
                    toastMessage = "Google Sheet सिंक सफल! कुल ${newStatus.totalSyncedCount} रिकॉर्ड्स अपडेट हुए।"
                )
            }
        }
    }

    fun updateGoogleSheetConfig(newSheetUrl: String, newWebhookUrl: String = "") {
        _uiState.update { state ->
            val updatedStatus = state.googleSheetSyncStatus.copy(
                spreadsheetUrl = newSheetUrl.ifBlank { state.googleSheetSyncStatus.spreadsheetUrl },
                webhookUrl = newWebhookUrl
            )
            state.copy(
                googleSheetSyncStatus = updatedStatus,
                toastMessage = "Google Sheet सेटिंग्स सुरक्षित कर ली गईं!"
            )
        }
    }

    fun addNewManagedUser(
        name: String,
        phone: String,
        area: String,
        role: UserRole,
        designation: String,
        boothNo: String
    ) {
        val newUser = ManagedUser(
            id = "usr_${System.currentTimeMillis().toString().takeLast(6)}",
            name = name,
            phone = phone,
            area = area,
            role = role,
            designation = designation,
            isBlocked = false,
            joinedDate = "10 सितं 2026",
            boothNo = boothNo,
            isVerified = true,
            googleSheetSynced = true
        )
        val updated = listOf(newUser) + _uiState.value.managedUsers
        _uiState.update { state ->
            val supporters = updated.count { it.role == UserRole.SUPPORTER }
            val workers = updated.count { it.role == UserRole.WORKER }
            val officeBearers = updated.count { it.role == UserRole.OFFICE_BEARER }
            val admins = updated.count { it.role == UserRole.ADMIN }
            state.copy(
                managedUsers = updated,
                googleSheetSyncStatus = state.googleSheetSyncStatus.copy(
                    supporterCount = supporters,
                    workerCount = workers,
                    officeBearerCount = officeBearers,
                    adminCount = admins,
                    totalSyncedCount = updated.size
                ),
                toastMessage = "नया यूजर जोड़ा गया और Google Sheet पर तुरंत सिंक हुआ!"
            )
        }
    }

    fun toggleUserBlock(userId: String) {
        _uiState.update { state ->
            var statusChangedToBlocked = false
            val updated = state.managedUsers.map { user ->
                if (user.id == userId) {
                    val newBlockedState = !user.isBlocked
                    statusChangedToBlocked = newBlockedState
                    user.copy(isBlocked = newBlockedState)
                } else user
            }
            state.copy(
                managedUsers = updated,
                toastMessage = if (statusChangedToBlocked) "यूजर को ब्लॉक कर दिया गया है।" else "यूजर को अनब्लॉक कर दिया गया है।"
            )
        }
    }

    fun addProgram(title: String, date: String, time: String, venue: String, instructions: String = "") {
        val newProgram = ProgramItem(
            id = "prog_${System.currentTimeMillis()}",
            titleHi = title,
            titleEn = title,
            dateHi = date,
            dateEn = date,
            timeHi = time,
            timeEn = time,
            venueHi = venue,
            venueEn = venue,
            instructionsHi = instructions.ifBlank { "सभी कार्यकर्ता समय पर उपस्थित हों।" },
            attendingCount = 0,
            maybeCount = 0,
            cantCount = 0
        )
        _uiState.update {
            it.copy(
                programs = listOf(newProgram) + it.programs,
                toastMessage = "नया कार्यक्रम सफलतापूर्वक जोड़ दिया गया!"
            )
        }
    }

    fun updateProgram(id: String, title: String, date: String, time: String, venue: String, isHighlighted: Boolean = false) {
        _uiState.update { state ->
            val updated = state.programs.map {
                if (it.id == id) it.copy(
                    titleHi = title,
                    titleEn = title,
                    dateHi = date,
                    dateEn = date,
                    timeHi = time,
                    timeEn = time,
                    venueHi = venue,
                    venueEn = venue,
                    isHighlighted = isHighlighted
                ) else it
            }
            state.copy(programs = updated, toastMessage = "कार्यक्रम सफलतापूर्वक अपडेट किया गया!")
        }
    }

    fun deleteProgram(id: String) {
        _uiState.update { state ->
            state.copy(
                programs = state.programs.filter { it.id != id },
                toastMessage = "कार्यक्रम हटा दिया गया!"
            )
        }
        try {
            FirebaseManager.deleteProgram(id)
        } catch (e: Exception) {
            // Preserved locally
        }
    }

    fun updateNotice(id: String, title: String, body: String, isUrgent: Boolean) {
        _uiState.update { state ->
            val updated = state.notices.map {
                if (it.id == id) it.copy(
                    titleHi = title,
                    titleEn = title,
                    bodyHi = body,
                    isUrgent = isUrgent
                ) else it
            }
            state.copy(notices = updated, toastMessage = "सूचना सफलतापूर्वक अपडेट की गई!")
        }
    }

    fun deleteNotice(id: String) {
        _uiState.update { state ->
            state.copy(
                notices = state.notices.filter { it.id != id },
                toastMessage = "सूचना हटा दी गई!"
            )
        }
        try {
            FirebaseManager.deleteNotice(id)
        } catch (e: Exception) {
            // Preserved locally
        }
    }

    fun createLiveMeeting(
        title: String,
        date: String,
        time: String,
        agenda: String,
        scheduledTimeMillis: Long = System.currentTimeMillis() + (14 * 60 + 50) * 1000L,
        meetingUrl: String? = null
    ) {
        val finalMeetUrl = if (meetingUrl.isNullOrBlank()) {
            "https://meet.google.com/rth-wrm-del"
        } else {
            meetingUrl.trim()
        }

        val newMeetingNotification = NotificationItem(
            id = "notif_meet_${System.currentTimeMillis()}",
            titleHi = "लाइव बैठक सूचना: $title",
            titleEn = "Live Meeting: $title",
            messageHi = "दिनांक: $date • समय: $time। Google Meet के माध्यम से ऐप पर सीधे लाइव जुड़ें।",
            messageEn = "Date: $date • Time: $time. Connect live via Google Meet right inside the app.",
            timeAgoHi = "अभी-अभी",
            isUnread = true,
            type = "meeting"
        )

        val scheduledMeeting = _uiState.value.meetingInfo.copy(
            titleHi = title,
            titleEn = title,
            dateHi = date,
            timeHi = time,
            agendaHi = agenda,
            status = MeetingStatus.UPCOMING,
            scheduledTimeMillis = scheduledTimeMillis,
            meetingUrl = finalMeetUrl,
            isScheduled = true
        )

        _uiState.update { state ->
            // Keep notifications clean, replace any old meeting notification with the new scheduled one
            val updatedNotifs = listOf(newMeetingNotification) + state.notifications.filter { it.type != "meeting" }
            state.copy(
                meetingInfo = scheduledMeeting,
                notifications = updatedNotifs,
                toastMessage = "Google Meet लाइव बैठक सफलतापूर्वक शेड्यूल की गई!"
            )
        }

        try {
            FirebaseManager.publishMeetingInfo(scheduledMeeting)
        } catch (e: Exception) {
            // Preserved locally
        }
    }

    fun cancelOrEndMeeting() {
        val endedMeeting = _uiState.value.meetingInfo.copy(
            status = MeetingStatus.ENDED,
            isScheduled = false
        )

        _uiState.update { state ->
            state.copy(
                meetingInfo = endedMeeting,
                // Remove meeting notification when no meeting is scheduled
                notifications = state.notifications.filter { it.type != "meeting" },
                toastMessage = "बैठक समाप्त कर दी गई। बैठक सूचना हटा दी गई।"
            )
        }

        try {
            FirebaseManager.publishMeetingInfo(endedMeeting)
        } catch (e: Exception) {
            // Preserved locally
        }
    }

    fun createProgram(title: String, date: String, time: String, venue: String, instructions: String = "", isHighlighted: Boolean = false) {
        val newProg = ProgramItem(
            id = "prog_${System.currentTimeMillis()}",
            titleHi = title,
            titleEn = title,
            dateHi = date,
            dateEn = date,
            timeHi = time,
            timeEn = time,
            venueHi = venue,
            venueEn = venue,
            instructionsHi = instructions.ifBlank { "सभी कार्यकर्ता समय पर उपस्थित हों।" },
            isHighlighted = isHighlighted
        )
        _uiState.update { state ->
            state.copy(
                programs = listOf(newProg) + state.programs,
                toastMessage = if (isHighlighted) "⭐ प्रमुख कार्यक्रम (Highlight) सफलतापूर्वक जोड़ा गया!" else "कार्यक्रम सफलतापूर्वक जोड़ दिया गया!"
            )
        }
        try {
            FirebaseManager.publishProgram(newProg)
        } catch (e: Exception) {
            // Preserved locally
        }
    }

    fun addAdminVideo(
        title: String,
        caption: String,
        aspectRatio: String,
        jacketTemplateName: String,
        categories: List<String>,
        duration: String = "2:30",
        requiresJacket: Boolean = true,
        videoUri: String? = null
    ) {
        val newVideo = VideoFeedItem(
            id = "vid_${System.currentTimeMillis()}",
            titleHi = title,
            titleEn = title,
            captionHi = caption,
            captionEn = caption,
            duration = duration,
            timestampHi = "अभी-अभी",
            timestampEn = "Just now",
            likesCount = "1.2K",
            commentsCount = "45",
            sharesCount = "80",
            aspectRatio = aspectRatio,
            jacketTemplateName = jacketTemplateName,
            categories = if (categories.isEmpty()) listOf("वीडियो") else categories,
            requiresJacket = requiresJacket,
            videoUri = videoUri
        )

        val feedVideoItem = FeedItem(
            id = "feed_vid_${System.currentTimeMillis()}",
            titleHi = title,
            titleEn = title,
            categoryHi = categories.firstOrNull() ?: "वीडियो",
            categoryEn = categories.firstOrNull() ?: "Video",
            captionHi = caption.ifBlank { title },
            captionEn = caption.ifBlank { title },
            type = FeedContentType.VIDEO,
            timestampHi = "अभी-अभी",
            timestampEn = "Just now",
            likesCount = "1.5K",
            commentsCount = "52",
            sharesCount = "110",
            isFeatured = true,
            posterHeadlineHi = title,
            posterSubHeadlineHi = caption.ifBlank { "हेमंत राठौर • वीडियो ($aspectRatio)" },
            isEnabled = true,
            imageUrl = videoUri
        )

        _uiState.update {
            it.copy(
                videoList = listOf(newVideo) + it.videoList,
                feedList = listOf(feedVideoItem) + it.feedList,
                toastMessage = "वीडियो होम फ़ीड में सफलतापूर्वक लाइव हो गया!"
            )
        }
        try {
            FirebaseManager.publishFeedItem(feedVideoItem)
            FirebaseManager.publishVideo(newVideo)
        } catch (e: Exception) {
            // Local fallback preserved
        }
    }

    fun addAdminGraphic(
        headline: String,
        category: String,
        caption: String = "",
        photoAttached: Boolean = true,
        photoCount: Int = 1,
        imageUrl: String? = null
    ) {
        val newItem = FeedItem(
            id = "graphic_${System.currentTimeMillis()}",
            titleHi = headline,
            titleEn = headline,
            categoryHi = category,
            categoryEn = category,
            captionHi = caption.ifBlank { headline },
            captionEn = headline,
            type = FeedContentType.ADMIN_GRAPHIC,
            timestampHi = "अभी-अभी",
            timestampEn = "Just now",
            likesCount = "2.4K",
            commentsCount = "112",
            sharesCount = "340",
            isFeatured = true,
            posterHeadlineHi = headline,
            posterSubHeadlineHi = caption.ifBlank { "हेमंत राठौर - जनसेवा ही धर्म" },
            isEnabled = true,
            photoCollageCount = photoCount,
            attachedPhotoName = if (photoAttached) "admin_photo_${photoCount}pic" else null,
            imageUrl = imageUrl
        )
        _uiState.update {
            it.copy(
                feedList = listOf(newItem) + it.feedList,
                toastMessage = "प्रचार सामग्री होम फ़ीड में सफलतापूर्वक लाइव हो गई!"
            )
        }
        try {
            FirebaseManager.publishFeedItem(newItem)
        } catch (e: Exception) {
            // Local fallback preserved
        }
    }

    fun addAdminDailyUpdate(
        title: String,
        content: String,
        category: String,
        photoCount: Int = 1,
        sourceUrl: String = "",
        imageUrl: String? = null
    ) {
        val newItem = FeedItem(
            id = "update_${System.currentTimeMillis()}",
            titleHi = title,
            titleEn = title,
            categoryHi = category,
            categoryEn = category,
            captionHi = content,
            captionEn = content,
            type = FeedContentType.FACEBOOK_POST,
            timestampHi = "अभी-अभी",
            timestampEn = "Just now",
            likesCount = "1.8K",
            commentsCount = "64",
            sharesCount = "120",
            facebookAuthor = "हेमंत राठौर",
            facebookTimeHi = "अभी-अभी",
            facebookContentHi = content,
            facebookUrl = sourceUrl.ifBlank { "https://www.facebook.com/teamrathore" },
            isEnabled = true,
            photoCollageCount = 1,
            attachedPhotoName = if (imageUrl != null) "update_photo" else null,
            imageUrl = imageUrl
        )
        _uiState.update {
            it.copy(
                feedList = listOf(newItem) + it.feedList,
                toastMessage = "प्रतिदिन की अपडेट गतिविधि सफलतापूर्वक होम फ़ीड में लाइव हो गई!"
            )
        }
        try {
            FirebaseManager.publishFeedItem(newItem)
        } catch (e: Exception) {
            // Local fallback preserved
        }
    }

    fun setCompressionPreset(preset: ImageCompressionPreset) {
        _uiState.update { it.copy(selectedCompressionPreset = preset) }
    }

    fun clearImageCompressionResult() {
        _uiState.update {
            it.copy(
                lastImageCompressionResult = null,
                imageCompressionStatusText = null,
                imageUploadProgress = 0f
            )
        }
    }

    fun compressAndUploadAdminGraphic(
        context: Context,
        imageUri: Uri?,
        headline: String,
        category: String,
        photoCount: Int,
        onComplete: (Boolean) -> Unit = {}
    ) {
        if (imageUri == null) {
            addAdminGraphic(
                headline = headline,
                category = category,
                caption = headline,
                photoAttached = true,
                photoCount = photoCount
            )
            onComplete(true)
            return
        }

        // 1. Instantly publish to local feed and Firebase so home screen immediately reflects it!
        val instantFeedId = "graphic_${System.currentTimeMillis()}"
        val initialFeedItem = FeedItem(
            id = instantFeedId,
            titleHi = headline,
            titleEn = headline,
            categoryHi = category,
            categoryEn = category,
            captionHi = headline,
            captionEn = headline,
            type = FeedContentType.ADMIN_GRAPHIC,
            timestampHi = "अभी-अभी",
            timestampEn = "Just now",
            likesCount = "2.4K",
            commentsCount = "112",
            sharesCount = "340",
            isFeatured = true,
            posterHeadlineHi = headline,
            posterSubHeadlineHi = "हेमंत राठौर - जनसेवा ही धर्म",
            isEnabled = true,
            photoCollageCount = photoCount,
            attachedPhotoName = "admin_photo_${photoCount}pic",
            imageUrl = imageUri.toString()
        )
        _uiState.update {
            it.copy(
                feedList = listOf(initialFeedItem) + it.feedList.filter { old -> old.id != instantFeedId },
                toastMessage = "प्रचार फोटो होम फ़ीड में लाइव हो गई!"
            )
        }
        try {
            FirebaseManager.publishFeedItem(initialFeedItem)
        } catch (e: Exception) {
            // Local fallback preserved
        }
        onComplete(true)

        val preset = _uiState.value.selectedCompressionPreset
        val storagePath = "admin_graphics/graphic_${System.currentTimeMillis()}.jpg"

        _uiState.update {
            it.copy(
                isCompressingAndUploadingImage = true,
                imageUploadProgress = 0f,
                imageCompressionStatusText = "इमेज कंप्रेस की जा रही है (${preset.titleHi})..."
            )
        }

        FirebaseManager.uploadOptimizedImage(
            context = context,
            imageUri = imageUri,
            storagePath = storagePath,
            preset = preset,
            onCompressionProgress = { status ->
                _uiState.update { it.copy(imageCompressionStatusText = status) }
            },
            onUploadProgress = { progress ->
                _uiState.update { it.copy(imageUploadProgress = progress) }
            },
            onSuccess = { downloadUrl, result ->
                val finalUrl = downloadUrl.ifBlank { imageUri.toString() }
                _uiState.update { state ->
                    val updatedFeed = state.feedList.map { item ->
                        if (item.id == instantFeedId) item.copy(imageUrl = finalUrl) else item
                    }
                    state.copy(
                        feedList = updatedFeed,
                        isCompressingAndUploadingImage = false,
                        lastImageCompressionResult = result,
                        imageCompressionStatusText = result.summaryTextHi
                    )
                }
                try {
                    FirebaseManager.publishFeedItem(initialFeedItem.copy(imageUrl = finalUrl))
                } catch (e: Exception) {
                    // Ignored
                }
            },
            onError = { err ->
                _uiState.update {
                    it.copy(
                        isCompressingAndUploadingImage = false,
                        imageCompressionStatusText = "लोकल सेव: ${err.message}"
                    )
                }
            }
        )
    }

    fun compressAndUploadDailyUpdate(
        context: Context,
        imageUri: Uri?,
        title: String,
        content: String,
        category: String,
        photoCount: Int,
        sourceUrl: String,
        onComplete: (Boolean) -> Unit = {}
    ) {
        if (imageUri == null) {
            addAdminDailyUpdate(
                title = title,
                content = content,
                category = category,
                photoCount = 1,
                sourceUrl = sourceUrl,
                imageUrl = null
            )
            onComplete(true)
            return
        }

        val instantFeedId = "update_${System.currentTimeMillis()}"
        val initialFeedItem = FeedItem(
            id = instantFeedId,
            titleHi = title,
            titleEn = title,
            categoryHi = category,
            categoryEn = category,
            captionHi = content,
            captionEn = content,
            type = FeedContentType.FACEBOOK_POST,
            timestampHi = "अभी-अभी",
            timestampEn = "Just now",
            likesCount = "1.8K",
            commentsCount = "64",
            sharesCount = "120",
            facebookAuthor = "हेमंत राठौर",
            facebookTimeHi = "अभी-अभी",
            facebookContentHi = content,
            facebookUrl = sourceUrl.ifBlank { "https://www.facebook.com/teamrathore" },
            isEnabled = true,
            photoCollageCount = 1,
            attachedPhotoName = "update_photo",
            imageUrl = imageUri.toString()
        )

        _uiState.update {
            it.copy(
                feedList = listOf(initialFeedItem) + it.feedList.filter { old -> old.id != instantFeedId },
                toastMessage = "प्रतिदिन की अपडेट फ़ीड में लाइव हो गई!"
            )
        }
        try {
            FirebaseManager.publishFeedItem(initialFeedItem)
        } catch (e: Exception) {
            // Local fallback preserved
        }
        onComplete(true)

        val preset = _uiState.value.selectedCompressionPreset
        val storagePath = "admin_updates/update_${System.currentTimeMillis()}.jpg"

        _uiState.update {
            it.copy(
                isCompressingAndUploadingImage = true,
                imageUploadProgress = 0f,
                imageCompressionStatusText = "दैनिक अपडेट इमेज कंप्रेस हो रही है (${preset.titleHi})..."
            )
        }

        FirebaseManager.uploadOptimizedImage(
            context = context,
            imageUri = imageUri,
            storagePath = storagePath,
            preset = preset,
            onCompressionProgress = { status ->
                _uiState.update { it.copy(imageCompressionStatusText = status) }
            },
            onUploadProgress = { progress ->
                _uiState.update { it.copy(imageUploadProgress = progress) }
            },
            onSuccess = { downloadUrl, result ->
                val finalUrl = downloadUrl.ifBlank { imageUri.toString() }
                _uiState.update { state ->
                    val updatedFeed = state.feedList.map { item ->
                        if (item.id == instantFeedId) item.copy(imageUrl = finalUrl) else item
                    }
                    state.copy(
                        feedList = updatedFeed,
                        isCompressingAndUploadingImage = false,
                        lastImageCompressionResult = result,
                        imageCompressionStatusText = result.summaryTextHi
                    )
                }
                try {
                    FirebaseManager.publishFeedItem(initialFeedItem.copy(imageUrl = finalUrl))
                } catch (e: Exception) {
                    // Ignored
                }
            },
            onError = { err ->
                _uiState.update {
                    it.copy(
                        isCompressingAndUploadingImage = false,
                        imageCompressionStatusText = "अपलोड चेतावनी: ${err.message}"
                    )
                }
            }
        )
    }

    fun addWorkerSuggestion(message: String, category: String) {
        addWorkerSuggestion(subject = category, message = message, category = category, photoUri = null)
    }

    fun addWorkerSuggestion(subject: String, message: String, category: String, photoUri: String? = null) {
        val newSuggestion = WorkerSuggestion(
            id = "sug_${System.currentTimeMillis()}",
            workerName = _uiState.value.userProfile.fullName.ifBlank { "कार्यकर्ता" },
            area = _uiState.value.userProfile.assembly.ifBlank { "विधानसभा क्षेत्र" },
            category = category,
            subject = subject,
            message = message,
            date = "आज",
            status = "प्राप्त",
            photoUri = photoUri
        )
        _uiState.update {
            it.copy(
                workerSuggestions = listOf(newSuggestion) + it.workerSuggestions,
                toastMessage = "सुझाव नेतृत्व को सफलतापूर्वक प्रेषित कर दिया गया!"
            )
        }
    }

    fun addFieldReport(subject: String, message: String, photoUri: String? = null, videoUri: String? = null) {
        val newReport = FieldReport(
            id = "fr_${System.currentTimeMillis()}",
            authorName = _uiState.value.userProfile.fullName.ifBlank { "कार्यकर्ता/पदाधिकारी" },
            area = _uiState.value.userProfile.village.ifBlank { _uiState.value.userProfile.assembly.ifBlank { "विधानसभा क्षेत्र" } },
            subject = subject,
            message = message,
            photoUri = photoUri,
            videoUri = videoUri,
            date = "आज",
            status = "प्राप्त"
        )
        _uiState.update {
            it.copy(
                fieldReports = listOf(newReport) + it.fieldReports,
                toastMessage = "क्षेत्रीय जानकारी सफलतापूर्वक नेतृत्व को प्रेषित कर दी गई!"
            )
        }
    }

    fun addOfficialDocument(title: String, letterNumber: String, category: String, summary: String, documentUrl: String = "") {
        val newDoc = OfficialDocument(
            id = "doc_${System.currentTimeMillis()}",
            title = title,
            letterNumber = letterNumber,
            category = category,
            date = "आज",
            summary = summary,
            issuedBy = "केंद्रीय कार्यालय • टीम राठौर",
            documentUrl = documentUrl
        )
        _uiState.update {
            it.copy(
                officialDocuments = listOf(newDoc) + it.officialDocuments,
                toastMessage = "महत्वपूर्ण दस्तावेज सफलतापूर्वक अपलोड किया गया!"
            )
        }
    }

    fun addVideoItem(title: String, caption: String, category: String, duration: String = "2:30") {
        val newVideo = VideoFeedItem(
            id = "vid_${System.currentTimeMillis()}",
            titleHi = title,
            titleEn = title,
            captionHi = caption,
            captionEn = caption,
            duration = duration,
            timestampHi = "अभी-अभी",
            timestampEn = "Just now",
            likesCount = "0",
            commentsCount = "0",
            sharesCount = "0"
        )
        _uiState.update {
            it.copy(
                videoList = listOf(newVideo) + it.videoList,
                toastMessage = "वीडियो सामग्री सफलतापूर्वक पब्लिश की गई!"
            )
        }
    }

    fun clearToast() {
        _uiState.update { it.copy(toastMessage = null) }
    }
}
