package io.thernal.console.mcp.tools

import io.thernal.console.mcp.hub.HubRecord
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private const val MAX_SUMMARY_CHARS = 300
private const val TIME_PATTERN = "HH:mm:ss.SSS"
private val TIME_FORMAT: DateTimeFormatter = DateTimeFormatter.ofPattern(TIME_PATTERN).withZone(ZoneId.systemDefault())

/** One compact line per record: `#seq time LEVEL [tag] summary`, on one line and cut at a fixed length. */
fun HubRecord.toLine(): String {
    val flat = summary.replace("\r\n", "\n").replace('\n', '⏎')
    val shown = if (flat.length > MAX_SUMMARY_CHARS) {
        flat.take(MAX_SUMMARY_CHARS) + "… (+${flat.length - MAX_SUMMARY_CHARS} chars)"
    } else {
        flat
    }
    return buildString {
        append("#").append(seq).append(' ').append(TIME_FORMAT.format(Instant.ofEpochMilli(time)))
        level?.takeIf { it != "None" }?.let { append(' ').append(it) }
        tag?.let { append(" [").append(it).append(']') }
        append(' ').append(shown)
    }
}

/** The whole record: its line, the fields and the full detail. */
fun HubRecord.toDetail(): String {
    return buildString {
        append(toLine()).append('\n')
        append("id: ").append(id)
        kind?.let { append(" · kind: ").append(it) }
        fields.forEach { (key, value) -> append('\n').append(key).append(": ").append(value) }
        detail?.takeIf { it.isNotBlank() }?.let { append("\n\n").append(it) }
    }
}
