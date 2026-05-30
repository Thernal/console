package az.theternal.console.compose.core

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.LocalInspectionMode

@Stable
abstract class ViewState {

    @Stable
    class StateField<T> internal constructor(
        internal val mutableState: MutableState<T>,
    ) : State<T> by mutableState

    protected fun <T> field(value: T): StateField<T> {
        return StateField(mutableStateOf(value))
    }
}

@Composable
fun <T> ViewState.StateField<T>.preview(value: T): ViewState.StateField<T> {
    if (LocalInspectionMode.current) {
        mutableState.value = value
    }
    return this
}
