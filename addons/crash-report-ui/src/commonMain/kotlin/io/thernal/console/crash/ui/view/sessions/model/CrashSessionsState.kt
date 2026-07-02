package io.thernal.console.crash.ui.view.sessions.model

import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import io.thernal.console.crash.ui.session.CrashSessionClass
import io.thernal.console.crash.ui.session.CrashSessionSummary
import io.thernal.console.ui.core.ViewState

@Stable
class CrashSessionsState : ViewState() {

    val sessions = field(emptyList<CrashSessionSummary>())

    /** Mirrors `CrashReportConfig.showSafeSessions`; toggled from the settings screen. */
    val showSafeSessions = field(false)

    val visibleSessions: State<List<CrashSessionSummary>> = derivedStateOf {
        if (showSafeSessions.value) {
            sessions.value
        } else {
            sessions.value.filter { it.classification != CrashSessionClass.Safe }
        }
    }
}
