package io.thernal.console.network.compose.components.networklogitem.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import io.thernal.console.compose.common.extensions.toHms
import io.thernal.console.designsystem.components.core.DsText
import io.thernal.console.designsystem.components.core.chip.DsChip
import io.thernal.console.designsystem.components.core.chip.DsChipSize
import io.thernal.console.designsystem.foundation.theme.Theme
import io.thernal.console.network.NetworkLog

@Composable
internal fun NetworkLogHeader(
    log: NetworkLog,
    accentColor: Color,
) {
    val title = when (log) {
        is NetworkLog.Request -> "Request"
        is NetworkLog.Response -> "Response"
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(
                space = Theme.dimens.dp6,
            ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            buildList {
                add(title)
                add(log.method)
                if (log is NetworkLog.Response) {
                    add(log.statusCode.toString())
                }
            }.forEach { label ->
                DsChip(
                    label = label,
                    color = accentColor,
                    size = DsChipSize.Small,
                )
            }
        }

        DsText(
            text = log.timestamp.toHms(),
            style = Theme.typography.label01,
            color = Theme.colors.content04,
        )
    }
}
