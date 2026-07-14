package io.thernal.console.settings

/** Typed encode/decode for one entry's persisted value — the whole "typed persistence" machinery. */
interface EntryCodec<T> {
    fun encode(value: T): String

    /** `null` → unparseable → the store discards this entry's override rather than crash. */
    fun decode(raw: String): T?
}
