package io.thernal.console.inspector.ui.engine

import androidx.compose.runtime.tooling.CompositionGroup
import androidx.compose.ui.layout.LayoutInfo

/**
 * One named composable call in the composition. [layouts] are the layout nodes it emits directly,
 * [primaryLayout] the first layout node anywhere below it.
 *
 * [group] is only valid while the slot table is unchanged, so it is read right after a refresh.
 */
internal class InspectorNode(
    val key: Any,
    val id: Int,
    val parentId: Int,
    val depth: Int,
    val name: String,
    val file: String?,
    val isFramework: Boolean,
    val isUnderApp: Boolean,
    val layouts: List<LayoutInfo>,
    val primaryLayout: LayoutInfo?,
    val childIds: List<Int>,
    val group: CompositionGroup,
)
