package az.theternal.console.ui.utils

private const val MILLIS_PER_SECOND = 1000L
private const val SECONDS_PER_MINUTE = 60L
private const val SECONDS_PER_HOUR = 3600L
private const val HOURS_PER_DAY = 24L

internal fun formatLogTimestamp(epochMillis: Long): String {
    val totalSeconds = epochMillis / MILLIS_PER_SECOND
    val hh = totalSeconds / SECONDS_PER_HOUR % HOURS_PER_DAY
    val mm = totalSeconds / SECONDS_PER_MINUTE % SECONDS_PER_MINUTE
    val ss = totalSeconds % SECONDS_PER_MINUTE
    return "${hh.toString().padStart(2, '0')}:${mm.toString().padStart(2, '0')}:${ss.toString().padStart(2, '0')} UTC"
}
