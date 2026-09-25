package com.example.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlin.math.abs

class TiltDetector(
    context: Context,
    private val onRollLeft: () -> Unit,
    private val onRollRight: () -> Unit,
    private val onRollCenter: () -> Unit,
    private val onPitchForward: () -> Unit, // Duck/Slide
    private val onPitchBack: () -> Unit     // Jump
) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
    private val accelerometer = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    var isEnabled: Boolean = false
    private var calibratedX = 0f
    private var calibratedY = 9.8f
    private var lastActionTime = 0L
    private val actionCooldown = 350L

    fun start() {
        accelerometer?.let {
            sensorManager?.registerListener(this, it, SensorManager.SENSOR_DELAY_GAME)
        }
    }

    fun stop() {
        sensorManager?.unregisterListener(this)
    }

    fun calibrateZeroTilt() {
        // Will calibrate to the next reading
        calibrateNext = true
    }

    private var calibrateNext = false

    override fun onSensorChanged(event: SensorEvent?) {
        if (!isEnabled || event == null) return

        val x = event.values[0]
        val y = event.values[1]

        if (calibrateNext) {
            calibratedX = x
            calibratedY = y
            calibrateNext = false
            return
        }

        val relX = x - calibratedX // Roll (left/right)
        val relY = y - calibratedY // Pitch (forward/back)

        val now = System.currentTimeMillis()
        if (now - lastActionTime < actionCooldown) return

        // Roll left/right detection
        if (relX > 2.8f) {
            lastActionTime = now
            onRollLeft()
        } else if (relX < -2.8f) {
            lastActionTime = now
            onRollRight()
        } else if (abs(relX) < 1.0f) {
            onRollCenter()
        }

        // Pitch forward (duck/slide) / back (jump)
        if (relY > 3.5f) {
            lastActionTime = now
            onPitchBack()
        } else if (relY < -3.5f) {
            lastActionTime = now
            onPitchForward()
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}
