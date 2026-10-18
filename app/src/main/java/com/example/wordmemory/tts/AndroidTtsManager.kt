package com.example.wordmemory.tts

import android.content.Context
import android.speech.tts.TextToSpeech
import com.example.wordmemory.tts.TtsManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.Locale

class AndroidTtsManager(context: Context) : TtsManager, TextToSpeech.OnInitListener {
    private var tts: TextToSpeech? = null
    private var isInitialized = false
    private var pendingLocale: Locale? = null

    override val isReady = MutableStateFlow(false)

    init {
        tts = TextToSpeech(context, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            isInitialized = true
            isReady.value = true
            pendingLocale?.let { locale ->
                tts?.language = locale
            }
        }
    }

    override fun speak(text: String) {
        if (isInitialized) {
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
        }
    }

    override fun setAccent(locale: Locale) {
        if (isInitialized) {
            tts?.language = locale
        } else {
            pendingLocale = locale
        }
    }
}
}
