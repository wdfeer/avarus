package wdfeer.avarus

import com.mojang.brigadier.context.CommandContext
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents
import net.minecraft.entity.attribute.{EntityAttribute, EntityAttributeModifier}
import net.minecraft.inventory.Inventory
import net.minecraft.item.Item
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text
import wdfeer.avarus.CommandResult.{Failure, Success}

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

  val name: String = Avarus.MOD_ID + item.toString.toLowerCase
  val uuid: UUID = UUID.nameUUIDFromBytes(name.getBytes)

  def isApplied(player: ServerPlayerEntity): Boolean

  protected def apply(player: ServerPlayerEntity): Unit

  def applyCommand(player: ServerPlayerEntity): CommandResult = {
    if isApplied(player) then {
      return Failure(s"${item.toString} effect already applied!")
    }

    val playerItemCount = player.getInventory.count(item)
    if playerItemCount >= itemsRequired then {
      consumeItems(player.getInventory)
      apply(player)
      Success
    }
    else {
      Failure(s"Not enough items! ($playerItemCount out of $itemsRequired)")
    }
  }

  private def consumeItems(inventory: Inventory): Unit = {
    var itemsUsed = 0
    for (i <- 0.until(inventory.size()) if itemsUsed < itemsRequired) {
      val stack = inventory.getStack(i)
      if (stack.getItem == item) {
        val itemCountToConsume = math.min(stack.getCount, itemsRequired - itemsUsed)
        itemsUsed += itemCountToConsume
        stack.decrement(itemCountToConsume)
      }
    }
  }
}
