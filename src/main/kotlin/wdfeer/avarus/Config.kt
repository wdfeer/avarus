package wdfeer.avarus

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import net.fabricmc.loader.api.FabricLoader
import java.io.FileReader
import java.io.FileWriter
import java.nio.file.Path

data class Config(val buffs: List<AttributeBuff>) {
    companion object {
        val default: Config = Config(defaultBuffs)

        val path: Path = FabricLoader.getInstance().configDir.resolve("avarus.json")

        fun loadConfig(): Config {
            if (path.toFile().exists()) {
                runCatching {
                    val gson = Gson()
                    FileReader(path.toFile()).use { reader ->
                        val type = object : TypeToken<List<CompressedBuff>>() {}.type
                        val buffs =
                            gson.fromJson<List<CompressedBuff>>(reader, type).associateWith { it.toAttributeBuff() }
                        val (invalid, valid) = buffs.entries.partition { it.value == null }
                        invalid.forEach {
                            Avarus.logger.error("Failed reading config entry for ${it.key.item}!")
                        }
                        Avarus.logger.info("Loaded ${valid.count()} config entries.")
                        return Config(valid.map { it.value!! })
                    }
                }
                Avarus.logger.error("Failed to read config! Using default config.")
                return default
            } else {
                Avarus.logger.info("Config file not found, writing default config.")
                saveConfig(default)
                return default
            }
        }

        fun saveConfig(config: Config) {
            val gson = GsonBuilder().setPrettyPrinting().create()
            FileWriter(path.toFile()).use {
                it.write(gson.toJson(config))
            }
        }
    }
}