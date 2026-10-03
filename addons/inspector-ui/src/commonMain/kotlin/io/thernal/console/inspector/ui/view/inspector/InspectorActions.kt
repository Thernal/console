package io.thernal.console.inspector.ui.view.inspector

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdsClick
import androidx.compose.material.icons.filled.PauseCircle
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.PlayCircle
import androidx.compose.material.icons.outlined.RestartAlt
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalClipboard
import io.thernal.console.api.navigation.LocalConsoleNavigator
import io.thernal.console.designsystem.components.core.DsIcon
import io.thernal.console.designsystem.components.core.DsIconButton
import io.thernal.console.designsystem.foundation.theme.Theme
import io.thernal.console.inspector.ConsoleInspector
import io.thernal.console.inspector.InspectorCommand
import io.thernal.console.inspector.ui.engine.InspectorEngine
import io.thernal.console.inspector.ui.engine.TreeExporter
import io.thernal.console.ui.common.toTextClipEntry
import kotlinx.coroutines.launch

@Composable
internal fun InspectorActions() {
    val navigator = LocalConsoleNavigator.current
    val clipboard = LocalClipboard.current
    val scope = rememberCoroutineScope()
    val isFrozen by InspectorEngine.isFrozen.collectAsState()
    val config by ConsoleInspector.config.collectAsState()

    Row(horizontalArrangement = Arrangement.spacedBy(Theme.dimens.dp4)) {
        DsIconButton(
            onClick = {
                InspectorEngine.refresh(force = true)
                InspectorEngine.setInspectMode(true)
                navigator.close()
            },
            enabled = config.enabled,
        ) {
            DsIcon(icon = Icons.Default.AdsClick, color = Theme.colors.content02)
        }
        DsIconButton(onClick = { InspectorEngine.setFrozen(!isFrozen) }) {
            DsIcon(
                icon = if (isFrozen) Icons.Outlined.PlayCircle else Icons.Default.PauseCircle,
                color = if (isFrozen) Theme.colors.warning else Theme.colors.content02,
            )
        }
        DsIconButton(onClick = { ConsoleInspector.dispatch(InspectorCommand.Refresh) }) {
            DsIcon(icon = Icons.Default.Refresh, color = Theme.colors.content02)
        }
        DsIconButton(
            onClick = {
                val text = TreeExporter.export(
                    snapshot = InspectorEngine.snapshot.value,
                    hideFramework = config.hideFrameworkNodes,
                    counts = InspectorEngine.nodeCounts.value,
                )
                scope.launch { clipboard.setClipEntry(text.toTextClipEntry()) }
            },
        ) {
            DsIcon(icon = Icons.Outlined.ContentCopy, color = Theme.colors.content02)
        }
        DsIconButton(onClick = { ConsoleInspector.dispatch(InspectorCommand.ResetStats) }) {
            DsIcon(icon = Icons.Outlined.RestartAlt, color = Theme.colors.content02)
        }
    }
}
