package az.theternal.console.ui.designsystem.components.product

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import az.theternal.console.ui.designsystem.components.core.Spacer
import az.theternal.console.ui.designsystem.foundation.theme.Theme

@Composable
fun DsAppbar(
    modifier: Modifier = Modifier,
    paddingHorizontal: PaddingValues = PaddingValues(horizontal = Theme.dimens.dp12),
    isLoading: Boolean = false,
    leading: (@Composable RowScope.() -> Unit)? = null,
    trailing: (@Composable RowScope.() -> Unit)? = null,
    content: (@Composable RowScope.() -> Unit)? = null,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Theme.colors.background1),
        contentAlignment = Alignment.Center,
    ) {
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .height(Theme.metrics.dividerHeight)
                    .background(Theme.colors.primary02),
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = Theme.dimens.dp8)
                .defaultMinSize(minHeight = Theme.dimens.dp48)
                .padding(paddingHorizontal),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            leading?.invoke(this)
            content?.invoke(this)
            trailing?.let {
                Spacer()
                trailing()
            }
        }
    }
}

@Composable
fun DsAppBar(
    modifier: Modifier = Modifier,
    paddingHorizontal: PaddingValues = PaddingValues(horizontal = Theme.dimens.dp12),
    isLoading: Boolean = false,
    leading: (@Composable RowScope.() -> Unit)? = null,
    trailing: (@Composable RowScope.() -> Unit)? = null,
    content: (@Composable RowScope.() -> Unit)? = null,
) {
    DsAppbar(
        modifier = modifier,
        paddingHorizontal = paddingHorizontal,
        isLoading = isLoading,
        leading = leading,
        trailing = trailing,
        content = content,
    )
}
