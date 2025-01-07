package wdfeer.avarus

import net.fabricmc.api.ModInitializer
import org.slf4j.LoggerFactory

object Avarus extends ModInitializer {
  private lazy val logger = LoggerFactory.getLogger("avarus")
  override def onInitialize(): Unit = {
    logger.info("AVARUS loaded!!!")
  }
}
