package io.thernal.console.inspector.ui.view.inspector.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import io.thernal.console.designsystem.components.core.DsText
import io.thernal.console.designsystem.components.core.DsTextField
import io.thernal.console.designsystem.foundation.theme.Theme
import io.thernal.console.inspector.ui.view.inspector.model.InspectorIntent
import io.thernal.console.inspector.ui.view.inspector.model.InspectorState

@Composable
internal fun TreePage(
    state: InspectorState,
    dispatch: (InspectorIntent) -> Unit,
) {
    val rows = state.rows.value
    Column(Modifier.fillMaxSize()) {
        DsTextField(
            value = TextFieldValue(state.query.value),
            onValueChange = { dispatch(InspectorIntent.SetQuery(it.text)) },
            hint = "Search by name or file",
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Theme.metrics.screenPaddingHorizontal),
        )
        DsText(
            text = state.summary.value,
            style = Theme.typography.label02,
            color = Theme.colors.content04,
            modifier = Modifier.padding(
                horizontal = Theme.metrics.screenPaddingHorizontal,
                vertical = Theme.dimens.dp8,
            ),
        )
        if (rows.isEmpty()) {
            EmptyHint("Nothing to show yet. Pull the tree with refresh, or check that the inspector is enabled.")
            return@Column
        }
        LazyColumn(Modifier.fillMaxSize()) {
            items(rows, key = { it.node.id }) { row ->
                TreeNodeRow(
                    row = row,
                    onClick = { dispatch(InspectorIntent.SelectNode(row.node.key)) },
                    onToggle = { dispatch(InspectorIntent.ToggleCollapsed(row.node.key)) },
                )
            }
        }
    }
}
