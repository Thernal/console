package az.theternal.console.info

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
