package io.thernal.console.sample.screens.brew

/**
 * Stages of the demo checkout flow. Each transition updates the visible timeline and emits a
 * log, so the console's log list (and the stepper) tells the same story the UI does.
 */
enum class OrderStage(val label: String) {
    Idle("Ready when you are"),
    Validating("Validating cart"),
    Reserving("Reserving items"),
    Charging("Charging card"),
    Confirming("Confirming order"),
    Done("Order placed"),
}
