package io.thernal.console.inspector.ui.engine.geometry

import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.InspectableValue

/** What a modifier contributes to a node's box, as far as framing it goes. */
internal enum class ModifierKind {
    /** `Modifier.padding`: space around what follows it in the chain. */
    Padding,

    /** Something drawn the user can see as the node's edge: a background, border, shadow or clip. */
    Visual,

    /** Anything else: size, click handling, semantics and the like. */
    Other,
    ;

    companion object {
        fun of(modifier: Modifier): ModifierKind {
            val name = (modifier as? InspectableValue)?.nameFallback
            return when (name) {
                in PADDING_MODIFIERS -> Padding
                in VISUAL_MODIFIERS -> Visual
                else -> Other
            }
        }
    }
}

/** `nameFallback` of the `Modifier.padding` family in Compose foundation. */
private val PADDING_MODIFIERS = setOf("padding", "absolutePadding")

/** `nameFallback` of the modifiers that draw what reads as a component's edge. */
private val VISUAL_MODIFIERS = setOf(
    "background", "border", "shadow", "clip", "clipToBounds", "graphicsLayer",
    "drawBehind", "drawWithContent", "drawWithCache", "paint",
)
