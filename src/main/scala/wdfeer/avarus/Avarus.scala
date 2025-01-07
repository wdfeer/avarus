package wdfeer.avarus

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.LiteralArgumentBuilder.literal
import com.mojang.brigadier.context.CommandContext
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.minecraft.item.{Item, Items}
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text
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

  private val ITEMS_REQUIRED = 512

  enum AvarusSubcommand(val action: CommandContext[ServerCommandSource] => Int):
    case Cobblestone extends AvarusSubcommand(context => {
      itemCommand(context, Items.COBBLESTONE, player => {
        player.addExperience(ITEMS_REQUIRED) // TODO: replace with a permanent stat boost
      })
    })

  def itemCommand(context: CommandContext[ServerCommandSource], item: Item, onSuccess: ServerPlayerEntity => Unit): Int = {
    val player = context.getSource.getPlayerOrThrow
    val count = player.getInventory.count(Items.COBBLESTONE)
    if count >= ITEMS_REQUIRED then {
      for
        i <- 0 until (player.getInventory.size - 1)
        if player.getInventory.getStack(i).getItem == Items.COBBLESTONE
      do player.getInventory.removeStack(i)

      onSuccess(player)
      0
    }
    else {
      context.getSource.sendMessage(Text.of(s"Not enough items! ($count out of $ITEMS_REQUIRED)"))
      1
    }
  }
}
