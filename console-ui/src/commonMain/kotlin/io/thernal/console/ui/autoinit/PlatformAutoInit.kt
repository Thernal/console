package io.thernal.console.ui.autoinit

/**
 * Triggers platform addon auto-registration that is not handled by an OS/binary startup hook.
 *
 * - Android: no-op — addons register via a `ContentProvider` before the process reaches here.
 * - Native (iOS): no-op — addons register via top-level `@EagerInitialization` at binary load.
 * - JVM: discovers and runs `ConsoleInitializer`s through `ServiceLoader`.
 *
 * Called once from `ConsoleProvider`; implementations must be idempotent.
 */
internal expect fun installPlatformAddons()
