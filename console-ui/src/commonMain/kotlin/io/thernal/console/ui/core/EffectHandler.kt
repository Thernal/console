package io.thernal.console.ui.core

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle

@Stable
interface ViewEffect

interface EffectHandler<T : ViewEffect> {

    val channel: EffectChannel<T>

    fun ViewModel.launchEffect(effect: T) {
        viewModelScope.launch { channel.launch(effect) }
    }

    class EffectChannel<T : ViewEffect> internal constructor() : AutoCloseable {
        private val _effects = Channel<T>()
        val effects: Flow<T> = _effects.receiveAsFlow()

        internal suspend fun launch(effect: T) {
            _effects.send(effect)
        }

        @Composable
        internal fun OnEffectUpdate(collector: (suspend CoroutineScope.(T) -> Unit)? = null) {
            val lifecycleOwner = LocalLifecycleOwner.current

            LaunchedEffect(this, lifecycleOwner) {
                lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                    effects.collect { effect ->
                        collector?.invoke(this, effect)
                    }
                }
            }
        }

        override fun close() {
            _effects.close()
        }
    }

    fun ViewModel.EffectChannel(): EffectChannel<T> {
        val channel = EffectChannel<T>()
        addCloseable(channel)
        return channel
    }

    @Composable
    fun OnEffectUpdate(collector: (suspend CoroutineScope.(T) -> Unit)? = null) {
        return channel.OnEffectUpdate(collector)
    }
}
