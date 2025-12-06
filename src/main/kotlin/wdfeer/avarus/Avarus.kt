package wdfeer.avarus

import net.fabricmc.api.ModInitializer
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object Avarus : ModInitializer {
	const val MOD_ID = "avarus"
    val logger: Logger = LoggerFactory.getLogger(MOD_ID)

	override fun onInitialize() {
		val config = Config.loadConfig()
		Commands.initialize(config)
		logger.info("Avarus initialized. Start grinding.")
	}
}