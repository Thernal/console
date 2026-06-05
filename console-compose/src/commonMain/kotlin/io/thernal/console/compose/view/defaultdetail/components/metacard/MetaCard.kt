package io.thernal.console.compose.view.defaultdetail.components.metacard

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import io.thernal.console.compose.common.extensions.toHms
import io.thernal.console.runtime.log.Log
import io.thernal.console.runtime.log.LogLevel
import io.thernal.console.designsystem.components.core.DsContainer
import io.thernal.console.designsystem.components.core.chip.DsChip
import io.thernal.console.designsystem.components.core.chip.DsChipSize
import io.thernal.console.designsystem.components.core.DsDivider
import io.thernal.console.designsystem.components.core.DsText
import io.thernal.console.designsystem.components.provider.ThemeProvider
import io.thernal.console.designsystem.foundation.theme.DsPreview
import io.thernal.console.designsystem.foundation.theme.Theme

@Composable
internal fun MetaCard(
    log: Log,
    accentColor: Color,
) {
    val tag = log.tag

    DsContainer(modifier = Modifier.fillMaxWidth()) {
        Column {
            MetaRow(label = "Tag") {
                if (tag != null) {
                    DsChip(
                        label = tag,
                        color = accentColor,
                        size = DsChipSize.Small,
                    )
                } else {
                    DsText(
                        text = "—",
                        style = Theme.typography.body02,
                        color = Theme.colors.content04,
                    )
                }
            }

            if (log.level != LogLevel.None) {
                DsDivider()
                MetaRow(label = "Level") {
                    DsChip(
                        label = log.level.name,
                        color = accentColor,
                        size = DsChipSize.Small,
                    )
                }
            }

            DsDivider()
            MetaRow(label = "Time") {
                DsText(
                    text = log.timestamp.toHms(),
                    style = Theme.typography.body02,
                    color = Theme.colors.content01,
                )
            }
        }
    }
}

@DsPreview
@Composable
private fun PreviewMetaCard() {
    ThemeProvider {
        MetaCard(
            log = Log("Request completed", tag = "HTTP", level = LogLevel.Success),
            accentColor = Theme.colors.success,
        )
    }
}
