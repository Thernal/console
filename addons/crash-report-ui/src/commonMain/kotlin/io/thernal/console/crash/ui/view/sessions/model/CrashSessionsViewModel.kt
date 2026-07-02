package io.thernal.console.crash.ui.view.sessions.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.thernal.console.crash.ui.CrashReports
import io.thernal.console.ui.core.IntentHandler
import io.thernal.console.ui.core.StateHolder
import kotlinx.coroutines.launch

internal class CrashSessionsViewModel :
    ViewModel(),
    StateHolder,
    IntentHandler<CrashSessionsIntent> {

    val state = CrashSessionsState()

    override val handler = onIntentUpdate { intent ->
        when (intent) {
            is CrashSessionsIntent.Delete -> CrashReports.delete(intent.sessionId)
        }
    }

    init {
        CrashReports.refreshSessions()
        viewModelScope.launch {
            CrashReports.sessions.collect { state.sessions.set(it) }
        }
        viewModelScope.launch {
            CrashReports.config.collect { state.showSafeSessions.set(it.showSafeSessions) }
        }
    }
}
