package io.thernal.console.runtime.console

import platform.Foundation.NSThread

internal actual fun isMainThread(): Boolean {
    return NSThread.isMainThread
}
