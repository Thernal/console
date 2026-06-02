package io.thernal.console.compose.view.defaultdetail.components.metacard

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import io.thernal.console.runtime.Log
import io.thernal.console.runtime.LogLevel
import io.thernal.console.designsystem.components.provider.ThemeProvider
import io.thernal.console.designsystem.foundation.theme.DsPreview
import io.thernal.console.compose.components.LogTagBadge
import io.thernal.console.compose.util.formatLogTimestampFull
import io.thernal.console.designsystem.components.core.DsCard
import io.thernal.console.designsystem.components.core.DsDivider
import io.thernal.console.designsystem.components.core.DsText
import io.thernal.console.designsystem.foundation.theme.Theme

@Composable
internal fun MetaCard(
    log: Log,
    accentColor: Color,
) {
    DsCard(modifier = Modifier.fillMaxWidth()) {
        Column {
            MetaRow(label = "Tag") {
                if (log.tag != null) {
                    LogTagBadge(tag = log.tag, color = accentColor)
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
                    DsText(
                        text = log.level.name,
                        style = Theme.typography.body02,
                        color = accentColor,
                    )
                }
            }

            DsDivider()
            MetaRow(label = "Time") {
                DsText(
                    text = formatLogTimestampFull(log.timestamp),
                    style = Theme.typography.body02,
                    color = Theme.colors.content01,
                )
            }

            DsDivider()
            MetaRow(label = "ID") {
                DsText(
                    text = log.id,
                    style = Theme.typography.label01.copy(
                        fontFamily = FontFamily.Monospace,
                    ),
                    color = Theme.colors.content03,
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
