package io.thernal.console.settings.ui.store

import io.thernal.console.core.ConsoleInternalApi
import io.thernal.console.io.ConsoleFileSystem
import io.thernal.console.settings.SettingsEntry
import io.thernal.console.settings.SettingsRegistry
import io.thernal.console.settings.SettingsSection
import kotlin.concurrent.Volatile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

private const val DIRECTORY_NAME = "console-settings"
private const val FILE_SUFFIX = ".settings"

/**
 * Persists sparse per-key overrides for every [SettingsSection.Entries] section, one small file
 * per section. Restoring is a synchronous read at registration time (a few hundred bytes);
 * writes go to a background dispatcher so a settings change never blocks the UI thread.
 */
@OptIn(ConsoleInternalApi::class)
internal object SettingsStore {

    private val writeScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    /** Test-only: overrides the resolved base directory instead of `console-settings`. */
    @Volatile
    internal var directoryPathForTest: String? = null

    /** Attaches as the registry's single consumer — replays the backlog and future registrations. */
    fun install() {
        SettingsRegistry.setOnRegistered(::restore)
    }

    private fun restore(section: SettingsSection) {
        if (section !is SettingsSection.Entries<*>) return
        applyOverrides(section)
    }

    /** The synchronous core of [restore] for a known [SettingsSection.Entries], exposed for tests. */
    internal fun <C : Any> applyOverrides(section: SettingsSection.Entries<C>) {
        val raw = readKeyValues(section.id) ?: return
        val known = section.entries.associateBy { it.key }
        raw.forEach { (key, value) ->
            val entry = known[key] ?: return@forEach
            applyOverride(section, entry, value)
        }
    }

    private fun <C : Any, T> applyOverride(
        section: SettingsSection.Entries<C>,
        entry: SettingsEntry<C, T>,
        raw: String,
    ) {
        val value = entry.codec.decode(raw) ?: return
        section.update { entry.write(this, value) }
    }

    /** Called only from the central renderers — writing through this IS the "user-touched" rule. */
    fun <C : Any, T> persist(
        section: SettingsSection.Entries<C>,
        entry: SettingsEntry<C, T>,
        value: T,
    ) {
        writeScope.launch { writeOverride(section, entry, value) }
    }

    /** The synchronous core of [persist], exposed for deterministic tests. */
    internal fun <C : Any, T> writeOverride(
        section: SettingsSection.Entries<C>,
        entry: SettingsEntry<C, T>,
        value: T,
    ) {
        val current = readKeyValues(section.id).orEmpty()
        val knownKeys = section.entries.mapTo(mutableSetOf()) { it.key }
        val next = current.filterKeys { it in knownKeys } + (entry.key to entry.codec.encode(value))
        writeKeyValues(section.id, next)
    }

    /** Deletes every persisted override file; in-session values stay until the next launch. */
    fun clearAll() {
        writeScope.launch { clearAllNow() }
    }

    /** The synchronous core of [clearAll], exposed for deterministic tests. */
    internal fun clearAllNow() {
        val directory = baseDirectoryPath() ?: return
        ConsoleFileSystem.listFileNames(directory).forEach { name ->
            ConsoleFileSystem.delete("$directory/$name")
        }
    }

    private fun readKeyValues(sectionId: String): Map<String, String>? {
        val path = filePath(sectionId) ?: return null
        return ConsoleFileSystem.readBytes(path)?.let(SettingsFileFormat::decode)
    }

    private fun writeKeyValues(
        sectionId: String,
        entries: Map<String, String>,
    ) {
        val directory = baseDirectoryPath() ?: return
        if (!ConsoleFileSystem.ensureDirectory(directory)) return
        ConsoleFileSystem.writeAtomically(
            path = "$directory/$sectionId$FILE_SUFFIX",
            bytes = SettingsFileFormat.encode(entries),
        )
    }

    private fun filePath(sectionId: String): String? {
        val directory = baseDirectoryPath() ?: return null
        return "$directory/$sectionId$FILE_SUFFIX"
    }

    private fun baseDirectoryPath(): String? {
        return directoryPathForTest ?: ConsoleFileSystem.baseDirectoryPath(DIRECTORY_NAME)
    }
}
