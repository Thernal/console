package io.thernal.console.runtime.console

import android.os.Looper

internal actual fun isMainThread(): Boolean {
    return Looper.getMainLooper().isCurrentThread
}
