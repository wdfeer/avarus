package wdfeer.avarus

import com.mojang.brigadier.context.CommandContext
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents
import net.minecraft.entity.attribute.{EntityAttribute, EntityAttributeModifier}
import net.minecraft.inventory.Inventory
import net.minecraft.item.Item
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text

import java.util.UUID

class AttributeBuff(item: Item,
                    itemsRequired: Int,
                    val value: Double,
                    val operation: EntityAttributeModifier.Operation,
                    val attribute: EntityAttribute) extends UUIDEffect(item, itemsRequired) {
  override def isApplied(player: ServerPlayerEntity): Boolean = {
    val attributeInstance = player.getAttributeInstance(attribute)
    if attributeInstance != null then
      attributeInstance.getModifier(uuid) != null
    else false
  }

  override def apply(player: ServerPlayerEntity): Unit = {
    val modifier = new EntityAttributeModifier(
      uuid,
      name,
      value,
      operation
    )

    val attributeInstance = player.getAttributeInstance(attribute)
    if attributeInstance != null then attributeInstance.addPersistentModifier(modifier)
  }
}

private abstract class UUIDEffect(val item: Item, val itemsRequired: Int) {
  ServerPlayerEvents.COPY_FROM.register((oldPlayer, newPlayer, _) => {
    if isApplied(oldPlayer) then apply(newPlayer)
  })

  val name: String = Avarus.MOD_ID + item.getName
  val uuid: UUID = UUID.nameUUIDFromBytes(name.getBytes)

  def isApplied(player: ServerPlayerEntity): Boolean

  def apply(player: ServerPlayerEntity): Unit

  def command(context: CommandContext[ServerCommandSource]): Int = {
    val player = context.getSource.getPlayerOrThrow
    if isApplied(player) then {
      context.getSource.sendMessage(Text.of(s"${item.toString} effect already applied!"))
      return 1
    }

    val playerItemCount = player.getInventory.count(item)
    if playerItemCount >= itemsRequired then {
      consumeItems(player.getInventory)
      apply(player)
      0
    }
    else {
      context.getSource.sendMessage(Text.of(s"Not enough items! ($playerItemCount out of $itemsRequired)"))
      1
    }
  }

  private def consumeItems(inventory: Inventory): Unit = {
    var itemsUsed = 0
    for (i <- 0 until inventory.size() if itemsUsed < itemsRequired) {
      val stack = inventory.getStack(i)
      if (stack.getItem == item) {
        val itemCountToConsume = math.min(stack.getCount, itemsRequired - itemsUsed)
        itemsUsed += itemCountToConsume
        stack.decrement(itemCountToConsume)
      }
    }
  }
}
