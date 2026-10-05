@file:OptIn(ExperimentalComposeRuntimeApi::class)

package io.thernal.console.inspector.ui

import androidx.compose.runtime.Composer
import androidx.compose.runtime.ExperimentalComposeRuntimeApi
import androidx.compose.runtime.tooling.ComposeStackTraceMode

/**
 * Turns on composable names and source positions in the slot table. It is a global switch that
 * compositions already running ignore, so it has to run before the first composition. Android and
 * iOS auto-init does it early enough; on JVM desktop call it before creating the window.
 */
fun enableInspectorSourceInformation() {
    Composer.setDiagnosticStackTraceMode(ComposeStackTraceMode.SourceInformation)
}
