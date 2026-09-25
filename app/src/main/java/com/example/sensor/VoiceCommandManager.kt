package com.example.sensor

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import java.util.Locale

class VoiceCommandManager(
    private val context: Context,
    private val onCommandReceived: (String) -> Unit,
    private val onTranscriptUpdated: ((String) -> Unit)? = null
) {
    private val mainHandler = Handler(Looper.getMainLooper())
    private var speechRecognizer: SpeechRecognizer? = null

    var isListening: Boolean = false
        private set

    val isSupported: Boolean
        get() = SpeechRecognizer.isRecognitionAvailable(context)

    private var autoRestart: Boolean = true
    private var isProcessingRestart: Boolean = false
    private var lastRecognizedCommand: String = ""
    private var lastCommandTimestamp: Long = 0L

    fun startListening() {
        if (!isSupported) {
            Log.w("VoiceCommandManager", "Speech recognition not available on this device")
            return
        }

        autoRestart = true

        mainHandler.post {
            try {
                // Safely cancel any active recognizer before creating or starting a new session
                cleanupRecognizer()

                speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
                    setRecognitionListener(createRecognitionListener())
                }

                val intent = createSpeechIntent()
                speechRecognizer?.startListening(intent)
                isListening = true
                Log.d("VoiceCommandManager", "SpeechRecognizer started listening for pause/resume commands")
            } catch (e: Exception) {
                Log.e("VoiceCommandManager", "Failed to start speech recognizer", e)
                isListening = false
                scheduleRestart(500L)
            }
        }
    }

    private fun createSpeechIntent(): Intent {
        return Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            // Free-form speech recognition model for natural spoken commands
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.US.toString())
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, Locale.US.toString())
            putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, context.packageName)

            // Capture multiple candidate hypotheses to guarantee matching "pause" and "resume"
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5)

            // Enable partial results for zero-latency detection (crucial for motor/physical accessibility)
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)

            // Optimized silence detection for rapid command responsiveness
            putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 1500L)
            putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 1200L)
            putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 300L)

            // Prefer offline recognition if available on device for reduced latency
            putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE, true)
        }
    }

    private fun createRecognitionListener(): RecognitionListener {
        return object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                isListening = true
                isProcessingRestart = false
            }

            override fun onBeginningOfSpeech() {
                isListening = true
            }

            override fun onRmsChanged(rmsdB: Float) {}

            override fun onBufferReceived(buffer: ByteArray?) {}

            override fun onEndOfSpeech() {
                isListening = false
            }

            override fun onError(error: Int) {
                isListening = false
                val errorMsg = when (error) {
                    SpeechRecognizer.ERROR_NO_MATCH -> "No match (silence)"
                    SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "Speech timeout"
                    SpeechRecognizer.ERROR_AUDIO -> "Audio recording error"
                    SpeechRecognizer.ERROR_CLIENT -> "Client error"
                    SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Permissions missing"
                    SpeechRecognizer.ERROR_NETWORK -> "Network error"
                    SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network timeout"
                    SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Recognizer busy"
                    SpeechRecognizer.ERROR_SERVER -> "Server error"
                    else -> "Error code $error"
                }
                Log.d("VoiceCommandManager", "Speech recognition notice: $errorMsg")

                // For normal timeout or no match, restart listening after a tiny backoff
                if (autoRestart) {
                    val delayMs = if (error == SpeechRecognizer.ERROR_RECOGNIZER_BUSY) 400L else 120L
                    scheduleRestart(delayMs)
                }
            }

            override fun onResults(results: Bundle?) {
                isListening = false
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                matches?.let { candidates ->
                    checkCandidatesForCommands(candidates)
                }
                if (autoRestart) {
                    scheduleRestart(100L)
                }
            }

            override fun onPartialResults(partialResults: Bundle?) {
                val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                matches?.let { candidates ->
                    checkCandidatesForCommands(candidates)
                }
            }

            override fun onEvent(eventType: Int, params: Bundle?) {}
        }
    }

    private fun scheduleRestart(delayMs: Long) {
        if (!autoRestart || isProcessingRestart) return
        isProcessingRestart = true
        mainHandler.postDelayed({
            if (autoRestart) {
                try {
                    cleanupRecognizer()
                    speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
                        setRecognitionListener(createRecognitionListener())
                    }
                    speechRecognizer?.startListening(createSpeechIntent())
                    isListening = true
                } catch (e: Exception) {
                    Log.w("VoiceCommandManager", "Restart failed, will retry", e)
                } finally {
                    isProcessingRestart = false
                }
            }
        }, delayMs)
    }

    private fun checkCandidatesForCommands(candidates: List<String>) {
        for (candidate in candidates) {
            val upper = candidate.trim().uppercase(Locale.US)
            if (upper.isNotEmpty()) {
                onTranscriptUpdated?.invoke(upper)
            }
            if (processCommand(upper)) {
                break
            }
        }
    }

    private fun processCommand(text: String): Boolean {
        val now = System.currentTimeMillis()

        // Robust command matching with phonetic and synonym support for accessibility
        val isPauseCommand = text.contains("PAUSE") ||
                text.contains("PAWS") || // common phonetic misinterpretation
                text.contains("STOP") ||
                text.contains("FREEZE") ||
                text.contains("WAIT") ||
                text.contains("HOLD") ||
                text.contains("HALT")

        val isResumeCommand = text.contains("RESUME") ||
                text.contains("START") ||
                text.contains("CONTINUE") ||
                text.contains("GO") ||
                text.contains("UNPAUSE") ||
                text.contains("PLAY") ||
                text.contains("RUN") ||
                text.contains("PROCEED")

        if (isPauseCommand) {
            if (now - lastCommandTimestamp > 700L || lastRecognizedCommand != "PAUSE") {
                lastRecognizedCommand = "PAUSE"
                lastCommandTimestamp = now
                onCommandReceived("PAUSE")
                return true
            }
        } else if (isResumeCommand) {
            if (now - lastCommandTimestamp > 700L || lastRecognizedCommand != "RESUME") {
                lastRecognizedCommand = "RESUME"
                lastCommandTimestamp = now
                onCommandReceived("RESUME")
                return true
            }
        } else if (text.contains("LEFT") || text.contains("WEST")) {
            if (now - lastCommandTimestamp > 400L) {
                lastRecognizedCommand = "LEFT"
                lastCommandTimestamp = now
                onCommandReceived("LEFT")
                return true
            }
        } else if (text.contains("RIGHT") || text.contains("EAST")) {
            if (now - lastCommandTimestamp > 400L) {
                lastRecognizedCommand = "RIGHT"
                lastCommandTimestamp = now
                onCommandReceived("RIGHT")
                return true
            }
        } else if (text.contains("JUMP") || text.contains("UP") || text.contains("HOP")) {
            if (now - lastCommandTimestamp > 400L) {
                lastRecognizedCommand = "JUMP"
                lastCommandTimestamp = now
                onCommandReceived("JUMP")
                return true
            }
        } else if (text.contains("SLIDE") || text.contains("DOWN") || text.contains("DUCK")) {
            if (now - lastCommandTimestamp > 400L) {
                lastRecognizedCommand = "SLIDE"
                lastCommandTimestamp = now
                onCommandReceived("SLIDE")
                return true
            }
        }
        return false
    }

    private fun cleanupRecognizer() {
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

    fun stopListening() {
        autoRestart = false
        mainHandler.removeCallbacksAndMessages(null)
        mainHandler.post {
            cleanupRecognizer()
        }
    }
}
