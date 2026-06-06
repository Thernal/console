package io.thernal.console.compose.trigger

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Handler
import android.os.Looper
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.composed
import androidx.compose.ui.platform.LocalContext
import io.thernal.console.api.trigger.ConsoleTrigger
import kotlin.math.sqrt
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.TimeSource

private val SHAKE_COOLDOWN = 500.milliseconds

actual fun ConsoleTrigger.Companion.shake(threshold: Float): ConsoleTrigger = ConsoleTrigger { onDetected ->
    composed {
        val context = LocalContext.current
        DisposableEffect(onDetected) {
            val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
            val sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
                ?: return@DisposableEffect onDispose {}

            var lastShakeMark = TimeSource.Monotonic.markNow() - SHAKE_COOLDOWN
            val listener = object : SensorEventListener {
                override fun onSensorChanged(event: SensorEvent) {
                    val x = event.values[0]
                    val y = event.values[1]
                    val z = event.values[2]
                    val acceleration = sqrt(x * x + y * y + z * z) - SensorManager.GRAVITY_EARTH
                    if (acceleration > threshold && lastShakeMark.elapsedNow() > SHAKE_COOLDOWN) {
                        lastShakeMark = TimeSource.Monotonic.markNow()
                        onDetected()
                    }
                }

                override fun onAccuracyChanged(
                    sensor: Sensor?,
                    accuracy: Int,
                ) {}
            }

            sensorManager.registerListener(
                listener,
                sensor,
                SensorManager.SENSOR_DELAY_GAME,
                Handler(Looper.getMainLooper()),
            )
            onDispose { sensorManager.unregisterListener(listener) }
        }
        this
    }
}
