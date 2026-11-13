package io.thernal.console.agent.ui.device

import io.thernal.console.io.ConsoleFileSystem
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

private const val DIRECTORY_NAME = "console-agent"
private const val FILE_NAME = "device-id"

/**
 * A device id kept in the app's own storage, so a restarted app (after a crash, say) is the same device to the hub
 * and its feeds read on across the restart. Made on first use; reinstalling the app makes a new one. Null when the
 * storage is not available, and then the caller falls back to an id per process.
 */
@OptIn(ExperimentalUuidApi::class)
internal fun storedDeviceId(): String? {
    val directory = ConsoleFileSystem.baseDirectoryPath(DIRECTORY_NAME) ?: return null
    val path = "$directory/$FILE_NAME"
    val stored = ConsoleFileSystem.readBytes(path)?.decodeToString()?.trim()
    if (!stored.isNullOrEmpty()) return stored
    val id = Uuid.random().toString()
    val written = ConsoleFileSystem.ensureDirectory(directory) &&
        ConsoleFileSystem.writeAtomically(path = path, bytes = id.encodeToByteArray())
    return id.takeIf { written }
}
