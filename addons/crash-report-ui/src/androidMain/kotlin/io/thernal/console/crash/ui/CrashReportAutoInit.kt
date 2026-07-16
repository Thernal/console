package io.thernal.console.crash.ui

import io.thernal.console.api.autoinit.ConsoleAutoInitProvider
import io.thernal.console.crash.ui.addon.CrashReportAddon

internal class CrashReportAutoInit : ConsoleAutoInitProvider() {

    override fun init() {
        CrashReportAddon.install()
    }
}
