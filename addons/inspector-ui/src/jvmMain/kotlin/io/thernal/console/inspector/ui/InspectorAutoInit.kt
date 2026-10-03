package io.thernal.console.inspector.ui

import io.thernal.console.api.autoinit.ConsoleInitializer
import io.thernal.console.inspector.ui.addon.InspectorAddon

internal class InspectorAutoInit : ConsoleInitializer {
    override fun init() {
        InspectorAddon.install()
    }
}
