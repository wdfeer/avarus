package wdfeer.avarus

import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import net.minecraft.server.command.CommandManager.literal
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text

fun getResultNumber(success: Boolean) = if (success) Command.SINGLE_SUCCESS else 0

sealed class CommandResult(val success: Boolean) {
    val number: Int get() = getResultNumber(success)

    object SilentSuccess : CommandResult(true)
    data class Success(val info: String) : CommandResult(true)
    data class Failure(val error: String) : CommandResult(false)
}

/** Registers a command that sends a message to the player, always succeeds. */
fun registerMessageCommand(
    dispatcher: CommandDispatcher<ServerCommandSource>,
    name: String,
    makeMessage: (CommandContext<ServerCommandSource>) -> Text
) {
    dispatcher.register(literal(name).executes { context ->
        context.source.sendMessage(makeMessage(context))
        0
    })
}

/** Registers a command that performs an action on the player and sends an error message on error. */
fun toCommand(execute: (ServerPlayerEntity) -> CommandResult): Command<ServerCommandSource> {
    return Command { context ->
        val player = context.source.player
        if (player != null) {
            val result = execute(player)
            when (result) {
                CommandResult.SilentSuccess -> {}
                is CommandResult.Success -> context.source.sendMessage(Text.of(result.info))
                is CommandResult.Failure -> context.source.sendMessage(Text.of(result.error))
            }
            result.number
        } else {
            getResultNumber(false)
        }
    }
}