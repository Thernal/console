package io.thernal.console.settings.ui.view.settings

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import io.thernal.console.settings.ui.view.settings.model.SettingsViewModel

@Composable
internal fun SettingsView() {
    val viewModel = viewModel { SettingsViewModel() }

    SettingsContent(
        state = viewModel.state,
        dispatch = viewModel::dispatch,
    )
}
