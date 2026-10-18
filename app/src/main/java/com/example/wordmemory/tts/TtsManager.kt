package com.example.wordmemory.tts

import kotlinx.coroutines.flow.StateFlow

interface TtsManager {
    val isReady: StateFlow<Boolean>
    fun speak(text: String)
    fun setAccent(locale: java.util.Locale)
}
