package io.thernal.console.crash

import io.thernal.console.core.ConsoleInternalApi
import io.thernal.console.core.log.BasicLog
import io.thernal.console.core.log.Log
import io.thernal.console.core.log.LogLevel
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.time.Instant

@OptIn(ConsoleInternalApi::class)
class LogCodecRegistryTest {

    @BeforeTest
    fun registerCodec() {
        LogCodecRegistry.register(
            type = FooLog::class,
            discriminator = FOO_DISCRIMINATOR,
            codec = FooLogCodec,
        )
    }

    @Test
    fun `restores a registered log type with full fidelity`() {
        val session = CrashSession(
            header = CrashSessionHeader(id = "s", crashedAt = 1, summary = "boom"),
            logs = listOf(
                FooLog(
                    message = "order placed",
                    orderId = "order-99",
                    amount = 4200,
                    id = "log-1",
                    timestamp = Instant.fromEpochMilliseconds(123),
                ),
            ),
        )

        val restored = CrashSessionSerializer.deserialize(CrashSessionSerializer.serialize(session))

        val log = assertIs<FooLog>(restored?.logs?.single())
        assertEquals("order-99", log.orderId)
        assertEquals(4200, log.amount)
        assertEquals("order placed", log.message)
        assertEquals("log-1", log.id)
        assertEquals(123, log.timestamp.toEpochMilliseconds())
    }

    @Test
    fun `falls back to a basic log when the codec returns null`() {
        val envelopeMissingPayload = LogEnvelope(
            type = FOO_DISCRIMINATOR,
            id = "log-1",
            timestamp = 7,
            level = LogLevel.Info.name,
            tag = null,
            message = "no payload",
            groupId = null,
        )

        val log = CrashSessionSerializer.decodeLog(envelopeMissingPayload)

        assertIs<BasicLog>(log)
        assertEquals("no payload", log.message)
    }

    private data class FooLog(
        override val message: String,
        val orderId: String,
        val amount: Long,
        override val id: String,
        override val timestamp: Instant,
        override val tag: String? = null,
        override val level: LogLevel = LogLevel.Info,
        override val groupId: String? = null,
    ) : Log

    private object FooLogCodec : LogCodec<FooLog> {
        override fun encode(log: FooLog): Map<String, String> = mapOf(
            KEY_ORDER_ID to log.orderId,
            KEY_AMOUNT to log.amount.toString(),
        )

        override fun decode(
            envelope: LogEnvelope,
            payload: Map<String, String>,
        ): FooLog? {
            val orderId = payload[KEY_ORDER_ID] ?: return null
            val amount = payload[KEY_AMOUNT]?.toLongOrNull() ?: return null
            return FooLog(
                message = envelope.message,
                orderId = orderId,
                amount = amount,
                id = envelope.id,
                timestamp = Instant.fromEpochMilliseconds(envelope.timestamp),
            )
        }
    }

    private companion object {
        const val FOO_DISCRIMINATOR = "test.foo"
        const val KEY_ORDER_ID = "orderId"
        const val KEY_AMOUNT = "amount"
    }
}
