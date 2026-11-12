package io.thernal.console.settings.ui.agent

import io.thernal.console.settings.EntryKind
import io.thernal.console.settings.SettingsEntry
import io.thernal.console.settings.SettingsSection

private const val NONE = "none"

/** The `settings` answer: each section, then one line per entry with its value, kind and whether it is enabled. */
internal fun settingsText(
    sections: List<SettingsSection>,
    sectionId: String?,
): String {
    val shown = sections.filter { sectionId.isNullOrBlank() || it.id == sectionId }
    if (shown.isEmpty()) {
        return if (sections.isEmpty()) {
            "No settings are registered."
        } else {
            "No settings section \"$sectionId\"; sections: ${sections.joinToString { it.id }}."
        }
    }
    return shown.joinToString(separator = "\n") { section ->
        when (section) {
            is SettingsSection.Entries<*> -> entriesText(section)
            is SettingsSection.Custom -> "${section.id} · ${section.title} · custom UI, nothing to read or set"
        }
    }
}

private fun <C : Any> entriesText(section: SettingsSection.Entries<C>): String {
    val config = section.config.value
    val lines = section.entries.map { entry ->
        val disabled = if (entry.enabledWhen(config)) "" else " · disabled by another entry"
        "  ${entry.key} = ${valueText(entry, config)} · ${entry.title} · ${kindText(entry.kind)}$disabled"
    }
    return (listOf("${section.id} · ${section.title}") + lines).joinToString(separator = "\n")
}

internal fun <C : Any, T> valueText(
    entry: SettingsEntry<C, T>,
    config: C,
): String {
    return entry.codec.encode(entry.read(config)).ifEmpty { NONE }
}

private fun kindText(kind: EntryKind<*>): String {
    return when (kind) {
        EntryKind.Toggle -> "true or false"

        is EntryKind.IntField -> "number ${kind.min}..${kind.max}"

        is EntryKind.EnumPicker<*> -> {
            val none = if (kind.noneLabel != null) " or $NONE" else ""
            "one of ${kind.options.joinToString { it.name }}$none"
        }

        EntryKind.Tags -> "tags separated by commas"
    }
}
