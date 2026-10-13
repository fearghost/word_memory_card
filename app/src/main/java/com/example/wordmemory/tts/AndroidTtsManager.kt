package com.example.wordmemory.tts

import android.content.Context
import android.speech.tts.TextToSpeech
import java.util.Locale

class AndroidTtsManager(context: Context) : TtsManager, TextToSpeech.OnInitListener {
    private var tts: TextToSpeech? = null
    private var isInitialized = false

    init {
        tts = TextToSpeech(context, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            isInitialized = true
        }
    }

    override fun speak(text: String) {
        if (isInitialized) {
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
        }
    }

    override fun setAccent(locale: Locale) {
        if (isInitialized) {
            tts?.language = locale
        }
    }
}
