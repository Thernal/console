package az.theternal.console.compose.core

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember

@Composable
inline fun <T, R> State<T>.select(crossinline transform: (T) -> R): State<R> {
    return remember { derivedStateOf { transform(value) } }
}
