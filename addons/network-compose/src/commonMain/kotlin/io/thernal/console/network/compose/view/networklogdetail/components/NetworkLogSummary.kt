package io.thernal.console.network.compose.view.networklogdetail.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import io.thernal.console.compose.common.extensions.toHms
import io.thernal.console.compose.common.highlight
import io.thernal.console.designsystem.components.core.DsContainer
import io.thernal.console.designsystem.components.core.DsDivider
import io.thernal.console.designsystem.components.core.DsText
import io.thernal.console.designsystem.components.core.chip.DsChip
import io.thernal.console.designsystem.components.core.chip.DsChipSize
import io.thernal.console.designsystem.foundation.theme.Theme
import io.thernal.console.network.NetworkLog

@Composable
internal fun NetworkLogSummary(
    log: NetworkLog,
    accentColor: Color,
) {
    DsContainer(modifier = Modifier.fillMaxWidth()) {
        SelectionContainer {
            Column(
                modifier = Modifier.padding(Theme.dimens.dp12),
                verticalArrangement = Arrangement.spacedBy(Theme.dimens.dp10),
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(Theme.dimens.dp8),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    DsChip(label = log.method, color = accentColor, size = DsChipSize.Small)

                    if (log is NetworkLog.Response) {
                        log.statusCode?.let { code ->
                            DsChip(label = code.toString(), color = accentColor, size = DsChipSize.Small)
                        }

                        log.durationMs?.let { ms ->
                            DsText(text = "${ms}ms", style = Theme.typography.label01, color = Theme.colors.content03)
                        }
                    }
                }

                DsText(text = log.url.highlight())

                DsDivider()

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    DsText(text = "Time", style = Theme.typography.label01, color = Theme.colors.content03)
                    DsText(
                        text = log.timestamp.toHms().highlight(),
                    )
                }
            }
        }
    }
}
