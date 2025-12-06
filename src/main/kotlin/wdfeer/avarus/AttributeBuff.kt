package wdfeer.avarus

import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents
import net.minecraft.entity.attribute.EntityAttribute
import net.minecraft.entity.attribute.EntityAttributeModifier
import net.minecraft.inventory.Inventory
import net.minecraft.item.Item
import net.minecraft.server.network.ServerPlayerEntity
import wdfeer.avarus.CommandResult.*
import java.util.*

class AttributeBuff(
    item: Item,
    itemsRequired: Int,
    val value: Double,
    val operation: EntityAttributeModifier.Operation,
    val attribute: EntityAttribute
) : UUIDEffect(item, itemsRequired) {

    override fun isApplied(player: ServerPlayerEntity): Boolean {
        val attributeInstance = player.getAttributeInstance(attribute)
        return attributeInstance?.getModifier(uuid) != null
    }

    override fun apply(player: ServerPlayerEntity) {
        val modifier = EntityAttributeModifier(
            uuid,
            name,
            value,
            operation
        )
        player.getAttributeInstance(attribute)?.addPersistentModifier(modifier)
    }
}

abstract class UUIDEffect(
    val item: Item,
    val itemsRequired: Int
) {
    init {
        ServerPlayerEvents.COPY_FROM.register { oldPlayer, newPlayer, _ ->
            if (isApplied(oldPlayer)) apply(newPlayer)
        }
    }

    val name: String = "${Avarus.MOD_ID}${item.toString().lowercase()}"
    val uuid: UUID = UUID.nameUUIDFromBytes(name.toByteArray())

    abstract fun isApplied(player: ServerPlayerEntity): Boolean
    abstract fun apply(player: ServerPlayerEntity)

    fun tryApply(player: ServerPlayerEntity): CommandResult {
        if (isApplied(player)) {
            return Failure("$item effect already applied!")
        }

        if (player.isCreative) {
            apply(player)
            return Success
        }

        val playerItemCount = player.inventory.count(item)
        return if (playerItemCount >= itemsRequired) {
            consumeItems(player.inventory)
            apply(player)
            Success
        } else {
            Failure("Not enough items! ($playerItemCount out of $itemsRequired)")
        }
    }

    private fun consumeItems(inventory: Inventory) {
        var itemsUsed = 0
        for (i in 0 until inventory.size()) {
            if (itemsUsed >= itemsRequired) break
            val stack = inventory.getStack(i)
            if (stack.item == item) {
                val itemCountToConsume = minOf(stack.count, itemsRequired - itemsUsed)
                itemsUsed += itemCountToConsume
                stack.decrement(itemCountToConsume)
            }
        }
    }
}