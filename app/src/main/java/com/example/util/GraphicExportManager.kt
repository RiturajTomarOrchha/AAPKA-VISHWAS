package com.example.util

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.core.content.res.ResourcesCompat
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.example.R
import com.example.model.PosterIdentityConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

/**
 * High-Performance Graphic Frame & Poster Engine for TEAM RATHORE.
 * Renders 1080 x 1350 px (4:5 social media standard) graphics adhering strictly to the
 * graphic frame editing rules:
 * - 4:5 Aspect ratio (1080 × 1350 px)
 * - Bottom-Left Circular User Photo mask (never distorted, face preserved)
 * - Strict User Name & Designation zones (Navy Curved or Red Ribbon style)
 * - Congress Party Hand Logo
 * - NO location rendered on the graphic (Database only)
 * - Real gallery save (MediaStore) and instant image sharing via FileProvider
 */
object GraphicExportManager {

    private const val POSTER_WIDTH = 1080
    private const val POSTER_HEIGHT = 1350

    suspend fun renderAndSavePoster(
        context: Context,
        config: PosterIdentityConfig,
        customHeadline: String,
        customSubHeadline: String,
        userName: String,
        userDesignation: String,
        userMobile: String
    ): Uri? = withContext(Dispatchers.IO) {
        val bitmap = renderPosterBitmap(
            context = context,
            config = config,
            customHeadline = customHeadline,
            customSubHeadline = customSubHeadline,
            userName = userName,
            userDesignation = userDesignation,
            userMobile = userMobile
        )
        val savedUri = saveBitmapToGallery(context, bitmap, "TeamRathore_${System.currentTimeMillis()}")
        withContext(Dispatchers.Main) {
            if (savedUri != null) {
                Toast.makeText(
                    context,
                    "✓ ग्राफ़िक सफलतापूर्वक आपकी गैलरी में (1080×1350 HD) सेव हो गया!",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                Toast.makeText(
                    context,
                    "ग्राफ़िक सेव करने में समस्या आई, कृपया पुनः प्रयास करें।",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        savedUri
    }

    suspend fun sharePoster(
        context: Context,
        config: PosterIdentityConfig,
        customHeadline: String,
        customSubHeadline: String,
        userName: String,
        userDesignation: String,
        userMobile: String
    ) = withContext(Dispatchers.IO) {
        val bitmap = renderPosterBitmap(
            context = context,
            config = config,
            customHeadline = customHeadline,
            customSubHeadline = customSubHeadline,
            userName = userName,
            userDesignation = userDesignation,
            userMobile = userMobile
        )

        val cacheDir = File(context.cacheDir, "images").apply { mkdirs() }
        val shareFile = File(cacheDir, "share_team_rathore_${System.currentTimeMillis()}.png")
        FileOutputStream(shareFile).use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }

        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            shareFile
        )

        withContext(Dispatchers.Main) {
            val captionText = buildString {
                appendLine("टीम राठौर • $customHeadline")
                if (customSubHeadline.isNotBlank()) appendLine(customSubHeadline)
                appendLine()
                appendLine("— $userName ($userDesignation)")
                appendLine("#टीम_राठौर #हेमंत_राठौर #निवाड़ी #पृथ्वीपुर")
            }

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "image/png"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_TEXT, captionText)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(shareIntent, "ग्राफ़िक शेयर करें"))
        }
    }

