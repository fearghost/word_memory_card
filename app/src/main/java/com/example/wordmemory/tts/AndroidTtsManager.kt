package com.example.wordmemory.tts

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
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
    override val isSpeaking = MutableStateFlow(false)

    private val utteranceId = "study_card"

    init {
        tts = TextToSpeech(context, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            isInitialized = true
            
            // Set up the progress listener to track when the engine is actually speaking
            tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    isSpeaking.value = true
                }

                override fun onDone(utteranceId: String?) {
                    isSpeaking.value = false
                }

                override fun onError(utteranceId: String?) {
                    isSpeaking.value = false
                }
            })

            pendingLocale?.let { locale ->
                tts?.language = locale
                pendingLocale = null
            }

            scope.launch {
                delay(2000) 
                isWarmedUp = true
                isReady.value = true
            }
        }
    }

    override fun speak(text: String) {
        if (isInitialized) {
            // Stop any ongoing speech to prevent overlap
            tts?.stop()
            // Use the specific utteranceId to allow tracking via UtteranceProgressListener
            // We pass null for the Bundle parameter to use the 4-argument version
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
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
