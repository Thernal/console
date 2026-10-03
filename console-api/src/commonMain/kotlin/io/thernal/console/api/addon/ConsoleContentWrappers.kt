package io.thernal.console.api.addon

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf

/** Wraps the host app content, so an addon can provide CompositionLocals to it. */
typealias ConsoleContentWrapper = @Composable (content: @Composable () -> Unit) -> Unit

/**
 * Registry of [ConsoleContentWrapper]s applied around the host app content, outermost first
 * (registration order). Register before the first composition: a late registration changes the
 * composition structure above the app content and resets its state.
 */
object ConsoleContentWrappers {
    private val _wrappers = mutableStateListOf<ConsoleContentWrapper>()
    val wrappers: List<ConsoleContentWrapper> get() = _wrappers

    internal fun register(wrapper: ConsoleContentWrapper) {
        _wrappers.add(wrapper)
    }
}
