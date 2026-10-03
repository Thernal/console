package io.thernal.console.inspector.ui

import io.thernal.console.api.autoinit.ConsoleAutoInitProvider
import io.thernal.console.inspector.ui.addon.InspectorAddon

internal class InspectorAutoInit : ConsoleAutoInitProvider() {
    override fun init() {
        InspectorAddon.install()
    }
}
