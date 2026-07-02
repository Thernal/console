package io.thernal.console.crash.ui

import io.thernal.console.api.autoinit.ConsoleAutoInitProvider
import io.thernal.console.crash.ui.addon.CrashReportAddon

internal class CrashReportAutoInit : ConsoleAutoInitProvider() {

    override fun init() {
        // The Context must land before install(): the store resolves its backup-excluded
        // noBackupFilesDir path during CrashReportRuntime.start().
        CrashReportContextHolder.applicationContext = context?.applicationContext
        CrashReportAddon.install()
    }
}
