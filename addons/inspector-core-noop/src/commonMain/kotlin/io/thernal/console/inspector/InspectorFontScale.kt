package io.thernal.console.inspector

/** Font scale multipliers offered as an override on top of the system font scale. */
enum class InspectorFontScale(val factor: Float) {
    Small(factor = 0.85f),
    Large(factor = 1.15f),
    Larger(factor = 1.3f),
    Largest(factor = 1.6f),
    Huge(factor = 2f),
}
