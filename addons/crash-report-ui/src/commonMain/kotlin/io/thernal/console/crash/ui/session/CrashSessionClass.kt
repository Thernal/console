package io.thernal.console.crash.ui.session

import io.thernal.console.crash.ui.store.CrashStoreEntry

/**
 * Termination classification of a past session, inferred on read from the sidecars (there is no
 * reliable "normal exit" callback on mobile, so a leftover session file implies abnormal
 * termination refined by the last known lifecycle state).
 */
enum class CrashSessionClass {

    /** A `.crash` sidecar exists — a managed exception was captured with its stack trace. */
    Confirmed,

    /** No trace, but the app died while in the foreground: native crash, OOM kill, or ANR. */
    Probable,

    /** Died in the background (or exited cleanly on desktop) — most likely just an OS kill. */
    Safe,
}

internal fun CrashStoreEntry.classify(): CrashSessionClass {
    return when {
        hasCrash -> CrashSessionClass.Confirmed
        state == TerminationState.BACKGROUND.name -> CrashSessionClass.Safe
        state == TerminationState.CLEAN.name -> CrashSessionClass.Safe
        else -> CrashSessionClass.Probable
    }
}
