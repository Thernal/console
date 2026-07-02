package io.thernal.console.ui.logdetail.model

import io.thernal.console.core.log.Log

/** A resolved log group for the shared detail pager: members in timestamp order + start page. */
data class LogGroup(
    val logs: List<Log>,
    val initialPageIndex: Int,
)

/**
 * Resolves the group shown for [targetLogId] out of [sourceLogs]: the target plus every log
 * sharing its `groupId`, in timestamp order. A target without a `groupId` yields a single-page
 * group; a missing target yields an empty one (the detail renders its not-found state).
 */
fun resolveLogGroup(
    sourceLogs: List<Log>,
    targetLogId: String,
): LogGroup {
    val target = sourceLogs.firstOrNull { it.id == targetLogId }
    val logs = when {
        target == null -> emptyList()

        target.groupId == null -> listOf(target)

        else ->
            sourceLogs
                .filter { it.id == targetLogId || it.groupId == target.groupId }
                .sortedBy(Log::timestamp)
    }
    val initialPageIndex = logs.indexOfFirst { it.id == targetLogId }
        .takeIf { it >= 0 } ?: 0

    return LogGroup(logs = logs, initialPageIndex = initialPageIndex)
}