    suspend fun renderPosterBitmap(
        context: Context,
        config: PosterIdentityConfig,
        customHeadline: String,
        customSubHeadline: String,
        userName: String,
        userDesignation: String,
        userMobile: String
    ): Bitmap = withContext(Dispatchers.IO) {
        val bitmap = Bitmap.createBitmap(POSTER_WIDTH, POSTER_HEIGHT, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // 1. Draw Background
        drawBackground(context, canvas, config)

        // 2. Draw Main Content / Photo (if custom main photo chosen)
        if (!config.customMainPhotoUri.isNullOrBlank()) {
            val contentBitmap = loadBitmapFromUri(context, config.customMainPhotoUri)
            if (contentBitmap != null) {
                drawContentImage(canvas, contentBitmap)
            }
        } else {
            // Draw default creative campaign artwork / typography in center
            drawCenterHeadline(canvas, customHeadline, customSubHeadline)
        }

        // 3. Draw Header (Leaders or Congress Header)
        if (!config.selectedFrame?.headerImageUri.isNullOrBlank()) {
            val headerBitmap = loadBitmapFromUri(context, config.selectedFrame?.headerImageUri)
            if (headerBitmap != null) {
                val dstRect = Rect(0, 0, POSTER_WIDTH, (POSTER_HEIGHT * 0.18f).toInt())
                canvas.drawBitmap(headerBitmap, null, dstRect, Paint(Paint.ANTI_ALIAS_FLAG))
            }
        } else {
            drawDefaultHeader(canvas)
        }

        // 4. Draw Footer Template
        val isRedRibbon = config.selectedFrame?.templateStyle == "RED_RIBBON" || config.templateIndex == 1
        val customFooterUri = config.selectedFrame?.footerImageUri

        if (!customFooterUri.isNullOrBlank()) {
            val footerBitmap = loadBitmapFromUri(context, customFooterUri)
            if (footerBitmap != null) {
                val footerH = (POSTER_HEIGHT * 0.28f).toInt()
                val dstRect = Rect(0, POSTER_HEIGHT - footerH, POSTER_WIDTH, POSTER_HEIGHT)
                canvas.drawBitmap(footerBitmap, null, dstRect, Paint(Paint.ANTI_ALIAS_FLAG))
            }
        } else if (isRedRibbon) {
            drawRedRibbonFooter(canvas)
        } else {
            drawNavyCurvedFooter(canvas)
        }

        // 5. Draw Optional Full PNG Frame Overlay if provided
        if (!config.selectedFrame?.fullFrameUri.isNullOrBlank()) {
            val fullFrameBitmap = loadBitmapFromUri(context, config.selectedFrame?.fullFrameUri)
            if (fullFrameBitmap != null) {
                val fullRect = Rect(0, 0, POSTER_WIDTH, POSTER_HEIGHT)
                canvas.drawBitmap(fullFrameBitmap, null, fullRect, Paint(Paint.ANTI_ALIAS_FLAG))
            }
        }

        // 6. Draw User Photo - Dynamic AI slot coordinates with fallback
        if (config.showPhoto) {
            val userPhotoBitmap = if (!config.userPhotoUri.isNullOrBlank()) {
                loadBitmapFromUri(context, config.userPhotoUri)
            } else null

            val frame = config.selectedFrame
            val circleCenterX = if (frame != null) frame.photoCenterX * POSTER_WIDTH else 195f
            val circleCenterY = if (frame != null) frame.photoCenterY * POSTER_HEIGHT else 1175f
            val circleRadius = if (frame != null) frame.photoRadius * POSTER_WIDTH else 145f

            drawUserPhotoCircle(canvas, userPhotoBitmap, circleCenterX, circleCenterY, circleRadius, isRedRibbon)
        }

        val frame = config.selectedFrame
        val parsedTextColor = try {
            if (frame != null) android.graphics.Color.parseColor(frame.textColorHex) else android.graphics.Color.WHITE
        } catch (_: Exception) {
            android.graphics.Color.WHITE
        }
        val parsedDesigColor = try {
            if (frame != null) android.graphics.Color.parseColor(frame.desigColorHex) else android.graphics.Color.parseColor("#E2E8F0")
        } catch (_: Exception) {
            android.graphics.Color.parseColor("#E2E8F0")
        }

        // 7. Draw User Name Zone (Dynamic AI slot coordinates & styling)
        if (config.showName) {
            val nameX = if (frame != null) frame.nameX * POSTER_WIDTH else 380f
            val nameY = if (frame != null) frame.nameY * POSTER_HEIGHT else (if (isRedRibbon) 1115f else 1120f)
            val nameMaxWidth = (POSTER_WIDTH - nameX - 160f).coerceAtLeast(200f)
            drawUserNameText(
                canvas = canvas,
                name = userName,
                x = nameX,
                y = nameY,
                maxWidth = nameMaxWidth,
                isRedRibbon = isRedRibbon,
                textColor = parsedTextColor,
                fontFamily = frame?.nameFontFamily ?: "DEFAULT_BOLD",
                fontWeight = frame?.nameFontWeight ?: "BOLD",
                fontSizeSp = frame?.nameFontSizeSp ?: 36f
            )
        }

        // 8. Draw Designation & Mobile Zone (Dynamic AI slot coordinates & styling)
        if (config.showDesignation) {
            val desigText = buildString {
                append(userDesignation)
                if (config.showMobile && userMobile.isNotBlank()) {
                    append(" | ")
                    append(userMobile)
                }
            }
            val desigX = if (frame != null) frame.desigX * POSTER_WIDTH else 380f
            val desigY = if (frame != null) frame.desigY * POSTER_HEIGHT else 1195f
            drawDesignationText(
                canvas = canvas,
                desigText = desigText,
                x = desigX,
                y = desigY,
                isRedRibbon = isRedRibbon,
                textColor = parsedDesigColor,
                fontFamily = frame?.desigFontFamily ?: "SANS_SERIF",
                fontWeight = frame?.desigFontWeight ?: "MEDIUM",
                fontSizeSp = frame?.desigFontSizeSp ?: 24f
            )
        }

        // 9. Draw Congress Hand Logo at Bottom-Right (Only for built-in templates so custom footers stay pristine)
        if (customFooterUri.isNullOrBlank()) {
            drawCongressLogo(context, canvas, 895f, 1070f, 135f)
        }

        bitmap
    }

    private fun drawBackground(context: Context, canvas: Canvas, config: PosterIdentityConfig) {
        // Pristine clean white background for custom frames and Template 1 as requested
        canvas.drawColor(android.graphics.Color.WHITE)
        if (config.selectedFrame == null && config.templateIndex != 0) {
            val paint = Paint(Paint.ANTI_ALIAS_FLAG)
            val gradient = LinearGradient(
                0f, 0f, 0f, POSTER_HEIGHT.toFloat(),
                intArrayOf(
                    android.graphics.Color.parseColor("#F8FAFC"),
                    android.graphics.Color.parseColor("#F0FDF4"),
                    android.graphics.Color.parseColor("#FFFBEB")
                ),
                floatArrayOf(0f, 0.45f, 1f),
                Shader.TileMode.CLAMP
            )
            paint.shader = gradient
            canvas.drawRect(0f, 0f, POSTER_WIDTH.toFloat(), POSTER_HEIGHT.toFloat(), paint)

            // Subtle tricolor diagonal glow
            val saffronPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = android.graphics.Color.parseColor("#15F97316")
                style = Paint.Style.FILL
            }
            canvas.drawCircle(POSTER_WIDTH * 0.85f, POSTER_HEIGHT * 0.15f, 320f, saffronPaint)

            val greenPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = android.graphics.Color.parseColor("#1216A34A")
                style = Paint.Style.FILL
            }
            canvas.drawCircle(POSTER_WIDTH * 0.15f, POSTER_HEIGHT * 0.75f, 340f, greenPaint)
        }
    }

    private fun drawContentImage(canvas: Canvas, contentBitmap: Bitmap) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
        val contentTop = (POSTER_HEIGHT * 0.18f).toInt()
        val contentBottom = (POSTER_HEIGHT * 0.76f).toInt()
        val contentHeight = contentBottom - contentTop

        val srcW = contentBitmap.width
        val srcH = contentBitmap.height
        val srcRatio = srcW.toFloat() / srcH.toFloat()
        val targetRatio = POSTER_WIDTH.toFloat() / contentHeight.toFloat()

        val srcRect: Rect
        if (srcRatio > targetRatio) {
            val cropW = (srcH * targetRatio).toInt()
            val left = (srcW - cropW) / 2
            srcRect = Rect(left, 0, left + cropW, srcH)
        } else {
            val cropH = (srcW / targetRatio).toInt()
            val top = (srcH - cropH) / 2
            srcRect = Rect(0, top, srcW, top + cropH)
        }

        val dstRect = Rect(0, contentTop, POSTER_WIDTH, contentBottom)
        canvas.drawBitmap(contentBitmap, srcRect, dstRect, paint)
    }

    private fun drawCenterHeadline(canvas: Canvas, headline: String, subHeadline: String) {
        val titlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = android.graphics.Color.parseColor("#0F172A")
            textSize = 56f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
        }

        val subPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = android.graphics.Color.parseColor("#334155")
            textSize = 34f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            textAlign = Paint.Align.CENTER
        }

        val tagPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = android.graphics.Color.parseColor("#EA580C")
            textSize = 28f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
        }

        // Center card backdrop
        val cardPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = android.graphics.Color.WHITE
            style = Paint.Style.FILL
            setShadowLayer(16f, 0f, 6f, android.graphics.Color.parseColor("#22000000"))
        }
        val cardRect = RectF(70f, 440f, POSTER_WIDTH - 70f, 860f)
        canvas.drawRoundRect(cardRect, 28f, 28f, cardPaint)

        // Accent line on card
        val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = android.graphics.Color.parseColor("#F97316")
            strokeWidth = 6f
            style = Paint.Style.STROKE
        }
        canvas.drawLine(POSTER_WIDTH / 2f - 60f, 490f, POSTER_WIDTH / 2f + 60f, 490f, linePaint)

        canvas.drawText("★ टीम राठौर संदेश ★", POSTER_WIDTH / 2f, 545f, tagPaint)

        // Multi-line wrap for headline
        val wrappedHeadline = wrapText(headline, titlePaint, 860f)
        var textY = 625f
        for (line in wrappedHeadline.take(2)) {
            canvas.drawText(line, POSTER_WIDTH / 2f, textY, titlePaint)
            textY += 66f
        }

        val wrappedSub = wrapText(subHeadline, subPaint, 860f)
        textY += 15f
        for (line in wrappedSub.take(2)) {
            canvas.drawText(line, POSTER_WIDTH / 2f, textY, subPaint)
            textY += 46f
        }
    }

    private fun drawDefaultHeader(canvas: Canvas) {
        // Tricolor top banner strip
        val saffronBar = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = android.graphics.Color.parseColor("#FF7A00")
            style = Paint.Style.FILL
        }
        val whiteBar = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = android.graphics.Color.WHITE
            style = Paint.Style.FILL
        }
        val greenBar = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = android.graphics.Color.parseColor("#138808")
            style = Paint.Style.FILL
        }

        canvas.drawRect(0f, 0f, POSTER_WIDTH.toFloat(), 18f, saffronBar)
        canvas.drawRect(0f, 18f, POSTER_WIDTH.toFloat(), 32f, whiteBar)
        canvas.drawRect(0f, 32f, POSTER_WIDTH.toFloat(), 50f, greenBar)

        // Header Title
        val headerTitlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = android.graphics.Color.parseColor("#1B5E20")
            textSize = 46f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.LEFT
        }
        canvas.drawText("टीम राठौर • जनसेवा ही संकल्प", 60f, 130f, headerTitlePaint)

        val headerSubPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = android.graphics.Color.parseColor("#64748B")
            textSize = 26f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            textAlign = Paint.Align.LEFT
        }
        canvas.drawText("विधानसभा क्षेत्र पृथ्वीपुर (म.प्र.)", 60f, 175f, headerSubPaint)
    }

    private fun drawNavyCurvedFooter(canvas: Canvas) {
        val navyPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = android.graphics.Color.parseColor("#0C2340") // Deep Royal Navy
            style = Paint.Style.FILL
        }

        val footerPath = Path().apply {
            moveTo(0f, 1070f)
            cubicTo(
                POSTER_WIDTH * 0.35f, 1020f,
                POSTER_WIDTH * 0.70f, 1110f,
                POSTER_WIDTH.toFloat(), 1060f
            )
            lineTo(POSTER_WIDTH.toFloat(), POSTER_HEIGHT.toFloat())
            lineTo(0f, POSTER_HEIGHT.toFloat())
            close()
        }
        canvas.drawPath(footerPath, navyPaint)

        // Tricolor top accent rim
        val rimPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = android.graphics.Color.parseColor("#F97316")
            strokeWidth = 6f
            style = Paint.Style.STROKE
        }
        canvas.drawPath(footerPath, rimPaint)
    }

    private fun drawRedRibbonFooter(canvas: Canvas) {
        // Lower navy base
        val navyBase = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = android.graphics.Color.parseColor("#0F172A")
            style = Paint.Style.FILL
        }
        canvas.drawRect(0f, 1145f, POSTER_WIDTH.toFloat(), POSTER_HEIGHT.toFloat(), navyBase)

        // Vivid Red Ribbon strip for Name
        val redRibbonPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = android.graphics.Color.parseColor("#DC2626") // Vivid Red
            style = Paint.Style.FILL
        }
        val ribbonPath = Path().apply {
            moveTo(160f, 1060f)
            lineTo(POSTER_WIDTH.toFloat(), 1060f)
            lineTo(POSTER_WIDTH.toFloat(), 1145f)
            lineTo(160f, 1145f)
            close()
        }
        canvas.drawPath(ribbonPath, redRibbonPaint)

        // Saffron ribbon border line
        val ribbonLine = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = android.graphics.Color.parseColor("#FBBF24")
            strokeWidth = 4f
            style = Paint.Style.STROKE
        }
        canvas.drawLine(160f, 1060f, POSTER_WIDTH.toFloat(), 1060f, ribbonLine)
    }

    private fun drawUserPhotoCircle(
        canvas: Canvas,
        photoBitmap: Bitmap?,
        cx: Float,
        cy: Float,
        radius: Float,
        isRedRibbon: Boolean
    ) {
        // Outer border ring
        val ringColor = if (isRedRibbon) android.graphics.Color.parseColor("#DC2626") else android.graphics.Color.parseColor("#0C2340")
        val ringPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = ringColor
            style = Paint.Style.FILL
            setShadowLayer(12f, 0f, 4f, android.graphics.Color.parseColor("#66000000"))
        }
        canvas.drawCircle(cx, cy, radius + 10f, ringPaint)

        // White separator ring
        val whiteRingPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = android.graphics.Color.WHITE
            style = Paint.Style.FILL
        }
        canvas.drawCircle(cx, cy, radius + 3f, whiteRingPaint)

        // Saffron inner ring
        val saffronRing = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = android.graphics.Color.parseColor("#F97316")
            strokeWidth = 5f
            style = Paint.Style.STROKE
        }
        canvas.drawCircle(cx, cy, radius, saffronRing)

        if (photoBitmap != null) {
            // Circular mask with proper aspect ratio crop
            val circularBitmap = getCroppedCircleBitmap(photoBitmap, (radius * 2).toInt())
            canvas.drawBitmap(circularBitmap, cx - radius, cy - radius, Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG))
        } else {
            // Placeholder avatar
            val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = android.graphics.Color.parseColor("#F1F5F9")
                style = Paint.Style.FILL
            }
            canvas.drawCircle(cx, cy, radius - 2f, bgPaint)

            val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = android.graphics.Color.parseColor("#1B5E20")
                textSize = 58f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                textAlign = Paint.Align.CENTER
            }
            canvas.drawText("फोटो", cx, cy + 20f, textPaint)
        }
    }

    private fun drawUserNameText(
        canvas: Canvas,
        name: String,
        x: Float,
        y: Float,
        maxWidth: Float,
        isRedRibbon: Boolean,
        textColor: Int = android.graphics.Color.WHITE,
        fontFamily: String = "DEFAULT_BOLD",
        fontWeight: String = "BOLD",
        fontSizeSp: Float = 36f
    ) {
        val resolvedTypeface = FrameStyleHelpers.getAndroidTypeface(fontFamily, fontWeight)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = textColor
            typeface = resolvedTypeface
            textAlign = Paint.Align.LEFT
        }

        // Auto-fit font size to avoid overflow (scaling sp into 1080p poster dimension)
        var fontSize = (fontSizeSp * 1.35f).coerceIn(32f, 68f)
        paint.textSize = fontSize
        while (paint.measureText(name) > maxWidth && fontSize > 28f) {
            fontSize -= 2f
            paint.textSize = fontSize
        }

        canvas.drawText(name, x, y, paint)
    }

    private fun drawDesignationText(
        canvas: Canvas,
        desigText: String,
        x: Float,
        y: Float,
        isRedRibbon: Boolean,
        textColor: Int = android.graphics.Color.WHITE,
        fontFamily: String = "SANS_SERIF",
        fontWeight: String = "MEDIUM",
        fontSizeSp: Float = 24f
    ) {
        val defaultDesigColor = if (isRedRibbon) android.graphics.Color.parseColor("#CBD5E1") else android.graphics.Color.parseColor("#E2E8F0")
        val resolvedTypeface = FrameStyleHelpers.getAndroidTypeface(fontFamily, fontWeight)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = if (textColor == android.graphics.Color.WHITE) defaultDesigColor else textColor
            textSize = (fontSizeSp * 1.25f).coerceIn(24f, 44f)
            typeface = resolvedTypeface
            textAlign = Paint.Align.LEFT
        }
        canvas.drawText(desigText, x, y, paint)
    }

    private fun drawCongressLogo(
        context: Context,
        canvas: Canvas,
        x: Float,
        y: Float,
        size: Float
    ) {
        try {
            val drawable = ResourcesCompat.getDrawable(context.resources, R.drawable.ic_congress_logo, null)
            if (drawable != null) {
                // Background circle badge
                val badgePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = android.graphics.Color.WHITE
                    style = Paint.Style.FILL
                    setShadowLayer(8f, 0f, 3f, android.graphics.Color.parseColor("#44000000"))
                }
                val cx = x + size / 2f
                val cy = y + size / 2f
                val radius = size / 2f

                canvas.drawCircle(cx, cy, radius + 4f, badgePaint)

                // Tricolor border for logo badge
                val tricolorPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = android.graphics.Color.parseColor("#138808")
                    strokeWidth = 3f
                    style = Paint.Style.STROKE
                }
                canvas.drawCircle(cx, cy, radius + 3f, tricolorPaint)

                drawable.setBounds(x.toInt(), y.toInt(), (x + size).toInt(), (y + size).toInt())
                drawable.draw(canvas)
            }
        } catch (_: Exception) {
            // Graceful fallback if drawable fails
        }
    }

    private fun getCroppedCircleBitmap(bitmap: Bitmap, diameter: Int): Bitmap {
        val output = Bitmap.createBitmap(diameter, diameter, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)

        val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
        val rect = Rect(0, 0, diameter, diameter)
        val rectF = RectF(rect)

        canvas.drawARGB(0, 0, 0, 0)
        canvas.drawOval(rectF, paint)

        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)

        // Center crop the source bitmap into the square
        val srcW = bitmap.width
        val srcH = bitmap.height
        val minDim = srcW.coerceAtMost(srcH)
        val srcX = (srcW - minDim) / 2
        val srcY = (srcH - minDim) / 2
        val srcRect = Rect(srcX, srcY, srcX + minDim, srcY + minDim)

        canvas.drawBitmap(bitmap, srcRect, rect, paint)
        return output
    }

    private suspend fun loadBitmapFromUri(context: Context, uriStr: String?): Bitmap? = withContext(Dispatchers.IO) {
        if (uriStr.isNullOrBlank()) return@withContext null
        try {
            val uri = Uri.parse(uriStr)
            val loader = ImageLoader(context)
            val request = ImageRequest.Builder(context)
                .data(uri)
                .allowHardware(false)
                .build()
            val result = (loader.execute(request) as? SuccessResult)?.drawable
            (result as? BitmapDrawable)?.bitmap
        } catch (e: Exception) {
            null
        }
    }

    private fun wrapText(text: String, paint: Paint, maxWidth: Float): List<String> {
        val words = text.split(" ")
        val lines = mutableListOf<String>()
        var currentLine = ""

        for (word in words) {
            val candidate = if (currentLine.isEmpty()) word else "$currentLine $word"
            if (paint.measureText(candidate) <= maxWidth) {
                currentLine = candidate
            } else {
                if (currentLine.isNotEmpty()) lines.add(currentLine)
                currentLine = word
            }
        }
        if (currentLine.isNotEmpty()) lines.add(currentLine)
        return lines
    }

    private fun saveBitmapToGallery(context: Context, bitmap: Bitmap, title: String): Uri? {
        val filename = "${title}.png"
        var fos: OutputStream? = null
        var imageUri: Uri? = null

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val resolver = context.contentResolver
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/TeamRathore")
                    put(MediaStore.MediaColumns.IS_PENDING, 1)
                }

                imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                if (imageUri != null) {
                    fos = resolver.openOutputStream(imageUri)
                    if (fos != null) {
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
                    }
                    contentValues.clear()
                    contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
                    resolver.update(imageUri, contentValues, null, null)
                }
            } else {
                val imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString() + "/TeamRathore"
                val file = File(imagesDir).apply { if (!exists()) mkdirs() }
                val imageFile = File(file, filename)
                fos = FileOutputStream(imageFile)
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
                imageUri = Uri.fromFile(imageFile)
                MediaScannerConnection.scanFile(context, arrayOf(imageFile.absolutePath), arrayOf("image/png"), null)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        } finally {
            fos?.close()
        }
        return imageUri
    }
}
