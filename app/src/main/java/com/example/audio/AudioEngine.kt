package com.example.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import com.example.model.ObstacleType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.sin

class AudioEngine {
    private val scope = CoroutineScope(Dispatchers.Default)
    private val sampleRate = 44100
    private var volume = 0.8f

    fun setVolume(vol: Float) {
        this.volume = vol.coerceIn(0f, 1f)
    }

    /**
     * Plays a synthetic spatial ping when player shifts lane.
     * Left lane: panned hard left (380Hz)
     * Right lane: panned hard right (580Hz)
     * Center lane: balanced (480Hz)
     */
    fun playSpatialLanePing(lane: Int) {
        val freq = when (lane) {
            -1 -> 380f
            1 -> 580f
            else -> 480f
        }
        val pan = when (lane) {
            -1 -> -0.9f
            1 -> 0.9f
            else -> 0f
        }
        playTone(
            freqStart = freq,
            freqEnd = freq,
            durationMs = 90,
            pan = pan,
            gain = 0.35f * volume
        )
    }

    /**
     * Plays pre-obstacle audio cue with spatial pan and distinct frequency:
     * LOW_BARRIER (Jump): 780Hz -> 820Hz high frequency blip
     * HIGH_BEAM (Slide): 320Hz -> 300Hz low hum
     * WALL (Shift): 540Hz two-tone pulse
     */
    fun playPreObstacleWarningTone(lane: Int, obsType: ObstacleType) {
        val baseFreq = when (obsType) {
            ObstacleType.LOW_BARRIER -> 780f
            ObstacleType.HIGH_BEAM -> 320f
            ObstacleType.WALL -> 540f
        }
        val pan = when (lane) {
            -1 -> -0.9f
            1 -> 0.9f
            else -> 0f
        }
        scope.launch {
            playTone(baseFreq, baseFreq * 1.15f, durationMs = 70, pan = pan, gain = 0.4f * volume)
            kotlinx.coroutines.delay(60)
            playTone(baseFreq * 1.25f, baseFreq * 1.35f, durationMs = 90, pan = pan, gain = 0.45f * volume)
        }
    }

    fun playJumpSound() {
        playTone(320f, 880f, durationMs = 180, pan = 0f, gain = 0.45f * volume)
    }

    fun playSlideSound() {
        playTone(260f, 110f, durationMs = 190, pan = 0f, gain = 0.4f * volume)
    }

    fun playCrashSound() {
        playTone(190f, 50f, durationMs = 260, pan = 0f, gain = 0.6f * volume)
    }

    fun playPauseTone() {
        playTone(620f, 240f, durationMs = 220, pan = 0f, gain = 0.45f * volume)
    }

    fun playResumeTone() {
        playTone(240f, 680f, durationMs = 220, pan = 0f, gain = 0.45f * volume)
    }

    fun playVictoryFanfare() {
        scope.launch {
            val notes = listOf(440f, 554f, 659f, 880f)
            for (freq in notes) {
                playTone(freq, freq, durationMs = 110, pan = 0f, gain = 0.45f * volume)
                kotlinx.coroutines.delay(90)
            }
        }
    }

    /**
     * Synthesizes and writes a stereo PCM audio buffer to AudioTrack on a background thread.
     */
    private fun playTone(
        freqStart: Float,
        freqEnd: Float,
        durationMs: Int,
        pan: Float, // -1.0 (left) to 1.0 (right)
        gain: Float
    ) {
        scope.launch {
            try {
                val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
                if (numSamples <= 0) return@launch

                val buffer = ShortArray(numSamples * 2) // Stereo (L, R interleaved)
                val leftGain = ((1f - pan) / 2f).coerceIn(0.05f, 1f) * gain
                val rightGain = ((1f + pan) / 2f).coerceIn(0.05f, 1f) * gain

                var phase = 0.0
                for (i in 0 until numSamples) {
                    val progress = i.toFloat() / numSamples
                    val currentFreq = freqStart + (freqEnd - freqStart) * progress

                    // Linear attack & exponential decay envelope
                    val envelope = if (progress < 0.1f) {
                        progress / 0.1f
                    } else {
                        (1f - progress).coerceAtLeast(0f)
                    }

                    phase += 2.0 * PI * currentFreq / sampleRate
                    val sample = sin(phase).toFloat() * envelope

                    val leftVal = (sample * leftGain * Short.MAX_VALUE).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt())
                    val rightVal = (sample * rightGain * Short.MAX_VALUE).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt())

                    buffer[i * 2] = leftVal.toShort()
                    buffer[i * 2 + 1] = rightVal.toShort()
                }

                val minBufferSize = AudioTrack.getMinBufferSize(
                    sampleRate,
                    AudioFormat.CHANNEL_OUT_STEREO,
                    AudioFormat.ENCODING_PCM_16BIT
                )

                val audioTrack = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_GAME)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(sampleRate)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_STEREO)
                            .build()
                    )
                    .setBufferSizeInBytes(buffer.size * 2.coerceAtLeast(minBufferSize))
                    .setTransferMode(AudioTrack.MODE_STATIC)
                    .build()

                audioTrack.write(buffer, 0, buffer.size)
                audioTrack.play()

                kotlinx.coroutines.delay(durationMs.toLong() + 50)
                audioTrack.stop()
                audioTrack.release()
            } catch (e: Exception) {
                // Silently recover if audio device is unavailable
            }
        }
    }
}
