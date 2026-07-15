package io.thernal.console.settings

import io.thernal.console.core.ConsoleInternalApi
import kotlin.concurrent.Volatile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Central registry every [SettingsSection] registers into. Overrides apply at registration time
 * (see [setOnRegistered]), not at settings-addon install time, so addon install order never
 * matters.
 */
object SettingsRegistry {

    private val _sections = MutableStateFlow<List<SettingsSection>>(emptyList())
    val sections: StateFlow<List<SettingsSection>> = _sections.asStateFlow()

    @Volatile
    private var onRegistered: ((SettingsSection) -> Unit)? = null

    /**
     * Registers [section]. Fails fast on a duplicate [SettingsSection.id] — a silent overwrite
     * would corrupt the persisted-override contract, which is keyed by section id.
     */
    @ConsoleInternalApi
    fun register(section: SettingsSection) {
        _sections.update { current ->
            check(current.none { it.id == section.id }) {
                "Settings section id \"${section.id}\" already registered"
            }
            current + section
        }
        onRegistered?.invoke(section)
    }

    /**
     * Single-consumer hook — its only intended caller is the settings addon's store, once, at
     * install. Addons never call this; they only [register]. Passive observers collect
     * [sections] instead.
     *
     * Replays every already-registered section through [listener] before returning, so a section
     * is restored exactly once regardless of install order: sections registered before this call
     * are replayed here, sections registered after are applied inline inside [register].
     */
    @ConsoleInternalApi
    fun setOnRegistered(listener: (SettingsSection) -> Unit) {
        check(onRegistered == null) { "SettingsRegistry consumer already attached" }
        onRegistered = listener
        _sections.value.forEach(listener)
    }

    /** Test-only: clears every registered section and detaches [setOnRegistered]'s consumer. */
    internal fun resetForTest() {
        _sections.value = emptyList()
        onRegistered = null
    }
}
