package io.thernal.console.crash.ui.view.detail.model

import androidx.compose.runtime.Stable
import io.thernal.console.core.log.Log
import io.thernal.console.ui.core.ViewState

@Stable
class CrashSessionDetailState : ViewState() {

    val isLoaded = field(false)

    /**
     * The session's restored logs, newest first (matching the live Logs view). The fatal crash
     * record — a `BasicLog` with the `Crash` tag and `Fatal` level appended by the handler — is
     * the first entry and renders through the same log-item UI as everything else.
     */
    val logs = field(emptyList<Log>())

    /** Crash summary from the sidecar; `null` for sessions without a captured trace. */
    val crashSummary = field<String?>(null)
}
