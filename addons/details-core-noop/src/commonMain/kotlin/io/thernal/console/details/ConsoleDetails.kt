package io.thernal.console.details

@Suppress("UnusedParameter")
object ConsoleDetails {
    fun put(
        key: String,
        value: String,
    ) = Unit
    fun put(detail: Pair<String, String>) = Unit
    fun remove(key: String) = Unit
    fun clear() = Unit
}
