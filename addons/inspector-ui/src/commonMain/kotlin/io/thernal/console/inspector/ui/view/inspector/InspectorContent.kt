package io.thernal.console.inspector.ui.view.inspector

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.thernal.console.inspector.ui.view.inspector.components.DetailsPage
import io.thernal.console.inspector.ui.view.inspector.components.PageSelector
import io.thernal.console.inspector.ui.view.inspector.components.RecompositionsPage
import io.thernal.console.inspector.ui.view.inspector.components.TreePage
import io.thernal.console.inspector.ui.view.inspector.model.InspectorIntent
import io.thernal.console.inspector.ui.view.inspector.model.InspectorPage
import io.thernal.console.inspector.ui.view.inspector.model.InspectorState

@Composable
internal fun InspectorContent(
    state: InspectorState,
    dispatch: (InspectorIntent) -> Unit,
) {
    Column(Modifier.fillMaxSize()) {
        PageSelector(
            selected = state.page.value,
            onSelect = { dispatch(InspectorIntent.SelectPage(it)) },
        )
        when (state.page.value) {
            InspectorPage.Tree -> TreePage(state = state, dispatch = dispatch)
            InspectorPage.Details -> DetailsPage(state = state, dispatch = dispatch)
            InspectorPage.Recompositions -> RecompositionsPage(state = state)
        }
    }
}
