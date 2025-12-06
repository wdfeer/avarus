package wdfeer.avarus

import net.fabricmc.api.ModInitializer
import org.slf4j.LoggerFactory

object Avarus : ModInitializer {
    private val logger = LoggerFactory.getLogger("avarus")

	override fun onInitialize() {
		logger.info("Hello Fabric world!")
	}
}