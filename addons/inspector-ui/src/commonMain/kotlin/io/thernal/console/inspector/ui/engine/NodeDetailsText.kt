package io.thernal.console.inspector.ui.engine

/** Plain-text form of [NodeDetails], for the clipboard. */
internal fun NodeDetails.toText(): String {
    return buildString {
        appendLine(name)
        definedIn?.let { appendLine("Source file: $it") }
        parentName?.let { appendLine("Parent: $it") }
        sizeDp?.let { appendLine("Size: $it") }
        positionDp?.let { appendLine("Position: $it") }
        padding?.let { appendLine("Padding: $it") }
        boundsPx?.let { appendLine("Bounds (px): $it") }
        appendLine("Recompositions: ${recompositionText()}")
        skippedPasses?.let { appendLine("Skipped: $it") }
        lastReason?.let { appendLine("Last reason: $it") }
        if (modifiers.isNotEmpty()) appendLine("Modifiers: ${modifiers.joinToString(" | ")}")
        if (parameterNames.isNotEmpty()) appendLine("Parameters: ${parameterNames.joinToString()}")
        if (slotValues.isNotEmpty()) appendLine("Slot values: ${slotValues.joinToString(" | ")}")
    }
}
