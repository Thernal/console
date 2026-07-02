package io.thernal.console.crash.ui.lifecycle

import io.thernal.console.crash.ui.session.TerminationState

internal actual fun installLifecycleTracking(onStateChanged: (TerminationState) -> Unit) {
    // Desktop has a real clean-exit signal. After a captured crash the session is already
    // finalized, so this state write lands on no active session and is a no-op — a crash is
    // never downgraded to CLEAN.
    Runtime.getRuntime().addShutdownHook(
        Thread { onStateChanged(TerminationState.CLEAN) },
    )
}
