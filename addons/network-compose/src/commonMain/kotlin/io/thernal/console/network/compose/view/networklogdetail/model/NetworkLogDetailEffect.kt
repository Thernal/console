package io.thernal.console.network.compose.view.networklogdetail.model

import io.thernal.console.compose.core.ViewEffect

sealed interface NetworkLogDetailEffect : ViewEffect {
    data class Copy(val data: String) : NetworkLogDetailEffect
}
