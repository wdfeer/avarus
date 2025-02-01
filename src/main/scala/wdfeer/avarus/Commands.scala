package wdfeer.avarus

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.LiteralArgumentBuilder.literal
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.Text
import wdfeer.avarus.CommandResult._

def initializeCommands(): Unit = {
  CommandRegistrationCallback.EVENT.register((commandDispatcher, _, _) => {
    implicit val dispatcher: CommandDispatcher[ServerCommandSource] = commandDispatcher

    registerMessageCommand("avarus-help", _ => Text.of(
      "Avarus is a mod allowing to obtain stat increases (e.g. max hp) by \"buying\" them with a large amount of items with the \"avarus-get\" command, e.g.:\n/avarus cobblestone"
    ))
    registerMessageCommand("avarus-status", context => {
      val player = context.getSource.getPlayer
      val (applied, notApplied) = defaultBuffs.partition(_.isApplied(player))
      Text.of(
        s"${applied.length}/${defaultBuffs.length} buffs applied.\n\n" +
          s"Available buffs: " +
          notApplied.take(3).map(_.item.toString).mkString(", ") +
          (if notApplied.length <= 3 then "" else s", +${notApplied.length - 3}")
      )
    })
    registerGetCommand()
    registerGetAllCommand()
  })
}

private def registerGetCommand()(implicit dispatcher: CommandDispatcher[ServerCommandSource]): Unit = {
  var builder: LiteralArgumentBuilder[ServerCommandSource] = literal("avarus-get")

  for buff <- defaultBuffs do
    builder = builder.`then`(literal(buff.item.toString.toLowerCase).executes(toCommand(buff.tryApply)))

  dispatcher.register(builder)
}

private def registerGetAllCommand()(implicit dispatcher: CommandDispatcher[ServerCommandSource]): Unit = {
  var builder: LiteralArgumentBuilder[ServerCommandSource] = literal("avarus-get-all")
  builder = builder.requires(_.hasPermissionLevel(2))

  builder = builder.executes(toCommand(player => {
    if player.isCreative then
      if defaultBuffs.map(_.tryApply(player)).contains(Success) then Success
      else Failure("All buffs already applied!")
    else Failure("You must be in creative mode!")
  }))

  dispatcher.register(builder)
}