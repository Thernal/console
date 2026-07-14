package io.thernal.console.settings

import kotlinx.coroutines.flow.StateFlow

/**
 * Builds a [SettingsSection.Entries] section bound to an addon facade's existing `config` /
 * `update` pair — the facade `StateFlow` stays the single source of truth. [id] is also the
 * persistence file name; entry keys declared in [build] must be unique within the section.
 */
fun <C : Any> settingsEntries(
    id: String,
    title: String,
    order: Int = Int.MAX_VALUE,
    config: StateFlow<C>,
    update: (C.() -> C) -> Unit,
    build: SettingsEntriesScope<C>.() -> Unit,
): SettingsSection {
    val scope = SettingsEntriesScopeBuilder<C>()
    scope.build()
    val duplicateKey = scope.entries
        .groupingBy { it.key }
        .eachCount()
        .entries
        .firstOrNull { it.value > 1 }
        ?.key
    check(duplicateKey == null) { "Duplicate settings entry key \"$duplicateKey\" in section \"$id\"" }
    return SettingsSection.Entries(
        id = id,
        title = title,
        order = order,
        config = config,
        update = update,
        entries = scope.entries,
    )
}

private class SettingsEntriesScopeBuilder<C : Any> : SettingsEntriesScope<C> {

    val entries = mutableListOf<SettingsEntry<C, *>>()

    override fun toggle(
        key: String,
        title: String,
        description: String?,
        enabledWhen: (C) -> Boolean,
        read: (C) -> Boolean,
        write: C.(Boolean) -> C,
    ) {
        entries += SettingsEntry(
            key = key,
            title = title,
            description = description,
            kind = EntryKind.Toggle,
            read = read,
            write = write,
            enabledWhen = enabledWhen,
            codec = BooleanCodec,
        )
    }

    override fun int(
        key: String,
        title: String,
        min: Int,
        max: Int,
        description: String?,
        enabledWhen: (C) -> Boolean,
        read: (C) -> Int,
        write: C.(Int) -> C,
    ) {
        entries += SettingsEntry(
            key = key,
            title = title,
            description = description,
            kind = EntryKind.IntField(min = min, max = max),
            read = read,
            write = write,
            enabledWhen = enabledWhen,
            codec = IntCodec,
        )
    }

    override fun <E : Enum<E>> enum(
        key: String,
        title: String,
        options: List<E>,
        noneLabel: String?,
        description: String?,
        enabledWhen: (C) -> Boolean,
        read: (C) -> E?,
        write: C.(E?) -> C,
    ) {
        entries += SettingsEntry(
            key = key,
            title = title,
            description = description,
            kind = EntryKind.EnumPicker(options = options, noneLabel = noneLabel),
            read = read,
            write = write,
            enabledWhen = enabledWhen,
            codec = EnumCodec(options),
        )
    }

    override fun tags(
        key: String,
        title: String,
        description: String?,
        enabledWhen: (C) -> Boolean,
        read: (C) -> Set<String>,
        write: C.(Set<String>) -> C,
    ) {
        entries += SettingsEntry(
            key = key,
            title = title,
            description = description,
            kind = EntryKind.Tags,
            read = read,
            write = write,
            enabledWhen = enabledWhen,
            codec = TagsCodec,
        )
    }
}

private object BooleanCodec : EntryCodec<Boolean> {
    override fun encode(value: Boolean): String {
        return value.toString()
    }

    override fun decode(raw: String): Boolean? {
        return raw.toBooleanStrictOrNull()
    }
}

private object IntCodec : EntryCodec<Int> {
    override fun encode(value: Int): String {
        return value.toString()
    }

    override fun decode(raw: String): Int? {
        return raw.toIntOrNull()
    }
}

private object TagsCodec : EntryCodec<Set<String>> {
    override fun encode(value: Set<String>): String {
        return value.joinToString(separator = ",")
    }

    override fun decode(raw: String): Set<String> {
        return raw.split(",").map { it.trim() }.filter { it.isNotEmpty() }.toSet()
    }
}

/** `null` covers both the explicit "none" choice and an unparseable/renamed enum constant. */
private class EnumCodec<E : Enum<E>>(private val options: List<E>) : EntryCodec<E?> {
    override fun encode(value: E?): String {
        return value?.name.orEmpty()
    }

    override fun decode(raw: String): E? {
        return options.firstOrNull { it.name == raw }
    }
}
