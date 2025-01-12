package wdfeer.avarus

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.LiteralArgumentBuilder.literal
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.Text
import org.slf4j.LoggerFactory
import wdfeer.avarus.CommandResult._

object Avarus extends ModInitializer {
  val MOD_ID = "avarus"
  private lazy val logger = LoggerFactory.getLogger("avarus")

  override def onInitialize(): Unit = {
    CommandRegistrationCallback.EVENT.register((commandDispatcher, _, _) => {
      implicit val dispatcher: CommandDispatcher[ServerCommandSource] = commandDispatcher

      registerMessageCommand("avarus-help", _ => Text.of(
        "Avarus is a mod allowing to obtain stat increases (e.g. max hp) by \"buying\" them with a large amount of items with the \"avarus-get\" command, e.g.:\n/avarus cobblestone"
      ))
      registerMessageCommand("avarus-status", context => {
        val player = context.getSource.getPlayer
        val (applied, notApplied) = buffs.partition(_.isApplied(player))
        Text.of(
          s"${applied.length}/${buffs.length} buffs applied.\n\n" +
            s"Available buffs: " +
            notApplied.take(3).map(_.item.toString).mkString(", ") +
            (if notApplied.length <= 3 then "" else s", +${notApplied.length - 3}")
        )
      })
      registerGetCommand()
      registerGetAllCommand()
    })
    logger.info("Avarus initialized. Start grinding.")
  }

  private def registerGetCommand()(implicit dispatcher: CommandDispatcher[ServerCommandSource]): Unit = {
    var builder: LiteralArgumentBuilder[ServerCommandSource] = literal("avarus-get")

    for buff <- buffs do
      builder = builder.`then`(literal(buff.item.toString.toLowerCase).executes(toCommand(buff.tryApply)))

    dispatcher.register(builder)
  }

  private def registerGetAllCommand()(implicit dispatcher: CommandDispatcher[ServerCommandSource]): Unit = {
    var builder: LiteralArgumentBuilder[ServerCommandSource] = literal("avarus-get-all")
    builder = builder.requires(_.hasPermissionLevel(2))

    builder = builder.executes(toCommand(player => {
      if player.isCreative then
        (if buffs.map(_.tryApply(player)).exists(_ == Success) then Success
        else Failure("All buffs already applied!"))
      else Failure("You must be in creative mode!")
    }))

    dispatcher.register(builder)
  }
}
