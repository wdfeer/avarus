package wdfeer.avarus

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.builder.LiteralArgumentBuilder.literal
import com.mojang.brigadier.context.CommandContext
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.Text

def registerMessageCommand(
    name: String,
    makeMessage: CommandContext[ServerCommandSource] => Text
)(implicit dispatcher: CommandDispatcher[ServerCommandSource]): Unit = {
  dispatcher.register(literal(name).executes(context => {
    context.getSource.sendMessage(makeMessage(context))
    0
  }))
}
