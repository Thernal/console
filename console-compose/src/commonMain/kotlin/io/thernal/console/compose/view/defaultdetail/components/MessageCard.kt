package io.thernal.console.compose.view.defaultdetail.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import io.thernal.console.compose.common.highlight
import io.thernal.console.runtime.log.Log
import io.thernal.console.runtime.log.LogLevel
import io.thernal.console.designsystem.components.core.DsContainer
import io.thernal.console.designsystem.components.core.DsIcon
import io.thernal.console.designsystem.components.core.DsIconButton
import io.thernal.console.designsystem.components.provider.ThemeProvider
import io.thernal.console.designsystem.foundation.theme.DsPreview
import io.thernal.console.designsystem.components.core.DsText
import io.thernal.console.designsystem.foundation.theme.Theme
import kotlinx.coroutines.delay

@Composable
internal fun MessageCard(
    log: Log,
    accentColor: Color,
    onCopy: (() -> Unit)? = null,
) {
    var copied by remember { mutableStateOf(false) }

    LaunchedEffect(copied) {
        if (copied) {
            delay(1500L)
            copied = false
        }
    }

    DsContainer(
        modifier = Modifier.fillMaxWidth(),
        borderColor = accentColor.copy(alpha = Theme.opacity.S35),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min),
        ) {
            Box(
                modifier = Modifier
                    .width(Theme.dimens.dp3)
                    .fillMaxHeight()
                    .background(accentColor),
            )
            SelectionContainer(modifier = Modifier.weight(1f)) {
                DsText(
                    text = log.message.highlight(),
                    modifier = Modifier.padding(
                        horizontal = Theme.dimens.dp12,
                        vertical = Theme.dimens.dp12,
                    ),
                    style = Theme.typography.body02.copy(
                        fontFamily = FontFamily.Monospace,
                        lineHeight = Theme.typography.body01.lineHeight,
                    ),
                    color = Theme.colors.content01,
                )
            }
            onCopy?.let { copyAction ->
                Box(
                    modifier = Modifier.fillMaxHeight(),
                    contentAlignment = Alignment.TopCenter,
                ) {
                    DsIconButton(
                        onClick = {
                            copied = true
                            copyAction()
                        },
                        contentColor = if (copied) Theme.colors.success else Theme.colors.content03,
                    ) {
                        DsIcon(
                            icon = if (copied) Icons.Outlined.Check else Icons.Outlined.ContentCopy,
                            size = Theme.metrics.iconSm,
                        )
                    }
                }
            }
        }
    }
}

@DsPreview
@Composable
private fun PreviewMessageCard() {
    ThemeProvider {
        MessageCard(
            log = Log("GET /api/users -> 200 OK", tag = "HTTP", level = LogLevel.Success),
            accentColor = Theme.colors.success,
        )
    }
}
