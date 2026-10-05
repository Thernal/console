package io.thernal.console.inspector.ui.engine.tree

internal fun isInspectorOwnFile(file: String?): Boolean {
    return file in inspectorOwnFiles
}

internal fun isGenericName(name: String): Boolean {
    return name.isEmpty() || name in genericNames || name.startsWith("<get-")
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
    if (name.firstOrNull()?.isUpperCase() != true) return true
    return file != null && (file in frameworkFiles || frameworkFilePrefixes.any { file.startsWith(it) })
}

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

/**
 * Well-known Compose library files. Names that an app file could plausibly share a prefix with are listed in full
 * here rather than matched by prefix, so app code such as `BasicScreen.kt` or `WindowSettings.kt` stays app code.
 * `WrappedContent.kt` is console-ui's content wrapper host.
 */
private val frameworkFiles = setOf(
    "Text.kt", "BasicText.kt", "BasicTextField.kt", "BasicTooltip.kt", "TextField.kt", "Box.kt", "Row.kt",
    "Column.kt", "Layout.kt", "Surface.kt", "Scaffold.kt", "Button.kt", "IconButton.kt", "Icon.kt", "Card.kt",
    "Composables.kt", "CompositionLocal.kt", "ComposableLambda.kt", "Effects.kt", "Spacer.kt",
    "Divider.kt", "HorizontalDivider.kt", "Switch.kt", "Checkbox.kt", "Image.kt", "Canvas.kt",
    "Clickable.kt", "MaterialTheme.kt", "Typography.kt", "ColorScheme.kt", "NavDisplay.kt",
    "AnimatedContent.kt", "AnimatedVisibility.kt", "Crossfade.kt", "BoxWithConstraints.kt",
    "SubcomposeLayout.kt", "Popup.kt", "Dialog.kt", "Shape.kt", "Ripple.kt", "Indication.kt",
    "Background.kt", "Scroll.kt", "Scrollable.kt", "Window.kt", "ProvideContentColorTextStyle.kt",
    "NavigationBar.kt", "NavigationRail.kt", "NavigationDrawer.kt", "WrappedContent.kt",
)

/** File name prefixes that only Compose library families use. */
private val frameworkFilePrefixes = listOf(
    "Lazy",
    "Animated",
    "Pager",
    "Decorated",
    "NavEntry",
    "SaveableState",
)

/**
 * Files of the inspector's own overlay layers: they redraw all the time, so they are never counted. The host is not
 * listed: the whole app content sits below it. Matched by file name, so keep this in sync when renaming them.
 */
private val inspectorOwnFiles = setOf(
    "InspectorBackdrop.kt",
    "InspectorCapture.kt",
    "InspectorDrawLayer.kt",
    "InspectorPickLayer.kt",
)
