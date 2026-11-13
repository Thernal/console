package io.thernal.console.settings.ui.agent

import io.thernal.console.agent.AgentConfig
import io.thernal.console.core.log.LogLevel
import io.thernal.console.io.ConsoleFileSystem
import io.thernal.console.settings.SettingsSection
import io.thernal.console.settings.settingsCustom
import io.thernal.console.settings.settingsEntries
import io.thernal.console.settings.ui.store.SettingsStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class SettingsAgentTest {
    private data class Config(
        val enabled: Boolean = true,
        val limit: Int = DEFAULT_LIMIT,
        val level: LogLevel? = null,
        val tags: Set<String> = emptySet(),
        val verbose: Boolean = false,
    )

    private val config = MutableStateFlow(Config())
    private val agentConfig = MutableStateFlow(Config())
    private lateinit var sections: List<SettingsSection>

    @BeforeTest
    fun setUp() {
        SettingsStore.directoryPathForTest =
            "${ConsoleFileSystem.temporaryDirectoryPath()}/settings-agent-${Uuid.random()}"
        sections = listOf(
            section(id = "demo", state = config),
            section(id = AgentConfig.SETTINGS_SECTION_ID, state = agentConfig),
            settingsCustom(id = "general", title = "General") {},
        )
    }

    @AfterTest
    fun tearDown() {
        SettingsStore.directoryPathForTest = null
    }

    @Test
    fun `entries are listed with values and kinds and what is disabled`() {
        config.update { it.copy(enabled = false) }

        val text = settingsText(sections, sectionId = "demo")

        val expected = """
            demo · Demo
              enabled = false · Enabled · true or false
              limit = 10 · Limit · number 1..100
              level = none · Level · one of None, Verbose, Debug, Info, Success, Warning, Error, Fatal or none
              tags = none · Tags · tags separated by commas
              verbose = false · Verbose · true or false · disabled by another entry
        """.trimIndent()
        assertEquals(expected, text)
    }

    @Test
    fun `each kind of entry is set from text`() {
        assertEquals("demo.enabled = false", setEntry(sections, "demo", "enabled", "FALSE"))
        assertEquals("demo.limit = 42", setEntry(sections, "demo", "limit", "42"))
        assertEquals("demo.level = Warning", setEntry(sections, "demo", "level", "warning"))
        assertEquals("demo.level = none", setEntry(sections, "demo", "level", "none"))
        assertEquals("demo.tags = a,b", setEntry(sections, "demo", "tags", "a, b,,"))
        assertEquals(Config(enabled = false, limit = 42, tags = setOf("a", "b")), config.value)
    }

    @Test
    fun `bad values and sections are refused with the reason`() {
        assertEquals("\"limit\" takes 1..100, not 500", refusal { setEntry(sections, "demo", "limit", "500") })
        assertEquals(
            "\"enabled\" takes true or false, not \"yes\"",
            refusal { setEntry(sections, "demo", "enabled", "yes") },
        )
        assertEquals(
            "The agent section is read-only for agents; a person changes it in Console Settings",
            refusal { setEntry(sections, AgentConfig.SETTINGS_SECTION_ID, "enabled", "false") },
        )
        assertEquals(
            "Section \"general\" has a custom UI and nothing to set",
            refusal { setEntry(sections, "general", "x", "1") },
        )
        config.update { it.copy(enabled = false) }
        assertEquals(
            "\"demo.verbose\" is disabled by another entry of the section; settings shows which",
            refusal { setEntry(sections, "demo", "verbose", "true") },
        )
    }

    @Test
    fun `reset without a section leaves the agent section alone`() {
        config.update { it.copy(limit = 1) }
        agentConfig.update { it.copy(limit = 2) }

        assertEquals("Reset to the code defaults: demo.", resetSettings(sections, sectionId = null))

        assertEquals(DEFAULT_LIMIT, config.value.limit)
        assertEquals(2, agentConfig.value.limit)
    }

    private fun refusal(block: () -> Unit): String? {
        return assertFailsWith<IllegalArgumentException> { block() }.message
    }

    private fun section(
        id: String,
        state: MutableStateFlow<Config>,
    ): SettingsSection {
        return settingsEntries(
            id = id,
            title = id.replaceFirstChar { it.uppercase() },
            config = state,
            update = { transform -> state.update { it.transform() } },
        ) {
            toggle(key = "enabled", title = "Enabled", read = { it.enabled }, write = { copy(enabled = it) })
            int(
                key = "limit",
                title = "Limit",
                min = 1,
                max = MAX_LIMIT,
                read = { it.limit },
                write = { copy(limit = it) },
            )
            enum(
                key = "level",
                title = "Level",
                options = LogLevel.entries,
                noneLabel = "All",
                read = { it.level },
                write = { copy(level = it) },
            )
            tags(key = "tags", title = "Tags", read = { it.tags }, write = { copy(tags = it) })
            toggle(
                key = "verbose",
                title = "Verbose",
                enabledWhen = { it.enabled },
                read = { it.verbose },
                write = { copy(verbose = it) },
            )
        }
    }

    private companion object {
        const val DEFAULT_LIMIT = 10
        const val MAX_LIMIT = 100
    }
}
