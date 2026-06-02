package io.thernal.console.compose.view.detail.model

import io.thernal.console.compose.core.ViewState
import io.thernal.console.runtime.Log

class LogDetailState : ViewState() {
    val log = field<Log?>(null)
}
