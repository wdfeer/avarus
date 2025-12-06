package wdfeer.avarus

sealed class CommandResult(number: Int) {
    object Success : CommandResult(0)
    data class Failure(val error: String) : CommandResult(1)
}

object Commands {
    fun initialize(config: Config) {
        TODO("Make commands, that's like the entire mod!")
    }
}