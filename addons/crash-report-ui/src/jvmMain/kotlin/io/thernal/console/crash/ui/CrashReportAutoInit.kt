package io.thernal.console.crash.ui

import io.thernal.console.api.autoinit.ConsoleInitializer
import io.thernal.console.crash.ui.addon.CrashReportAddon

internal class CrashReportAutoInit : ConsoleInitializer {

    override fun init() {
        CrashReportAddon.install()
    }
}
