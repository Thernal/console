package az.theternal.console.compose.core

import kotlin.reflect.KProperty

interface StateHolder {
    fun <T> ViewState.StateField<T>.update(producer: T.() -> T) {
        mutableState.value = mutableState.value.producer()
    }

    operator fun <T> ViewState.StateField<T>.getValue(
        thisObj: Any?,
        property: KProperty<*>,
    ): T = value
}
