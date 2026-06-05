package io.thernal.console.compose.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import io.thernal.console.compose.common.extensions.toHms
import io.thernal.console.runtime.log.Log
import io.thernal.console.runtime.log.LogLevel
import io.thernal.console.compose.common.highlight
import io.thernal.console.compose.common.logAccentColor
import io.thernal.console.designsystem.components.core.DsCard
import io.thernal.console.designsystem.components.core.chip.DsChip
import io.thernal.console.designsystem.components.core.chip.DsChipSize
import io.thernal.console.designsystem.components.core.DsText
import io.thernal.console.designsystem.components.provider.ThemeProvider
import io.thernal.console.designsystem.foundation.theme.DsPreview
import io.thernal.console.designsystem.foundation.theme.Theme

@Composable
internal fun BasicLogItem(
    log: Log,
    modifier: Modifier = Modifier,
) {
    val accentColor = log.logAccentColor()

    DsCard(
        modifier = modifier,
        color = accentColor,
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(
                    horizontal = Theme.dimens.dp12,
                    vertical = Theme.dimens.dp12,
                ),
            verticalArrangement = Arrangement.spacedBy(
                space = Theme.dimens.dp6,
            ),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                DsChip(
                    label = log.tag ?: "No Tag",
                    color = accentColor,
                    size = DsChipSize.Small,
                )

                DsText(
                    text = log.timestamp.toHms(),
                    style = Theme.typography.label01,
                    color = Theme.colors.content04,
                )
            }

            DsText(
                text = log.message.highlight(),
                style = Theme.typography.body02,
                color = Theme.colors.content01,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@DsPreview
@Composable
private fun PreviewBasicLogItemSingle() {
    ThemeProvider {
        BasicLogItem(
            log = Log(
                message = "Request completed successfully",
                tag = "HTTP",
                level = LogLevel.Success,
            ),
        )
    }
}

@DsPreview
@Composable
private fun PreviewBasicLogItemMultiline() {
    ThemeProvider {
        BasicLogItem(
            log = Log(
                message = "Exception in thread \"main\"\n" +
                    "java.lang.NullPointerException\n" +
                    "\tat com.example.Foo.bar(Foo.kt:42)",
                tag = "CRASH",
                level = LogLevel.Fatal,
            ),
        )
    }
}
