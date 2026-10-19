package com.example.wordmemory.tts

import android.content.Context
import android.speech.tts.TextToSpeech
import com.example.wordmemory.tts.TtsManager
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.Locale

class AndroidTtsManager(context: Context) : TtsManager, TextToSpeech.OnInitListener {
    private var tts: TextToSpeech? = null
    private var isInitialized = false
    private var isWarmedUp = false
    private var pendingLocale: Locale? = null
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    override val isReady = MutableStateFlow(false)

    init {
        tts = TextToSpeech(context, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            isInitialized = true
            
            // Apply any locale that was requested before initialization
            pendingLocale?.let { locale ->
                tts?.language = locale
                pendingLocale = null
            }

            // The engine is "initialized" but might not be "warmed up" for the first playback.
            // We use a delay to ensure the engine is stable and the language is applied.
            scope.launch {
                delay(2000) 
                isWarmedUp = true
                isReady.value = true
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