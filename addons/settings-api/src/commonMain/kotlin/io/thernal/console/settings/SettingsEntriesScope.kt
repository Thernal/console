package io.thernal.console.settings

/** DSL receiver for [settingsEntries] — one function per entry kind, persistence included. */
interface SettingsEntriesScope<C : Any> {
    fun toggle(
        key: String,
        title: String,
        description: String? = null,
        enabledWhen: (C) -> Boolean = { true },
        read: (C) -> Boolean,
        write: C.(Boolean) -> C,
    )

    fun int(
        key: String,
        title: String,
        min: Int = 0,
        max: Int = Int.MAX_VALUE,
        description: String? = null,
        enabledWhen: (C) -> Boolean = { true },
        read: (C) -> Int,
        write: C.(Int) -> C,
    )

    /** [noneLabel] non-null renders an extra "none" option and makes the value nullable. */
    fun <E : Enum<E>> enum(
        key: String,
        title: String,
        options: List<E>,
        noneLabel: String? = null,
        description: String? = null,
        enabledWhen: (C) -> Boolean = { true },
        read: (C) -> E?,
        write: C.(E?) -> C,
    )

    fun tags(
        key: String,
        title: String,
        description: String? = null,
        enabledWhen: (C) -> Boolean = { true },
        read: (C) -> Set<String>,
        write: C.(Set<String>) -> C,
    )
}
