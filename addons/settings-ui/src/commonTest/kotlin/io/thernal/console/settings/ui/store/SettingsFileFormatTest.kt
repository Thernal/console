package io.thernal.console.settings.ui.store

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class SettingsFileFormatTest {

    @Test
    fun `encode then decode round-trips every entry`() {
        val entries = mapOf("a" to "1", "b" to "true", "c" to "x,y,z")

        val decoded = SettingsFileFormat.decode(SettingsFileFormat.encode(entries))

        assertEquals(entries, decoded)
    }

    @Test
    fun `decode rejects an unrecognized header`() {
        assertNull(SettingsFileFormat.decode("console-settings v99\na=1".encodeToByteArray()))
    }

    @Test
    fun `decode ignores a line without a separator`() {
        val decoded = SettingsFileFormat.decode(
            "console-settings v1\na=1\nmalformed\nb=2".encodeToByteArray(),
        )

        assertEquals(mapOf("a" to "1", "b" to "2"), decoded)
    }

    @Test
    fun `encode of an empty map still carries the header`() {
        val decoded = SettingsFileFormat.decode(SettingsFileFormat.encode(emptyMap()))

        assertEquals(emptyMap(), decoded)
    }
}
