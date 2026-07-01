package io.thernal.console.crash

/**
 * On-disk format version written into every serialized crash-session container.
 *
 * Bumped only when the wire layout itself changes — never on an app-version change, so a plain
 * app update does not wipe previously recorded crashes. On read, a missing or mismatched version
 * discards the whole session (a changed *codec*, in contrast, only falls the affected log back to
 * [io.thernal.console.core.log.BasicLog]).
 */
const val CRASH_FORMAT_VERSION: Int = 1

/**
 * Discriminator for logs serialized as the baseline [io.thernal.console.core.log.BasicLog] envelope —
 * the free fallback used when a log type has no registered [LogCodec].
 */
const val BASIC_DISCRIMINATOR: String = "basic"
