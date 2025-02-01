package wdfeer.avarus.config

import com.google.gson.{Gson, JsonArray, JsonObject}
import net.fabricmc.loader.api.FabricLoader
import wdfeer.avarus.{defaultBuffs, AttributeBuff}
import java.io.{File, FileReader, FileWriter}

private val filename = "avarus.json"

def loadBuffsConfig(): Array[AttributeBuff] = {
  val path = FabricLoader.getInstance().getConfigDir.resolve(filename).toAbsolutePath.toString
  if File(path).exists() then {
    val reader = FileReader(path)

    val gson = Gson()
    val jsonArray: JsonArray = gson.fromJson(reader, classOf[JsonArray])
    reader.close()

    Array.tabulate(jsonArray.size())(i => deserializeBuff(jsonArray.get(i).getAsJsonObject))
  } else {
    saveBuffsConfig(defaultBuffs)
    defaultBuffs
  }
}

def saveBuffsConfig(buffs: Array[AttributeBuff]): Unit = {
  val objects = buffs.map(serializeBuff)
  val json = JsonArray()
  for (elem <- objects) {
    json.add(elem)
  }

  val path = FabricLoader.getInstance().getConfigDir.resolve(filename)
  val writer = FileWriter(path.toString)

  val gson = Gson()
  gson.toJson(json, writer)

  writer.close()
}