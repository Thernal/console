package io.thernal.console.network.compose.view.networklogdetail.model

import androidx.compose.runtime.Stable
import io.thernal.console.compose.core.ViewState

@Stable
class NetworkLogDetailState : ViewState() {
    val headersExpanded = field(false)
    val bodyExpanded = field(false)
}
