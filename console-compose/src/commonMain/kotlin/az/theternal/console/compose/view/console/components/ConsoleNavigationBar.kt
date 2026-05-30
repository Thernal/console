package az.theternal.console.compose.view.console.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import az.theternal.console.api.addon.ConsoleTab
import az.theternal.console.compose.view.console.ConsoleIntent
import az.theternal.console.designsystem.components.core.DsIcon
import az.theternal.console.designsystem.components.core.DsText
import az.theternal.console.designsystem.components.core.navigationbar.DsNavigationBar
import az.theternal.console.designsystem.components.core.navigationbar.DsNavigationBarItem
import az.theternal.console.designsystem.foundation.theme.Theme

@Composable
fun ConsoleNavigationBar(
    tabs: List<ConsoleTab>,
    selectedIndex: State<Int>,
    dispatch: (ConsoleIntent) -> Unit,
) {
    DsNavigationBar {
        tabs.forEachIndexed { index, tab ->
            DsNavigationBarItem(
                selected = index == selectedIndex.value,
                onClick = {
                    dispatch(ConsoleIntent.SelectTab(index))
                },
                icon = {
                    DsIcon(
                        icon = tab.icon,
                        size = Theme.metrics.iconMd,
                    )
                },
                label = {
                    DsText(
                        text = tab.title,
                        style = Theme.typography.label01,
                    )
                },
            )
        }
    }
}
