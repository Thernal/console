package io.thernal.console.network.compose.common.extensions

internal fun Map<*, *>.toDisplayText(): String {
    return entries.joinToString(separator = "\n") { (key, value) -> "$key: $value" }
}

internal fun Map<*, *>.containsQuery(query: String): Boolean {
    return entries.any { pair ->
        "${pair.key}${pair.value}".contains(query, ignoreCase = false)
    }
}
