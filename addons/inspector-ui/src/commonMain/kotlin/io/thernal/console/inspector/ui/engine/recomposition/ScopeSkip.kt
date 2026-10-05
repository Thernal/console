// The only way to tell a skipped pass from one that ran: `RecomposeScopeImpl.skipped` has no public equivalent.
// Kept to this one file; if the suppression stops compiling on a newer Kotlin, drop the file and let
// `wasSkipped` return null, which counts every pass as before.
@file:Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE")

package io.thernal.console.inspector.ui.engine.recomposition

import androidx.compose.runtime.RecomposeScope
import androidx.compose.runtime.RecomposeScopeImpl
import kotlin.concurrent.Volatile

/** Written from composition threads, read on the main thread. */
@Volatile
private var isReadable = true

/** Whether the runtime is readable for skips on this Compose version; false once a read has failed. */
internal val canReadSkips: Boolean get() = isReadable

/**
 * Whether [scope]'s function was skipped on the pass that just ended: it was re-entered because its parent recomposed,
 * but its parameters had not changed, so its body did not run. The public observer cannot tell, so this reads the
 * runtime's own flag. That flag is internal to Compose and may change with a version, so a failed read (a linkage
 * error included) switches the feature off and the counts fall back to counting every pass.
 */
internal fun wasSkipped(scope: RecomposeScope): Boolean? {
    if (!isReadable) return null
    return runCatching { (scope as? RecomposeScopeImpl)?.skipped }
        .onFailure { isReadable = false }
        .getOrNull()
}
