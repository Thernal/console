package io.thernal.console.network.ui.view.networklogdetail.model

import io.thernal.console.ui.core.ViewEffect

sealed interface NetworkLogDetailEffect : ViewEffect {
    data class Copy(val data: String) : NetworkLogDetailEffect
}
