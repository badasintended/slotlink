package badasintended.slotlink.client.gui.widget

import badasintended.slotlink.common.slotAction
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.Screen
import spinnery.client.render.BaseRenderer
import spinnery.client.render.TextRenderer
import spinnery.widget.WSlot
import spinnery.widget.api.Action
import spinnery.widget.api.Action.CLONE
import spinnery.widget.api.Action.PICKUP
import kotlin.math.ceil
import kotlin.math.ln
import kotlin.math.pow

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

    override fun draw() {
        if (isHidden) return

        val x = x.toDouble()
        val y = y.toDouble()
        val z = z.toDouble()
        val w = width.toDouble()
        val h = height.toDouble()

        BaseRenderer.drawBeveledPanel(
            x, y, z, w, h,
            style.asColor("top_left"),
            style.asColor("background.unfocused"),
            style.asColor("bottom_right")
        )

        val itemRenderer = BaseRenderer.getItemRenderer()
        val textRenderer = BaseRenderer.getTextRenderer()

        val count = stack.count
        val countText = when {
            count <= 1 -> ""
            count < 1000 -> "$count"
            else -> {
                val exp = (ln(count.toDouble()) / ln(1000.0)).toInt()
                String.format("%.1f%c", count / 1000.0.pow(exp.toDouble()), "KMGTPE"[exp - 1])
            }
        }

        val itemX = ((1 + x) + ((w - 18) / 2)).toInt()
        val itemY = ((1 + y) + ((h - 18) / 2)).toInt()

        itemRenderer.renderGuiItemIcon(stack, itemX, itemY)
        itemRenderer.renderGuiItemOverlay(textRenderer, stack, itemX, itemY, "")

        val factor = MinecraftClient.getInstance().window.scaleFactor
        val scale = (1 / factor) * ceil(factor / 2)

        TextRenderer.pass()
            .text(countText).color(0xFFFFFF).shadow(true)
            .scale(scale)
            .at(
                (x + 17 - (TextRenderer.width(countText) * scale)),
                (y + 17 - (TextRenderer.height() * scale)),
                (z + 200)
            )
            .render()

        if (isFocused) BaseRenderer.drawRectangle(
            (x + 1), (y + 1), z, (w - 2), (h - 2),
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
                linkedSlots.forEach {
                    slotAction(container, it.slotNumber, it.invNumber, button, Action.QUICK_MOVE, player)
                }
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


}
