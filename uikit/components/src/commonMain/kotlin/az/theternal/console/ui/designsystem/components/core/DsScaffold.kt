package az.theternal.console.ui.designsystem.components.core

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import az.theternal.console.ui.designsystem.foundation.dimension.Dimens
import az.theternal.console.ui.designsystem.foundation.theme.Theme

@Composable
fun DsScaffold(
    modifier: Modifier = Modifier,
    containerColor: Color = Theme.colors.background1,
    safeTopPadding: Boolean = true,
    safeBottomPadding: Boolean = true,
    contentPadding: PaddingValues = PaddingValues(Dimens.dp0),
    topBar: (@Composable () -> Unit)? = null,
    bottomBar: (@Composable () -> Unit)? = null,
    content: @Composable (PaddingValues) -> Unit,
) {
    val contentWindowInsets = WindowInsets.safeDrawing
    val density = LocalDensity.current
    val layoutDirection = LocalLayoutDirection.current

    val safeTopForContent = when {
        topBar != null -> Dimens.dp0
        safeTopPadding -> with(density) { contentWindowInsets.getTop(this).toDp() }
        else -> Dimens.dp0
    }

    val safeBottomForContent = when {
        bottomBar != null -> Dimens.dp0
        safeBottomPadding -> with(density) { contentWindowInsets.getBottom(this).toDp() }
        else -> Dimens.dp0
    }

    val resolvedContentPadding = PaddingValues(
        start = contentPadding.calculateStartPadding(layoutDirection),
        top = contentPadding.calculateTopPadding() + safeTopForContent,
        end = contentPadding.calculateEndPadding(layoutDirection),
        bottom = contentPadding.calculateBottomPadding() + safeBottomForContent,
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(containerColor),
    ) {
        if (topBar != null) {
            Box(
                modifier = Modifier.fillMaxWidth(),
            ) {
                topBar()
            }
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
        ) {
            content(resolvedContentPadding)
        }

        if (bottomBar != null) {
            Box(
                modifier = Modifier.fillMaxWidth(),
            ) {
                bottomBar()
            }
        }
    }
}
