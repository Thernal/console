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

    /** Safe sessions (background/clean kills) are hidden by default. */
    val showSafe = field(false)

    val visibleSessions: State<List<CrashSessionSummary>> = derivedStateOf {
        if (showSafe.value) {
            sessions.value
        } else {
            sessions.value.filter { it.classification != CrashSessionClass.Safe }
        }
    }

    val hiddenSafeCount: State<Int> = derivedStateOf {
        sessions.value.count { it.classification == CrashSessionClass.Safe }
    }
}
