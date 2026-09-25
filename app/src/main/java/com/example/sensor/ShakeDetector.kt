package com.example.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlin.math.abs

class ShakeDetector(
    context: Context,
    private val onShake: () -> Unit
) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
    private val accelerometer = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    private var lastX: Float? = null
    private var lastY: Float? = null
    private var lastZ: Float? = null
    private var lastShakeTime: Long = 0
    private val cooldownMs = 1500L
    private val shakeThreshold = 22.0f

    var isEnabled: Boolean = true

    fun start() {
        accelerometer?.let {
            sensorManager?.registerListener(this, it, SensorManager.SENSOR_DELAY_GAME)
        }
    }

    fun stop() {
        sensorManager?.unregisterListener(this)
        lastX = null
        lastY = null
        lastZ = null
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (!isEnabled || event == null) return

        val now = System.currentTimeMillis()
        if (now - lastShakeTime < cooldownMs) return

        val x = event.values[0]
        val y = event.values[1]
        val z = event.values[2]

        val prevX = lastX
        val prevY = lastY
        val prevZ = lastZ

        if (prevX != null && prevY != null && prevZ != null) {
            val deltaX = abs(x - prevX)
            val deltaY = abs(y - prevY)
            val deltaZ = abs(z - prevZ)
            val totalDelta = deltaX + deltaY + deltaZ

            if (totalDelta > shakeThreshold) {
                lastShakeTime = now
                onShake()
            }
        }

        lastX = x
        lastY = y
        lastZ = z
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}
