package wdfeer.avarus

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.LiteralArgumentBuilder.literal
import com.mojang.brigadier.context.CommandContext
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.minecraft.item.{Item, Items}
import net.minecraft.server.command.ServerCommandSource
import org.slf4j.LoggerFactory

object Avarus extends ModInitializer {
  private lazy val logger = LoggerFactory.getLogger("avarus")

  override def onInitialize(): Unit = {
    CommandRegistrationCallback.EVENT.register((dispatcher, _, _) => registerCommand(dispatcher))

    logger.info("Avarus initialized. Start grinding.")
  }

  private def registerCommand(dispatcher: CommandDispatcher[ServerCommandSource]): Unit = {
    var builder: LiteralArgumentBuilder[ServerCommandSource] = literal("avarus")
    for entry <- AvarusSubcommand.values do
      builder = builder.`then`(literal(entry.toString.toLowerCase).executes(context => entry.action(context)))
    dispatcher.register(builder)
  }

  enum AvarusSubcommand(val item: Item, val action: CommandContext[ServerCommandSource] => Int):
    case Cobblestone extends AvarusSubcommand(Items.COBBLESTONE, context => {
      val player = context.getSource.getPlayerOrThrow
      for
        i <- 0 until (player.getInventory.size - 1)
        if player.getInventory.getStack(i).getItem == Items.COBBLESTONE
      do player.getInventory.removeStack(i) // TODO: implement intended logic

      0
    })
}
