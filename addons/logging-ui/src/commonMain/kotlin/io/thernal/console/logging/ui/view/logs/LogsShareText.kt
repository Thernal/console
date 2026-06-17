package io.thernal.console.logging.ui.view.logs

import io.thernal.console.ui.common.extensions.toHms
import io.thernal.console.core.log.Log

/**
 * Renders the full log list as shareable plain text in capture (chronological) order. Each entry is
 * its timestamp followed by the log's own [Log.toShareText] representation, so custom log types
 * (e.g. network logs) contribute their richer content rather than just a summary line.
 */
internal fun List<Log>.toShareText(): String {
    return joinToString(separator = "\n\n") { log ->
        "${log.timestamp.toHms()} ${log.toShareText()}"
    }
}
