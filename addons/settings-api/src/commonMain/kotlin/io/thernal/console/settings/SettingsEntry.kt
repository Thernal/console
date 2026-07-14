package io.thernal.console.settings

/**
 * A persisted, typed row inside a [SettingsSection.Entries] section — [key] + [read]/[write] +
 * [codec] together are the persistence binding, so declaring an entry is the only thing an addon
 * does; there is no second declaration to keep in sync.
 */
class SettingsEntry<C : Any, T> internal constructor(
    val key: String,
    val title: String,
    val description: String?,
    val kind: EntryKind<T>,
    val read: (C) -> T,
    val write: C.(T) -> C,
    val enabledWhen: (C) -> Boolean,
    val codec: EntryCodec<T>,
)
