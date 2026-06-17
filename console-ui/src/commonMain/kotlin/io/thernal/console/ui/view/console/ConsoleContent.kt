package io.thernal.console.ui.view.console

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.BugReport
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import io.thernal.console.api.addon.ConsoleTab
import io.thernal.console.ui.view.console.components.ConsoleNavigationBar
import io.thernal.console.ui.view.console.model.ConsoleIntent
import io.thernal.console.designsystem.components.core.DsAppBar
import io.thernal.console.designsystem.components.core.DsIcon
import io.thernal.console.designsystem.components.core.DsIconButton
import io.thernal.console.designsystem.components.core.DsScaffold
import io.thernal.console.designsystem.components.core.DsText
import io.thernal.console.designsystem.components.provider.ThemeProvider
import io.thernal.console.designsystem.foundation.theme.DsPreview
import io.thernal.console.designsystem.foundation.theme.Theme

@Composable
internal fun ConsoleContent(
    tabs: List<ConsoleTab>,
    selectedIndex: State<Int>,
    dispatch: (ConsoleIntent) -> Unit,
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
                    tabs.getOrNull(selectedIndex.value)?.Actions()

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
                ConsoleNavigationBar(
                    tabs = tabs,
                    selectedIndex = selectedIndex,
                    dispatch = dispatch,
                )
            }
        } else {
            null
        },
    ) { padding ->
        AnimatedContent(
            targetState = selectedIndex.value,
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
private fun PreviewConsoleContentSingleTab() {
    ThemeProvider {
        ConsoleContent(
            tabs = listOf(previewTab("Logs", Icons.AutoMirrored.Outlined.List)),
            selectedIndex = remember { mutableStateOf(0) },
            dispatch = {},
            onClose = {},
        )
    }
}

@DsPreview
@Composable
private fun PreviewConsoleContentMultipleTabs() {
    ThemeProvider {
        ConsoleContent(
            tabs = listOf(
                previewTab("Logs", Icons.AutoMirrored.Outlined.List),
                previewTab("Stepper", Icons.Outlined.BugReport),
            ),
            selectedIndex = remember { mutableStateOf(0) },
            dispatch = {},
            onClose = {},
        )
    }
}
