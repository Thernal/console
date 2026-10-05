package io.thernal.console.inspector.ui.view.inspector.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import io.thernal.console.designsystem.components.core.DsText
import io.thernal.console.designsystem.foundation.theme.Theme

@Composable
internal fun InspectorEmptyState(text: String) {
    Box(
        modifier = Modifier.fillMaxSize().padding(Theme.dimens.dp24),
        contentAlignment = Alignment.Center,
    ) {
        DsText(
            text = text,
            style = Theme.typography.body02.copy(textAlign = TextAlign.Center),
            color = Theme.colors.content03,
        )
    }
}
