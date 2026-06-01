package az.theternal.console.compose.view.console.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.outlined.BugReport
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.ImageVector
import az.theternal.console.api.addon.ConsoleTab
import az.theternal.console.compose.view.console.model.ConsoleIntent
import az.theternal.console.designsystem.components.core.DsIcon
import az.theternal.console.designsystem.components.core.DsText
import az.theternal.console.designsystem.components.core.navigationbar.DsNavigationBar
import az.theternal.console.designsystem.components.core.navigationbar.DsNavigationBarItem
import az.theternal.console.designsystem.components.provider.ThemeProvider
import az.theternal.console.designsystem.foundation.theme.DsPreview
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

private fun previewTab(
    name: String,
    icon: ImageVector,
) = object : ConsoleTab {
    override val title = name
    override val icon = icon

    @Composable override fun Content() {}
}

@DsPreview
@Composable
private fun PreviewConsoleNavigationBar() {
    ThemeProvider {
        ConsoleNavigationBar(
            tabs = listOf(
                previewTab("Logs", Icons.AutoMirrored.Outlined.List),
                previewTab("Stepper", Icons.Outlined.BugReport),
            ),
            selectedIndex = remember { mutableStateOf(0) },
            dispatch = {},
        )
    }
}
