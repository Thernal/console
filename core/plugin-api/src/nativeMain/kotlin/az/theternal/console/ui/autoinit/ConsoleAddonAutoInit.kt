package az.theternal.console.ui.autoinit

fun consoleAddonInit(block: () -> Unit): Any {
    return object {
        init {
            block()
        }
    }
}
