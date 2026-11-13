package io.thernal.console.inspector.ui.agent

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.semantics.SemanticsConfiguration
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull

/**
 * The composition's semantics root where the platform shares it with app code (Android's compose view), else null.
 * Read-only, so it adds no group to the tree the inspector shows.
 */
@Composable
@ReadOnlyComposable
internal expect fun currentSemanticsRoot(): Any?

/** Keeps [root] (from [currentSemanticsRoot]) for [platformSemanticsTexts]; called from the inspector's host. */
internal expect fun keepSemanticsRoot(root: Any?)

/**
 * Texts per semantics id (`LayoutInfo.semanticsId`) from the platform's semantics tree: what a `Text` shows even when
 * it is a literal the slot table never holds. Empty where the platform keeps that tree internal (iOS, desktop).
 * Main thread only.
 */
internal expect fun platformSemanticsTexts(): Map<Int, List<String>>

/** The text, edited text and content descriptions in [config]. */
internal fun semanticsTextsOf(config: SemanticsConfiguration): List<String> {
    return buildList {
        config.getOrNull(SemanticsProperties.Text)?.forEach { add(it.text) }
        config.getOrNull(SemanticsProperties.EditableText)?.let { add(it.text) }
        config.getOrNull(SemanticsProperties.ContentDescription)?.let(::addAll)
    }
}
