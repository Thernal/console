package io.thernal.console.inspector.ui.agent

import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.node.RootForTest
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.semantics.getAllSemanticsNodes
import java.lang.ref.WeakReference

private var semanticsRoot: WeakReference<View>? = null

@Composable
@ReadOnlyComposable
internal actual fun currentSemanticsRoot(): Any? {
    return LocalView.current
}

internal actual fun keepSemanticsRoot(root: Any?) {
    semanticsRoot = (root as? View)?.let(::WeakReference)
}

internal actual fun platformSemanticsTexts(): Map<Int, List<String>> {
    val owner = (semanticsRoot?.get() as? RootForTest)?.semanticsOwner ?: return emptyMap()
    return owner.getAllSemanticsNodes(mergingEnabled = false)
        .associate { node -> node.id to semanticsTextsOf(node.config) }
        .filterValues { it.isNotEmpty() }
}
