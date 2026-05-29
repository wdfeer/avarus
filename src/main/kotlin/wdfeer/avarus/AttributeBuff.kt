package wdfeer.avarus

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents
import net.minecraft.entity.attribute.EntityAttribute
import net.minecraft.entity.attribute.EntityAttributeModifier
import net.minecraft.inventory.Inventory
import net.minecraft.item.Item
import net.minecraft.registry.Registries
import net.minecraft.registry.entry.RegistryEntry
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier
import wdfeer.avarus.CommandResult.*
import java.util.*

@Serializable
data class AttributeBuff(
    val itemId: String,
    val itemsRequired: Int,
    val attributeId: String,
    val value: Double,
    val operationId: String,
    /** Unique buff name shown to the player.*/
    var name: String = "${Identifier.of(itemId).path}$itemsRequired",
) {
    @Transient
    val item: Item = Registries.ITEM.getOrEmpty(Identifier.of(itemId)).orElseThrow {
        NoSuchElementException("No item with id \"$itemId\" found!")
    }

    @Transient
    val attribute: RegistryEntry<EntityAttribute> =
        Registries.ATTRIBUTE.getEntry(Identifier.of(attributeId)).orElseThrow {
            NoSuchElementException("No attribute \"$attributeId\" found!")
        }

    @Transient
    val operation: EntityAttributeModifier.Operation = when (operationId) {
        "addition" -> EntityAttributeModifier.Operation.ADD_VALUE
        "multiply_base" -> EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE
        "multiply_total" -> EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
        else -> throw IllegalArgumentException("Invalid operationId: \"$operationId\"")
    }

    @Transient
    val internalId: Identifier = Identifier.of(Avarus.MOD_ID, name)

    /* -------------------- Lifecycle -------------------- */

    init {
        ServerPlayerEvents.COPY_FROM.register { oldPlayer, newPlayer, _ ->
            if (isApplied(oldPlayer)) apply(newPlayer)
        }
    }

    /* -------------------- Effect logic -------------------- */

    fun isApplied(player: ServerPlayerEntity): Boolean {
        val attr = attribute
        val instance = player.getAttributeInstance(attr)
        return instance?.getModifier(internalId) != null
    }

    fun apply(player: ServerPlayerEntity) {
        val modifier = EntityAttributeModifier(internalId, value, operation)
        player.getAttributeInstance(attribute)?.addPersistentModifier(modifier)
    }

    fun remove(player: ServerPlayerEntity) {
        player.getAttributeInstance(attribute)?.removeModifier(internalId)
    }

    /* -------------------- Command helper -------------------- */

    fun tryApply(player: ServerPlayerEntity): CommandResult {
        if (isApplied(player)) {
            return Failure("$name effect already applied!")
        }

        if (player.isCreative) {
            apply(player)
            return Success("$name effect applied.")
        }

        val playerItemCount = player.inventory.count(item)
        return if (playerItemCount >= itemsRequired) {
            consumeItems(player.inventory)
            apply(player)
            Success("$name effect applied.")
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