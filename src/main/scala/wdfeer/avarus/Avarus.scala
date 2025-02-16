package wdfeer.avarus

import net.fabricmc.api.ModInitializer
import org.slf4j.{Logger, LoggerFactory}
import wdfeer.avarus.config.loadBuffsConfig

object Avarus extends ModInitializer {
  val MOD_ID = "avarus"
  val logger: Logger = LoggerFactory.getLogger("avarus")

  override def onInitialize(): Unit = {
    val buffs = loadBuffsConfig()
    initializeCommands(buffs)
    logger.info("Avarus initialized. Start grinding.")
  }
}
