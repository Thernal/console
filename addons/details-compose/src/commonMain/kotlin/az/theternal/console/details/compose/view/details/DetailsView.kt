package az.theternal.console.details.compose.view.details

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
internal fun DetailsView() {
    val viewModel = viewModel { DetailsViewModel() }
    DetailsContent(state = viewModel.state)
}
