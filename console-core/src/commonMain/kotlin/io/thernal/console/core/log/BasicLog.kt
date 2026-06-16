package io.thernal.console.core.log

import kotlin.time.Clock
import kotlin.time.Instant
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
data class BasicLog(
    override val message: String,
    override val tag: String? = null,
    override val level: LogLevel = LogLevel.None,
    override val id: String = Uuid.random().toString(),
    override val timestamp: Instant = Clock.System.now(),
    override val groupId: String? = null,
) : Log
