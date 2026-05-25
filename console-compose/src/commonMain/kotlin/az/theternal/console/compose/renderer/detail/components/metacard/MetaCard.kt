package az.theternal.console.compose.renderer.detail.components.metacard

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import az.theternal.console.runtime.Log
import az.theternal.console.runtime.LogLevel
import az.theternal.console.compose.components.LogTagBadge
import az.theternal.console.compose.util.formatLogTimestampFull
import az.theternal.console.designsystem.components.core.DsCard
import az.theternal.console.designsystem.components.core.DsDivider
import az.theternal.console.designsystem.components.core.DsText
import az.theternal.console.designsystem.foundation.theme.Theme

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
