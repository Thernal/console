package io.thernal.console.api.autoinit

fun consoleAddonInit(block: () -> Unit): Any {
    return object {
        init {
            block()
        }
    }
}
