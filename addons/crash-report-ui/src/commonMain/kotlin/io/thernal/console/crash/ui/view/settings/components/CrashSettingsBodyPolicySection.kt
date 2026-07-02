package io.thernal.console.crash.ui.view.settings.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import io.thernal.console.crash.ui.config.CrashBodyPolicy
import io.thernal.console.designsystem.components.core.DsText
import io.thernal.console.designsystem.components.core.chip.DsChip
import io.thernal.console.designsystem.components.modifier.pressable
import io.thernal.console.designsystem.components.provider.ThemeProvider
import io.thernal.console.designsystem.foundation.theme.DsPreview
import io.thernal.console.designsystem.foundation.theme.Theme
import io.thernal.console.ui.core.select

@Composable
internal fun CrashSettingsBodyPolicySection(
    selectedPolicy: State<CrashBodyPolicy>,
    onPolicySelected: (CrashBodyPolicy) -> Unit,
) {
    CrashSettingsSection(title = "Network bodies") {
        Row(horizontalArrangement = Arrangement.spacedBy(Theme.dimens.dp8)) {
            CrashBodyPolicy.entries.forEach { policy ->
                PolicyChip(
                    label = policy.name,
                    isSelected = selectedPolicy.select { it == policy },
                    onPress = { onPolicySelected(policy) },
                )
            }
        }
        DsText(
            text = "How much of a request/response body is persisted into a session",
            style = Theme.typography.body03,
            color = Theme.colors.content04,
        )
    }
}

@Composable
private fun PolicyChip(
    label: String,
    isSelected: State<Boolean>,
    onPress: () -> Unit,
) {
    DsChip(
        label = label,
        color = Theme.colors.primary01,
        selected = isSelected.value,
        modifier = Modifier.pressable(
            onPress = onPress,
        ),
    )
}

@DsPreview
@Composable
private fun PreviewCrashSettingsBodyPolicySection() {
    ThemeProvider {
        CrashSettingsBodyPolicySection(
            selectedPolicy = remember { mutableStateOf(CrashBodyPolicy.Truncated) },
            onPolicySelected = {},
        )
    }
}
