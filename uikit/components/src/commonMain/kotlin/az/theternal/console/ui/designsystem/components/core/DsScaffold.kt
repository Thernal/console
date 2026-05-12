package az.theternal.console.ui.designsystem.components.core

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import az.theternal.console.ui.designsystem.foundation.dimension.Dimens
import az.theternal.console.ui.designsystem.foundation.theme.Theme

@Composable
fun DsScaffold(
    modifier: Modifier = Modifier,
    containerColor: Color = Theme.colors.background1,
    contentWindowInsets: WindowInsets = WindowInsets.safeDrawing,
    safeTopPadding: Boolean = true,
    safeBottomPadding: Boolean = true,
    safeHorizontalPadding: Boolean = true,
    contentSafeHorizontalPadding: Boolean = true,
    topBarHandlesSafeTopPadding: Boolean = true,
    bottomBarHandlesSafeBottomPadding: Boolean = true,
    topBar: (@Composable () -> Unit)? = null,
    bottomBar: (@Composable () -> Unit)? = null,
    content: @Composable (PaddingValues) -> Unit,
) {
    val hasTopBar = topBar != null
    val hasBottomBar = bottomBar != null

    val applyTopPaddingToScaffold = safeTopPadding && (!hasTopBar || !topBarHandlesSafeTopPadding)
    val applyBottomPaddingToScaffold = safeBottomPadding && (!hasBottomBar || !bottomBarHandlesSafeBottomPadding)
    val applyHorizontalPaddingToContent = safeHorizontalPadding && contentSafeHorizontalPadding

    val topInsets = if (applyTopPaddingToScaffold) {
        contentWindowInsets.only(WindowInsetsSides.Top)
    } else {
        null
    }

    val bottomInsets = if (applyBottomPaddingToScaffold) {
        contentWindowInsets.only(WindowInsetsSides.Bottom)
    } else {
        null
    }

    val contentInsets = if (applyHorizontalPaddingToContent) {
        contentWindowInsets.only(WindowInsetsSides.Horizontal)
    } else {
        null
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(containerColor),
    ) {
        if (topBar != null || topInsets != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .then(if (topInsets != null) Modifier.windowInsetsPadding(topInsets) else Modifier),
            ) {
                topBar?.invoke()
            }
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .then(if (contentInsets != null) Modifier.windowInsetsPadding(contentInsets) else Modifier),
        ) {
            content(PaddingValues(Dimens.dp0))
        }

        if (bottomBar != null || bottomInsets != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .then(if (bottomInsets != null) Modifier.windowInsetsPadding(bottomInsets) else Modifier),
            ) {
                bottomBar?.invoke()
            }
        }
    }
}
