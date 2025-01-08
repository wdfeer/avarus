package wdfeer.avarus

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.MinecraftClient
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.util.InputUtil
import org.lwjgl.glfw.GLFW

object AvarusClient extends ClientModInitializer:
  override def onInitializeClient(): Unit = {
    val keyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
      "wdfeer.avarus.controls.open_gui",
      InputUtil.Type.KEYSYM,
      GLFW.GLFW_KEY_SEMICOLON,
      "wdfeer.avarus.controls.category"
    ))

    ClientTickEvents.END_CLIENT_TICK.register(client => {
      while keyBinding.wasPressed do
        MinecraftClient.getInstance().setScreen(new AvarusGUI())
    })
  }
