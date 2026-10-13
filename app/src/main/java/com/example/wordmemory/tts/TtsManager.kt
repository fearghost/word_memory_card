package com.example.wordmemory.tts

interface TtsManager {
    fun speak(text: String)
    fun setAccent(locale: java.util.Locale)
}
