package wdfeer.avarus

import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents
import net.minecraft.entity.attribute.EntityAttribute
import net.minecraft.entity.attribute.EntityAttributeModifier
import net.minecraft.inventory.Inventory
import net.minecraft.item.Item
import net.minecraft.item.Items
import net.minecraft.registry.Registries
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier
import wdfeer.avarus.CommandResult.*
import java.util.*

data class AttributeBuff(
    val itemId: String,
    val itemsRequired: Int,
    val attributeId: String,
    val value: Double,
    val operationId: String
) {

    val item: Item =
        Registries.ITEM[Identifier(itemId)]

    val attribute: EntityAttribute? =
        Registries.ATTRIBUTE[Identifier(attributeId)]

    val operation: EntityAttributeModifier.Operation? =
        when (operationId) {
            "addition" -> EntityAttributeModifier.Operation.ADDITION
            "multiply_base" -> EntityAttributeModifier.Operation.MULTIPLY_BASE
            "multiply_total" -> EntityAttributeModifier.Operation.MULTIPLY_TOTAL
            else -> null
        }

    val name: String by lazy {
        "${Avarus.MOD_ID}${item.toString().lowercase()}"
    }

    val uuid: UUID by lazy {
        UUID.nameUUIDFromBytes(name.toByteArray())
    }

    /* -------------------- Lifecycle -------------------- */

    init {
        ServerPlayerEvents.COPY_FROM.register { oldPlayer, newPlayer, _ ->
            if (isApplied(oldPlayer)) apply(newPlayer)
        }
    }

    /* -------------------- Effect logic -------------------- */

    fun isValid(): Boolean {
        return item != Items.AIR && attribute != null && operation != null
    }

    fun isApplied(player: ServerPlayerEntity): Boolean {
        val attr = attribute ?: return false
        val instance = player.getAttributeInstance(attr)
        return instance?.getModifier(uuid) != null
    }

    fun apply(player: ServerPlayerEntity) {
        val attr = attribute ?: return
        val op = operation ?: return

        val modifier = EntityAttributeModifier(uuid, name, value, op)
        player.getAttributeInstance(attr)?.addPersistentModifier(modifier)
    }

    fun remove(player: ServerPlayerEntity) {
        val attr = attribute ?: return
        player.getAttributeInstance(attr)?.removeModifier(uuid)
    }

    /* -------------------- Command helper -------------------- */

    fun tryApply(player: ServerPlayerEntity): CommandResult {
        if (!isValid()) return Failure("Invalid buff configuration.")

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
            Success("${itemsRequired}x $item effect applied.")
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
                val toConsume = minOf(stack.count, itemsRequired - itemsUsed)
                itemsUsed += toConsume
                stack.decrement(toConsume)
            }
        }
    }
}