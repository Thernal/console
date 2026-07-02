package io.thernal.console.crash.ui.session

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class CrashSidecarTest {

    @Test
    fun `encode and parse round-trip with a multi-line stack trace`() {
        val sidecar = CrashSidecar(
            crashedAtMs = 1_234,
            summary = "IllegalStateException: boom",
            stackTrace = "IllegalStateException: boom\n  at a()\n  at b()",
        )

        assertEquals(sidecar, CrashSidecar.parse(sidecar.encode()))
    }

    @Test
    fun `of builds the summary from the first trace line`() {
        val sidecar = CrashSidecar.of(throwable = IllegalStateException("boom"), crashedAtMs = 7)

        assertEquals(7, sidecar.crashedAtMs)
        assertTrue(sidecar.summary.contains("IllegalStateException"))
        assertTrue(sidecar.summary.contains("boom"))
        assertTrue(sidecar.stackTrace.startsWith(sidecar.summary))
    }

    @Test
    fun `parse rejects malformed content`() {
        assertNull(CrashSidecar.parse(""))
        assertNull(CrashSidecar.parse("only-one-line"))
        assertNull(CrashSidecar.parse("not-a-number\nsummary\ntrace"))
    }
}
