package wdfeer.avarus

import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents
import net.minecraft.entity.attribute.EntityAttribute
import net.minecraft.entity.attribute.EntityAttributeModifier
import net.minecraft.inventory.Inventory
import net.minecraft.item.Item
import net.minecraft.registry.Registries
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier
import wdfeer.avarus.CommandResult.*
import java.util.*

// Used for serialization
data class CompressedBuff(
    val item: String,
    val count: Int,
    val attribute: String,
    val attributeValue: Double,
    val operation: String
) {
    fun toAttributeBuff(): AttributeBuff? {
        val item = Registries.ITEM[Identifier(item)]
        val attribute = Registries.ATTRIBUTE[Identifier(attribute)] ?: return null
        val operation = when (operation) {
            "addition" -> EntityAttributeModifier.Operation.ADDITION
            "multiply_base" -> EntityAttributeModifier.Operation.MULTIPLY_BASE
            "multiply_total" -> EntityAttributeModifier.Operation.MULTIPLY_TOTAL
            else -> return null
        }
        return AttributeBuff(item, count, attributeValue, operation, attribute)
    }
}

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

    fun remove(player: ServerPlayerEntity) {
        player.getAttributeInstance(attribute)?.removeModifier(uuid)
    }

    fun toCompressedBuff(): CompressedBuff {
        return CompressedBuff(
            Registries.ITEM.getId(item).toString(),
            itemsRequired,
            Registries.ATTRIBUTE.getId(attribute).toString(),
            value,
            when (operation) {
                EntityAttributeModifier.Operation.ADDITION -> "addition"
                EntityAttributeModifier.Operation.MULTIPLY_BASE -> "multiply_base"
                EntityAttributeModifier.Operation.MULTIPLY_TOTAL -> "multiply_total"
            }
        )
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
            return Success("${itemsRequired}x $item effect applied.")
        }

        val playerItemCount = player.inventory.count(item)
        return if (playerItemCount >= itemsRequired) {
            consumeItems(player.inventory)
            apply(player)
            return Success("${itemsRequired}x $item effect applied.")
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