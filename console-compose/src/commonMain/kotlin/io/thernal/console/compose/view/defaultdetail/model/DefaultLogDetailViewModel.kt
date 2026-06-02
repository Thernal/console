package io.thernal.console.compose.view.defaultdetail.model

import androidx.lifecycle.ViewModel
import io.thernal.console.compose.core.IntentHandler
import io.thernal.console.compose.core.StateHolder

class DefaultLogDetailViewModel : ViewModel(), StateHolder, IntentHandler<DefaultLogDetailIntent> {
    val state = DefaultLogDetailState()

    override val handler = Handler { intent ->
        when (intent) {
            is DefaultLogDetailIntent.SetQuery -> state.detailQuery.update { intent.query }
            DefaultLogDetailIntent.CopyMessage -> state.copyTrigger.update { this + 1 }
        }
    }
}
