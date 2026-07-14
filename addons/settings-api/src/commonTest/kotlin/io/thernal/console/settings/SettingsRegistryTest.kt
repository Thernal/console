@file:OptIn(ConsoleInternalApi::class)

package io.thernal.console.settings

import io.thernal.console.core.ConsoleInternalApi
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class SettingsRegistryTest {

    @BeforeTest
    fun setUp() {
        SettingsRegistry.resetForTest()
    }

    @AfterTest
    fun tearDown() {
        SettingsRegistry.resetForTest()
    }

    @Test
    fun `register rejects a duplicate section id`() {
        SettingsRegistry.register(section("dup"))
        assertFailsWith<IllegalStateException> { SettingsRegistry.register(section("dup")) }
    }

    @Test
    fun `setOnRegistered replays sections registered before attach exactly once`() {
        SettingsRegistry.register(section("before"))
        val restored = mutableListOf<String>()

        SettingsRegistry.setOnRegistered { restored += it.id }

        assertEquals(listOf("before"), restored)
    }

    @Test
    fun `setOnRegistered applies sections registered after attach exactly once`() {
        val restored = mutableListOf<String>()
        SettingsRegistry.setOnRegistered { restored += it.id }

        SettingsRegistry.register(section("after"))

        assertEquals(listOf("after"), restored)
    }

    @Test
    fun `setOnRegistered rejects a second consumer`() {
        SettingsRegistry.setOnRegistered { }
        assertFailsWith<IllegalStateException> { SettingsRegistry.setOnRegistered { } }
    }

    @Test
    fun `sections reflects every registered section regardless of replay timing`() {
        SettingsRegistry.register(section("a"))
        SettingsRegistry.setOnRegistered { }
        SettingsRegistry.register(section("b"))

        assertEquals(listOf("a", "b"), SettingsRegistry.sections.value.map { it.id })
    }

    private fun section(id: String): SettingsSection {
        return settingsCustom(id = id, title = id) { }
    }
}
