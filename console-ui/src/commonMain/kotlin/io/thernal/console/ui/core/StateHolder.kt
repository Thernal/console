package io.thernal.console.ui.core

import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.Snapshot
import androidx.compose.ui.platform.LocalInspectionMode
import kotlin.reflect.KProperty

interface StateHolder {
    fun <T> ViewState.StateField<T>.update(producer: T.() -> T) {
        write(value.producer())
    }

    fun <T> ViewState.StateField<T>.set(value: T) {
        write(value)
    }

    operator fun <T> ViewState.StateField<T>.getValue(
        thisObj: Any?,
        property: KProperty<*>,
    ): T = value

    fun snapshot(block: () -> Unit) {
        Snapshot.withMutableSnapshot(block)
    }
}

class PreviewStateHolder<S : ViewState> internal constructor(val state: S) {
    fun <T> ViewState.StateField<T>.set(value: T) {
        write(value)
    }
}

@Composable
fun <S : ViewState> S.preview(block: PreviewStateHolder<S>.() -> Unit): S {
    if (LocalInspectionMode.current) {
        PreviewStateHolder(this).block()
    }
    return this
}
