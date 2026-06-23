package io.thernal.console.sample.platform

import android.os.Build

actual fun platformLabel(): String {
    return "Android ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})"
}

actual fun deviceModel(): String {
    return "${Build.MANUFACTURER} ${Build.MODEL}"
}
