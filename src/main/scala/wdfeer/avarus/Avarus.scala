package wdfeer.avarus

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.LiteralArgumentBuilder.literal
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.minecraft.entity.attribute.EntityAttributeModifier.Operation
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.item.Items
import net.minecraft.server.command.ServerCommandSource
import org.slf4j.LoggerFactory

object Avarus extends ModInitializer {
  val MOD_ID = "avarus"
  private lazy val logger = LoggerFactory.getLogger("avarus")

  override def onInitialize(): Unit = {
    CommandRegistrationCallback.EVENT.register((dispatcher, _, _) => registerCommand(dispatcher))
    logger.info("Avarus initialized. Start grinding.")
  }

  private def registerCommand(dispatcher: CommandDispatcher[ServerCommandSource]): Unit = {
    var builder: LiteralArgumentBuilder[ServerCommandSource] = literal("avarus")

    for e <- effects do
      builder = builder.`then`(literal(e.item.toString.toLowerCase).executes(context => e.command(context)))

    dispatcher.register(builder)
  }

  private val effects: List[AttributeEffect] = List(
    AttributeEffect(Items.COBBLESTONE, EntityAttributes.GENERIC_ARMOR, 2.0, Operation.ADDITION)
  )
}
