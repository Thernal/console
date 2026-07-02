package io.thernal.console.crash.ui.view.detail.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.thernal.console.designsystem.components.core.DsCard
import io.thernal.console.designsystem.components.core.DsText
import io.thernal.console.designsystem.components.core.chip.DsChip
import io.thernal.console.designsystem.components.core.chip.DsChipSize
import io.thernal.console.designsystem.components.provider.ThemeProvider
import io.thernal.console.designsystem.foundation.theme.DsPreview
import io.thernal.console.designsystem.foundation.theme.Theme

/** The highlighted fatal crash at the end of a session detail: summary chip + full stack trace. */
@Composable
internal fun FatalCrashCard(
    summary: String,
    stackTrace: String,
    modifier: Modifier = Modifier,
) {
    DsCard(
        modifier = modifier,
        color = Theme.colors.fatal,
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
            DsChip(
                label = "Fatal crash",
                color = Theme.colors.fatal,
                size = DsChipSize.Small,
            )

            DsText(
                text = summary,
                style = Theme.typography.body01,
                color = Theme.colors.content01,
            )

            DsText(
                text = stackTrace,
                style = Theme.typography.body03,
                color = Theme.colors.content02,
            )
        }
    }
}

@DsPreview
@Composable
private fun PreviewFatalCrashCard() {
    ThemeProvider {
        FatalCrashCard(
            summary = "IllegalStateException: boom",
            stackTrace = "IllegalStateException: boom\n  at PlaygroundViewModel.crash(PlaygroundViewModel.kt:42)",
        )
    }
}
