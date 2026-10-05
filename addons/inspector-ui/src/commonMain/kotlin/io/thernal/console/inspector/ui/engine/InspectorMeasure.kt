package io.thernal.console.inspector.ui.engine

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Two-point measuring. The anchor is the first item; the engine's normal selection is the second, and the overlay and
 * the details show the distance between them. The anchor is set by tapping while [isMeasuring] in inspect mode (see
 * `InspectorPicker`), or from the details of the selected node in the tab.
 */
internal object InspectorMeasure {
    private val _isMeasuring = MutableStateFlow(false)
    val isMeasuring: StateFlow<Boolean> = _isMeasuring.asStateFlow()

    private val _anchorKey = MutableStateFlow<Any?>(null)
    val anchorKey: StateFlow<Any?> = _anchorKey.asStateFlow()

    /** Turning measuring off also drops the anchor it set. */
    fun setMeasuring(isMeasuring: Boolean) {
        _isMeasuring.value = isMeasuring
        if (!isMeasuring) _anchorKey.value = null
    }

    fun setAnchor(key: Any?) {
        _anchorKey.value = key
    }
}
