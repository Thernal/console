package io.thernal.console.core

/**
 * Marks registration APIs that are internal to Console and its first-party addons.
 *
 * These are not part of the stable public surface: consumers compose behavior by adding
 * addon dependencies, not by calling registration directly. Opting in is a deliberate,
 * reviewable step — it raises the bar against casual or accidental third-party
 * registration, but it is not a security boundary (any caller can opt in, and a release
 * binary can be reverse-engineered). Pair it with `Console.seal()`
 * and release R8 obfuscation for defense in depth.
 */
@RequiresOptIn(
    message = "Internal Console registration API for first-party addons only. " +
        "Not part of the stable public API and may change without notice. " +
        "Opt in only when building a Console addon.",
    level = RequiresOptIn.Level.ERROR,
)
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY)
annotation class ConsoleInternalApi
