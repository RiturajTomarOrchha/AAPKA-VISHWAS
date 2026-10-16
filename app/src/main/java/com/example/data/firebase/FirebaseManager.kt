package com.example.data.firebase

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.model.*
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata
import com.example.util.CompressionResult
import com.example.util.ImageCompressionPreset
import com.example.util.ImageCompressionUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.util.concurrent.TimeUnit

/**
 * Production Firebase Manager for Team Rathore:
 * 1. Firebase Phone Authentication (Real-time SMS OTP)
 * 2. Cloud Firestore (Real-time sync of Feed, Notices, Programs, and Requests)
 * 3. Firebase Storage (Posters, photos, and video uploads)
 */
object FirebaseManager {

    private const val TAG = "FirebaseManager"
    private var isInitialized = false

    fun isConfigured(context: Context): Boolean {
        return try {
            FirebaseApp.getApps(context).isNotEmpty()
        } catch (e: Exception) {
            false
        }
    }

    private fun getAuth(): FirebaseAuth? {
        return try {
            FirebaseAuth.getInstance()
        } catch (e: Exception) {
            Log.w(TAG, "Firebase Auth not available: ${e.message}")
            null
        }
    }

    private fun getFirestore(): FirebaseFirestore? {
        return try {
            FirebaseFirestore.getInstance()
        } catch (e: Exception) {
            Log.w(TAG, "Firestore not available: ${e.message}")
            null
        }
    }

    private fun getStorage(): FirebaseStorage? {
        return try {
            FirebaseStorage.getInstance()
        } catch (e: Exception) {
            Log.w(TAG, "Firebase Storage not available: ${e.message}")
            null
        }
    }

    // ==========================================
    // 1. FIREBASE PHONE AUTHENTICATION (SMS OTP)
    // ==========================================

    fun sendVerificationCode(
        phoneNumber: String,
        activity: Activity,
        onCodeSent: (verificationId: String) -> Unit,
        onError: (String) -> Unit
    ) {
        val auth = getAuth()
        if (auth == null) {
            // Graceful fallback for preview / testing
            onError("Firebase कॉन्फ़िगरेशन प्रतीक्षित है। कृपया अपना google-services.json संलग्न करें।")
            return
        }

        // Format to E.164 (add +91 if missing for Indian numbers)
        val formattedNumber = if (phoneNumber.startsWith("+")) {
            phoneNumber
        } else {
            "+91${phoneNumber.trim()}"
        }

        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                // Auto-retrieval / Instant verification
                auth.signInWithCredential(credential)
                    .addOnSuccessListener {
                        Log.d(TAG, "Phone auth auto-completed successfully")
                    }
                    .addOnFailureListener { e ->
                        Log.e(TAG, "Sign in with auto credential failed: ${e.message}")
                    }
            }

