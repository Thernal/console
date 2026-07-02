package io.thernal.console.crash.ui.handler

internal actual fun installCrashHandler(onCrash: (Throwable) -> Unit) {
    val previous = Thread.getDefaultUncaughtExceptionHandler()
    Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
        onCrash(throwable)
        previous?.uncaughtException(thread, throwable)
    }
}
