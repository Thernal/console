package io.thernal.console.crash.ui.view.detail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalClipboard
import androidx.lifecycle.viewmodel.compose.viewModel
import io.thernal.console.api.navigation.LocalConsoleNavigator
import io.thernal.console.crash.ui.view.detail.model.CrashSessionDetailViewModel
import io.thernal.console.ui.common.toTextClipEntry
import kotlinx.coroutines.launch

@Composable
internal fun CrashSessionDetailView(sessionId: String) {
    val viewModel = viewModel(key = sessionId) { CrashSessionDetailViewModel(sessionId = sessionId) }
    val navigator = LocalConsoleNavigator.current
    val clipboard = LocalClipboard.current
    val scope = rememberCoroutineScope()

    CrashSessionDetailContent(
        state = viewModel.state,
        onBack = navigator::pop,
        onShareClick = {
            scope.launch {
                viewModel.shareText()?.let { text ->
                    clipboard.setClipEntry(text.toTextClipEntry())
                }
            }
        },
    )
}
