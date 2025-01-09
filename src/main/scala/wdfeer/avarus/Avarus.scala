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
import net.minecraft.text.Text
import org.slf4j.LoggerFactory

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
        val (applied, notApplied) = effects.partition(_.isApplied(player))
        Text.of(
          s"${applied.size}/${effects.size} effects applied.\n\n" +
            s"Available effects: " +
            notApplied.take(3).map(_.item.toString).mkString(", ") +
            (if notApplied.size <= 3 then "" else s", +${notApplied.size - 3}")
        )
      })
      registerGetEffectCommand()
    })
    logger.info("Avarus initialized. Start grinding.")
  }

  private def registerGetEffectCommand()(implicit dispatcher: CommandDispatcher[ServerCommandSource]): Unit = {
    var builder: LiteralArgumentBuilder[ServerCommandSource] = literal("avarus-get")

    for e <- effects do
      builder = builder.`then`(literal(e.item.toString.toLowerCase).executes(context => e.command(context)))

    dispatcher.register(builder)
  }

  private val effects: List[AttributeEffect] = List(
    AttributeEffect(Items.COBBLESTONE, 2048, 1.0, Operation.ADDITION, EntityAttributes.GENERIC_ARMOR),
    AttributeEffect(Items.COBBLED_DEEPSLATE, 2048, 1.0, Operation.ADDITION, EntityAttributes.GENERIC_ARMOR),
    AttributeEffect(Items.STONE, 2024, 2.0, Operation.ADDITION, EntityAttributes.GENERIC_ARMOR),
    AttributeEffect(Items.DEEPSLATE, 2024, 2.0, Operation.ADDITION, EntityAttributes.GENERIC_ARMOR),
    AttributeEffect(Items.BRICKS, 512, 2.0, Operation.ADDITION, EntityAttributes.GENERIC_ARMOR),
    AttributeEffect(Items.SHULKER_SHELL, 128, 2.0, Operation.ADDITION, EntityAttributes.GENERIC_ARMOR),

    AttributeEffect(Items.WHEAT, 512, 1.0, Operation.ADDITION, EntityAttributes.GENERIC_MAX_HEALTH),
    AttributeEffect(Items.POTATO, 512, 1.0, Operation.ADDITION, EntityAttributes.GENERIC_MAX_HEALTH),
    AttributeEffect(Items.CARROT, 512, 1.0, Operation.ADDITION, EntityAttributes.GENERIC_MAX_HEALTH),
    AttributeEffect(Items.BEETROOT, 512, 1.0, Operation.ADDITION, EntityAttributes.GENERIC_MAX_HEALTH),
    AttributeEffect(Items.PUMPKIN, 512, 1.0, Operation.ADDITION, EntityAttributes.GENERIC_MAX_HEALTH),
    AttributeEffect(Items.MELON_SLICE, 512, 1.0, Operation.ADDITION, EntityAttributes.GENERIC_MAX_HEALTH),
    AttributeEffect(Items.SUGAR_CANE, 512, 1.0, Operation.ADDITION, EntityAttributes.GENERIC_MAX_HEALTH),
    AttributeEffect(Items.CACTUS, 512, 1.0, Operation.ADDITION, EntityAttributes.GENERIC_MAX_HEALTH),
    AttributeEffect(Items.MUSHROOM_STEW, 32, 1.0, Operation.ADDITION, EntityAttributes.GENERIC_MAX_HEALTH),

    AttributeEffect(Items.COOKIE, 1024, 2.0, Operation.ADDITION, EntityAttributes.GENERIC_MAX_HEALTH),
    AttributeEffect(Items.BREAD, 512, 2.0, Operation.ADDITION, EntityAttributes.GENERIC_MAX_HEALTH),
    AttributeEffect(Items.CAKE, 32, 2.0, Operation.ADDITION, EntityAttributes.GENERIC_MAX_HEALTH),

    AttributeEffect(Items.NETHER_STAR, 4, 1.5, Operation.MULTIPLY_TOTAL, EntityAttributes.GENERIC_MAX_HEALTH),
  )
}
