package io.thernal.console.network.compose.view.networklogdetail.model

import io.thernal.console.compose.core.ViewIntent

sealed interface NetworkLogDetailIntent : ViewIntent {
    data object ToggleHeadersExpanded : NetworkLogDetailIntent
    data object ToggleBodyExpanded : NetworkLogDetailIntent
    data object CopyAll : NetworkLogDetailIntent
    data object CopyHeaders : NetworkLogDetailIntent
    data object CopyBody : NetworkLogDetailIntent
    data object CopyCurl : NetworkLogDetailIntent
}
