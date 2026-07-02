package io.thernal.console.crash.ui.view.sessions.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.DeleteForever
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import io.thernal.console.crash.ui.session.CrashSessionClass
import io.thernal.console.crash.ui.session.CrashSessionSummary
import io.thernal.console.designsystem.components.core.DsCard
import io.thernal.console.designsystem.components.core.DsIcon
import io.thernal.console.designsystem.components.core.DsIconButton
import io.thernal.console.designsystem.components.core.DsText
import io.thernal.console.designsystem.components.core.chip.DsChip
import io.thernal.console.designsystem.components.core.chip.DsChipSize
import io.thernal.console.designsystem.components.provider.ThemeProvider
import io.thernal.console.designsystem.foundation.theme.DsPreview
import io.thernal.console.designsystem.foundation.theme.Theme
import io.thernal.console.designsystem.foundation.theme.ThemeColors
import io.thernal.console.ui.common.extensions.toHms
import kotlin.time.Instant

@Composable
internal fun CrashSessionItem(
    session: CrashSessionSummary,
    isDeleteArmed: Boolean,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val accentColor = session.classification.accentColor(Theme.colors)

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
                    label = session.classification.name,
                    color = accentColor,
                    size = DsChipSize.Small,
                )

                DsText(
                    text = Instant.fromEpochMilliseconds(session.crashedAtMs ?: session.startedAtMs).toHms(),
                    style = Theme.typography.label01,
                    color = Theme.colors.content04,
                )
            }

            DsText(
                text = session.summary ?: session.classification.fallbackSummary(),
                style = Theme.typography.body02,
                color = Theme.colors.content01,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }

        DsIconButton(onClick = onDeleteClick) {
            DsIcon(
                icon = if (isDeleteArmed) Icons.Outlined.DeleteForever else Icons.Outlined.Delete,
                color = if (isDeleteArmed) Theme.colors.danger else Theme.colors.content03,
            )
        }
    }
}

internal fun CrashSessionClass.accentColor(colors: ThemeColors): Color {
    return when (this) {
        CrashSessionClass.Confirmed -> colors.fatal
        CrashSessionClass.Probable -> colors.warning
        CrashSessionClass.Safe -> colors.content03
    }
}

private fun CrashSessionClass.fallbackSummary(): String {
    return when (this) {
        CrashSessionClass.Confirmed -> "Captured crash"
        CrashSessionClass.Probable -> "Abnormal termination (no captured trace)"
        CrashSessionClass.Safe -> "Killed in background"
    }
}

@DsPreview
@Composable
private fun PreviewCrashSessionItem() {
    ThemeProvider {
        Column(verticalArrangement = Arrangement.spacedBy(Theme.dimens.dp8)) {
            CrashSessionItem(
                session = CrashSessionSummary(
                    id = "a",
                    startedAtMs = 1_700_000_000_000,
                    classification = CrashSessionClass.Confirmed,
                    summary = "IllegalStateException: boom",
                    crashedAtMs = 1_700_000_060_000,
                ),
                isDeleteArmed = false,
                onDeleteClick = {},
            )
            CrashSessionItem(
                session = CrashSessionSummary(
                    id = "b",
                    startedAtMs = 1_700_000_000_000,
                    classification = CrashSessionClass.Probable,
                    summary = null,
                    crashedAtMs = null,
                ),
                isDeleteArmed = true,
                onDeleteClick = {},
            )
        }
    }
}
