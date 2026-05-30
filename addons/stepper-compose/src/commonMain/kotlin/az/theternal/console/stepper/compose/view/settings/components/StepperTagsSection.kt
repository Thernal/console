package az.theternal.console.stepper.compose.view.settings.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import az.theternal.console.compose.core.ViewState
import az.theternal.console.designsystem.components.core.DsChip
import az.theternal.console.designsystem.components.core.DsIcon
import az.theternal.console.designsystem.components.core.DsText
import az.theternal.console.designsystem.components.modifier.pressable
import az.theternal.console.designsystem.foundation.theme.Theme
import az.theternal.console.stepper.DebugStepper
import az.theternal.console.stepper.compose.view.settings.DebugStepperIntent

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun StepperTagsSection(
    config: ViewState.StateField<DebugStepper.Config>,
    tagInput: ViewState.StateField<TextFieldValue>,
    dispatch: (DebugStepperIntent) -> Unit,
) {
    SettingsSection(title = "Tags") {
        TagInputField(
            value = tagInput.value,
            onValueChange = { dispatch(DebugStepperIntent.SetTagInput(it)) },
            onAdd = { dispatch(DebugStepperIntent.AddTag) },
        )
        if (config.value.pauseOnTags.isEmpty()) {
            DsText(
                text = "Matching all tags",
                style = Theme.typography.body03,
                color = Theme.colors.content04,
            )
        } else {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(Theme.dimens.dp8),
                verticalArrangement = Arrangement.spacedBy(Theme.dimens.dp8),
            ) {
                config.value.pauseOnTags.forEach { tag ->
                    DsChip(
                        label = tag,
                        color = Theme.colors.primary01,
                        selected = true,
                        modifier = Modifier.pressable(onPress = {
                            dispatch(DebugStepperIntent.RemovePauseTag(tag))
                        }),
                        trailing = {
                            DsIcon(
                                icon = Icons.Outlined.Close,
                                size = Theme.metrics.iconXs,
                                color = Theme.colors.primary01,
                                modifier = Modifier.padding(start = Theme.dimens.dp4),
                            )
                        },
                    )
                }
            }
        }
    }
}
