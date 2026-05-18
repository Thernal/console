package az.theternal.console.ui.designsystem.components.modifier

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import az.theternal.console.ui.designsystem.foundation.indication.PressableIndication

fun Modifier.pressable(
    onPress: () -> Unit,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource? = null,
): Modifier {
    return composed {
        return@composed clickable(
            onClick = onPress,
            enabled = enabled,
            interactionSource = interactionSource ?: remember {
                MutableInteractionSource()
            },
            indication = PressableIndication(),
        )
    }
}
