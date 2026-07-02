package io.thernal.console.crash.ui.view.detail.model

import androidx.compose.runtime.Stable
import io.thernal.console.core.log.Log
import io.thernal.console.ui.core.ViewState

@Stable
class CrashSessionDetailState : ViewState() {

    val isLoaded = field(false)

    /** Logs preceding the crash, in capture order (fatal record excluded). */
    val precedingLogs = field(emptyList<Log>())

    /** Crash summary from the sidecar; `null` for sessions without a captured trace. */
    val crashSummary = field<String?>(null)

    val stackTrace = field<String?>(null)

    val crashedAtMs = field<Long?>(null)
}
