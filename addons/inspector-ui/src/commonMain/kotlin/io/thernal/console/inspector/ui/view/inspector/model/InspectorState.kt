package io.thernal.console.inspector.ui.view.inspector.model

import androidx.compose.runtime.Stable
import androidx.compose.ui.text.input.TextFieldValue
import io.thernal.console.inspector.ui.engine.NodeDetails
import io.thernal.console.inspector.ui.engine.recomposition.InspectorTiming
import io.thernal.console.inspector.ui.engine.recomposition.RecompositionEntry
import io.thernal.console.ui.core.ViewState

@Stable
internal class InspectorState : ViewState() {
    val page = field(InspectorPage.Tree)
    val query = field(TextFieldValue())
    val rows = field<List<InspectorRowModel>>(emptyList())
    val details = field<NodeDetails?>(null)
    val ranking = field<List<RecompositionEntry>>(emptyList())
    val timing = field(InspectorTiming())
    val summary = field("")
}
