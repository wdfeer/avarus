package wdfeer.avarus

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.util.InputUtil
import net.minecraft.text.Text
import org.lwjgl.glfw.GLFW

object AvarusClient extends ClientModInitializer:
  override def onInitializeClient(): Unit = {
    val keyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
      "wdfeer.avarus.avarus_open_gui_keybind",
      InputUtil.Type.KEYSYM,
      GLFW.GLFW_KEY_SEMICOLON,
      "wdfeer.avarus.avarus_keybinds"
    ))

    ClientTickEvents.END_CLIENT_TICK.register(client => {
      // TODO: show gui screen
      while (keyBinding.wasPressed) client.player.sendMessage(Text.literal("Key 1 was pressed!"), false)
    })
  }
