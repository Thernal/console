package az.theternal.console.details

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
