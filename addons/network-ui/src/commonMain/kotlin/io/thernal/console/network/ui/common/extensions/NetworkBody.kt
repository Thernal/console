package io.thernal.console.network.ui.common.extensions

/**
 * Classified representation of a network body, decided from its content type and bytes so the
 * detail view can render readable payloads and avoid dumping unreadable binary content.
 */
internal sealed interface NetworkBody {

    /** A text payload, pretty-printed when it was detected as JSON, raw otherwise. */
    data class Text(val value: String) : NetworkBody

    /** A binary payload represented by metadata instead of its raw bytes. */
    data class Binary(
        val mimeType: String?,
        val byteCount: Long?,
    ) : NetworkBody
}
