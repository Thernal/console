package io.thernal.console.ui.autoinit

// Android addons auto-register via a ConsoleAutoInitProvider (ContentProvider) before the
// process reaches first composition, so no shell-side trigger is needed.
internal actual fun installPlatformAddons() = Unit
