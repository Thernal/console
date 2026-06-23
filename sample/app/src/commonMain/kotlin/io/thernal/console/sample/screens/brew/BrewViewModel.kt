package io.thernal.console.sample.screens.brew

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.thernal.console.core.log.Log
import io.thernal.console.core.log.LogLevel
import io.thernal.console.details.ConsoleDetails
import io.thernal.console.runtime.console.Console
import io.thernal.console.sample.network.FakeTodoRepository
import io.thernal.console.sample.platform.deviceModel
import io.thernal.console.sample.platform.platformLabel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class BrewViewModel : ViewModel() {

    private val repository = FakeTodoRepository()

    val cart: List<CartItem> = listOf(
        CartItem(emoji = "☕", name = "Cappuccino", detail = "Oat milk · Medium", price = "$4.50"),
        CartItem(emoji = "🥐", name = "Almond croissant", detail = "Warmed", price = "$4.00"),
    )
    val total: String = "$8.50"

    private val _stage = mutableStateOf(OrderStage.Idle)
    val stage: State<OrderStage> = _stage

    init {
        // Realistic ambient context — shows up live in the console's Details tab.
        ConsoleDetails.put("User" to "alice@brew.app")
        ConsoleDetails.put("App" to "Brew 1.4.2 (debug)")
        ConsoleDetails.put("Platform" to platformLabel())
        ConsoleDetails.put("Device" to deviceModel())
        ConsoleDetails.put("Env" to "staging")

        viewModelScope.launch {
            emit(LogLevel.Info, "Checkout opened")
        }
    }

    // Drives the visible timeline through one stage at a time, emitting a log at each step so the
    // console tells the same story. The pauses keep it watchable — and steppable in the console.
    fun placeOrder() {
        if (_stage.value != OrderStage.Idle && _stage.value != OrderStage.Done) return

        viewModelScope.launch {
            moveTo(OrderStage.Validating)
            emit(LogLevel.Debug, "Validating cart — ${cart.size} items")
            delay(STEP_DELAY)

            moveTo(OrderStage.Reserving)
            emit(LogLevel.Info, "Reserved ${cart.size} items in warehouse EU-2")
            delay(STEP_DELAY)

            moveTo(OrderStage.Charging)
            emit(LogLevel.Info, "Authorizing payment of $total")
            chargeCard()
            delay(STEP_DELAY)

            moveTo(OrderStage.Confirming)
            emit(LogLevel.Info, "Order #$ORDER_NUMBER confirmed")
            delay(STEP_DELAY)

            moveTo(OrderStage.Done)
            emit(LogLevel.Success, "Order placed — see you soon ☕")
            ConsoleDetails.put("Last order" to "#$ORDER_NUMBER")
        }
    }

    fun reset() {
        if (_stage.value != OrderStage.Done) return
        viewModelScope.launch { moveTo(OrderStage.Idle) }
    }

    // Real HTTP request — captured automatically by the Ktor network addon, no extra wiring.
    private suspend fun chargeCard() {
        runCatching { repository.createPost() }
            .onSuccess { emit(LogLevel.Success, "Payment authorized · txn $TXN_ID") }
            .onFailure { emit(LogLevel.Warning, "Payment gateway slow — falling back to retry") }
    }

    // Each state transition is logged too — so stepping through the console narrates the state
    // machine itself, in lockstep with the timeline advancing on screen.
    private suspend fun moveTo(next: OrderStage) {
        val from = _stage.value
        _stage.value = next
        // asyncNotify suspends until the event is processed, so transitions stay in order.
        Console.asyncNotify {
            Log(message = "State: ${from.name} → ${next.name}", level = LogLevel.Verbose, tag = "State")
        }
    }

    private suspend fun emit(
        level: LogLevel,
        message: String,
    ) {
        Console.asyncNotify {
            Log(message = message, level = level, tag = "Checkout")
        }
    }

    override fun onCleared() {
        repository.close()
        super.onCleared()
    }

    companion object {
        private const val STEP_DELAY = 650L
        private const val ORDER_NUMBER = "1042"
        private const val TXN_ID = "txn_4Ab92"
    }
}
