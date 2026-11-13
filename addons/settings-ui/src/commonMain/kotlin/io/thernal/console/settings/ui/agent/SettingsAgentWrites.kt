package io.thernal.console.settings.ui.agent

import io.thernal.console.agent.AgentConfig
import io.thernal.console.settings.EntryKind
import io.thernal.console.settings.SettingsEntry
import io.thernal.console.settings.SettingsSection
import io.thernal.console.settings.ui.store.SettingsStore

private const val NONE = "none"

/**
 * `settings_set`: applies the value through the section's `update` and persists it, the same two steps a row of the
 * Settings tab takes, so the tab shows the change and it survives a relaunch.
 */
internal fun setEntry(
    sections: List<SettingsSection>,
    sectionId: String,
    key: String,
    raw: String,
): String {
    val section = writableSection(sections, sectionId)
    return setIn(section, key, raw.trim())
}

/** `settings_reset`: one section, or every section but the agent's, back to the code defaults. */
internal fun resetSettings(
    sections: List<SettingsSection>,
    sectionId: String?,
): String {
    val targets = if (sectionId.isNullOrBlank()) {
        sections.filterIsInstance<SettingsSection.Entries<*>>().filter { it.id != AgentConfig.SETTINGS_SECTION_ID }
    } else {
        listOf(writableSection(sections, sectionId))
    }
    targets.forEach(SettingsStore::resetSectionNow)
    return "Reset to the code defaults: ${targets.joinToString { it.id }}."
}

private fun writableSection(
    sections: List<SettingsSection>,
    sectionId: String,
): SettingsSection.Entries<*> {
    require(sectionId != AgentConfig.SETTINGS_SECTION_ID) {
        "The agent section is read-only for agents; a person changes it in Console Settings"
    }
    val section = requireNotNull(sections.firstOrNull { it.id == sectionId }) {
        "No settings section \"$sectionId\"; sections: ${sections.joinToString { it.id }}"
    }
    require(section is SettingsSection.Entries<*>) { "Section \"$sectionId\" has a custom UI and nothing to set" }
    return section
}

private fun <C : Any> setIn(
    section: SettingsSection.Entries<C>,
    key: String,
    raw: String,
): String {
    val entry = requireNotNull(section.entries.firstOrNull { it.key == key }) {
        "No entry \"$key\" in \"${section.id}\"; keys: ${section.entries.joinToString { it.key }}"
    }
    require(entry.enabledWhen(section.config.value)) {
        "\"${section.id}.$key\" is disabled by another entry of the section; settings shows which"
    }
    return write(section, entry, raw)
}

private fun <C : Any, T> write(
    section: SettingsSection.Entries<C>,
    entry: SettingsEntry<C, T>,
    raw: String,
): String {
    val value = parse(entry, raw)
    section.update { entry.write(this, value) }
    SettingsStore.persist(section, entry, value)
    return "${section.id}.${entry.key} = ${valueText(entry, section.config.value)}"
}

@Suppress("UNCHECKED_CAST")
private fun <C : Any, T> parse(
    entry: SettingsEntry<C, T>,
    raw: String,
): T {
    val name = entry.key
    return when (val kind = entry.kind) {
        EntryKind.Toggle -> requireNotNull(raw.lowercase().toBooleanStrictOrNull()) {
            "\"$name\" takes true or false, not \"$raw\""
        } as T

        is EntryKind.IntField -> {
            val number = requireNotNull(raw.toIntOrNull()) { "\"$name\" takes a number, not \"$raw\"" }
            require(number in kind.min..kind.max) { "\"$name\" takes ${kind.min}..${kind.max}, not $number" }
            number as T
        }

        is EntryKind.EnumPicker<*> -> parseOption(name, kind, raw) as T

        EntryKind.Tags -> entry.codec.decode(raw) as T
    }
}

private fun parseOption(
    name: String,
    kind: EntryKind.EnumPicker<*>,
    raw: String,
): Enum<*>? {
    if (kind.noneLabel != null && (raw.isEmpty() || raw.equals(NONE, ignoreCase = true))) return null
    return requireNotNull(kind.options.firstOrNull { it.name.equals(raw, ignoreCase = true) }) {
        val none = if (kind.noneLabel != null) " or $NONE" else ""
        "\"$name\" takes one of ${kind.options.joinToString { it.name }}$none, not \"$raw\""
    }
}
