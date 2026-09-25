package com.example.sensor

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import java.util.Locale

class VoiceCommandManager(
    private val context: Context,
    private val onCommandReceived: (String) -> Unit
) {
    private var speechRecognizer: SpeechRecognizer? = null
    var isListening: Boolean = false
        private set
    var isSupported: Boolean = SpeechRecognizer.isRecognitionAvailable(context)

    fun startListening() {
        if (!isSupported) return
        stopListening()

        try {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
                setRecognitionListener(object : RecognitionListener {
                    override fun onReadyForSpeech(params: Bundle?) {
                        isListening = true
                    }

                    override fun onBeginningOfSpeech() {}
                    override fun onRmsChanged(rmsdB: Float) {}
                    override fun onBufferReceived(buffer: ByteArray?) {}
                    override fun onEndOfSpeech() {
                        isListening = false
                    }

                    override fun onError(error: Int) {
                        isListening = false
                        // Automatically restart listener if permitted and active
                        if (autoRestart) {
                            startListening()
                        }
                    }

                    override fun onResults(results: Bundle?) {
                        isListening = false
                        val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        matches?.firstOrNull()?.let { spokenText ->
                            processCommand(spokenText.uppercase(Locale.US))
                        }
                        if (autoRestart) {
                            startListening()
                        }
                    }

                    override fun onPartialResults(partialResults: Bundle?) {
                        val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        matches?.firstOrNull()?.let { spokenText ->
                            processCommand(spokenText.uppercase(Locale.US))
                        }
                    }

                    override fun onEvent(eventType: Int, params: Bundle?) {}
                })
            }

            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.US.toString())
                putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
            }

            speechRecognizer?.startListening(intent)
            isListening = true
        } catch (e: Exception) {
            Log.w("VoiceCommandManager", "Failed to start speech recognizer", e)
            isListening = false
        }
    }

    private var autoRestart: Boolean = true

    fun stopListening() {
        autoRestart = false
        try {
            speechRecognizer?.stopListening()
            speechRecognizer?.cancel()
            speechRecognizer?.destroy()
        } catch (e: Exception) {
            // Ignored
        }
        speechRecognizer = null
        isListening = false
    }

    fun enableAutoRestart(enable: Boolean) {
        autoRestart = enable
    }

    private fun processCommand(text: String) {
        when {
            text.contains("PAUSE") || text.contains("STOP") || text.contains("FREEZE") || text.contains("HOLD") -> {
                onCommandReceived("PAUSE")
            }
            text.contains("RESUME") || text.contains("START") || text.contains("CONTINUE") || text.contains("GO") -> {
                onCommandReceived("RESUME")
            }
            text.contains("LEFT") || text.contains("WEST") -> {
                onCommandReceived("LEFT")
            }
            text.contains("RIGHT") || text.contains("EAST") -> {
                onCommandReceived("RIGHT")
            }
            text.contains("JUMP") || text.contains("UP") || text.contains("HOP") -> {
                onCommandReceived("JUMP")
            }
            text.contains("SLIDE") || text.contains("DOWN") || text.contains("DUCK") -> {
                onCommandReceived("SLIDE")
            }
        }
    }
}
