package wdfeer.avarus

sealed class CommandResult(val number: Int) {
    object Success : CommandResult(0)
    data class Failure(val error: String) : CommandResult(1)
}