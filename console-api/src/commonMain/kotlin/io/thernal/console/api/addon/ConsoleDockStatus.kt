package io.thernal.console.api.addon

/** What a dock widget is doing, which decides its tint and whether it may be hidden. */
enum class ConsoleDockStatus {
    /** Nothing is running. */
    Idle,

    /** The widget's feature is running. */
    Active,

    /** The user must act: the dock keeps the widget on screen and draws attention to it. */
    Attention,
}