            override fun onVerificationFailed(e: FirebaseException) {
                Log.e(TAG, "Verification failed: ${e.message}", e)
                onError(e.localizedMessage ?: "ओटीपी भेजने में त्रुटि हुई")
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                Log.d(TAG, "Code sent to $formattedNumber, verificationId: $verificationId")
                onCodeSent(verificationId)
            }
        }

        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(formattedNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(callbacks)
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    fun verifyCode(
        verificationId: String,
        code: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val auth = getAuth()
        if (auth == null) {
            onError("Firebase Auth अनुपलब्ध है।")
            return
        }

        try {
            val credential = PhoneAuthProvider.getCredential(verificationId, code)
            auth.signInWithCredential(credential)
                .addOnSuccessListener {
                    onSuccess()
                }
                .addOnFailureListener { e ->
                    onError(e.localizedMessage ?: "अमान्य ओटीपी कोड")
                }
        } catch (e: Exception) {
            onError("ओटीपी सत्यापन में त्रुटि: ${e.message}")
        }
    }

    // ==========================================
    // 2. CLOUD FIRESTORE REAL-TIME SYNC
    // ==========================================

    fun observeFeedItems(): Flow<List<FeedItem>> = callbackFlow {
        val firestore = getFirestore()
        if (firestore == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val listener = firestore.collection("feed_items")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w(TAG, "Listen failed for feed_items: ${error.message}")
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val items = snapshot.documents.mapNotNull { doc ->
                        try {
                            val feedTypeStr = doc.getString("type") ?: "ADMIN_GRAPHIC"
                            val feedType = try {
                                FeedContentType.valueOf(feedTypeStr)
                            } catch (e: Exception) {
                                FeedContentType.ADMIN_GRAPHIC
                            }
                            FeedItem(
                                id = doc.getString("id") ?: doc.id,
                                titleHi = doc.getString("titleHi") ?: "",
                                titleEn = doc.getString("titleEn") ?: "",
                                categoryHi = doc.getString("categoryHi") ?: "सामान्य",
                                categoryEn = doc.getString("categoryEn") ?: "General",
                                captionHi = doc.getString("captionHi") ?: "",
                                captionEn = doc.getString("captionEn") ?: "",
                                type = feedType,
                                timestampHi = doc.getString("timestampHi") ?: "अभी-अभी",
                                timestampEn = doc.getString("timestampEn") ?: "Just now",
                                likesCount = doc.getString("likesCount") ?: "128",
                                commentsCount = doc.getString("commentsCount") ?: "34",
                                sharesCount = doc.getString("sharesCount") ?: "56",
                                isFeatured = doc.getBoolean("isFeatured") ?: false,
                                posterHeadlineHi = doc.getString("posterHeadlineHi") ?: "",
                                posterSubHeadlineHi = doc.getString("posterSubHeadlineHi") ?: "",
                                facebookAuthor = doc.getString("facebookAuthor") ?: "टीम राठौर",
                                facebookTimeHi = doc.getString("facebookTimeHi") ?: "हाल ही में",
                                facebookContentHi = doc.getString("facebookContentHi") ?: "",
                                facebookUrl = doc.getString("facebookUrl") ?: "",
                                imageUrl = doc.getString("imageUrl")?.takeIf { it.isNotBlank() },
                                photoCollageCount = doc.getLong("photoCollageCount")?.toInt() ?: 1,
                                isEnabled = doc.getBoolean("isEnabled") ?: true
                            )
                        } catch (e: Exception) {
                            Log.e(TAG, "Error parsing feed item doc ${doc.id}: ${e.message}")
                            null
                        }
                    }
                    trySend(items)
                }
            }

        awaitClose { listener.remove() }
    }

    fun observeNotices(): Flow<List<ImportantNotice>> = callbackFlow {
        val firestore = getFirestore()
        if (firestore == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val listener = firestore.collection("notices")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w(TAG, "Listen failed for notices: ${error.message}")
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val notices = snapshot.documents.mapNotNull { doc ->
                        try {
                            @Suppress("UNCHECKED_CAST")
                            val photoList = (doc.get("photos") as? List<String>) ?: emptyList()
                            ImportantNotice(
                                id = doc.getString("id") ?: doc.id,
                                titleHi = doc.getString("titleHi") ?: "",
                                titleEn = doc.getString("titleEn") ?: "",
                                dateHi = doc.getString("dateHi") ?: "आज",
                                dateEn = doc.getString("dateEn") ?: "Today",
                                bodyHi = doc.getString("bodyHi") ?: "",
                                isUrgent = doc.getBoolean("isUrgent") ?: false,
                                viewsCount = (doc.getLong("viewsCount") ?: 0L).toInt(),
                                likesCount = (doc.getLong("likesCount") ?: 0L).toInt(),
                                agreeCount = (doc.getLong("agreeCount") ?: 0L).toInt(),
                                photoAttached = doc.getBoolean("photoAttached") ?: false,
                                photoTitle = doc.getString("photoTitle"),
                                photos = photoList
                            )
                        } catch (e: Exception) {
                            null
                        }
                    }
                    trySend(notices)
                }
            }

        awaitClose { listener.remove() }
    }

    fun observePrograms(): Flow<List<ProgramItem>> = callbackFlow {
        val firestore = getFirestore()
        if (firestore == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val listener = firestore.collection("programs")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w(TAG, "Listen failed for programs: ${error.message}")
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val programs = snapshot.documents.mapNotNull { doc ->
                        try {
                            ProgramItem(
                                id = doc.getString("id") ?: doc.id,
                                titleHi = doc.getString("titleHi") ?: "",
                                titleEn = doc.getString("titleEn") ?: "",
                                dateHi = doc.getString("dateHi") ?: "",
                                dateEn = doc.getString("dateEn") ?: "",
                                timeHi = doc.getString("timeHi") ?: "",
                                timeEn = doc.getString("timeEn") ?: "",
                                venueHi = doc.getString("venueHi") ?: "",
                                venueEn = doc.getString("venueEn") ?: "",
                                instructionsHi = doc.getString("instructionsHi") ?: "",
                                isHighlighted = doc.getBoolean("isHighlighted") ?: false,
                                attendingCount = (doc.getLong("attendingCount") ?: 0L).toInt(),
                                maybeCount = (doc.getLong("maybeCount") ?: 0L).toInt(),
                                cantCount = (doc.getLong("cantCount") ?: 0L).toInt()
                            )
                        } catch (e: Exception) {
                            null
                        }
                    }
                    trySend(programs)
                }
            }

        awaitClose { listener.remove() }
    }

    // ==========================================
    // ADMIN FRAMES (Real-time Firestore Sync)
    // ==========================================

    fun observeFrames(): Flow<List<AdminFrameItem>> = callbackFlow {
        val firestore = getFirestore()
        if (firestore == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val listener = firestore.collection("admin_frames")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w(TAG, "Listen failed for admin_frames: ${error.message}")
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val frames = snapshot.documents.mapNotNull { doc ->
                        try {
                            val pCx = (doc.getDouble("photoCenterX") ?: 0.20).toFloat()
                            val pCy = (doc.getDouble("photoCenterY") ?: 0.86).toFloat()
                            val pR = (doc.getDouble("photoRadius") ?: 0.135).toFloat()
                            val nX = (doc.getDouble("nameX") ?: 0.38).toFloat()
                            val nY = (doc.getDouble("nameY") ?: 0.89).toFloat()
                            val dX = (doc.getDouble("desigX") ?: 0.38).toFloat()
                            val dY = (doc.getDouble("desigY") ?: 0.94).toFloat()
                            val txtColor = doc.getString("textColorHex") ?: "#FFFFFF"
                            val tStyle = doc.getString("templateStyle") ?: "TEMPLATE_1"
                            val nFont = doc.getString("nameFontFamily") ?: "DEFAULT_BOLD"
                            val nSize = (doc.getDouble("nameFontSizeSp") ?: 36.0).toFloat()
                            val nWeight = doc.getString("nameFontWeight") ?: "BOLD"
                            val dFont = doc.getString("desigFontFamily") ?: "SANS_SERIF"
                            val dSize = (doc.getDouble("desigFontSizeSp") ?: 24.0).toFloat()
                            val dWeight = doc.getString("desigFontWeight") ?: "MEDIUM"
                            val dColor = doc.getString("desigColorHex") ?: "#E2E8F0"

                            AdminFrameItem(
                                id = doc.getString("id") ?: doc.id,
                                title = doc.getString("title") ?: "आधिकारिक फ्रेम",
                                headerImageUri = doc.getString("headerImageUri"),
                                footerImageUri = doc.getString("footerImageUri"),
                                fullFrameUri = doc.getString("fullFrameUri"),
                                category = doc.getString("category") ?: "सामान्य",
                                aspectRatio = doc.getString("aspectRatio") ?: "4:5",
                                templateStyle = tStyle,
                                isDefault = doc.getBoolean("isDefault") ?: false,
                                photoCenterX = pCx,
                                photoCenterY = pCy,
                                photoRadius = pR,
                                nameX = nX,
                                nameY = nY,
                                nameFontFamily = nFont,
                                nameFontSizeSp = nSize,
                                nameFontWeight = nWeight,
                                textColorHex = txtColor,
                                desigX = dX,
                                desigY = dY,
                                desigFontFamily = dFont,
                                desigFontSizeSp = dSize,
                                desigFontWeight = dWeight,
                                desigColorHex = dColor,
                                createdAt = doc.getLong("createdAt") ?: System.currentTimeMillis()
                            )
                        } catch (e: Exception) {
                            null
                        }
                    }
                    trySend(frames)
                }
            }

        awaitClose { listener.remove() }
    }

    fun publishFrame(
        frame: AdminFrameItem,
        onSuccess: () -> Unit = {},
        onError: (Exception) -> Unit = {}
    ) {
        val firestore = getFirestore() ?: return
        val docData = hashMapOf(
            "id" to frame.id,
            "title" to frame.title,
            "headerImageUri" to (frame.headerImageUri ?: ""),
            "footerImageUri" to (frame.footerImageUri ?: ""),
            "fullFrameUri" to (frame.fullFrameUri ?: ""),
            "category" to frame.category,
            "aspectRatio" to frame.aspectRatio,
            "templateStyle" to frame.templateStyle,
            "isDefault" to frame.isDefault,
            "photoCenterX" to frame.photoCenterX,
            "photoCenterY" to frame.photoCenterY,
            "photoRadius" to frame.photoRadius,
            "nameX" to frame.nameX,
            "nameY" to frame.nameY,
            "nameFontFamily" to frame.nameFontFamily,
            "nameFontSizeSp" to frame.nameFontSizeSp,
            "nameFontWeight" to frame.nameFontWeight,
            "textColorHex" to frame.textColorHex,
            "desigX" to frame.desigX,
            "desigY" to frame.desigY,
            "desigFontFamily" to frame.desigFontFamily,
            "desigFontSizeSp" to frame.desigFontSizeSp,
            "desigFontWeight" to frame.desigFontWeight,
            "desigColorHex" to frame.desigColorHex,
            "createdAt" to frame.createdAt
        )
        firestore.collection("admin_frames").document(frame.id)
            .set(docData)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it) }
    }

    fun updateFrame(
        frame: AdminFrameItem,
        onSuccess: () -> Unit = {},
        onError: (Exception) -> Unit = {}
    ) {
        val firestore = getFirestore() ?: return
        val docData = hashMapOf<String, Any>(
            "id" to frame.id,
            "title" to frame.title,
            "headerImageUri" to (frame.headerImageUri ?: ""),
            "footerImageUri" to (frame.footerImageUri ?: ""),
            "fullFrameUri" to (frame.fullFrameUri ?: ""),
            "category" to frame.category,
            "aspectRatio" to frame.aspectRatio,
            "templateStyle" to frame.templateStyle,
            "isDefault" to frame.isDefault,
            "photoCenterX" to frame.photoCenterX,
            "photoCenterY" to frame.photoCenterY,
            "photoRadius" to frame.photoRadius,
            "nameX" to frame.nameX,
            "nameY" to frame.nameY,
            "nameFontFamily" to frame.nameFontFamily,
            "nameFontSizeSp" to frame.nameFontSizeSp,
            "nameFontWeight" to frame.nameFontWeight,
            "textColorHex" to frame.textColorHex,
            "desigX" to frame.desigX,
            "desigY" to frame.desigY,
            "desigFontFamily" to frame.desigFontFamily,
            "desigFontSizeSp" to frame.desigFontSizeSp,
            "desigFontWeight" to frame.desigFontWeight,
            "desigColorHex" to frame.desigColorHex,
            "createdAt" to frame.createdAt
        )
        firestore.collection("admin_frames").document(frame.id)
            .set(docData, com.google.firebase.firestore.SetOptions.merge())
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it) }
    }

    fun renameFrame(
        frameId: String,
        newTitle: String,
        onSuccess: () -> Unit = {},
        onError: (Exception) -> Unit = {}
    ) {
        val firestore = getFirestore() ?: return
        firestore.collection("admin_frames").document(frameId)
            .update("title", newTitle)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it) }
    }

    fun deleteFrame(
        frameId: String,
        onSuccess: () -> Unit = {},
        onError: (Exception) -> Unit = {}
    ) {
        val firestore = getFirestore() ?: return
        firestore.collection("admin_frames").document(frameId)
            .delete()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it) }
    }

    // Publishing by Admin
    fun publishFeedItem(
        item: FeedItem,
        onSuccess: () -> Unit = {},
        onError: (Exception) -> Unit = {}
    ) {
        val firestore = getFirestore() ?: return
        val docData = hashMapOf(
            "id" to item.id,
            "titleHi" to item.titleHi,
            "titleEn" to item.titleEn,
            "categoryHi" to item.categoryHi,
            "categoryEn" to item.categoryEn,
            "captionHi" to item.captionHi,
            "captionEn" to item.captionEn,
            "type" to item.type.name,
            "timestampHi" to item.timestampHi,
            "timestampEn" to item.timestampEn,
            "likesCount" to item.likesCount,
            "commentsCount" to item.commentsCount,
            "sharesCount" to item.sharesCount,
            "isFeatured" to item.isFeatured,
            "posterHeadlineHi" to item.posterHeadlineHi,
            "posterSubHeadlineHi" to item.posterSubHeadlineHi,
            "facebookAuthor" to item.facebookAuthor,
            "facebookTimeHi" to item.facebookTimeHi,
            "facebookContentHi" to item.facebookContentHi,
            "imageUrl" to (item.imageUrl ?: ""),
            "photoCollageCount" to item.photoCollageCount,
            "facebookUrl" to item.facebookUrl,
            "isEnabled" to item.isEnabled,
            "timestamp" to System.currentTimeMillis()
        )
        firestore.collection("feed_items").document(item.id)
            .set(docData)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it) }
    }

    fun publishNotice(
        notice: ImportantNotice,
        onSuccess: () -> Unit = {},
        onError: (Exception) -> Unit = {}
    ) {
        val firestore = getFirestore() ?: return
        val docData = hashMapOf(
            "id" to notice.id,
            "titleHi" to notice.titleHi,
            "titleEn" to notice.titleEn,
            "dateHi" to notice.dateHi,
            "dateEn" to notice.dateEn,
            "bodyHi" to notice.bodyHi,
            "isUrgent" to notice.isUrgent,
            "viewsCount" to notice.viewsCount,
            "likesCount" to notice.likesCount,
            "agreeCount" to notice.agreeCount,
            "photoAttached" to notice.photoAttached,
            "photoTitle" to notice.photoTitle,
            "photos" to notice.photos,
            "timestamp" to System.currentTimeMillis()
        )
        firestore.collection("notices").document(notice.id)
            .set(docData)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it) }
    }

    fun publishProgram(
        program: ProgramItem,
        onSuccess: () -> Unit = {},
        onError: (Exception) -> Unit = {}
    ) {
        val firestore = getFirestore() ?: return
        val docData = hashMapOf(
            "id" to program.id,
            "titleHi" to program.titleHi,
            "titleEn" to program.titleEn,
            "dateHi" to program.dateHi,
            "dateEn" to program.dateEn,
            "timeHi" to program.timeHi,
            "timeEn" to program.timeEn,
            "venueHi" to program.venueHi,
            "venueEn" to program.venueEn,
            "instructionsHi" to program.instructionsHi,
            "isHighlighted" to program.isHighlighted,
            "attendingCount" to program.attendingCount,
            "maybeCount" to program.maybeCount,
            "cantCount" to program.cantCount,
            "timestamp" to System.currentTimeMillis()
        )
        firestore.collection("programs").document(program.id)
            .set(docData)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it) }
    }

    fun publishVideo(
        video: VideoFeedItem,
        onSuccess: () -> Unit = {},
        onError: (Exception) -> Unit = {}
    ) {
        val firestore = getFirestore() ?: return
        val docData = hashMapOf(
            "id" to video.id,
            "titleHi" to video.titleHi,
            "titleEn" to video.titleEn,
            "captionHi" to video.captionHi,
            "captionEn" to video.captionEn,
            "duration" to video.duration,
            "timestampHi" to video.timestampHi,
            "timestampEn" to video.timestampEn,
            "likesCount" to video.likesCount,
            "commentsCount" to video.commentsCount,
            "sharesCount" to video.sharesCount,
            "aspectRatio" to video.aspectRatio,
            "jacketTemplateName" to (video.jacketTemplateName ?: ""),
            "categories" to video.categories,
            "requiresJacket" to video.requiresJacket,
            "videoUri" to (video.videoUri ?: ""),
            "timestamp" to System.currentTimeMillis()
        )
        firestore.collection("videos").document(video.id)
            .set(docData)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it) }
    }

    fun deleteFeedItem(
        id: String,
        onSuccess: () -> Unit = {},
        onError: (Exception) -> Unit = {}
    ) {
        val firestore = getFirestore() ?: return
        firestore.collection("feed_items").document(id)
            .delete()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it) }
    }

    fun deleteNotice(
        id: String,
        onSuccess: () -> Unit = {},
        onError: (Exception) -> Unit = {}
    ) {
        val firestore = getFirestore() ?: return
        firestore.collection("notices").document(id)
            .delete()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it) }
    }

    fun deleteProgram(
        id: String,
        onSuccess: () -> Unit = {},
        onError: (Exception) -> Unit = {}
    ) {
        val firestore = getFirestore() ?: return
        firestore.collection("programs").document(id)
            .delete()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it) }
    }

    fun submitCitizenRequest(
        request: CitizenRequest,
        onSuccess: () -> Unit = {},
        onError: (Exception) -> Unit = {}
    ) {
        val firestore = getFirestore() ?: return
        val docData = hashMapOf(
            "id" to request.id,
            "categoryHi" to request.categoryHi,
            "categoryEn" to request.categoryEn,
            "message" to request.message,
            "dateHi" to request.dateHi,
            "status" to request.status.name,
            "adminResponseHi" to request.adminResponseHi,
            "attachmentName" to request.attachmentName,
            "timestamp" to System.currentTimeMillis()
        )
        firestore.collection("citizen_requests").document(request.id)
            .set(docData)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it) }
    }

    // ==========================================
    // 2.1 REAL-TIME LIVE MEETING SYNC
    // ==========================================

    fun observeMeetingInfo(): Flow<MeetingInfo?> = callbackFlow {
        val firestore = getFirestore()
        if (firestore == null) {
            trySend(null)
            close()
            return@callbackFlow
        }

        val listener = firestore.collection("app_config").document("live_meeting")
            .addSnapshotListener { doc, error ->
                if (error != null) {
                    Log.w(TAG, "Listen failed for live_meeting: ${error.message}")
                    return@addSnapshotListener
                }

                if (doc != null && doc.exists()) {
                    try {
                        val statusStr = doc.getString("status") ?: MeetingStatus.UPCOMING.name
                        val meetingStatus = try {
                            MeetingStatus.valueOf(statusStr)
                        } catch (e: Exception) {
                            MeetingStatus.UPCOMING
                        }

                        val info = MeetingInfo(
                            id = doc.getString("id") ?: "live_meet_curr",
                            titleHi = doc.getString("titleHi") ?: "विधानसभा समन्वय बैठक",
                            titleEn = doc.getString("titleEn") ?: "Assembly Coordination Meeting",
                            dateHi = doc.getString("dateHi") ?: "",
                            timeHi = doc.getString("timeHi") ?: "",
                            hostHi = doc.getString("hostHi") ?: "माननीय नितेंद्र सिंह राठौर",
                            agendaHi = doc.getString("agendaHi") ?: "",
                            participantCount = (doc.getLong("participantCount") ?: 42L).toInt(),
                            status = meetingStatus,
                            scheduledTimeMillis = doc.getLong("scheduledTimeMillis") ?: (System.currentTimeMillis() + 15 * 60 * 1000L),
                            meetingUrl = doc.getString("meetingUrl"),
                            isScheduled = doc.getBoolean("isScheduled") ?: false
                        )
                        trySend(info)
                    } catch (e: Exception) {
                        Log.e(TAG, "Error parsing live_meeting: ${e.message}")
                    }
                } else {
                    // Document doesn't exist or is cleared
                    trySend(null)
                }
            }

        awaitClose { listener.remove() }
    }

    fun publishMeetingInfo(
        meetingInfo: MeetingInfo,
        onSuccess: () -> Unit = {},
        onError: (Exception) -> Unit = {}
    ) {
        val firestore = getFirestore() ?: return
        val docData = hashMapOf(
            "id" to meetingInfo.id,
            "titleHi" to meetingInfo.titleHi,
            "titleEn" to meetingInfo.titleEn,
            "dateHi" to meetingInfo.dateHi,
            "timeHi" to meetingInfo.timeHi,
            "hostHi" to meetingInfo.hostHi,
            "agendaHi" to meetingInfo.agendaHi,
            "participantCount" to meetingInfo.participantCount,
            "status" to meetingInfo.status.name,
            "scheduledTimeMillis" to meetingInfo.scheduledTimeMillis,
            "meetingUrl" to (meetingInfo.meetingUrl ?: ""),
            "isScheduled" to meetingInfo.isScheduled,
            "updatedAt" to System.currentTimeMillis()
        )

        firestore.collection("app_config").document("live_meeting")
            .set(docData)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it) }
    }

    // ==========================================
    // 3. FIREBASE CLOUD STORAGE
    // ==========================================

    fun uploadMediaFile(
        fileUri: Uri,
        storagePath: String,
        onProgress: (Float) -> Unit = {},
        onSuccess: (downloadUrl: String) -> Unit,
        onError: (Exception) -> Unit
    ) {
        val storage = getStorage()
        if (storage == null) {
            onError(IllegalStateException("Firebase Storage अनुपलब्ध है।"))
            return
        }

        val ref = storage.reference.child(storagePath)
        val uploadTask = ref.putFile(fileUri)

        uploadTask.addOnProgressListener { taskSnapshot ->
            val progress = (100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount).toFloat()
            onProgress(progress)
        }.addOnSuccessListener {
            ref.downloadUrl.addOnSuccessListener { uri ->
                onSuccess(uri.toString())
            }.addOnFailureListener { e ->
                onError(e)
            }
        }.addOnFailureListener { e ->
            onError(e)
        }
    }

    /**
     * Upload an image to Firebase Storage with intelligent client-side compression.
     * Downscales large camera photos to optimal dimensions and applies JPEG compression,
     * drastically reducing bandwidth usage and transfer times.
     */
    fun uploadOptimizedImage(
        context: Context,
        imageUri: Uri,
        storagePath: String,
        preset: ImageCompressionPreset = ImageCompressionPreset.HD_OPTIMIZED,
        onCompressionProgress: (String) -> Unit = {},
        onUploadProgress: (Float) -> Unit = {},
        onSuccess: (downloadUrl: String, result: CompressionResult) -> Unit,
        onError: (Exception) -> Unit
    ) {
        CoroutineScope(Dispatchers.Main).launch {
            onCompressionProgress("क्लाइंट-साइड छवि ऑप्टिमाइज़ेशन प्रगति पर है...")
            val compressionResult = ImageCompressionUtil.compressImage(context, imageUri, preset)

            compressionResult.fold(
                onSuccess = { result ->
                    onCompressionProgress(result.summaryTextHi)
                    val storage = getStorage()
                    if (storage == null) {
                        // In local / offline mode, return compressed file URI directly
                        Log.d(TAG, "Storage null, using local compressed URI: ${result.summaryTextHi}")
                        onSuccess(result.compressedUri.toString(), result)
                        return@launch
                    }

                    try {
                        val metadata = StorageMetadata.Builder()
                            .setContentType("image/jpeg")
                            .setCustomMetadata("originalSize", result.originalSizeBytes.toString())
                            .setCustomMetadata("compressedSize", result.compressedSizeBytes.toString())
                            .setCustomMetadata("bandwidthSaved", "${result.savedPercentage}%")
                            .setCustomMetadata("preset", preset.name)
                            .build()

                        val ref = storage.reference.child(storagePath)
                        val uploadTask = ref.putFile(result.compressedUri, metadata)

                        uploadTask.addOnProgressListener { taskSnapshot ->
                            val progress = if (taskSnapshot.totalByteCount > 0) {
                                (100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount).toFloat()
                            } else 0f
                            onUploadProgress(progress)
                        }.addOnSuccessListener {
                            ref.downloadUrl.addOnSuccessListener { uri ->
                                onSuccess(uri.toString(), result)
                            }.addOnFailureListener {
                                onSuccess(result.compressedUri.toString(), result)
                            }
                        }.addOnFailureListener { e ->
                            onError(e)
                        }
                    } catch (e: Exception) {
                        onError(e)
                    }
                },
                onFailure = { err ->
                    onError(Exception("इमेज कंप्रेशन विफल: ${err.localizedMessage}", err))
                }
            )
        }
    }
}
