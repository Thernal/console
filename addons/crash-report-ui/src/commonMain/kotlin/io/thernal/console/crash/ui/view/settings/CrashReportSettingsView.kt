package io.thernal.console.crash.ui.view.settings

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import io.thernal.console.api.navigation.LocalConsoleNavigator
import io.thernal.console.crash.ui.view.settings.model.CrashReportSettingsViewModel

@Composable
internal fun CrashReportSettingsView() {
    val viewModel = viewModel { CrashReportSettingsViewModel() }
    val navigator = LocalConsoleNavigator.current

    CrashReportSettingsContent(
        state = viewModel.state,
        dispatch = viewModel::dispatch,
        onBack = navigator::pop,
    )
}
