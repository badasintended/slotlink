package badasintended.slotlink.client.keybind

import badasintended.slotlink.Slotlink
import badasintended.slotlink.client.util.c2s
import badasintended.slotlink.compat.trinkets.TrinketsAccess
import badasintended.slotlink.init.Packets
import badasintended.slotlink.item.RemoteItem
import badasintended.slotlink.util.actionBar
import badasintended.slotlink.util.int
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.MinecraftClient
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.util.InputUtil
import net.minecraft.item.ItemStack
import org.lwjgl.glfw.GLFW

@Environment(EnvType.CLIENT)
object RemoteKeyBinding : KeyBinding("key.slotlink.open_remote", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_I, Slotlink.ID) {

    override fun setPressed(pressed: Boolean) {
        super.setPressed(pressed)

        if (!pressed) return
        val player = MinecraftClient.getInstance().player ?: return
        if (TrinketsAccess.tryOpenRemote(player)) return

        player as RemoteItem.Holder

        var highestStack = ItemStack.EMPTY
        var highestSlot = -1

        val iterator = player.possibleRemoteSlots.intIterator()
        while (iterator.hasNext()) {
            val slot = iterator.nextInt()
            val stack = player.inventory.getStack(slot)
            val item = stack.item
            if (item is RemoteItem) {
                val level = item.level
                val highestItem = highestStack.item
                val highestLevel = if (highestItem is RemoteItem) highestItem.level else Int.MAX_VALUE
                if (level < highestLevel) {
                    highestStack = stack
                    highestSlot = slot
                }
            } else {
                iterator.remove()
            }
        }

        val highestItem = highestStack.item
        if (highestItem is RemoteItem) {
            c2s(Packets.OPEN_REMOTE) {
                int(highestSlot)
            }
        } else {
            player.actionBar("key.slotlink.open_remote.missing")
        }
    }

}