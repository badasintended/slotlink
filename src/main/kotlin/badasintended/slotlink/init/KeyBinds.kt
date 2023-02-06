package badasintended.slotlink.init

import badasintended.slotlink.client.keybind.RemoteKeyBinding
import badasintended.slotlink.util.Value
import badasintended.slotlink.util.wrap
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.option.KeyBinding

@Suppress("MemberVisibilityCanBePrivate")
object KeyBinds : Initializer {

    lateinit var OPEN_REMOTE: Value<KeyBinding>

    @Environment(EnvType.CLIENT)
    override fun client() {
        OPEN_REMOTE = KeyBindingHelper.registerKeyBinding(RemoteKeyBinding).wrap()
    }

}