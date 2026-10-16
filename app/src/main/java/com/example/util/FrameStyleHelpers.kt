package com.example.util

import android.graphics.Typeface
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight

object FrameStyleHelpers {

    val FONT_FAMILY_OPTIONS = listOf(
        "DEFAULT_BOLD" to "डिफ़ॉल्ट बोल्ड",
        "SERIF" to "देवनागरी सेरिफ (Serif)",
        "SANS_SERIF" to "मॉडर्न सैन्स (Sans)",
        "MONOSPACE" to "मोनोस्पेस (Monospace)",
        "CURSIVE" to "स्टाइलिश कर्सिव (Cursive)"
    )

    val FONT_WEIGHT_OPTIONS = listOf(
        "NORMAL" to "सामान्य (Normal)",
        "MEDIUM" to "मध्यम (Medium)",
        "BOLD" to "बोल्ड (Bold)",
        "BLACK" to "एक्स्ट्रा बोल्ड (Black)"
    )

    val TEXT_COLOR_OPTIONS = listOf(
        "#FFFFFF" to "सफेद",
        "#FBBF24" to "गोल्डन",
        "#0C2340" to "नेवी ब्लू",
        "#FF6A00" to "केसरिया",
        "#138808" to "तिरंगा हरा",
        "#E2E8F0" to "सिल्वर"
    )

    fun getComposeFontFamily(fontFamilyKey: String?): FontFamily {
        return when (fontFamilyKey?.uppercase()) {
            "SERIF", "ELEGANT_SERIF" -> FontFamily.Serif
            "SANS_SERIF" -> FontFamily.SansSerif
            "MONOSPACE" -> FontFamily.Monospace
            "CURSIVE" -> FontFamily.Cursive
            else -> FontFamily.Default
        }
    }

    fun getComposeFontWeight(fontWeightKey: String?): FontWeight {
        return when (fontWeightKey?.uppercase()) {
            "NORMAL" -> FontWeight.Normal
            "MEDIUM" -> FontWeight.Medium
            "BLACK", "EXTRA_BOLD" -> FontWeight.Black
            else -> FontWeight.Bold
        }
    }

    fun getAndroidTypeface(fontFamilyKey: String?, fontWeightKey: String?): Typeface {
        val baseTypeface = when (fontFamilyKey?.uppercase()) {
            "SERIF", "ELEGANT_SERIF" -> Typeface.SERIF
            "SANS_SERIF" -> Typeface.SANS_SERIF
            "MONOSPACE" -> Typeface.MONOSPACE
            else -> Typeface.DEFAULT
        }
        val isBold = fontWeightKey?.uppercase() in listOf("BOLD", "EXTRA_BOLD", "BLACK")
        val style = if (isBold) Typeface.BOLD else Typeface.NORMAL
        return Typeface.create(baseTypeface, style)
    }
}
