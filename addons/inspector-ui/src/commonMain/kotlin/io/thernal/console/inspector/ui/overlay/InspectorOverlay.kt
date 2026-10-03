package io.thernal.console.inspector.ui.overlay

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import io.thernal.console.inspector.ConsoleInspector
import io.thernal.console.inspector.ui.engine.InspectorEngine

/** Backdrop layer: everything the inspector draws over the host app. It never handles touches. */
@Composable
internal fun BoxScope.InspectorBackdrop() {
    val config by ConsoleInspector.config.collectAsState()
    if (config.enabled) InspectorDrawLayer()
}

/** Capture layer: swallows touches and picks the node under the pointer while inspect mode is on. */
@Composable
internal fun BoxScope.InspectorCapture() {
    val config by ConsoleInspector.config.collectAsState()
    val isInspecting by InspectorEngine.inspectMode.collectAsState()
    if (config.enabled && isInspecting) {
        InspectorPickLayer(
            onPick = { point, isPress ->
                InspectorEngine.selectAt(point, isPress)
            },
        )
    }
}
