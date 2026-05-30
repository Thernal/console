package az.theternal.console.compose.view.defaultdetail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalClipboard
import androidx.lifecycle.viewmodel.compose.viewModel
import az.theternal.console.compose.util.toTextClipEntry
import az.theternal.console.compose.view.defaultdetail.model.DefaultLogDetailViewModel
import az.theternal.console.runtime.Log

@Composable
internal fun DefaultLogDetailView(
    log: Log,
    onBack: () -> Unit,
) {
    val viewModel = viewModel { DefaultLogDetailViewModel() }
    val clipboard = LocalClipboard.current

    LaunchedEffect(viewModel.state.copyTrigger.value) {
        if (viewModel.state.copyTrigger.value > 0) clipboard.setClipEntry(log.message.toTextClipEntry())
    }

    DefaultLogDetailContent(
        log = log,
        onBack = onBack,
        state = viewModel.state,
        onDispatch = viewModel::dispatch,
    )
}
