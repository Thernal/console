package io.thernal.console.stepper.ui.stepper

/**
 * The fixed set of auto-resume delays offered in settings — mirrors the chip options the
 * hand-written auto-resume control used to offer. `null` (no selection) maps to
 * [Stepper.Config.autoResumeSeconds] being `null` (auto-resume off).
 */
enum class AutoResumeDelay(val seconds: Int) {
    Three(seconds = 3),
    Five(seconds = 5),
    Ten(seconds = 10),
    Thirty(seconds = 30),
    ;

    companion object {
        fun fromSecondsOrNull(seconds: Int?): AutoResumeDelay? {
            return entries.find { it.seconds == seconds }
        }
    }
}
