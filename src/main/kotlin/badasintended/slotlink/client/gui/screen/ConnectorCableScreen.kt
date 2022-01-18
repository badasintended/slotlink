package badasintended.slotlink.client.gui.screen

import badasintended.slotlink.client.gui.widget.ButtonWidget
import badasintended.slotlink.client.util.c2s
import badasintended.slotlink.init.Packets.PRIORITY_SETTINGS
import badasintended.slotlink.screen.ConnectorCableScreenHandler
import badasintended.slotlink.util.int
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.text.Text

@Environment(EnvType.CLIENT)
open class ConnectorCableScreen<H : ConnectorCableScreenHandler>(h: H, inventory: PlayerInventory, title: Text) :
    FilterScreen<H>(h, inventory, title) {

    private var priority = handler.priority
    private var blacklist = handler.blacklist

    override fun init() {
        super.init()

        val x = x + 7
        val y = y + titleY + 11

        add(ButtonWidget(x + 2 * 18, y + 2, 14, 14)) {
            bgU = 228
            bgV = 28
            u = { 242 }
            v = { 0 }
            onPressed = {
                priority++
                sync()
            }
        }

        add(ButtonWidget(x + 2 * 18, y + 2 + 2 * 18, 14, 14)) {
            bgU = 228
            bgV = 28
            u = { 242 }
            v = { 14 }
            onPressed = {
                priority--
                sync()
            }
        }
    }

    override fun sync() {
        super.sync()
        c2s(PRIORITY_SETTINGS) {
            int(handler.syncId)
            int(priority)
        }
    }

    override fun drawForeground(matrices: MatrixStack, mouseX: Int, mouseY: Int) {
        super.drawForeground(matrices, mouseX, mouseY)

        textRenderer.draw(matrices, "$priority", 7 + 2 * 18f, titleY + 31f, 4210752)
    }

}
