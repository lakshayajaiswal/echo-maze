package com.example.audio

import android.content.Context
import android.speech.tts.TextToSpeech
import java.util.Locale

class SpeechSynthesizer(context: Context) : TextToSpeech.OnInitListener {
    private var tts: TextToSpeech? = null
    private var isReady = false
    private var speechRate = 1.35f
    private var enabled = true

    init {
        tts = TextToSpeech(context.applicationContext, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts?.language = Locale.US
            tts?.setSpeechRate(speechRate)
            tts?.setPitch(1.05f)
            isReady = true
        }
    }

    fun setSpeechRate(rate: Float) {
        this.speechRate = rate.coerceIn(0.8f, 2.0f)
        if (isReady) {
            tts?.setSpeechRate(speechRate)
        }
    }

    fun setEnabled(enabled: Boolean) {
        this.enabled = enabled
        if (!enabled) {
            tts?.stop()
        }
    }

    fun speak(text: String, priority: Boolean = false) {
        if (!enabled || !isReady) return
        val queueMode = if (priority) TextToSpeech.QUEUE_FLUSH else TextToSpeech.QUEUE_ADD
        tts?.speak(text, queueMode, null, "EchoRunner_${System.currentTimeMillis()}")
    }

    fun stop() {
        tts?.stop()
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
        tts = null
        isReady = false
    }
}
