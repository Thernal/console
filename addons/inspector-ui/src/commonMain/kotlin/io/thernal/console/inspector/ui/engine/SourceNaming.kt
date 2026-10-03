package io.thernal.console.inspector.ui.engine

private val genericNames = setOf(
    "remember",
    "rememberComposableLambda",
    "CompositionLocalProvider",
    "ReusableComposeNode",
    "ComposeNode",
    "ReusableContentHost",
    "Layout",
    "key",
)

private val frameworkFiles = setOf(
    "Text.kt", "BasicText.kt", "BasicTextField.kt", "TextField.kt", "Box.kt", "Row.kt", "Column.kt",
    "Layout.kt", "Surface.kt", "Scaffold.kt", "Button.kt", "IconButton.kt", "Icon.kt", "Card.kt",
    "Composables.kt", "CompositionLocal.kt", "ComposableLambda.kt", "Effects.kt", "Spacer.kt",
    "Divider.kt", "HorizontalDivider.kt", "Switch.kt", "Checkbox.kt", "Image.kt", "Canvas.kt",
    "Clickable.kt", "MaterialTheme.kt", "Typography.kt", "ColorScheme.kt", "NavDisplay.kt",
    "AnimatedContent.kt", "AnimatedVisibility.kt", "Crossfade.kt", "BoxWithConstraints.kt",
    "SubcomposeLayout.kt", "Popup.kt", "Dialog.kt", "Shape.kt", "Ripple.kt", "Indication.kt", "WrappedContent.kt",
)

private val frameworkFilePrefixes = listOf(
    "Lazy", "Animated", "Material", "Pager", "Scroll", "Window", "Provide",
    "Navigation", "Decorated", "NavEntry", "SaveableState", "Basic", "Background",
)

/**
 * Files of the inspector's own overlay layers: they redraw all the time, so they are never counted. The host is not
 * listed: the whole app content sits below it.
 */
private val inspectorOwnFiles = setOf(
    "InspectorOverlay.kt",
    "InspectorDrawLayer.kt",
    "InspectorPickLayer.kt",
)

internal fun isInspectorOwnFile(file: String?): Boolean {
    return file in inspectorOwnFiles
}

internal fun isGenericName(name: String): Boolean {
    return name in genericNames || name.startsWith("<get-")
}

/**
 * Whether a composable looks like library code rather than app code.
 *
 * With [appTags] set, app code is whatever matches a tag (file or function name) and everything
 * else counts as framework. Without tags, lower-case names (`remember*`, `collectAsState`: value
 * helpers, not UI) and a built-in list of well-known Compose file names count as framework.
 */
internal fun isFrameworkNode(
    name: String,
    file: String?,
    appTags: Set<String>,
): Boolean {
    if (appTags.isNotEmpty()) {
        return appTags.none { tag ->
            name.contains(tag, ignoreCase = true) || file?.contains(tag, ignoreCase = true) == true
        }
    }
    if (!name.first().isUpperCase()) return true
    return file != null && (file in frameworkFiles || frameworkFilePrefixes.any { file.startsWith(it) })
}
