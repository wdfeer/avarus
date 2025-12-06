package wdfeer.avarus

import net.fabricmc.api.ModInitializer
import org.slf4j.LoggerFactory

object Avarus : ModInitializer {
	const val MOD_ID = "avarus"
    private val logger = LoggerFactory.getLogger(MOD_ID)

	override fun onInitialize() {
		val buffs = Config.loadConfig()
		Commands.initialize(buffs)
		logger.info("Avarus initialized. Start grinding.")
	}
}