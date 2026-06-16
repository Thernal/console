package io.thernal.console.network.ui.components.networklogitem

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import io.thernal.console.ui.common.highlight
import io.thernal.console.ui.common.logAccentColor
import io.thernal.console.designsystem.components.core.DsCard
import io.thernal.console.designsystem.components.core.DsText
import io.thernal.console.designsystem.components.provider.ThemeProvider
import io.thernal.console.designsystem.foundation.theme.DsPreview
import io.thernal.console.designsystem.foundation.theme.Theme
import io.thernal.console.network.NetworkLog
import io.thernal.console.network.ui.components.networklogitem.components.NetworkLogHeader
import io.thernal.console.network.ui.components.networklogitem.components.NetworkLogMeta
import io.thernal.console.core.log.LogLevel

@Composable
internal fun NetworkLogItem(
    log: NetworkLog,
    modifier: Modifier = Modifier,
) {
    val accentColor = when (log) {
        is NetworkLog.Response -> log.logAccentColor()
        is NetworkLog.Request -> Theme.colors.primary01
    }

    DsCard(
        modifier = modifier,
        color = accentColor,
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(
                    horizontal = Theme.dimens.dp12,
                    vertical = Theme.dimens.dp10,
                ),
            verticalArrangement = Arrangement.spacedBy(
                space = Theme.dimens.dp6,
            ),
        ) {
            NetworkLogHeader(
                log = log,
                accentColor = accentColor,
            )

            DsText(
                text = log.url.highlight(),
                style = Theme.typography.body02,
                color = Theme.colors.content01,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            NetworkLogMeta(
                log = log,
            )
        }
    }
}

@Composable
@DsPreview
private fun PreviewNetworkLogItem() {
    ThemeProvider {
        NetworkLogItem(
            log = NetworkLog.Response(
                method = "POST",
                url = "https://api.exampl.com/v1/users",
                headers = mapOf(),
                body = "",
                statusCode = 200,
                durationMs = 100,
                level = LogLevel.Success,
            ),
        )
    }
}
