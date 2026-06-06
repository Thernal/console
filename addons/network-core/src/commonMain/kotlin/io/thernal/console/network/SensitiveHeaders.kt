package io.thernal.console.network

private val DEFAULT_SENSITIVE_HEADER_NAMES: Set<String> = setOf(
    "authorization",
    "cookie",
    "set-cookie",
    "x-api-key",
    "proxy-authorization",
)

data class SensitiveHeaders(
    val names: Set<String> = DEFAULT_SENSITIVE_HEADER_NAMES,
    val mask: String = "***",
) {
    fun shouldMask(headerName: String): Boolean {
        return headerName.lowercase() in names
    }

    companion object {
        val DEFAULT: SensitiveHeaders = SensitiveHeaders()
        val NONE: SensitiveHeaders = SensitiveHeaders(names = emptySet())
    }
}
