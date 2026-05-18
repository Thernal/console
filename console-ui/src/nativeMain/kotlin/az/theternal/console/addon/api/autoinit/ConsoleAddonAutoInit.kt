package az.theternal.console.addon.api.autoinit

fun consoleAddonInit(block: () -> Unit): Any {
    return object {
        init {
            block()
        }
    }
}
