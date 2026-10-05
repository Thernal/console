package io.thernal.console.inspector.ui.engine

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Two-point measuring on top of inspect mode. The first tap sets an anchor; taps and drags after that choose the
 * second item, which is the engine's normal selection. Tapping the anchor again lets the next tap set a new one.
 */
internal object InspectorMeasure {
    private val _isMeasuring = MutableStateFlow(false)
    val isMeasuring: StateFlow<Boolean> = _isMeasuring.asStateFlow()

    private val _anchorKey = MutableStateFlow<Any?>(null)
    val anchorKey: StateFlow<Any?> = _anchorKey.asStateFlow()

    fun setMeasuring(isMeasuring: Boolean) {
        _isMeasuring.value = isMeasuring
        if (!isMeasuring) _anchorKey.value = null
    }

    /**
     * Routes a pick while measuring and says what the selection becomes. [isPress] is a fresh touch, as opposed to a
     * finger moving on.
     */
    fun route(
        hitKey: Any?,
        isPress: Boolean,
    ): Pick {
        val anchor = _anchorKey.value
        return when {
            anchor == null && isPress -> {
                _anchorKey.value = hitKey
                Pick.Select(hitKey)
            }

            anchor == null -> Pick.Keep

            isPress && hitKey == anchor -> {
                _anchorKey.value = null
                Pick.Select(null)
            }

            else -> Pick.Select(hitKey)
        }
    }

    /** What a pick does to the selection while measuring. */
    sealed interface Pick {
        /** Leave the selection as it is. */
        data object Keep : Pick

        /** Select [key], or clear the selection when it is null. */
        data class Select(val key: Any?) : Pick
    }
}
