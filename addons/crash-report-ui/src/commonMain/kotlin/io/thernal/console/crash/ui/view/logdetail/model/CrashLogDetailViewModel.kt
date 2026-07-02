package io.thernal.console.crash.ui.view.logdetail.model

import androidx.lifecycle.ViewModel
import io.thernal.console.crash.ui.CrashReports
import io.thernal.console.ui.core.StateHolder

/**
 * Looks the log up from the persisted session rather than the live pipeline: restored logs are
 * never re-emitted through `Console.notify`, so `logging-ui`'s own detail screen (which reads its
 * live observer) cannot find them.
 */
internal class CrashLogDetailViewModel(
    sessionId: String,
    logId: String,
) : ViewModel(),
    StateHolder {

    val state = CrashLogDetailState()

    init {
        val log = CrashReports.open(sessionId)?.logs?.firstOrNull { it.id == logId }
        snapshot {
            state.log.set(log)
            state.isLoaded.set(true)
        }
    }
}
