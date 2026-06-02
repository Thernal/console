package io.thernal.console.compose.util

private const val MILLIS_PER_SECOND = 1000L
private const val SECONDS_PER_MINUTE = 60L
private const val SECONDS_PER_HOUR = 3600L
private const val HOURS_PER_DAY = 24L
private const val TIME_COMPONENT_WIDTH = 2
private const val MILLIS_WIDTH = 3

internal fun formatLogTimestamp(epochMillis: Long): String {
    return buildTimestamp(epochMillis, showUtc = false)
}

internal fun formatLogTimestampFull(epochMillis: Long): String {
    return buildTimestamp(epochMillis, showUtc = true)
}

private fun buildTimestamp(
    epochMillis: Long,
    showUtc: Boolean,
): String {
    val totalSeconds = epochMillis / MILLIS_PER_SECOND
    val millis = epochMillis % MILLIS_PER_SECOND
    val hh = totalSeconds / SECONDS_PER_HOUR % HOURS_PER_DAY
    val mm = totalSeconds / SECONDS_PER_MINUTE % SECONDS_PER_MINUTE
    val ss = totalSeconds % SECONDS_PER_MINUTE
    val base = "${hh.toString().padStart(TIME_COMPONENT_WIDTH, '0')}:" +
        "${mm.toString().padStart(TIME_COMPONENT_WIDTH, '0')}:" +
        "${ss.toString().padStart(TIME_COMPONENT_WIDTH, '0')}." +
        millis.toString().padStart(MILLIS_WIDTH, '0')
    return if (showUtc) "$base UTC" else base
}
