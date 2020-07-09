package badasintended.slotlink.client.gui.widget

import badasintended.spinnery.client.render.BaseRenderer
import badasintended.spinnery.widget.WAbstractWidget
import badasintended.spinnery.widget.WSlot
import badasintended.spinnery.widget.api.Action.*
import badasintended.slotlink.common.slotAction
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.util.math.MatrixStack
import kotlin.math.ceil
import kotlin.math.floor

@Environment(EnvType.CLIENT)
class WMultiSlot(
    private val actionPerformed: (Boolean) -> Unit,
    private val sort: () -> Unit
) : WSlot() {

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
            matrices, provider, x, y, z, w, h,
            style.asColor("top_left"),
            style.asColor("background.unfocused"),
            style.asColor("bottom_right")
        )

        val stack = if (previewStack.isEmpty) stack else previewStack

        val itemRenderer = BaseRenderer.getItemRenderer()
        val textRenderer = BaseRenderer.getTextRenderer()

        val count = stack.count
        val countText = if (count <= 1) "" else withSuffix(count.toLong())

        val itemX = ((1 + x) + ((w - 18) / 2)).toInt()
        val itemY = ((1 + y) + ((h - 18) / 2)).toInt()

        itemRenderer.renderInGui(stack, itemX, itemY)
        itemRenderer.renderGuiItemOverlay(textRenderer, stack, itemX, itemY, "")

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

        if (isFocused) BaseRenderer.drawQuad(
            matrices, provider,
            (x + 1), (y + 1), (z + 201), (w - 2), (h - 2),
            style.asColor("overlay")
        )
    }

    /**
     * Drag event shouldn't happen here
     */
    override fun onMouseReleased(mouseX: Float, mouseY: Float, button: Int) {
        `interface`.container.flush()
        skipRelease = false
        isHeld = false
    }

    override fun onMouseDragged(mouseX: Float, mouseY: Float, button: Int, deltaX: Double, deltaY: Double) {}

    override fun onMouseClicked(mouseX: Float, mouseY: Float, button: Int) {
        if (!isFocused or isLocked) return

        val container = `interface`.container
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
            isHeld = true
            heldSince = System.currentTimeMillis()
            sort.invoke()
        }
    }

    override fun <W : WAbstractWidget> setHidden(isHidden: Boolean): W {
        this.isHidden = isHidden
        if (isHidden) setFocus(false)
        return this as W
    }
}
