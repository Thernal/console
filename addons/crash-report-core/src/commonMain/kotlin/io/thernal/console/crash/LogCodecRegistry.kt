package io.thernal.console.crash

import io.thernal.console.core.ConsoleInternalApi
import io.thernal.console.core.log.Log
import kotlin.reflect.KClass

/**
 * Registry of [LogCodec]s, mirroring `LogRendererRegistry`: encode is keyed by the log's [KClass],
 * decode is keyed by the on-disk discriminator string.
 *
 * Registration is [ConsoleInternalApi] — first-party addons and consumers opt in deliberately, the
 * same bar as `LogRendererRegistry.register` / `Console.addObserver`.
 */
object LogCodecRegistry {

    /** Resolved encoder for a log: the discriminator to write plus the codec to run. */
    class Encoder internal constructor(
        val discriminator: String,
        val codec: LogCodec<Log>,
    )

    private class Registration(
        val discriminator: String,
        val codec: LogCodec<*>,
    )

    private val byType = mutableMapOf<KClass<out Log>, Registration>()
    private val byDiscriminator = mutableMapOf<String, LogCodec<*>>()

    @ConsoleInternalApi
    fun <T : Log> register(
        type: KClass<T>,
        discriminator: String,
        codec: LogCodec<T>,
    ) {
        byType[type] = Registration(discriminator = discriminator, codec = codec)
        byDiscriminator[discriminator] = codec
    }

    @ConsoleInternalApi
    inline fun <reified T : Log> register(
        discriminator: String,
        codec: LogCodec<T>,
    ) {
        register(type = T::class, discriminator = discriminator, codec = codec)
    }

    /** Encoder for [log], or `null` to serialize it as the baseline [BASIC_DISCRIMINATOR] envelope. */
    @Suppress("UNCHECKED_CAST")
    internal fun encoderFor(log: Log): Encoder? {
        val registration = byType[log::class] ?: return null
        return Encoder(
            discriminator = registration.discriminator,
            codec = registration.codec as LogCodec<Log>,
        )
    }

    /** Decoder for [discriminator], or `null` to rebuild the log as a `BasicLog`. */
    @Suppress("UNCHECKED_CAST")
    internal fun decoderFor(discriminator: String): LogCodec<Log>? {
        return byDiscriminator[discriminator] as? LogCodec<Log>
    }
}
