package io.thernal.console.ui.common.extensions

import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format.char
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Instant

val HmsFormat = LocalTime.Format {
    hour()
    char(':')
    minute()
    char(':')
    second()
}

fun Instant.toHms(timeZone: TimeZone = TimeZone.currentSystemDefault()): String {
    val localTime = toLocalDateTime(timeZone).time
    return HmsFormat.format(localTime)
}
