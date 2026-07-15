package io.thernal.console.settings.ui.store

import io.thernal.console.io.ConsoleFileSystem
import io.thernal.console.settings.SettingsEntry
import io.thernal.console.settings.SettingsSection
import io.thernal.console.settings.settingsEntries
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import kotlinx.coroutines.flow.MutableStateFlow

private data class FakeConfig(
    val enabled: Boolean = false,
    val maxCount: Int = 10,
)

/**
 * Exercises [SettingsStore] through its internal synchronous entry points
 * ([SettingsStore.writeOverride], [SettingsStore.applyOverrides], [SettingsStore.clearAllNow])
 * rather than the live [io.thernal.console.settings.SettingsRegistry] singleton: on native
 * targets, this module's own `@EagerInitialization` auto-init already attaches
 * [SettingsStore.install] to the registry before any test runs, so a second `install()` call (or
 * anything relying on it being the first consumer) would legitimately throw.
 */
@OptIn(ExperimentalUuidApi::class)
class SettingsStoreTest {

    private lateinit var directoryPath: String

    @BeforeTest
    fun setUp() {
        directoryPath = "${ConsoleFileSystem.temporaryDirectoryPath()}/settings-store-test-${Uuid.random()}"
        SettingsStore.directoryPathForTest = directoryPath
    }

    @AfterTest
    fun tearDown() {
        ConsoleFileSystem.listFileNames(directoryPath).forEach { name ->
            ConsoleFileSystem.delete("$directoryPath/$name")
        }
        ConsoleFileSystem.delete(directoryPath)
        SettingsStore.directoryPathForTest = null
    }

    @Test
    fun `a written value survives being read back`() {
        val config = MutableStateFlow(FakeConfig())
        val section = section(config)

        SettingsStore.writeOverride(section, entry(section), true)

        val decoded = readSection(section.id)
        assertEquals("true", decoded?.get("enabled"))
    }

    @Test
    fun `a key no longer declared is dropped on the next write`() {
        val config = MutableStateFlow(FakeConfig())
        val fullSection = section(config)
        SettingsStore.writeOverride(fullSection, entry(fullSection), true)

        @Suppress("UNCHECKED_CAST")
        val prunedSection = settingsEntries(
            id = fullSection.id,
            title = "Fake",
            config = config,
            update = update(config),
        ) {
            int("maxCount", "Max", read = { it.maxCount }, write = { copy(maxCount = it) })
        } as SettingsSection.Entries<FakeConfig>

        @Suppress("UNCHECKED_CAST")
        val prunedEntry = prunedSection.entries.single() as SettingsEntry<FakeConfig, Int>

        SettingsStore.writeOverride(prunedSection, prunedEntry, 42)

        assertEquals(setOf("maxCount"), readSection(fullSection.id)?.keys)
    }

    @Test
    fun `clearAllNow deletes every persisted file`() {
        val config = MutableStateFlow(FakeConfig())
        val section = section(config)
        SettingsStore.writeOverride(section, entry(section), true)

        SettingsStore.clearAllNow()

        assertEquals(emptyList(), ConsoleFileSystem.listFileNames(directoryPath))
    }

    @Test
    fun `clearAllNow resets a passed section's live config back to its default`() {
        val config = MutableStateFlow(FakeConfig())
        val section = section(config)
        SettingsStore.writeOverride(section, entry(section), true)
        config.value = FakeConfig(enabled = true, maxCount = 42) // simulate user edits in-session

        SettingsStore.clearAllNow(sections = listOf(section))

        assertEquals(FakeConfig(), config.value)
    }

    @Test
    fun `applyOverrides restores a persisted value over the code default`() {
        val config = MutableStateFlow(FakeConfig())
        val section = section(config)
        SettingsStore.writeOverride(section, entry(section), true)
        config.value = FakeConfig() // simulate a fresh launch: config resets to its default

        SettingsStore.applyOverrides(section)

        assertEquals(true, config.value.enabled)
    }

    @Test
    fun `applyOverrides is idempotent`() {
        val config = MutableStateFlow(FakeConfig())
        val section = section(config)
        SettingsStore.writeOverride(section, entry(section), true)
        config.value = FakeConfig()

        SettingsStore.applyOverrides(section)
        SettingsStore.applyOverrides(section)

        assertEquals(true, config.value.enabled)
    }

    @Test
    fun `applyOverrides discards an unparseable value without crashing`() {
        val config = MutableStateFlow(FakeConfig())
        val section = section(config)
        writeRaw(section.id, "console-settings v1\nenabled=not-a-boolean")

        SettingsStore.applyOverrides(section)

        assertEquals(false, config.value.enabled)
    }

    @Test
    fun `applyOverrides ignores an unrecognized format version`() {
        val config = MutableStateFlow(FakeConfig())
        val section = section(config)
        writeRaw(section.id, "console-settings v99\nenabled=true")

        SettingsStore.applyOverrides(section)

        assertEquals(false, config.value.enabled)
    }

    @Test
    fun `applyOverrides ignores a key no longer declared`() {
        val config = MutableStateFlow(FakeConfig())
        val section = section(config)
        writeRaw(section.id, "console-settings v1\nenabled=true\northerKey=x")

        SettingsStore.applyOverrides(section)

        assertEquals(true, config.value.enabled)
    }

    @Suppress("UNCHECKED_CAST")
    private fun section(
        config: MutableStateFlow<FakeConfig>,
        id: String = "fake",
    ): SettingsSection.Entries<FakeConfig> {
        val section = settingsEntries(id = id, title = "Fake", config = config, update = update(config)) {
            toggle("enabled", "Enabled", read = { it.enabled }, write = { copy(enabled = it) })
        }
        return section as SettingsSection.Entries<FakeConfig>
    }

    @Suppress("UNCHECKED_CAST")
    private fun entry(section: SettingsSection.Entries<FakeConfig>): SettingsEntry<FakeConfig, Boolean> {
        return section.entries.single() as SettingsEntry<FakeConfig, Boolean>
    }

    private fun update(config: MutableStateFlow<FakeConfig>): (FakeConfig.() -> FakeConfig) -> Unit {
        return { transform -> config.value = config.value.transform() }
    }

    private fun readSection(sectionId: String): Map<String, String>? {
        val raw = ConsoleFileSystem.readBytes("$directoryPath/$sectionId.settings")
        return raw?.let(SettingsFileFormat::decode)
    }

    private fun writeRaw(
        sectionId: String,
        text: String,
    ) {
        ConsoleFileSystem.ensureDirectory(directoryPath)
        ConsoleFileSystem.writeAtomically("$directoryPath/$sectionId.settings", text.encodeToByteArray())
    }
}
