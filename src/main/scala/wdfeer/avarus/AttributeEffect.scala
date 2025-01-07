package wdfeer.avarus

import com.mojang.brigadier.context.CommandContext
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents
import net.minecraft.entity.attribute.{EntityAttributeModifier, EntityAttributes}
import net.minecraft.item.{Item, Items}
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text

import java.util.UUID

abstract class AttributeEffect(item: Item) {
  ServerPlayerEvents.COPY_FROM.register((oldPlayer, newPlayer, _) => {
    if isApplied(oldPlayer) then apply(newPlayer)
  })

  val name: String = Avarus.MOD_ID + item.getName
  val uuid: UUID = UUID.nameUUIDFromBytes(name.getBytes)
  private val itemsRequired = 512

  def isApplied(player: ServerPlayerEntity): Boolean

  def apply(player: ServerPlayerEntity): Unit

  def command(context: CommandContext[ServerCommandSource]): Int = {
    val player = context.getSource.getPlayerOrThrow
    if isApplied(player) then {
      context.getSource.sendMessage(Text.of(s"${item.getName} effect already applied!"))
      return 1
    }

    val count = player.getInventory.count(Items.COBBLESTONE)
    if count >= itemsRequired then {
      for
        i <- 0 until (player.getInventory.size - 1)
        if player.getInventory.getStack(i).getItem == Items.COBBLESTONE
      do player.getInventory.removeStack(i)

      apply(player)
      0
    }
    else {
      context.getSource.sendMessage(Text.of(s"Not enough items! ($count out of $itemsRequired)"))
      1
    }
  }
}

object CobblestoneEffect extends AttributeEffect(Items.COBBLESTONE) {
  override def isApplied(player: ServerPlayerEntity): Boolean = {
    val attributeInstance = player.getAttributeInstance(EntityAttributes.GENERIC_ARMOR)
    if attributeInstance != null then
      attributeInstance.getModifier(uuid) != null
    else false
  }

  override def apply(player: ServerPlayerEntity): Unit = {
    val modifier = new EntityAttributeModifier(
      uuid,
      name,
      2.0,
      EntityAttributeModifier.Operation.ADDITION
    )

    val attributeInstance = player.getAttributeInstance(EntityAttributes.GENERIC_ARMOR)
    if attributeInstance != null then attributeInstance.addPersistentModifier(modifier)
  }
}