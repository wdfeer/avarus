package wdfeer.avarus.config

import com.google.gson.JsonObject
import net.minecraft.entity.attribute.EntityAttributeModifier.Operation
import net.minecraft.item.Item
import net.minecraft.registry.Registries
import net.minecraft.util.Identifier
import wdfeer.avarus.AttributeBuff

def serializeBuff(buff: AttributeBuff): JsonObject = {
  val obj = JsonObject()
  obj.addProperty("item", Registries.ITEM.getId(buff.item).toString)
  obj.addProperty("count", buff.itemsRequired)
  obj.addProperty("attribute", Registries.ATTRIBUTE.getId(buff.attribute).toString)
  obj.addProperty("operation", buff.operation.getId)
  obj.addProperty("value", buff.value)
  obj
}

def deserializeBuff(obj: JsonObject): Either[String, AttributeBuff] = {
  val id = Identifier(obj.get("item").getAsString)
  if !Registries.ITEM.containsId(id) then Left(s"Item with identifier $id from the config not found!")
  else {
    val item: Item = Registries.ITEM.get(id)
    val count = obj.get("count").getAsInt
    val attribute = Registries.ATTRIBUTE.get(Identifier(obj.get("attribute").getAsString))
    val operation = Operation.fromId(obj.get("operation").getAsInt)
    val value = obj.get("value").getAsDouble

    Right(AttributeBuff(item, count, value, operation, attribute))
  }
}
