package io.thernal.console.designsystem.components.core.chip

import androidx.compose.foundation.layout.PaddingValues
import io.thernal.console.designsystem.foundation.theme.Theme

enum class DsChipSize {
    Small,
    Medium,
    ;

    val padding: PaddingValues get() {
        return when (this) {
            Small -> PaddingValues(
                horizontal = Theme.dimens.dp8,
                vertical = Theme.dimens.dp4,
            )

            Medium -> PaddingValues(
                horizontal = Theme.dimens.dp14,
                vertical = Theme.dimens.dp8,
            )
        }
    }
}
