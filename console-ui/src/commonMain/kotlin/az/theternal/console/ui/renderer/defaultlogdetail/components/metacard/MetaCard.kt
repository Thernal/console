package az.theternal.console.ui.renderer.defaultlogdetail.components.metacard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import az.theternal.console.runtime.model.Log
import az.theternal.console.ui.designsystem.components.core.DsDivider
import az.theternal.console.ui.designsystem.components.core.DsText
import az.theternal.console.ui.designsystem.foundation.theme.Theme
import az.theternal.console.ui.renderer.defaultlogdetail.components.metacard.components.MetaRow
import az.theternal.console.ui.utils.LogTagBadge
import az.theternal.console.ui.utils.formatLogTimestampFull

@Composable
internal fun MetaCard(
    log: Log,
    accentColor: Color,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(Theme.rounding.r12)
            .background(Theme.colors.background2)
            .border(Theme.metrics.borderWidth, Theme.colors.border, Theme.rounding.r12),
    ) {
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

            log.level?.let { level ->
                DsDivider()
                MetaRow(label = "Level") {
                    DsText(
                        text = level.name,
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
