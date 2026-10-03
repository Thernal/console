package io.thernal.console.inspector.ui.view.inspector.model

import io.thernal.console.ui.core.ViewIntent

internal sealed interface InspectorIntent : ViewIntent {
    data class SelectPage(val page: InspectorPage) : InspectorIntent

    data class SelectNode(val key: Any) : InspectorIntent

    data object SelectParent : InspectorIntent

    data class ToggleCollapsed(val key: Any) : InspectorIntent

    data class SetQuery(val query: String) : InspectorIntent
}
