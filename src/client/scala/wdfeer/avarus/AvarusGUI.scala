package wdfeer.avarus

import io.wispforest.owo.ui.base.BaseOwoScreen
import io.wispforest.owo.ui.container.{Containers, FlowLayout}
import io.wispforest.owo.ui.core.{OwoUIAdapter, Surface}

class AvarusScreen extends TabbedScreen

class TabbedScreen extends BaseOwoScreen[FlowLayout]:
  // TODO: create tabs: 'armor' and 'health' for the respective attribute effects

  override def createAdapter(): OwoUIAdapter[FlowLayout] = OwoUIAdapter.create(this, Containers.verticalFlow)

  override def build(root: FlowLayout): Unit = {
    root.surface(Surface.VANILLA_TRANSLUCENT)
  }
