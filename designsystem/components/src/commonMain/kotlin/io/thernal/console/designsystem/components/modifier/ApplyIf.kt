package io.thernal.console.designsystem.components.modifier

import androidx.compose.ui.Modifier

/**
 * ```
 * Usage:
 *
 * Modifier.applyIf(someCondition) { someModifierExtensions() }
 * ```
 * */
inline fun Modifier.applyIf(
    condition: Boolean,
    ifTrue: Modifier.() -> Modifier,
): Modifier {
    return if (condition) {
        then(ifTrue())
    } else {
        this
    }
}

/**
 * ```
 * Usage:
 *
 * Modifier.applyIf(
 *     someCondition,
 *     ifTrue = { someModifierExtensions() },
 *     ifFalse = { anotherModifierExtensions() }
 * )
 * ```
 * */
inline fun Modifier.applyIf(
    condition: Boolean,
    ifTrue: Modifier.() -> Modifier,
    ifFalse: Modifier.() -> Modifier = { this },
): Modifier {
    return if (condition) {
        then(ifTrue())
    } else {
        then(ifFalse())
    }
}
