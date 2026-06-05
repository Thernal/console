package io.thernal.console.network.compose.view.networklogdetail.model

import androidx.lifecycle.ViewModel
import io.thernal.console.compose.core.EffectHandler
import io.thernal.console.compose.core.IntentHandler
import io.thernal.console.compose.core.StateHolder
import io.thernal.console.network.NetworkLog
import io.thernal.console.network.compose.common.extensions.toCurlCommand
import io.thernal.console.network.compose.common.extensions.toDisplayText
import io.thernal.console.network.compose.common.extensions.toShareText

class NetworkLogDetailViewModel(
    private val log: NetworkLog,
) : ViewModel(), StateHolder, IntentHandler<NetworkLogDetailIntent>, EffectHandler<NetworkLogDetailEffect> {
    val state = NetworkLogDetailState()
    override val channel = EffectChannel()

    override val handler = onIntentUpdate { intent ->
        when (intent) {
            NetworkLogDetailIntent.ToggleHeadersExpanded -> {
                state.headersExpanded.update { !this }
            }

            NetworkLogDetailIntent.ToggleBodyExpanded -> {
                state.bodyExpanded.update { !this }
            }

            NetworkLogDetailIntent.CopyAll -> {
                launchEffect(NetworkLogDetailEffect.Copy(data = log.toShareText()))
            }

            NetworkLogDetailIntent.CopyHeaders -> {
                launchEffect(NetworkLogDetailEffect.Copy(data = log.headers.toDisplayText()))
            }

            NetworkLogDetailIntent.CopyBody -> {
                launchEffect(NetworkLogDetailEffect.Copy(data = log.body.orEmpty()))
            }

            NetworkLogDetailIntent.CopyCurl -> {
                if (log is NetworkLog.Request) {
                    launchEffect(NetworkLogDetailEffect.Copy(data = log.toCurlCommand()))
                }
            }
        }
    }
}
