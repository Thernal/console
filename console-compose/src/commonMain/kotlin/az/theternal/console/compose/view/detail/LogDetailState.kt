package az.theternal.console.compose.view.detail

import az.theternal.console.compose.core.ViewState
import az.theternal.console.runtime.Log

class LogDetailState : ViewState() {
    val log = field<Log?>(null)
}
