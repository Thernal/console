package io.thernal.console.details.compose.view.details

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import io.thernal.console.details.compose.view.details.model.DetailsViewModel

@Composable
internal fun DetailsView() {
    val viewModel = viewModel { DetailsViewModel() }

    DetailsContent(
        state = viewModel.state,
    )
}
