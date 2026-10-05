package io.thernal.console.ui.addon

import androidx.compose.runtime.Composable
import io.thernal.console.api.addon.ConsoleContentWrapper

/** Applies [wrappers] around [content], the first wrapper being the outermost. */
@Composable
internal fun WrappedContent(
    wrappers: List<ConsoleContentWrapper>,
    index: Int = 0,
    content: @Composable () -> Unit,
) {
    if (index >= wrappers.size) {
        content()
    } else {
        wrappers[index] {
            WrappedContent(wrappers = wrappers, index = index + 1, content = content)
        }
    }
}
