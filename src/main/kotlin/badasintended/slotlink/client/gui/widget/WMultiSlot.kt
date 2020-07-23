package badasintended.slotlink.client.gui.widget

import badasintended.slotlink.common.slotAction
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.MinecraftClient
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.render.item.ItemRenderer
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.item.ItemStack
import spinnery.widget.WAbstractWidget
import spinnery.widget.api.Action.*
import kotlin.math.ceil

@Environment(EnvType.CLIENT)
class WMultiSlot(
    private val actionPerformed: (Boolean) -> Unit,
    private val sort: () -> Unit
) : WVanillaSlot() {

    private val linkedSlots = arrayListOf<WLinkedSlot>()

    fun setLinkedSlots(vararg serverSlot: WLinkedSlot) {
        linkedSlots.clear()
        linkedSlots.addAll(serverSlot)
        linkedSlots.sortByDescending { it.stack.count }
    }

    override fun drawItem(
        matrices: MatrixStack,
        stack: ItemStack, itemRenderer: ItemRenderer, textRenderer: TextRenderer,
        x: Float, y: Float, w: Float, h: Float
    ) {
        val count = stack.count
        val countText = countText(count)

        val itemX = ((1 + x) + ((w - 18) / 2)).toInt()
        val itemY = ((1 + y) + ((h - 18) / 2)).toInt()

        itemRenderer.renderGuiItemIcon(stack, itemX, itemY)
        itemRenderer.renderGuiItemOverlay(
            textRenderer, stack, itemX, itemY, ""
        )

        val factor = MinecraftClient.getInstance().window.scaleFactor.toFloat()
        val scale = (1 / factor) * ceil(factor / 2)

        matrices.push()
        matrices.translate(0.0, 0.0, (z + 200.0))
        matrices.scale(scale, scale, 1f)
        textRenderer.drawWithShadow(
            matrices, countText,
            ((x + 17 - (textRenderer.getWidth(countText) * scale)) / scale),
            ((y + 17 - (textRenderer.fontHeight * scale)) / scale),
            0xFFFFFF
        )
        matrices.pop()
    }

    /**
     * Drag event shouldn't happen here
     */
    override fun onMouseReleased(mouseX: Float, mouseY: Float, button: Int) {
        `interface`.handler.flush()
        skipRelease = false
        held = false
    }

    override fun onMouseDragged(mouseX: Float, mouseY: Float, button: Int, deltaX: Double, deltaY: Double) {}

    override fun onMouseClicked(mouseX: Float, mouseY: Float, button: Int) {
        if (!isFocused or isLocked) return

        val container = `interface`.handler
        val playerInventory = container.playerInventory
        val player = playerInventory.player

        val isCursorEmpty = playerInventory.cursorStack.isEmpty

        val slot = linkedSlots.first()
        val sSlotN = slot.slotNumber
        val sSlotInvN = slot.invNumber

        if (Screen.hasShiftDown()) {
            if (button == LEFT) {
                if (Screen.hasControlDown()) linkedSlots.forEach {
                    slotAction(container, it.slotNumber, it.invNumber, button, QUICK_MOVE, player)
                } else slotAction(container, sSlotN, sSlotInvN, button, QUICK_MOVE, player)
                actionPerformed.invoke(true)
                sort.invoke()
            }
        } else {
            if ((button == LEFT) or (button == RIGHT) and isCursorEmpty) {
                skipRelease = true
                slotAction(container, sSlotN, sSlotInvN, button, PICKUP, player)
                actionPerformed.invoke(true)
                sort.invoke()
            } else if (button == MIDDLE) {
                slotAction(container, sSlotN, sSlotInvN, button, CLONE, player)
                actionPerformed.invoke(true)
                sort.invoke()
            }
        }


        if (isWithinBounds(mouseX, mouseY)) {
            held = true
            heldSince = System.currentTimeMillis()
            sort.invoke()
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <W : WAbstractWidget> setHidden(isHidden: Boolean): W {
        this.hidden = isHidden
        if (isHidden) setFocus(false)
        return this as W
    }
}
