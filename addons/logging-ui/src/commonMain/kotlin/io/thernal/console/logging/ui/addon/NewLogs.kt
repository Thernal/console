package io.thernal.console.logging.ui.addon

import io.thernal.console.core.log.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Each log once, read from the list the Logs tab keeps: on every change, the logs after the last one already
 * passed on. The first list passes on every log kept so far, so a feed collected late still starts from the
 * oldest. When the last one passed on is gone (the list was cleared, or more logs came than it keeps), the whole
 * list is new.
 */
internal fun newLogs(lists: Flow<List<Log>>): Flow<Log> {
    return flow {
        var lastId: String? = null
        lists.collect { logs ->
            val seen = lastId?.let { id -> logs.indexOfLast { it.id == id } } ?: -1
            for (index in seen + 1..logs.lastIndex) emit(logs[index])
            if (logs.isNotEmpty()) lastId = logs.last().id
        }
    }
}
