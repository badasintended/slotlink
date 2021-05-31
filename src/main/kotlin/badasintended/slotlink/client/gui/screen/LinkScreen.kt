package badasintended.slotlink.client.gui.screen

import badasintended.slotlink.client.gui.widget.ButtonWidget
import badasintended.slotlink.client.gui.widget.FilterSlotWidget
import badasintended.slotlink.client.util.c2s
import badasintended.slotlink.init.Packets.LINK_SETTINGS
import badasintended.slotlink.screen.LinkScreenHandler
import badasintended.slotlink.util.bool
import badasintended.slotlink.util.int
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.text.Text

@Environment(EnvType.CLIENT)
open class LinkScreen<H : LinkScreenHandler>(h: H, inventory: PlayerInventory, title: Text) :
    ModScreen<H>(h, inventory, title) {

    private var priority = handler.priority
    private var blacklist = handler.blacklist

    override val baseTlKey: String
        get() = "container.slotlink.cable"

    override fun init() {
        super.init()

        titleX = (backgroundWidth - textRenderer.getWidth(title)) / 2

        val x = x + 7
        val y = y + titleY + 11

        for (i in 0 until 9) {
            add(FilterSlotWidget(handler, i, x + 3 * 18 + (i % 3) * 18, y + (i / 3) * 18))
        }

        add(ButtonWidget(x + 2 * 18, y + 2, 14, 14)) {
            u = { 242 }
            v = { 0 }
            onPressed = {
                priority++
                sync()
            }
        }

        add(ButtonWidget(x + 2 * 18, y + 2 + 2 * 18, 14, 14)) {
            u = { 242 }
            v = { 14 }
            onPressed = {
                priority--
                sync()
            }
        }

        add(ButtonWidget(x + 6 * 18 + 4, y + 20, 14, 14)) {
            u = { 228 }
            v = { if (blacklist) 14 else 0 }
            onPressed = {
                blacklist = !blacklist
                sync()
            }
            onHovered = { matrices, x, y ->
                renderTooltip(matrices, tl("blacklist.$blacklist"), x, y)
            }
        }

    }

    protected open fun sync() {
        c2s(LINK_SETTINGS) {
            int(handler.syncId)
            int(priority)
            bool(blacklist)
        }
    }

    override fun drawForeground(matrices: MatrixStack, mouseX: Int, mouseY: Int) {
        super.drawForeground(matrices, mouseX, mouseY)

        textRenderer.draw(matrices, "$priority", 7 + 2 * 18f, titleY + 31f, 4210752)
    }

}
