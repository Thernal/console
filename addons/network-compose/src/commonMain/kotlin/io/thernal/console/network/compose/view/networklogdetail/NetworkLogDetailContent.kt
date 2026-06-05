package io.thernal.console.network.compose.view.networklogdetail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import io.thernal.console.compose.core.preview
import io.thernal.console.compose.common.logAccentColor
import io.thernal.console.designsystem.components.provider.ThemeProvider
import io.thernal.console.designsystem.foundation.theme.DsPreview
import io.thernal.console.designsystem.foundation.theme.Theme
import io.thernal.console.network.NetworkLog
import io.thernal.console.network.compose.view.networklogdetail.components.CopyActions
import io.thernal.console.network.compose.view.networklogdetail.components.NetworkLogBody
import io.thernal.console.network.compose.view.networklogdetail.components.NetworkLogHeaders
import io.thernal.console.network.compose.view.networklogdetail.components.NetworkLogSummary
import io.thernal.console.network.compose.view.networklogdetail.model.NetworkLogDetailIntent
import io.thernal.console.network.compose.view.networklogdetail.model.NetworkLogDetailState
import io.thernal.console.runtime.log.LogLevel

@Composable
internal fun NetworkLogDetailContent(
    log: NetworkLog,
    state: NetworkLogDetailState,
    onDispatch: (NetworkLogDetailIntent) -> Unit,
) {
    val accentColor = when (log) {
        is NetworkLog.Response -> log.logAccentColor()
        is NetworkLog.Request -> Theme.colors.primary01
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(Theme.dimens.dp12),
    ) {
        CopyActions(
            log = log,
            accentColor = accentColor,
            onDispatch = onDispatch,
        )

        NetworkLogSummary(
            log = log,
            accentColor = accentColor,
        )

        if (log.headers.isNotEmpty()) {
            NetworkLogHeaders(
                headers = log.headers,
                accentColor = accentColor,
                expanded = state.headersExpanded.value,
                onToggle = {
                    onDispatch(NetworkLogDetailIntent.ToggleHeadersExpanded)
                },
            )
        }

        log.body?.takeIf { it.isNotBlank() }?.let { body ->
            NetworkLogBody(
                title = if (log is NetworkLog.Request) {
                    "Request Body"
                } else {
                    "Response Body"
                },
                body = body,
                accentColor = accentColor,
                expanded = state.bodyExpanded.value,
                onToggle = {
                    onDispatch(NetworkLogDetailIntent.ToggleBodyExpanded)
                },
            )
        }
    }
}

@DsPreview
@Composable
private fun PreviewNetworkLogDetailContentRequest() {
    ThemeProvider {
        NetworkLogDetailContent(
            log = NetworkLog.Request(
                method = "POST",
                url = "https://api.example.com/v1/users",
                headers = mapOf("Content-Type" to "application/json", "Authorization" to "***"),
                body = """{"name": "John", "email": "john@example.com"}""",
            ),
            state = NetworkLogDetailState().preview { state.bodyExpanded.set(true) },
            onDispatch = {},
        )
    }
}

@DsPreview
@Composable
private fun PreviewNetworkLogDetailContentResponse() {
    ThemeProvider {
        NetworkLogDetailContent(
            log = NetworkLog.Response(
                method = "POST",
                url = "https://api.example.com/v1/users",
                statusCode = 201,
                headers = mapOf("Content-Type" to "application/json", "X-Request-Id" to "abc-123"),
                body = """{"id": 42, "name": "John"}""",
                durationMs = 342L,
                level = LogLevel.Success,
            ),
            state = NetworkLogDetailState().preview { state.headersExpanded.set(true) },
            onDispatch = {},
        )
    }
}
