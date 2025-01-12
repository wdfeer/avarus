package wdfeer.avarus

import com.mojang.brigadier.{Command, CommandDispatcher}
import com.mojang.brigadier.builder.LiteralArgumentBuilder.literal
import com.mojang.brigadier.context.CommandContext
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text
import wdfeer.avarus

import scala.language.postfixOps
import wdfeer.avarus.CommandResult.Failure

def registerMessageCommand(
    name: String,
    makeMessage: CommandContext[ServerCommandSource] => Text
)(implicit dispatcher: CommandDispatcher[ServerCommandSource]): Unit = {
  dispatcher.register(literal(name).executes(context => {
    context.getSource.sendMessage(makeMessage(context))
    0
  }))
}

def toCommand(execute: ServerPlayerEntity => CommandResult): Command[ServerCommandSource] = {
  context => {
    val player = context.getSource.getPlayer
    if player != null then {
      val result = execute(player)
      result match
        case CommandResult.Failure(error) => context.getSource.sendMessage(Text.of(error))
        case CommandResult.Success => ()
      
      result.number
    } else 1
  }
}

enum CommandResult(val number: Int) {
  case Success extends CommandResult(0)
  case Failure(error: String) extends CommandResult(1)
}