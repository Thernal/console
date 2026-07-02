package io.thernal.console.crash.ui.config

/**
 * How much of a network log's body is persisted into a crash session. Bodies are the main
 * privacy and disk-size lever: they may hold tokens/PII even after upstream header masking.
 */
enum class CrashBodyPolicy {

    /** Drop the body entirely; only the request/response metadata is persisted. */
    None,

    /** Persist at most `maxBodySize` characters of the body (the default). */
    Truncated,

    /** Persist the body as captured. */
    Full,
}
