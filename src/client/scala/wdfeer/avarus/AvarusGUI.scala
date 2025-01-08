package wdfeer.avarus

import net.fabricmc.api.{EnvType, Environment}
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.tooltip.Tooltip
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.text.Text

class AvarusGUI extends Screen(Text.translatable("wdfeer.avarus.gui.title")):
  override def init(): Unit = {
    val button = ButtonWidget.builder(Text.literal("Button 1"), button => {
      System.out.println("You clicked button1!")
    }).dimensions(width / 2 - 205, 20, 200, 20).tooltip(Tooltip.of(Text.literal("Tooltip of button1"))).build

    addDrawableChild(button)
  }
