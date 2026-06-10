package io.thernal.console.ui.core

import androidx.compose.runtime.Stable

@Stable
interface ViewIntent

interface IntentHandler<T : ViewIntent> {
    val handler: Handler<T>

    fun dispatch(intent: T) {
        handler.onIntentUpdate(intent)
    }

    @Stable
    class Handler<T : ViewIntent> internal constructor(
        internal val onIntentUpdate: (T) -> Unit,
    )

    fun onIntentUpdate(onIntentUpdate: (T) -> Unit): Handler<T> {
        return Handler(
            onIntentUpdate = onIntentUpdate,
        )
    }
}
