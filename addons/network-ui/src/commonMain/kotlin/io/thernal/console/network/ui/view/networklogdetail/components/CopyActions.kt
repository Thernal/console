package io.thernal.console.network.ui.view.networklogdetail.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Notes
import androidx.compose.material.icons.automirrored.outlined.Subject
import androidx.compose.material.icons.outlined.DataObject
import androidx.compose.material.icons.outlined.Terminal
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import io.thernal.console.designsystem.foundation.theme.Theme
import io.thernal.console.network.NetworkLog
import io.thernal.console.network.ui.view.networklogdetail.model.NetworkLogDetailIntent

@Composable
internal fun CopyActions(
    log: NetworkLog,
    accentColor: Color,
    onDispatch: (NetworkLogDetailIntent) -> Unit,
) {
    Row(horizontalArrangement = Arrangement.spacedBy(Theme.dimens.dp8)) {
        CopyChip(
            label = "All",
            icon = Icons.AutoMirrored.Outlined.Subject,
            accentColor = accentColor,
            onClick = { onDispatch(NetworkLogDetailIntent.CopyAll) },
        )

        CopyChip(
            label = "Headers",
            icon = Icons.AutoMirrored.Outlined.Notes,
            accentColor = accentColor,
            onClick = { onDispatch(NetworkLogDetailIntent.CopyHeaders) },
        )

        if (!log.body.isNullOrBlank()) {
            CopyChip(
                label = "Body",
                icon = Icons.Outlined.DataObject,
                accentColor = accentColor,
                onClick = { onDispatch(NetworkLogDetailIntent.CopyBody) },
            )
        }

        if (log is NetworkLog.Request) {
            CopyChip(
                label = "cURL",
                icon = Icons.Outlined.Terminal,
                accentColor = accentColor,
                onClick = { onDispatch(NetworkLogDetailIntent.CopyCurl) },
            )
        }
    }
}
