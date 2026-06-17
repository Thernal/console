package io.thernal.console.core.log

import kotlin.time.Instant

interface Log {
    val id: String
    val tag: String?
    val level: LogLevel
    val message: String
    val groupId: String?
    val timestamp: Instant
    val tab: String get() = tag ?: "Log"

    fun contains(query: String): Boolean {
        if (query.isBlank()) {
            return false
        }
        return buildString {
            append(message)
            tag?.let(::append)
            if (level != LogLevel.None) {
                append(level.name)
            }
        }.contains(query, ignoreCase = true)
    }

    /**
     * Shareable plain-text representation of this log, used when copying logs out of the console.
     * Defaults to the level/tag-prefixed message; custom log types may override it to expose their
     * richer content (e.g. a network log's headers and body).
     */
    fun toShareText(): String {
        return buildString {
            if (level != LogLevel.None) {
                append("[${level.name}] ")
            }
            tag?.let { append("[$it] ") }
            append(message)
        }
    }

    companion object {
        operator fun invoke(
            message: String,
            tag: String? = null,
            level: LogLevel = LogLevel.None,
            groupId: String? = null,
        ): Log {
            return BasicLog(
                message = message,
                tag = tag,
                level = level,
                groupId = groupId,
            )
        }
    }
}
