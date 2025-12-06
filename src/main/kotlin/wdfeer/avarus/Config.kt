package wdfeer.avarus

import net.fabricmc.loader.api.FabricLoader

object Config {
    val path = FabricLoader.getInstance().configDir.resolve("avarus.cfg")

    fun loadConfig(): List<AttributeBuff> {
        TODO("Implement loading config from file!")
    }

    fun saveConfig(buffs: List<AttributeBuff>) {
        TODO("Implement saving config to file!")
    }
}