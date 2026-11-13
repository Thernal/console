package io.thernal.console.runtime.console

/** Whether the calling thread is the one the app's UI runs on, where blocking would freeze the app. */
internal expect fun isMainThread(): Boolean
