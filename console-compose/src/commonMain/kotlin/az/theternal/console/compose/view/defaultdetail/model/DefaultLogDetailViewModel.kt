package az.theternal.console.compose.view.defaultdetail.model

import androidx.lifecycle.ViewModel
import az.theternal.console.compose.core.IntentHandler
import az.theternal.console.compose.core.StateHolder

class DefaultLogDetailViewModel : ViewModel(), StateHolder, IntentHandler<DefaultLogDetailIntent> {
    override val state = DefaultLogDetailState()

    override val handler = Handler { intent ->
        when (intent) {
            is DefaultLogDetailIntent.SetQuery -> state.detailQuery.update { intent.query }
            DefaultLogDetailIntent.CopyMessage -> state.copyTrigger.update { this + 1 }
        }
    }
}
