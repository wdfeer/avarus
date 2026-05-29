package wdfeer.avarus

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import net.fabricmc.loader.api.FabricLoader
import java.io.*

@Serializable
data class Config(val buffs: List<AttributeBuff>) {
    companion object {
        val file: File = FabricLoader.getInstance().configDir.resolve("avarus.json").toFile()

        const val DEFAULT_CONFIG = "data/avarus/default_buffs.json"

        fun loadDefaultConfig(): Config = loadConfig(
            (getFileInJar(DEFAULT_CONFIG) ?: throw FileNotFoundException("Could not find the default config!")).reader()
        )

        fun loadUserConfig(): Config? = if (file.exists()) loadConfig(FileReader(file)) else null

        private fun loadConfig(reader: Reader): Config {
            reader.use { reader ->
                val loaded = Json.decodeFromString<Config>(reader.readText())
                Avarus.logger.info("Loaded ${loaded.buffs.count()} config entries.")
                return loaded
            }
        }

        private val prettyJson = Json {
            prettyPrint = true
        }

        fun saveConfig(config: Config) {
            FileWriter(file).use { writer ->
                writer.write(prettyJson.encodeToString(config))
            }
        }
    }
}