package badasintended.slotlink.client.gui.widget

import badasintended.slotlink.util.mc
import badasintended.slotlink.util.slotAction
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.item.ItemStack
import sbinnery.client.render.BaseRenderer
import sbinnery.widget.WAbstractWidget
import sbinnery.widget.WSlot
import sbinnery.widget.api.Action
import sbinnery.widget.api.Action.PICKUP
import sbinnery.widget.api.Action.QUICK_MOVE
import kotlin.math.*
import kotlin.reflect.KMutableProperty0

@Environment(EnvType.CLIENT)
class WMultiSlot(
    private val shouldSort: KMutableProperty0<Boolean>
) : WSlot() {

    private var multiStack = ItemStack.EMPTY

    private val linkedSlots = arrayListOf<WLinkedSlot>()

    fun setLinkedSlots(vararg serverSlot: WLinkedSlot) {
        linkedSlots.clear()
        linkedSlots.addAll(serverSlot)
        linkedSlots.sortByDescending { it.stack.count }
    }

    override fun draw(matrices: MatrixStack, provider: VertexConsumerProvider.Immediate) {
        if (isHidden) return

        val x = floor(x)
        val y = floor(y)
        val w = floor(width)
        val h = floor(height)

        BaseRenderer.drawBeveledPanel(
            matrices, provider, x, y, z, w, h, style.asColor("top_left"), style.asColor("background.unfocused"),
            style.asColor("bottom_right")
        )
        provider.draw()

        val itemRenderer = BaseRenderer.getItemRenderer()
        val textRenderer = BaseRenderer.getDefaultTextRenderer()
        val itemX = ((1 + x) + ((w - 18) / 2)).toInt()
        val itemY = ((1 + y) + ((h - 18) / 2)).toInt()

        val count = stack.count
        val countText = when {
            count <= 1 -> ""
            count < 1000 -> "$count"
            else -> {
                val exp = (ln(count.toDouble()) / ln(1000.0)).toInt()
                String.format("%.1f%c", count / 1000.0.pow(exp.toDouble()), "KMGTPE"[exp - 1])
            }
        }

        itemRenderer.renderGuiItemIcon(stack, itemX, itemY)
        itemRenderer.renderGuiItemOverlay(
            textRenderer, stack, itemX, itemY, ""
        )

        val factor = mc().window.scaleFactor.toFloat()
        val scale = (1 / factor) * ceil(factor / 2)

        matrices.push()
        matrices.translate(0.0, 0.0, (z + 200.0))
        matrices.scale(scale, scale, 1f)
        textRenderer.drawWithShadow(
            matrices, countText, ((itemX + 16 - (textRenderer.getWidth(countText) * scale)) / scale),
            ((itemY + 16 - (textRenderer.fontHeight * scale)) / scale), 0xFFFFFF
        )
        matrices.pop()

        if (isFocused) BaseRenderer.drawQuad(
            matrices, provider, (x + 1), (y + 1), (z + 201), (w - 2), (h - 2), style.asColor("overlay")
        )
    }

    /**
     * Drag event shouldn't happen here
     */
    override fun onMouseReleased(mouseX: Float, mouseY: Float, button: Int) {}

    override fun onMouseDragged(mouseX: Float, mouseY: Float, button: Int, deltaX: Double, deltaY: Double) {}

    override fun onMouseClicked(mouseX: Float, mouseY: Float, button: Int) {
        if (!isFocused or isLocked) return

        val container = `interface`.handler
        val playerInventory = container.playerInventory
        val player = playerInventory.player

        val isCursorEmpty = playerInventory.cursorStack.isEmpty

        val slot = linkedSlots.firstOrNull() ?: return
        val sSlotN = slot.slotNumber
        val sSlotInvN = slot.invNumber

        shouldSort.set(!isCursorEmpty)

        if (Screen.hasShiftDown()) {
            if (button == LEFT) {
                if (Screen.hasControlDown()) linkedSlots.forEach {
                    slotAction(container, it.slotNumber, it.invNumber, button, QUICK_MOVE, player)
                } else slotAction(container, sSlotN, sSlotInvN, button, QUICK_MOVE, player)
            }
        } else {
            if (((button == LEFT) or (button == RIGHT)) and isCursorEmpty) {
                slotAction(container, sSlotN, sSlotInvN, button, PICKUP, player)
            } else if (button == MIDDLE) {
                slotAction(container, sSlotN, sSlotInvN, button, Action.CLONE, player)
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <W : WAbstractWidget> setHidden(isHidden: Boolean): W {
        this.hidden = isHidden
        if (isHidden) setFocus(false)
        return this as W
    }

    override fun <W : WSlot> setStack(stack: ItemStack): W {
        multiStack = stack
        return this as W
    }

    override fun getStack(): ItemStack = multiStack

}
