package io.thernal.console.inspector.ui.engine

/** Aggregate composition timings since the last reset. */
internal data class InspectorTiming(
    val count: Int = 0,
    val totalMicros: Long = 0L,
    val maxMicros: Long = 0L,
    val lastMicros: Long = 0L,
) {
    val averageMicros: Long get() = if (count == 0) 0L else totalMicros / count

    fun plus(micros: Long): InspectorTiming {
        return InspectorTiming(
            count = count + 1,
            totalMicros = totalMicros + micros,
            maxMicros = maxOf(maxMicros, micros),
            lastMicros = micros,
        )
    }
}
