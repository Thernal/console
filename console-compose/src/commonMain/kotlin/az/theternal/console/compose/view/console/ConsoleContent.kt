package az.theternal.console.compose.view.console

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import az.theternal.console.api.addon.ConsoleTab
import az.theternal.console.designsystem.components.core.DsAppBar
import az.theternal.console.designsystem.components.core.DsIcon
import az.theternal.console.designsystem.components.core.DsIconButton
import az.theternal.console.designsystem.components.core.DsScaffold
import az.theternal.console.designsystem.components.core.DsText
import az.theternal.console.designsystem.components.core.navigationbar.DsNavigationBar
import az.theternal.console.designsystem.components.core.navigationbar.DsNavigationBarItem
import az.theternal.console.designsystem.foundation.theme.Theme

@Composable
internal fun ConsoleContent(
    tabs: List<ConsoleTab>,
    selectedIndex: Int,
    onTabSelect: (Int) -> Unit,
    onClose: () -> Unit,
) {
    DsScaffold(
        topBar = {
            DsAppBar(
                leading = {
                    DsText(
                        text = "Console",
                        style = Theme.typography.title01,
                        color = Theme.colors.content01,
                    )
                },
                trailing = {
                    DsIconButton(onClick = onClose) {
                        DsIcon(
                            icon = Icons.Default.Close,
                            color = Theme.colors.content02,
                        )
                    }
                },
            )
        },
        bottomBar = if (tabs.size > 1) {
            {
                DsNavigationBar {
                    tabs.forEachIndexed { index, tab ->
                        DsNavigationBarItem(
                            selected = index == selectedIndex,
                            onClick = { onTabSelect(index) },
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
        } else {
            null
        },
    ) { padding ->
        AnimatedContent(
            targetState = selectedIndex,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "console_tab",
        ) { index ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
            ) {
                tabs.getOrNull(index)?.Content()
            }
        }
    }
}
