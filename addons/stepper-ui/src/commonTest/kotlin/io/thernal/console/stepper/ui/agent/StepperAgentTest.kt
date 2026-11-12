package io.thernal.console.stepper.ui.agent

import io.thernal.console.core.log.BasicLog
import io.thernal.console.core.log.LogLevel
import io.thernal.console.stepper.ui.stepper.Stepper
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class StepperAgentTest {
    private val order = BasicLog(message = "POST /orders\nbody", tag = "Checkout", level = LogLevel.Info, id = "a")
    private val created = BasicLog(message = "Order created", tag = "Checkout", level = LogLevel.Info, id = "b")

    @Test
    fun `each hold and release becomes a record even when one hold follows another at once`(): TestResult {
        return runTest {
            val states = flowOf(
                Stepper.State(),
                held(order),
                held(created, before = listOf(order)),
                Stepper.State(steppedEvents = listOf(order, created)),
            )

            val records = stepperEvents(states).toList()

            assertEquals(
                listOf(
                    "Held: POST /orders",
                    "Released: POST /orders",
                    "Held: Order created",
                    "Released: Order created",
                ),
                records.map { it.summary },
            )
            assertEquals(listOf("a", "a", "b", "b"), records.map { it.fields["log_id"] })
        }
    }

    @Test
    fun `the state names the rules the held log and an agent pause`() {
        val config = Stepper.Config(
            enabled = true,
            paused = true,
            pauseOnMatch = true,
            pauseOnTags = setOf("Checkout"),
            pauseOnLevelAtLeast = LogLevel.Info,
        )

        val text = stepperStateText(config, held(order), pausedByAgent = true)

        assertEquals(
            """
            Stepper paused: holds logs tagged Checkout at level Info or above.
            Auto-resume: off
            Paused by an agent: it resumes by itself when the hub disconnects.
            Held now: a Info [Checkout] POST /orders
            Logs held or queued in the stepper: 1
            """.trimIndent(),
            text,
        )
    }

    private fun held(
        log: BasicLog,
        before: List<BasicLog> = emptyList(),
    ): Stepper.State {
        return Stepper.State(steppedEvents = before + log, pendingLogs = 1, blockedLogId = log.id, blockedTag = log.tag)
    }
}
