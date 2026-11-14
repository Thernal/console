@file:OptIn(InternalComposeApi::class)

package io.thernal.console.inspector.ui.host

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.InternalComposeApi
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.currentComposer
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.tooling.LocalInspectionTables
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalGraphicsContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import io.thernal.console.inspector.ConsoleInspector
import io.thernal.console.inspector.InspectorLayoutDirection
import io.thernal.console.inspector.ui.agent.currentSemanticsRoot
import io.thernal.console.inspector.ui.agent.keepSemanticsRoot
import io.thernal.console.inspector.ui.engine.InspectorEngine
import io.thernal.console.inspector.ui.overlay.rememberInspectorDrawStyle

/**
 * Wraps the host app content. Keeps its structure stable whatever the config says, so toggling
 * settings never resets app state: only the provided values change.
 *
 * The content sits in a plain `Layout` that measures and places it as the console's own `Box` around it does, so the
 * app lays out the same; its draw records a frame for the agent's snapshot when one is asked for ([ContentCapture]).
 * `Layout` is a generic name, so it adds no node to the inspected tree.
 *
 * The tree builder finds the app content by this function's name (`HOST_NAME` in `TreeBuilder.kt`),
 * so keep the two in sync when renaming it.
 */
@Composable
internal fun InspectorHost(content: @Composable () -> Unit) {
    val config by ConsoleInspector.config.collectAsState()
    val baseDensity = LocalDensity.current
    val baseDirection = LocalLayoutDirection.current

    val compositionData = currentComposer.compositionData
    val semanticsRoot = currentSemanticsRoot()
    val captureLayer = rememberGraphicsLayer()
    val graphics = LocalGraphicsContext.current
    val style = rememberInspectorDrawStyle()
    val measurer = rememberTextMeasurer()
    SideEffect {
        InspectorEngine.rootData = compositionData
        keepSemanticsRoot(semanticsRoot)
        ContentCapture.attach(
            layer = captureLayer,
            graphics = graphics,
            style = style,
            measurer = measurer,
            density = baseDensity,
            layoutDirection = baseDirection,
        )
    }
    DisposableEffect(captureLayer) { onDispose { ContentCapture.detach(captureLayer) } }
    LaunchedEffect(Unit) { InspectorEngine.run() }

    val density = remember(baseDensity, config.fontScale) {
        Density(baseDensity.density, baseDensity.fontScale * (config.fontScale?.factor ?: 1f))
    }
    Layout(
        content = {
            CompositionLocalProvider(
                LocalInspectionTables provides if (config.enabled) InspectorEngine.tables else null,
                LocalDensity provides density,
                LocalLayoutDirection provides (config.layoutDirection?.toComposeDirection() ?: baseDirection),
                content = content,
            )
        },
        modifier = ContentCapture.modifier,
        measurePolicy = StackAtStart,
    )
}

/** What a `Box` with the default alignment does: loose constraints, every child at the top start. */
private val StackAtStart = MeasurePolicy { measurables, constraints ->
    val loose = constraints.copy(minWidth = 0, minHeight = 0)
    val placeables = measurables.map { it.measure(loose) }
    val width = maxOf(constraints.minWidth, placeables.maxOfOrNull { it.width } ?: 0)
    val height = maxOf(constraints.minHeight, placeables.maxOfOrNull { it.height } ?: 0)
    layout(width, height) { placeables.forEach { it.placeRelative(x = 0, y = 0) } }
}

private fun InspectorLayoutDirection.toComposeDirection(): LayoutDirection {
    return when (this) {
        InspectorLayoutDirection.Ltr -> LayoutDirection.Ltr
        InspectorLayoutDirection.Rtl -> LayoutDirection.Rtl
    }
}
