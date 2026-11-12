package io.thernal.console.agent.ui.bridge

import io.thernal.console.agent.AgentRecord

/**
 * Replaces the values of sensitive keys before anything leaves the app: JSON members (`"accessToken": "…"`), query
 * and form parameters (`token=…`) and header lines (`Authorization: …`). A key matches every name containing it,
 * ignoring case, so `token` also covers `access_token` and `refreshToken`.
 */
internal class AgentRedactor(keys: Set<String>) {
    private val name = "[A-Za-z0-9_\\-]*(?:${keys.joinToString("|") { Regex.escape(it) }})[A-Za-z0-9_\\-]*"
    private val sensitiveKey = Regex("^$name$", RegexOption.IGNORE_CASE)
    private val patterns: List<Pair<Regex, String>> = if (keys.isEmpty()) {
        emptyList()
    } else {
        val options = setOf(RegexOption.IGNORE_CASE, RegexOption.MULTILINE)
        listOf(
            Regex("(\"$name\"\\s*:\\s*)\"(?:[^\"\\\\]|\\\\.)*\"", options) to "$1\"$MASK\"",
            Regex("(\"$name\"\\s*:\\s*)(?![\\s\"\\[{])[^,}\\]\\s]+", options) to "$1\"$MASK\"",
            Regex("(\\b$name=)[^&\\s\"]+", options) to "$1$MASK",
            Regex("^(\\s*$name\\s*:[ \\t]*)\\S.*$", options) to "$1$MASK",
        )
    }

    fun redact(text: String): String {
        return patterns.fold(text) { current, (regex, replacement) -> regex.replace(current, replacement) }
    }

    fun redact(record: AgentRecord): AgentRecord {
        if (patterns.isEmpty()) return record
        return record.copy(
            summary = redact(record.summary),
            detail = record.detail?.let(::redact),
            fields = record.fields.mapValues { (key, value) -> if (sensitiveKey.matches(key)) MASK else redact(value) },
        )
    }

    companion object {
        const val MASK = "[redacted]"

        /** Always redacted, whatever the addons register. */
        val DEFAULT_KEYS = setOf(
            "authorization",
            "cookie",
            "password",
            "secret",
            "token",
            "apikey",
            "api_key",
            "api-key",
        )
    }
}
