package io.thernal.console.network.ui.view.networklogdetail.model

import androidx.compose.runtime.Stable
import io.thernal.console.ui.core.ViewState

@Stable
class NetworkLogDetailState : ViewState() {
    val headersExpanded = field(false)
    val bodyExpanded = field(false)
}
