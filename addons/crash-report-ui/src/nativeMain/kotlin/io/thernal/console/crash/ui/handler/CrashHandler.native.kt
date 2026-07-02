package io.thernal.console.crash.ui.handler

import kotlin.experimental.ExperimentalNativeApi
import kotlin.native.setUnhandledExceptionHook
import kotlin.native.terminateWithUnhandledException

@OptIn(ExperimentalNativeApi::class)
internal actual fun installCrashHandler(onCrash: (Throwable) -> Unit) {
    var previous: ((Throwable) -> Unit)? = null
    previous = setUnhandledExceptionHook { throwable ->
        onCrash(throwable)
        // Chain to the previously installed hook; without one, fall through to the default
        // termination so the crash still reports normally.
        previous?.invoke(throwable) ?: terminateWithUnhandledException(throwable)
    }
}
