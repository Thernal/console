package io.thernal.console.inspector.ui.dock

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.OpenInNew
import androidx.compose.material.icons.outlined.Close
import androidx.compose.runtime.Composable
import io.thernal.console.api.navigation.LocalConsoleNavigator
import io.thernal.console.designsystem.components.core.DsQuickAction
import io.thernal.console.inspector.ui.engine.InspectorEngine
import io.thernal.console.inspector.ui.navigation.InspectorTab

/** Shown beside the collapsed dock while picking: jump to the details, or leave inspect mode. */
@Composable
internal fun InspectorQuickAction() {
    val navigator = LocalConsoleNavigator.current
    DsQuickAction(
        icon = Icons.AutoMirrored.Outlined.OpenInNew,
        onClick = {
            InspectorEngine.setInspectMode(false, keepSelection = true)
            navigator.openTab(InspectorTab)
        },
    )
    DsQuickAction(
        icon = Icons.Outlined.Close,
        isPrimary = true,
        onClick = { InspectorEngine.setInspectMode(false) },
    )
}
