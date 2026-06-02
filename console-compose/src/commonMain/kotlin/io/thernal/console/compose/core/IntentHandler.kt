package io.thernal.console.compose.core

interface Intent

interface IntentHandler<T : Intent> {
    val handler: Handler<T>

    fun dispatch(intent: T) {
        handler.onIntentUpdate(intent)
    }

    class Handler<T : Intent> internal constructor(
        internal val onIntentUpdate: (T) -> Unit,
    )

    fun Handler(onIntentUpdate: (T) -> Unit): Handler<T> {
        return IntentHandler.Handler(
            onIntentUpdate = onIntentUpdate,
        )
    }
}
