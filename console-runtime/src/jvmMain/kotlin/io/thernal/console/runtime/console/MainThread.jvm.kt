package io.thernal.console.runtime.console

import java.awt.EventQueue

private const val EVENT_DISPATCH_THREAD_PREFIX = "AWT-EventQueue-"

/**
 * The AWT event dispatch thread, Compose Desktop's UI thread. Its name is checked first, so a process without a UI
 * never starts AWT just to ask.
 */
internal actual fun isMainThread(): Boolean {
    return Thread.currentThread().name.startsWith(EVENT_DISPATCH_THREAD_PREFIX) && EventQueue.isDispatchThread()
}
