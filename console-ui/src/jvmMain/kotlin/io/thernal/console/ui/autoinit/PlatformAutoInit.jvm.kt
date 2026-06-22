package io.thernal.console.ui.autoinit

import io.thernal.console.api.autoinit.runConsoleInitializers

internal actual fun installPlatformAddons() {
    runConsoleInitializers()
}
