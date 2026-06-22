package io.thernal.console.ui.autoinit

// Native addons auto-register via a top-level @EagerInitialization property at binary load,
// so no shell-side trigger is needed.
internal actual fun installPlatformAddons() = Unit
