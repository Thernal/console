package io.thernal.console.stepper.ui.view.caught.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BugReport
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import io.thernal.console.designsystem.components.core.DsIcon
import io.thernal.console.designsystem.components.core.DsText
import io.thernal.console.designsystem.foundation.theme.Theme

@Composable
internal fun StepperCaughtEmptyState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            DsIcon(
                icon = Icons.Outlined.BugReport,
                size = Theme.dimens.dp32,
                color = Theme.colors.content04,
            )
            DsText(
                text = "Nothing caught yet",
                style = Theme.typography.body02,
                color = Theme.colors.content03,
                modifier = Modifier.padding(top = Theme.dimens.dp8),
            )
        }
    }
}
