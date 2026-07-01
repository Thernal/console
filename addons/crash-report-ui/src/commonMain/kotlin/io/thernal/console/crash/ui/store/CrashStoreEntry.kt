package io.thernal.console.crash.ui.store

/**
 * One past session as seen from the directory listing + sidecars alone — the big `.log` stays
 * closed until the session is opened.
 *
 * @property state raw content of the `.state` sidecar (last fg/bg lifecycle state), or `null`
 *   when the sidecar was never written.
 */
internal data class CrashStoreEntry(
    val id: String,
    val startedAtMs: Long,
    val hasCrash: Boolean,
    val state: String?,
)
