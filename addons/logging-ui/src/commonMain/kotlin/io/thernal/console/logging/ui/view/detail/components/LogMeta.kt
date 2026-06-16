package io.thernal.console.logging.ui.view.detail.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.runtime.Composable
import io.thernal.console.ui.common.extensions.toHms
import io.thernal.console.ui.common.logAccentColor
import io.thernal.console.designsystem.components.core.chip.DsChip
import io.thernal.console.designsystem.components.core.chip.DsChipSize
import io.thernal.console.designsystem.foundation.theme.Theme
import io.thernal.console.core.log.Log
import io.thernal.console.core.log.LogLevel

@Composable
fun LogMeta(log: Log) {
    val accentColor = log.logAccentColor()

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(Theme.dimens.dp6),
    ) {
        log.tag?.let { tag ->
            item {
                DsChip(
                    label = tag,
                    color = accentColor,
                    size = DsChipSize.Small,
                )
            }
        }
        if (log.level != LogLevel.None) {
            item {
                DsChip(
                    label = log.level.name,
                    color = accentColor,
                    size = DsChipSize.Small,
                )
            }
        }
        item {
            DsChip(
                label = log.timestamp.toHms(),
                color = Theme.colors.content03,
                size = DsChipSize.Small,
            )
        }
    }
}
