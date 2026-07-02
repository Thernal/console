package io.thernal.console.crash.ui

import android.content.Context
import kotlin.concurrent.Volatile

/**
 * Application context captured by the addon's `ConsoleAutoInitProvider` before first composition;
 * `CrashFileSystem` reads it to resolve the backup-excluded `noBackupFilesDir` path. Stays `null`
 * (store disabled) when auto-init never ran.
 */
internal object CrashReportContextHolder {

    @Volatile
    var applicationContext: Context? = null
}
