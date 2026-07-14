package io.thernal.console.settings

import androidx.compose.foundation.lazy.LazyListScope
import kotlinx.coroutines.flow.StateFlow

/**
 * A registered settings section — one per addon, plus a console-wide section (e.g. General).
 * [id] doubles as the persistence file name (`<id>.settings`) once a store consumes it, so it
 * must be unique across every registered section.
 */
sealed interface SettingsSection {
    val id: String
    val title: String
    val order: Int

    /**
     * Standard path: typed entries bound to an addon facade's `StateFlow` [config], rendered and
     * persisted centrally with no addon-written UI code — see [SettingsEntry].
     */
    class Entries<C : Any> internal constructor(
        override val id: String,
        override val title: String,
        override val order: Int,
        val config: StateFlow<C>,
        val update: (C.() -> C) -> Unit,
        val entries: List<SettingsEntry<C, *>>,
    ) : SettingsSection

    /**
     * Escape hatch for non-standard UI: free-form lazy items contributed straight into the
     * Settings tab's single `LazyColumn`. Loses entry-level search/persistence inside its own
     * boundary; [keywords] widen what title-only search matches for this section.
     */
    class Custom internal constructor(
        override val id: String,
        override val title: String,
        override val order: Int,
        val keywords: Set<String>,
        val content: LazyListScope.() -> Unit,
    ) : SettingsSection
}
