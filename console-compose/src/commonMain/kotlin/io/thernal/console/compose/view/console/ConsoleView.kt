package io.thernal.console.compose.view.console

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import io.thernal.console.api.addon.ConsoleNavigation
import io.thernal.console.api.addon.ConsoleTab
import io.thernal.console.api.navigation.LocalConsoleNavigator
import io.thernal.console.compose.view.console.model.ConsoleIntent
import io.thernal.console.compose.view.console.model.ConsoleViewModel

@Composable
internal fun ConsoleView(
    requestedTab: ConsoleTab? = null,
    onRequestConsumed: () -> Unit = {},
) {
    val viewModel = viewModel { ConsoleViewModel() }
    val navigator = LocalConsoleNavigator.current

    LaunchedEffect(requestedTab) {
        viewModel.dispatch(ConsoleIntent.RequestTab(requestedTab))
        if (requestedTab != null) onRequestConsumed()
    }

    ConsoleContent(
        tabs = ConsoleNavigation.tabs,
        selectedIndex = viewModel.state.selectedIndex,
        dispatch = viewModel::dispatch,
        onClose = navigator::close,
    )
}
