@file:OptIn(InternalComposeApi::class)

package io.thernal.console.inspector.ui.host

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.InternalComposeApi
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.currentComposer
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.tooling.LocalInspectionTables
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import io.thernal.console.inspector.ConsoleInspector
import io.thernal.console.inspector.InspectorLayoutDirection
import io.thernal.console.inspector.ui.engine.InspectorEngine

/**
 * Wraps the host app content. Keeps its structure stable whatever the config says, so toggling
 * settings never resets app state: only the provided values change.
 */
@Composable
internal fun InspectorHost(content: @Composable () -> Unit) {
    val config by ConsoleInspector.config.collectAsState()
    val baseDensity = LocalDensity.current
    val baseDirection = LocalLayoutDirection.current

    InspectorEngine.rootData = currentComposer.compositionData
    LaunchedEffect(Unit) { InspectorEngine.run() }

    val density = remember(baseDensity, config.fontScale) {
        Density(baseDensity.density, baseDensity.fontScale * (config.fontScale?.factor ?: 1f))
    }
    CompositionLocalProvider(
        LocalInspectionTables provides if (config.enabled) InspectorEngine.tables else null,
        LocalDensity provides density,
        LocalLayoutDirection provides (config.layoutDirection?.toComposeDirection() ?: baseDirection),
        content = content,
    )
}

private fun InspectorLayoutDirection.toComposeDirection(): LayoutDirection {
    return when (this) {
        InspectorLayoutDirection.Ltr -> LayoutDirection.Ltr
        InspectorLayoutDirection.Rtl -> LayoutDirection.Rtl
    }
}
