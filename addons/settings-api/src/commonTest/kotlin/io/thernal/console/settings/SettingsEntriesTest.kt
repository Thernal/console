package io.thernal.console.settings

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlinx.coroutines.flow.MutableStateFlow

private data class FakeConfig(
    val enabled: Boolean = false,
    val count: Int = 0,
)

private enum class FakeLevel { Low, High }

class SettingsEntriesTest {

    private val config = MutableStateFlow(FakeConfig())
    private val update: (FakeConfig.() -> FakeConfig) -> Unit = { transform ->
        config.value = config.value.transform()
    }

    @Test
    fun `rejects a duplicate entry key`() {
        assertFailsWith<IllegalStateException> {
            settingsEntries(id = "dup", title = "Dup", config = config, update = update) {
                toggle("enabled", "Enabled", read = { it.enabled }, write = { copy(enabled = it) })
                toggle("enabled", "Enabled again", read = { it.enabled }, write = { copy(enabled = it) })
            }
        }
    }

    @Test
    fun `toggle entry carries its kind and codec`() {
        val entry = singleEntry {
            toggle("enabled", "Enabled", read = { it.enabled }, write = { copy(enabled = it) })
        }

        assertEquals(EntryKind.Toggle, entry.kind)
        @Suppress("UNCHECKED_CAST")
        val codec = entry.codec as EntryCodec<Boolean>
        assertEquals(true, codec.decode(codec.encode(true)))
        assertNull(codec.decode("not-a-boolean"))
    }

    @Test
    fun `int entry carries min and max`() {
        val entry = singleEntry {
            int("count", "Count", min = 1, max = 10, read = { it.count }, write = { copy(count = it) })
        }

        assertEquals(EntryKind.IntField(min = 1, max = 10), entry.kind)
    }

    @Test
    fun `enum entry decode treats none and unparseable alike`() {
        val entry = singleEntry {
            enum(
                "level",
                "Level",
                options = FakeLevel.entries,
                noneLabel = "Any",
                read = { null },
                write = { this },
            )
        }

        @Suppress("UNCHECKED_CAST")
        val codec = entry.codec as EntryCodec<FakeLevel?>
        assertNull(codec.decode(""))
        assertNull(codec.decode("garbage"))
        assertEquals(FakeLevel.High, codec.decode(codec.encode(FakeLevel.High)))
    }

    @Test
    fun `tags entry trims blanks and dedupes on decode`() {
        val entry = singleEntry {
            tags("names", "Names", read = { emptySet() }, write = { this })
        }

        @Suppress("UNCHECKED_CAST")
        val codec = entry.codec as EntryCodec<Set<String>>
        assertEquals(setOf("a", "b"), codec.decode(" a, b ,a"))
    }

    @Suppress("UNCHECKED_CAST")
    private fun singleEntry(build: SettingsEntriesScope<FakeConfig>.() -> Unit): SettingsEntry<FakeConfig, *> {
        val section = settingsEntries(id = "s", title = "S", config = config, update = update, build = build)
            as SettingsSection.Entries<FakeConfig>
        return section.entries.single()
    }
}
