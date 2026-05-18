package az.theternal.console.details.api

interface ConsoleKey {
    val name: String

    fun set(value: Any?) {
        if (value == null) {
            remove()
        } else {
            ConsoleDetails.put(name to value.toString())
        }
    }

    fun remove() {
        ConsoleDetails.remove(name)
    }
}
