package io.thernal.console.settings.ui.view.settings

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import io.thernal.console.designsystem.components.core.collapsible.DsCollapsible
import io.thernal.console.designsystem.foundation.theme.Theme
import io.thernal.console.settings.EntryKind
import io.thernal.console.settings.SettingsEntry
import io.thernal.console.settings.SettingsSection
import io.thernal.console.settings.ui.store.SettingsStore
import io.thernal.console.settings.ui.view.settings.components.SettingsEnumPickerRow
import io.thernal.console.settings.ui.view.settings.components.SettingsIntFieldRow
import io.thernal.console.settings.ui.view.settings.components.SettingsSearchBar
import io.thernal.console.settings.ui.view.settings.components.SettingsSectionDivider
import io.thernal.console.settings.ui.view.settings.components.SettingsSectionHeader
import io.thernal.console.settings.ui.view.settings.components.SettingsTagsEditorRow
import io.thernal.console.settings.ui.view.settings.components.SettingsToggleRow
import io.thernal.console.settings.ui.view.settings.model.SettingsIntent
import io.thernal.console.settings.ui.view.settings.model.SettingsState
import io.thernal.console.ui.common.LocalSearchQuery
import io.thernal.console.ui.core.select

@Composable
internal fun SettingsContent(
    state: SettingsState,
    dispatch: (SettingsIntent) -> Unit,
) {
    val sections by state.sections
    val query by state.searchQuery

    CompositionLocalProvider(LocalSearchQuery provides state.searchQuery.select { it.text }) {
        DsCollapsible(
            modifier = Modifier.fillMaxSize(),
            header = {
                SettingsSearchBar(
                    query = query,
                    onQueryChange = { dispatch(SettingsIntent.SetQuery(it)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Theme.dimens.dp12, vertical = Theme.dimens.dp8),
                )
            },
        ) {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(bottom = Theme.dimens.dp16),
            ) {
                sections.sortedBy { it.order }.forEach { section ->
                    when (section) {
                        is SettingsSection.Entries<*> -> entriesSection(section, query.text)
                        is SettingsSection.Custom -> customSection(section, query.text)
                    }
                }
            }
        }
    }
}

private fun <C : Any> LazyListScope.entriesSection(
    section: SettingsSection.Entries<C>,
    query: String,
) {
    val sectionMatches = query.isBlank() || section.title.contains(query, ignoreCase = true)
    val hits = if (sectionMatches) {
        section.entries
    } else {
        section.entries.filter { entry -> entry.title.contains(query, ignoreCase = true) }
    }
    if (hits.isEmpty()) return

    item(key = section.id) { SettingsSectionHeader(title = section.title) }
    hits.forEach { entry ->
        item(key = "${section.id}.${entry.key}") {
            EntryRow(section = section, entry = entry)
        }
    }
    item(key = "${section.id}.divider") { SettingsSectionDivider() }
}

private fun LazyListScope.customSection(
    section: SettingsSection.Custom,
    query: String,
) {
    val matches = query.isBlank() ||
        section.title.contains(query, ignoreCase = true) ||
        section.keywords.any { it.contains(query, ignoreCase = true) }
    if (!matches) return

    item(key = section.id) { SettingsSectionHeader(title = section.title) }
    section.content(this)
    item(key = "${section.id}.divider") { SettingsSectionDivider() }
}

@Composable
private fun <C : Any> EntryRow(
    section: SettingsSection.Entries<C>,
    entry: SettingsEntry<C, *>,
) {
    // Not destructured with `by` here on purpose: reading `.value` at this scope would recompose
    // every row in the section whenever ANY field changes. Passing the raw State down lets each
    // row below select only the slice it actually reads.
    val configState = section.config.collectAsState()

    when (val kind = entry.kind) {
        EntryKind.Toggle -> ToggleFieldRow(section, entry, configState)
        is EntryKind.IntField -> IntFieldRow(section, entry, kind, configState)
        is EntryKind.EnumPicker<*> -> EnumFieldRow(section, entry, kind, configState)
        EntryKind.Tags -> TagsFieldRow(section, entry, configState)
    }
}

/**
 * Each branch above is only reached for the [SettingsEntry] its own builder function
 * (`toggle`/`int`/`enum`/`tags`) produced — [EntryKind] and the entry's value type are paired by
 * construction in `settings-api`, so every cast below always succeeds.
 *
 * Each wrapper below derives a `State` per field via [io.thernal.console.ui.core.select] and hands
 * it straight to the row composable — it never reads `.value` itself (no `by`), so its own body has
 * zero read dependency on [configState] and never recomposes when config changes. The row
 * composable is the only place that reads `.value`, and only for its own field, so a change to one
 * field in the shared config doesn't recompose any other row in the section.
 */
@Composable
private fun <C : Any> ToggleFieldRow(
    section: SettingsSection.Entries<C>,
    entry: SettingsEntry<C, *>,
    configState: State<C>,
) {
    @Suppress("UNCHECKED_CAST")
    val typed = entry as SettingsEntry<C, Boolean>
    val checked = configState.select { typed.read(it) }
    val enabled = configState.select { typed.enabledWhen(it) }
    SettingsToggleRow(
        title = typed.title,
        description = typed.description,
        checked = checked,
        enabled = enabled,
        onCheckedChange = { value ->
            section.update { typed.write(this, value) }
            SettingsStore.persist(section, typed, value)
        },
    )
}

@Composable
private fun <C : Any> IntFieldRow(
    section: SettingsSection.Entries<C>,
    entry: SettingsEntry<C, *>,
    kind: EntryKind.IntField,
    configState: State<C>,
) {
    @Suppress("UNCHECKED_CAST")
    val typed = entry as SettingsEntry<C, Int>
    val value = configState.select { typed.read(it) }
    val enabled = configState.select { typed.enabledWhen(it) }
    SettingsIntFieldRow(
        title = typed.title,
        description = typed.description,
        value = value,
        min = kind.min,
        max = kind.max,
        enabled = enabled,
        onValueChange = { newValue ->
            section.update { typed.write(this, newValue) }
            SettingsStore.persist(section, typed, newValue)
        },
    )
}

@Composable
private fun <C : Any, E : Enum<E>> EnumFieldRow(
    section: SettingsSection.Entries<C>,
    entry: SettingsEntry<C, *>,
    kind: EntryKind.EnumPicker<E>,
    configState: State<C>,
) {
    @Suppress("UNCHECKED_CAST")
    val typed = entry as SettingsEntry<C, E?>
    val selected = configState.select { typed.read(it) }
    val enabled = configState.select { typed.enabledWhen(it) }
    SettingsEnumPickerRow(
        title = typed.title,
        description = typed.description,
        options = kind.options,
        noneLabel = kind.noneLabel,
        selected = selected,
        enabled = enabled,
        onSelect = { value ->
            section.update { typed.write(this, value) }
            SettingsStore.persist(section, typed, value)
        },
    )
}

@Composable
private fun <C : Any> TagsFieldRow(
    section: SettingsSection.Entries<C>,
    entry: SettingsEntry<C, *>,
    configState: State<C>,
) {
    @Suppress("UNCHECKED_CAST")
    val typed = entry as SettingsEntry<C, Set<String>>
    val tags = configState.select { typed.read(it) }
    val enabled = configState.select { typed.enabledWhen(it) }
    SettingsTagsEditorRow(
        title = typed.title,
        description = typed.description,
        tags = tags,
        enabled = enabled,
        onTagsChange = { value ->
            section.update { typed.write(this, value) }
            SettingsStore.persist(section, typed, value)
        },
    )
}
