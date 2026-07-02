package io.thernal.console.crash.ui.view.settings.model

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.thernal.console.crash.ui.CrashReports
import io.thernal.console.ui.core.IntentHandler
import io.thernal.console.ui.core.StateHolder
import kotlinx.coroutines.launch

internal class CrashReportSettingsViewModel :
    ViewModel(),
    StateHolder,
    IntentHandler<CrashReportSettingsIntent> {

    val state = CrashReportSettingsState()

    override val handler = onIntentUpdate { intent ->
        when (intent) {
            is CrashReportSettingsIntent.SetShowSafeSessions ->
                CrashReports.updateConfig { copy(showSafeSessions = intent.showSafeSessions) }

            is CrashReportSettingsIntent.SetPersistOnMatch ->
                CrashReports.updateConfig { copy(persistOnMatch = intent.persistOnMatch) }

            is CrashReportSettingsIntent.SetPersistLevelAtLeast ->
                CrashReports.updateConfig { copy(persistLevelAtLeast = intent.level) }

            is CrashReportSettingsIntent.SetBodyPolicy ->
                CrashReports.updateConfig { copy(bodyPolicy = intent.policy) }

            is CrashReportSettingsIntent.SetIncludeTagInput -> state.includeTagInput.set(intent.value)

            CrashReportSettingsIntent.AddIncludeTag -> addIncludeTag()

            is CrashReportSettingsIntent.RemoveIncludeTag ->
                CrashReports.updateConfig { copy(includeTags = includeTags - intent.tag) }

            is CrashReportSettingsIntent.SetExcludeTagInput -> state.excludeTagInput.set(intent.value)

            CrashReportSettingsIntent.AddExcludeTag -> addExcludeTag()

            is CrashReportSettingsIntent.RemoveExcludeTag ->
                CrashReports.updateConfig { copy(excludeTags = excludeTags - intent.tag) }
        }
    }

    init {
        viewModelScope.launch {
            CrashReports.config.collect { config ->
                snapshot {
                    state.showSafeSessions.set(config.showSafeSessions)
                    state.persistOnMatch.set(config.persistOnMatch)
                    state.persistLevelAtLeast.set(config.persistLevelAtLeast)
                    state.includeTags.set(config.includeTags)
                    state.excludeTags.set(config.excludeTags)
                    state.bodyPolicy.set(config.bodyPolicy)
                }
            }
        }
    }

    private fun addIncludeTag() {
        val tag = state.includeTagInput.value.text.trim()
        if (tag.isEmpty()) return
        CrashReports.updateConfig { copy(includeTags = includeTags + tag) }
        state.includeTagInput.set(TextFieldValue())
    }

    private fun addExcludeTag() {
        val tag = state.excludeTagInput.value.text.trim()
        if (tag.isEmpty()) return
        CrashReports.updateConfig { copy(excludeTags = excludeTags + tag) }
        state.excludeTagInput.set(TextFieldValue())
    }
}
