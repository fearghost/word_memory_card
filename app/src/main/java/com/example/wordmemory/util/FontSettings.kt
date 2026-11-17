package com.example.wordmemory.util

import android.content.Context
import android.content.SharedPreferences

/**
 * Global font scale settings persisted via SharedPreferences.
 * Shared across all bundles — one setting for the whole app.
 */
object FontSettings {
    private const val PREFS_NAME = "font_settings"
    private const val KEY_FONT_SCALE_INDEX = "font_scale_index"

    // 6 levels: S(0) / M(1) / L(2) / XL(3) / XXL(4) / XXXL(5)
    const val MIN_INDEX = 0
    const val MAX_INDEX = 5
    const val DEFAULT_INDEX = 4 // default to XXL

    private lateinit var prefs: SharedPreferences

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    /** Persisted font scale index (0–5). Falls back to DEFAULT_INDEX if never set. */
    fun getFontScaleIndex(): Int {
        return prefs.getInt(KEY_FONT_SCALE_INDEX, DEFAULT_INDEX)
    }

    fun setFontScaleIndex(index: Int) {
        val clamped = index.coerceIn(MIN_INDEX, MAX_INDEX)
        prefs.edit().putInt(KEY_FONT_SCALE_INDEX, clamped).apply()
    }

    /** Convert index to multiplier for font size calculations. */
    fun getFontScaleMultiplier(index: Int): Float {
        return when (index) {
            0 -> 1.0f   // S
            1 -> 1.25f  // M
            2 -> 1.5f   // L
            3 -> 1.75f  // XL
            4 -> 2.0f   // XXL
            5 -> 2.5f   // XXXL
            else -> 2.0f // fallback to XXL
        }
    }

    fun getFontLabel(index: Int): String {
        return when (index) {
            0 -> "S"
            1 -> "M"
            2 -> "L"
            3 -> "XL"
            4 -> "XXL"
            5 -> "XXXL"
            else -> "XXL"
        }
    }
}
