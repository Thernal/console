package io.thernal.console.inspector.ui.overlay

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import io.thernal.console.inspector.ConsoleInspector

/** Backdrop layer: everything the inspector draws over the host app. It never handles touches. */
@Composable
internal fun BoxScope.InspectorBackdrop() {
    val config by ConsoleInspector.config.collectAsState()
    if (config.enabled) InspectorDrawLayer()
}
