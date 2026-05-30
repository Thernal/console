package az.theternal.console.compose.view.console

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import az.theternal.console.api.addon.ConsoleNavigation
import az.theternal.console.api.addon.ConsoleTab
import az.theternal.console.api.navigation.LocalConsoleNavigator
import az.theternal.console.compose.view.console.model.ConsoleIntent
import az.theternal.console.compose.view.console.model.ConsoleViewModel

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
