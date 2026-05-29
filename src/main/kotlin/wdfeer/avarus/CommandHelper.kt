package wdfeer.avarus

import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import net.minecraft.server.command.CommandManager.literal
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text

sealed class CommandResult(val number: Int) {
    fun isSuccess(): Boolean {
        return this is SilentSuccess || this is Success
    }

    // TODO: check the sus zeros
    object SilentSuccess : CommandResult(0)
    data class Success(val info: String) : CommandResult(0)
    data class Failure(val error: String) : CommandResult(0)
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
            1
        }
    }
}