package az.theternal.console.info

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

object ConsoleDetails {

    private val detailsState = MutableStateFlow<Map<String, String>>(emptyMap())
    val flow: StateFlow<Map<String, String>> = detailsState.asStateFlow()

    fun put(detail: Pair<String, String>) {
        detailsState.update { it + detail }
    }

    fun remove(key: String) {
        detailsState.update { it - key }
    }

    fun clear() {
        detailsState.value = emptyMap()
    }
}
