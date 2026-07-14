package io.thernal.console.io

/**
 * Minimal platform file-system access shared by addons that persist small amounts of data
 * (crash sessions, settings overrides). Implementations must keep writes safe to call from a
 * dying thread (plain blocking IO, no coroutine machinery).
 */
expect object ConsoleFileSystem {

    /**
     * Backup-excluded platform base directory for [directoryName] (Android `noBackupFilesDir`,
     * iOS Application Support + `NSURLIsExcludedFromBackupKey`, JVM home), or `null` when
     * unavailable (e.g. Android before the auto-init `Context` arrives).
     */
    fun baseDirectoryPath(directoryName: String): String?

    /** Platform temporary directory; used by tests. */
    fun temporaryDirectoryPath(): String

    fun ensureDirectory(path: String): Boolean

    fun listFileNames(directoryPath: String): List<String>

    /** Opens [path] for appending, creating it when missing. */
    fun openAppend(path: String): AppendSink?

    fun readBytes(path: String): ByteArray?

    /** Writes via a temp file + rename so readers never observe a half-written sidecar. */
    fun writeAtomically(
        path: String,
        bytes: ByteArray,
    ): Boolean

    fun rename(
        fromPath: String,
        toPath: String,
    ): Boolean

    fun delete(path: String): Boolean

    fun exists(path: String): Boolean
}
