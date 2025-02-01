package wdfeer.avarus

import net.fabricmc.api.ModInitializer
import org.slf4j.LoggerFactory

object Avarus extends ModInitializer {
  val MOD_ID = "avarus"
  private lazy val logger = LoggerFactory.getLogger("avarus")

  override def onInitialize(): Unit = {
    initializeCommands()
    logger.info("Avarus initialized. Start grinding.")
  }
}
