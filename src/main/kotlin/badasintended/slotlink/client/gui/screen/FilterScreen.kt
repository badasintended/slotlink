package badasintended.slotlink.client.gui.screen

import badasintended.slotlink.client.gui.widget.ButtonWidget
import badasintended.slotlink.client.gui.widget.FilterSlotWidget
import badasintended.slotlink.client.util.GuiTextures
import badasintended.slotlink.client.util.bind
import badasintended.slotlink.client.util.c2s
import badasintended.slotlink.init.Packets.FILTER_SETTINGS
import badasintended.slotlink.screen.FilterScreenHandler
import badasintended.slotlink.util.bool
import badasintended.slotlink.util.int
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.text.Text

@Environment(EnvType.CLIENT)
open class FilterScreen<H : FilterScreenHandler>(h: H, inventory: PlayerInventory, title: Text) :
    ModScreen<H>(h, inventory, title) {

    val filterSlots = mutableListOf<FilterSlotWidget>()

    private var blacklist = handler.blacklist

    override val baseTlKey: String
        get() = "container.slotlink.filter"

    override fun init() {
        super.init()

        titleX = (backgroundWidth - textRenderer.getWidth(title)) / 2
        val x = x + 7
        val y = y + titleY + 11

        val filterSlotX = x + (3 * 18)

        filterSlots.clear()
        for (i in 0 until 9) {
            filterSlots += add(FilterSlotWidget(handler, i, filterSlotX + (i % 3) * 18, y + (i / 3) * 18))
        }

        add(ButtonWidget(x + 6 * 18 + 4, y + 20, 14, 14)) {
            bgU = 228
            bgV = 28
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

    override fun drawBackground(matrices: MatrixStack, delta: Float, mouseX: Int, mouseY: Int) {
        super.drawBackground(matrices, delta, mouseX, mouseY)

        GuiTextures.FILTER.bind()
        drawTexture(matrices, x, y, 0, 0, 176, 166)
    }

    protected open fun sync() {
        c2s(FILTER_SETTINGS) {
            int(handler.syncId)
            bool(blacklist)
        }
    }

}