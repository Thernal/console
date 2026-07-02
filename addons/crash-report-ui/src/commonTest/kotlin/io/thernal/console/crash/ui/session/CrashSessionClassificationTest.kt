package io.thernal.console.crash.ui.session

import io.thernal.console.crash.ui.store.CrashStoreEntry
import kotlin.test.Test
import kotlin.test.assertEquals

class CrashSessionClassificationTest {

    @Test
    fun `a captured crash sidecar means Confirmed regardless of state`() {
        assertEquals(CrashSessionClass.Confirmed, entry(hasCrash = true, state = null).classify())
        assertEquals(CrashSessionClass.Confirmed, entry(hasCrash = true, state = "BACKGROUND").classify())
    }

    @Test
    fun `foreground or unknown death without a trace is Probable`() {
        assertEquals(CrashSessionClass.Probable, entry(hasCrash = false, state = "FOREGROUND").classify())
        assertEquals(CrashSessionClass.Probable, entry(hasCrash = false, state = null).classify())
        assertEquals(CrashSessionClass.Probable, entry(hasCrash = false, state = "garbage").classify())
    }

    @Test
    fun `background death or clean desktop exit is Safe`() {
        assertEquals(CrashSessionClass.Safe, entry(hasCrash = false, state = "BACKGROUND").classify())
        assertEquals(CrashSessionClass.Safe, entry(hasCrash = false, state = "CLEAN").classify())
    }

    private fun entry(
        hasCrash: Boolean,
        state: String?,
    ): CrashStoreEntry {
        return CrashStoreEntry(id = "id", startedAtMs = 0, hasCrash = hasCrash, state = state)
    }
}
