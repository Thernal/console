package io.thernal.console.stepper.ui.dock

/**
 * The live line in the panel header: off, or the pipeline status with how many events are being held. A paused
 * pipeline with nothing waiting reads "Paused", never "Idle", so the amber dock says why it is amber.
 */
internal fun stepperSummary(
    isEnabled: Boolean,
    isPaused: Boolean,
    statusText: String,
    heldCount: Int,
): String {
    if (!isEnabled) return "Off"
    val status = if (isPaused && statusText == IDLE) PAUSED else statusText
    return if (heldCount > 0) "$status · $heldCount held" else status
}

private const val IDLE = "Idle"
private const val PAUSED = "Paused"
