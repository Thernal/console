package io.thernal.console.network.ui.view.networklogdetail.model

import androidx.lifecycle.ViewModel
import io.thernal.console.ui.core.EffectHandler
import io.thernal.console.ui.core.IntentHandler
import io.thernal.console.ui.core.StateHolder
import io.thernal.console.network.NetworkLog
import io.thernal.console.network.resolveNetworkBody
import io.thernal.console.network.toCopyText
import io.thernal.console.network.ui.common.extensions.toCurlCommand
import io.thernal.console.network.ui.common.extensions.toDisplayText

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
                val data = log.body
                    ?.takeIf { it.isNotBlank() }
                    ?.let { resolveNetworkBody(rawBody = it, headers = log.headers).toCopyText() }
                    .orEmpty()
                launchEffect(NetworkLogDetailEffect.Copy(data = data))
            }

            NetworkLogDetailIntent.CopyCurl -> {
                if (log is NetworkLog.Request) {
                    launchEffect(NetworkLogDetailEffect.Copy(data = log.toCurlCommand()))
                }
            }
        }
    }
}
