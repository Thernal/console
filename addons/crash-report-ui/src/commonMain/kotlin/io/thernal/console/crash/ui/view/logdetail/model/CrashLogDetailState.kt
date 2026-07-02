package io.thernal.console.crash.ui.view.logdetail.model

import androidx.compose.runtime.Stable
import io.thernal.console.core.log.Log
import io.thernal.console.ui.core.ViewState

@Stable
class CrashLogDetailState : ViewState() {

    val isLoaded = field(false)

    /** The restored log to render, or `null` when it no longer exists (session deleted). */
    val log = field<Log?>(null)
}
