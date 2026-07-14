package io.thernal.console.settings

/**
 * Same DSL receiver as `settings-api` — kept so an addon's `build` lambda (never invoked in the
 * no-op path) still type-checks unchanged between build variants.
 */
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
