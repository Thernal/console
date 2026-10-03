package io.thernal.console.inspector.ui.view.inspector.model

import androidx.compose.runtime.Immutable
import io.thernal.console.inspector.ui.engine.InspectorNode

@Immutable
internal data class InspectorRowModel(
    val node: InspectorNode,
    val depth: Int,
    val hasChildren: Boolean,
    val isCollapsed: Boolean,
    val count: Int,
    val isSelected: Boolean,
)
