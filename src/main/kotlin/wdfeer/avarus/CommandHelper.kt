package wdfeer.avarus

import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import net.minecraft.server.command.CommandManager.literal
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text

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

fun toCommand(execute: (ServerPlayerEntity) -> CommandResult): Command<ServerCommandSource> {
    return Command { context ->
        val player = context.source.player
        if (player != null) {
            val result = execute(player)
            if (result is CommandResult.Failure) {
                context.source.sendMessage(Text.of(result.error))
            }
            result.number
        } else {
            1
        }
    }
}