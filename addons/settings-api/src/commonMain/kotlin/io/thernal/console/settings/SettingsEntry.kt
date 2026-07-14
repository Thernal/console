package io.thernal.console.settings

/** One row inside a [SettingsSection.Entries] section. */
sealed interface SettingsEntry<C : Any> {
    val title: String

    /**
     * A persisted, typed entry — [key] + [read]/[write] + [codec] together are the persistence
     * binding, so declaring an entry is the only thing an addon does; there is no second
     * declaration to keep in sync.
     */
    class Field<C : Any, T> internal constructor(
        val key: String,
        override val title: String,
        val description: String?,
        val kind: EntryKind<T>,
        val read: (C) -> T,
        val write: C.(T) -> C,
        val enabledWhen: (C) -> Boolean,
        val codec: EntryCodec<T>,
    ) : SettingsEntry<C>

    /** A settings-owned operation (e.g. General → "Reset all settings"); never persisted. */
    class Action<C : Any> internal constructor(
        override val title: String,
        val destructive: Boolean,
        val onClick: () -> Unit,
    ) : SettingsEntry<C>
}
