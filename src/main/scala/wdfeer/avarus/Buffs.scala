package wdfeer.avarus

import net.minecraft.entity.attribute.EntityAttributeModifier.Operation
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.item.Items

val buffs: List[AttributeBuff] = List(
  AttributeBuff(Items.COBBLESTONE, 2048, 1.0, Operation.ADDITION, EntityAttributes.GENERIC_ARMOR),
  AttributeBuff(Items.COBBLED_DEEPSLATE, 2048, 1.0, Operation.ADDITION, EntityAttributes.GENERIC_ARMOR),
  AttributeBuff(Items.STONE, 2024, 2.0, Operation.ADDITION, EntityAttributes.GENERIC_ARMOR),
  AttributeBuff(Items.DEEPSLATE, 2024, 2.0, Operation.ADDITION, EntityAttributes.GENERIC_ARMOR),
  AttributeBuff(Items.BRICKS, 512, 2.0, Operation.ADDITION, EntityAttributes.GENERIC_ARMOR),
  AttributeBuff(Items.SHULKER_SHELL, 128, 2.0, Operation.ADDITION, EntityAttributes.GENERIC_ARMOR),

  AttributeBuff(Items.WHEAT, 512, 1.0, Operation.ADDITION, EntityAttributes.GENERIC_MAX_HEALTH),
  AttributeBuff(Items.POTATO, 512, 1.0, Operation.ADDITION, EntityAttributes.GENERIC_MAX_HEALTH),
  AttributeBuff(Items.CARROT, 512, 1.0, Operation.ADDITION, EntityAttributes.GENERIC_MAX_HEALTH),
  AttributeBuff(Items.BEETROOT, 512, 1.0, Operation.ADDITION, EntityAttributes.GENERIC_MAX_HEALTH),
  AttributeBuff(Items.PUMPKIN, 512, 1.0, Operation.ADDITION, EntityAttributes.GENERIC_MAX_HEALTH),
  AttributeBuff(Items.MELON_SLICE, 512, 1.0, Operation.ADDITION, EntityAttributes.GENERIC_MAX_HEALTH),
  AttributeBuff(Items.SUGAR_CANE, 512, 1.0, Operation.ADDITION, EntityAttributes.GENERIC_MAX_HEALTH),
  AttributeBuff(Items.CACTUS, 512, 1.0, Operation.ADDITION, EntityAttributes.GENERIC_MAX_HEALTH),
  AttributeBuff(Items.MUSHROOM_STEW, 32, 1.0, Operation.ADDITION, EntityAttributes.GENERIC_MAX_HEALTH),

  AttributeBuff(Items.COOKIE, 1024, 2.0, Operation.ADDITION, EntityAttributes.GENERIC_MAX_HEALTH),
  AttributeBuff(Items.BREAD, 512, 2.0, Operation.ADDITION, EntityAttributes.GENERIC_MAX_HEALTH),
  AttributeBuff(Items.CAKE, 32, 2.0, Operation.ADDITION, EntityAttributes.GENERIC_MAX_HEALTH),

  AttributeBuff(Items.NETHER_STAR, 4, 1.5, Operation.MULTIPLY_TOTAL, EntityAttributes.GENERIC_MAX_HEALTH),
)