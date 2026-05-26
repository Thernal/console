package az.theternal.console.details

interface ConsoleKey {
    val name: String

    fun set(value: Any?) = Unit

    fun remove() = Unit
}
