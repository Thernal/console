package io.thernal.console.inspector.ui.view.inspector.model

import androidx.compose.runtime.Immutable

/** One row of the tree page. Holds plain values only, never the live node with its layout and slot table handles. */
@Immutable
internal data class InspectorRowModel(
    val id: Int,
    val key: Any,
    val name: String,
    val file: String?,
    val isFramework: Boolean,
    val depth: Int,
    val hasChildren: Boolean,
    val isCollapsed: Boolean,
    val count: Int,
    val isSelected: Boolean,
)
