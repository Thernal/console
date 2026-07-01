@file:OptIn(ConsoleInternalApi::class)

package io.thernal.console.crash.ui.addon

import io.thernal.console.api.addon.ConsoleAddon
import io.thernal.console.core.ConsoleInternalApi
import io.thernal.console.crash.ui.runtime.CrashReportRuntime
import io.thernal.console.crash.ui.writer.CrashStreamWriter
import io.thernal.console.runtime.console.Console

object CrashReportAddon : ConsoleAddon {

    override fun onInstall() {
        CrashReportRuntime.start()
        Console.addObserver(CrashStreamWriter)
    }
}
