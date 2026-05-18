package az.theternal.console.details.api

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

object ConsoleDetails {

    private val detailsState = MutableStateFlow<Map<String, String>>(emptyMap())
    val flow: StateFlow<Map<String, String>> = detailsState.asStateFlow()

    fun put(
        key: String,
        value: String,
    ) {
        detailsState.update { it + (key to value) }
    }

    fun put(detail: Pair<String, String>) {
        put(detail.first, detail.second)
    }

    fun remove(key: String) {
        detailsState.update { it - key }
    }

    fun clear() {
        detailsState.value = emptyMap()
    }
}
