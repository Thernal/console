package io.thernal.console.crash.ui.handler

/**
 * Installs the process-wide uncaught-exception handler, chaining to any previously installed
 * handler so the real crash still happens (Android/JVM `Thread.setDefaultUncaughtExceptionHandler`,
 * native `setUnhandledExceptionHook`). [onCrash] runs synchronously on the dying thread before
 * the chain continues.
 */
internal expect fun installCrashHandler(onCrash: (Throwable) -> Unit)
