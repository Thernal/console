package io.thernal.console.crash.ui.session

/**
 * Parsed `.crash` sidecar: written atomically at crash time, small enough to read for the list.
 * Encoded as three parts split on newlines — the crash instant, the single-line summary
 * (exception class + message), then the full multi-line stack trace.
 */
internal data class CrashSidecar(
    val crashedAtMs: Long,
    val summary: String,
    val stackTrace: String,
) {

    fun encode(): String {
        return "$crashedAtMs\n$summary\n$stackTrace"
    }

    companion object {

        private const val PART_COUNT = 3

        fun of(
            throwable: Throwable,
            crashedAtMs: Long,
        ): CrashSidecar {
            val stackTrace = throwable.stackTraceToString()
            return CrashSidecar(
                crashedAtMs = crashedAtMs,
                summary = stackTrace.lineSequence().firstOrNull().orEmpty().trim(),
                stackTrace = stackTrace,
            )
        }

        fun parse(content: String): CrashSidecar? {
            val parts = content.split("\n", limit = PART_COUNT)
            if (parts.size < PART_COUNT) return null
            val crashedAtMs = parts[0].toLongOrNull() ?: return null
            return CrashSidecar(
                crashedAtMs = crashedAtMs,
                summary = parts[1],
                stackTrace = parts[2],
            )
        }
    }
}
