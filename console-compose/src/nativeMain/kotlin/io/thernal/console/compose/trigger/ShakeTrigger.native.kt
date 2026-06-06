@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package io.thernal.console.compose.trigger

import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.composed
import io.thernal.console.api.trigger.ConsoleTrigger
import kotlinx.cinterop.useContents
import platform.CoreMotion.CMMotionManager
import platform.Foundation.NSOperationQueue
import kotlin.math.sqrt
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.TimeSource

private val SHAKE_COOLDOWN = 500.milliseconds
private const val ACCELEROMETER_INTERVAL = 0.05
private const val GRAVITY_EARTH = 9.81f

actual fun ConsoleTrigger.Companion.shake(threshold: Float): ConsoleTrigger {
    return ConsoleTrigger { onDetected ->
        composed {
            DisposableEffect(onDetected) {
                val motionManager = CMMotionManager()
                if (!motionManager.accelerometerAvailable) {
                    return@DisposableEffect onDispose {}
                }
                motionManager.startShakeDetection(
                    threshold = threshold,
                    onDetected = onDetected,
                )
                onDispose {
                    motionManager.stopAccelerometerUpdates()
                }
            }
            this
        }
    }
}

private fun CMMotionManager.startShakeDetection(
    threshold: Float,
    onDetected: () -> Unit,
) {
    accelerometerUpdateInterval = ACCELEROMETER_INTERVAL
    val detector = ShakeDetector(
        threshold = threshold.toIosThreshold(),
        onDetected = onDetected,
    )
    startAccelerometerUpdatesToQueue(
        queue = NSOperationQueue.mainQueue,
        withHandler = { data, _ ->
            data?.acceleration?.useContents {
                detector.onAccelerationChanged(x, y, z)
            }
        },
    )
}

private class ShakeDetector(
    private val threshold: Double,
    private val onDetected: () -> Unit,
) {
    private var lastShakeMark = TimeSource.Monotonic.markNow() - SHAKE_COOLDOWN
    fun onAccelerationChanged(
        x: Double,
        y: Double,
        z: Double,
    ) {
        val magnitude = sqrt(x * x + y * y + z * z)
        if (magnitude <= threshold) return
        if (lastShakeMark.elapsedNow() <= SHAKE_COOLDOWN) return
        lastShakeMark = TimeSource.Monotonic.markNow()
        onDetected()
    }
}

private fun Float.toIosThreshold(): Double {
    return (this / GRAVITY_EARTH).toDouble()
}
