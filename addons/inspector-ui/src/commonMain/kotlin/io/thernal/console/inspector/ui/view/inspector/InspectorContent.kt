package io.thernal.console.inspector.ui.view.inspector

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.thernal.console.inspector.ui.view.inspector.components.InspectorDetailsPage
import io.thernal.console.inspector.ui.view.inspector.components.InspectorPageSelector
import io.thernal.console.inspector.ui.view.inspector.components.InspectorRecompositionsPage
import io.thernal.console.inspector.ui.view.inspector.components.InspectorTreePage
import io.thernal.console.inspector.ui.view.inspector.model.InspectorIntent
import io.thernal.console.inspector.ui.view.inspector.model.InspectorPage
import io.thernal.console.inspector.ui.view.inspector.model.InspectorState

@Composable
internal fun InspectorContent(
    state: InspectorState,
    dispatch: (InspectorIntent) -> Unit,
) {
    Column(Modifier.fillMaxSize()) {
        InspectorPageSelector(
            selected = state.page.value,
            onSelect = { dispatch(InspectorIntent.SelectPage(it)) },
        )
        when (state.page.value) {
            InspectorPage.Tree -> InspectorTreePage(state = state, dispatch = dispatch)
            InspectorPage.Details -> InspectorDetailsPage(state = state, dispatch = dispatch)
            InspectorPage.Recompositions -> InspectorRecompositionsPage(state = state)
        }
    }
}
