package io.thernal.console.inspector.ui.view.inspector

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import io.thernal.console.inspector.ui.engine.InspectorEngine
import io.thernal.console.inspector.ui.view.inspector.model.InspectorViewModel

@Composable
internal fun InspectorView() {
    val viewModel = viewModel { InspectorViewModel() }

    DisposableEffect(Unit) {
        InspectorEngine.setTabVisible(true)
        onDispose { InspectorEngine.setTabVisible(false) }
    }

    InspectorContent(
        state = viewModel.state,
        dispatch = viewModel::dispatch,
    )
}
