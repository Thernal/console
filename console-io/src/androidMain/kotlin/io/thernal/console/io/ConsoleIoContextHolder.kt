package io.thernal.console.io

import android.content.Context
import kotlin.concurrent.Volatile

/**
 * Application context captured by an addon's `ConsoleAutoInitProvider` before first composition;
 * [ConsoleFileSystem] reads it to resolve backup-excluded base directories. Stays `null` (stores
 * disabled) when auto-init never ran.
 */
object ConsoleIoContextHolder {

    @Volatile
    var applicationContext: Context? = null
}
