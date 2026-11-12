package io.thernal.console.inspector.ui.agent

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable

@Composable
@ReadOnlyComposable
internal actual fun currentSemanticsRoot(): Any? {
    return null
}

internal actual fun keepSemanticsRoot(root: Any?) = Unit

internal actual fun platformSemanticsTexts(): Map<Int, List<String>> {
    return emptyMap()
}
