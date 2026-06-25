package io.thernal.console.ui.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import io.thernal.console.designsystem.components.core.DsIcon
import io.thernal.console.designsystem.components.core.DsText
import io.thernal.console.designsystem.foundation.theme.Theme

/**
 * Rendered by [ConsoleNavHost]'s entry-provider fallback when a route on the back stack has no
 * registered entry — e.g. an addon that owns the route was not installed. This MUST never throw:
 * the console renders inside the host app's composition, so a thrown fallback (navigation3's
 * default) would crash the host app rather than the console. Degrade visibly instead.
 */
@Composable
internal fun UnknownRouteState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Theme.dimens.dp8),
        ) {
            DsIcon(
                icon = Icons.Outlined.Explore,
                size = Theme.dimens.dp32,
                color = Theme.colors.content04,
            )
            DsText(
                text = "Screen unavailable",
                style = Theme.typography.body02,
                color = Theme.colors.content03,
            )
            DsText(
                text = "The addon that provides this screen isn't installed.",
                style = Theme.typography.body03,
                color = Theme.colors.content04,
            )
        }
    }
}
