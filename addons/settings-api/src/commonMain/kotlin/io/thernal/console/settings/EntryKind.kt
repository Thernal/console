package io.thernal.console.settings

/** Render/validation metadata specific to one entry kind; paired 1:1 with a settings-ui row. */
sealed interface EntryKind<T> {
    data object Toggle : EntryKind<Boolean>

    data class IntField(
        val min: Int,
        val max: Int,
    ) : EntryKind<Int>

    /** [noneLabel] non-null renders an extra "none" option, covering nullable enums like `LogLevel?`. */
    data class EnumPicker<E : Enum<E>>(
        val options: List<E>,
        val noneLabel: String?,
    ) : EntryKind<E?>

    data object Tags : EntryKind<Set<String>>
}
