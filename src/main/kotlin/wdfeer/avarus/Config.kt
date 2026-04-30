package wdfeer.avarus

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import net.fabricmc.loader.api.FabricLoader
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.io.Reader

data class Config(val buffs: List<AttributeBuff>) {
    companion object {
        val file: File = FabricLoader.getInstance().configDir.resolve("avarus.json").toFile()

        fun loadDefaultConfig(): Config? = loadConfig(getFileInJar("data/avarus/default_buffs.json")!!.reader())
        fun loadUserConfig(): Config? {
            return if (file.exists()) loadConfig(FileReader(file)) else null
        }

        private fun loadConfig(reader: Reader): Config? {
            runCatching {
                val gson = Gson()
                reader.use { reader ->
                    val type = Config::class.java
                    val loaded = gson.fromJson(reader, type)
                    Avarus.logger.info("Loaded ${loaded.buffs.count()} config entries.")
                    return loaded
                }
            }
            return null
        }

        fun saveConfig(config: Config) {
            val gson = GsonBuilder().setPrettyPrinting().create()
            val type = object : TypeToken<List<AttributeBuff>>() {}.type
            FileWriter(file).use { writer ->
                writer.write(gson.toJson(config.buffs, type))
            }
        }
    }
}