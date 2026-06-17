package io.thernal.console.network

import kotlin.math.roundToInt

private const val CONTENT_TYPE_HEADER = "Content-Type"
private const val CONTENT_LENGTH_HEADER = "Content-Length"
private const val BYTES_PER_UNIT = 1024.0
private const val DECIMAL_SCALE = 10
private val SIZE_UNITS = listOf("KB", "MB", "GB", "TB", "PB")

private const val NUL_CODE = 0x00
private const val REPLACEMENT_CODE = 0xFFFD

private val BINARY_MIME_PREFIXES = listOf("image/", "audio/", "video/", "font/")
private val BINARY_MIME_TYPES = setOf(
    "application/octet-stream",
    "application/pdf",
    "application/zip",
    "application/gzip",
    "application/x-gzip",
    "application/x-tar",
    "application/x-7z-compressed",
    "application/x-rar-compressed",
    "application/wasm",
    "application/protobuf",
    "application/x-protobuf",
    "application/grpc",
    "application/msgpack",
    "application/x-msgpack",
)

/**
 * Decides how a non-blank body should be rendered: pretty-printed JSON, raw text, or binary
 * metadata. Detection prefers the declared `Content-Type`, then falls back to inspecting the bytes
 * so unreadable payloads are never dumped as raw text.
 */
fun resolveNetworkBody(
    rawBody: String,
    headers: Map<String, String>,
): NetworkBody {
    val mimeType = headers.contentMimeType()
    val byteCount = headers.contentLength()

    if (mimeType.isBinaryMime() || rawBody.looksBinary()) {
        return NetworkBody.Binary(mimeType = mimeType, byteCount = byteCount)
    }

    val jsonByContent = mimeType == null && rawBody.looksLikeJson()
    val isJson = mimeType.isJsonMime() || jsonByContent
    if (isJson) {
        return NetworkBody.Text(rawBody.toPrettyJsonOrNull() ?: rawBody)
    }

    return NetworkBody.Text(rawBody)
}

/** Plain-text rendering of a body for copy/share actions, mirroring what the detail view shows. */
fun NetworkBody.toCopyText(): String {
    return when (this) {
        is NetworkBody.Text -> value

        is NetworkBody.Binary -> buildString {
            append("$CONTENT_TYPE_HEADER: ${mimeType ?: "unknown"}")
            byteCount?.let { append("\n$CONTENT_LENGTH_HEADER: $it bytes (${it.toHumanReadableSize()})") }
        }
    }
}

/** Formats a byte count as a human-readable size such as `12 B`, `4.2 KB` or `3.0 MB`. */
fun Long.toHumanReadableSize(): String {
    if (this < BYTES_PER_UNIT) return "$this B"

    var size = this / BYTES_PER_UNIT
    var unitIndex = 0
    while (size >= BYTES_PER_UNIT && unitIndex < SIZE_UNITS.lastIndex) {
        size /= BYTES_PER_UNIT
        unitIndex++
    }

    val scaled = (size * DECIMAL_SCALE).roundToInt()
    val whole = scaled / DECIMAL_SCALE
    val fraction = scaled % DECIMAL_SCALE
    val unit = SIZE_UNITS[unitIndex]
    return if (fraction == 0) "$whole $unit" else "$whole.$fraction $unit"
}

private fun Map<String, String>.contentMimeType(): String? {
    return header(CONTENT_TYPE_HEADER)
        ?.substringBefore(';')
        ?.trim()
        ?.lowercase()
        ?.takeIf { it.isNotEmpty() }
}

private fun Map<String, String>.contentLength(): Long? {
    return header(CONTENT_LENGTH_HEADER)
        ?.substringBefore(',')
        ?.trim()
        ?.toLongOrNull()
        ?.takeIf { it >= 0 }
}

private fun Map<String, String>.header(name: String): String? =
    entries.firstOrNull { it.key.equals(name, ignoreCase = true) }?.value

private fun String?.isJsonMime(): Boolean {
    return this != null && (this == "application/json" || endsWith("+json"))
}

private fun String?.isBinaryMime(): Boolean {
    if (this == null || startsWith("text/")) return false
    return BINARY_MIME_PREFIXES.any { startsWith(it) } || this in BINARY_MIME_TYPES
}

private fun String.looksLikeJson(): Boolean {
    val trimmed = trim()
    return trimmed.startsWith('{') || trimmed.startsWith('[')
}

/**
 * Heuristic for content that decoded into unreadable text: a NUL byte or the Unicode replacement
 * character (produced when binary bytes fail UTF-8 decoding) marks the payload as binary.
 */
private fun String.looksBinary(): Boolean {
    return any { it.code == NUL_CODE || it.code == REPLACEMENT_CODE }
}
