package badasintended.slotlink.client.gui.screen

import badasintended.slotlink.client.gui.widget.ButtonWidget
import badasintended.slotlink.client.gui.widget.CraftingResultSlotWidget
import badasintended.slotlink.client.gui.widget.MultiSlotWidget
import badasintended.slotlink.client.gui.widget.ScrollBarWidget
import badasintended.slotlink.client.gui.widget.TextFieldWidget
import badasintended.slotlink.screen.RequestScreenHandler
import badasintended.slotlink.util.buf
import badasintended.slotlink.util.c2s
import badasintended.slotlink.util.drawNinePatch
import badasintended.slotlink.util.hasMod
import me.shedaniel.rei.api.REIHelper
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.MinecraftClient
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.text.Text
import org.lwjgl.glfw.GLFW
import badasintended.slotlink.init.Networks as N

@Environment(EnvType.CLIENT)
class RequestScreen<H : RequestScreenHandler>(handler: H, inv: PlayerInventory, title: Text) : ModScreen<H>(handler, inv, title) {

    private val syncId get() = handler.syncId
    private val viewedHeight get() = handler.viewedHeight
    private val maxScroll get() = handler.maxScroll

    private val hasRei = hasMod("roughlyenoughitems")

    private var sort = handler.lastSort
    private var filter = ""

    private lateinit var searchBar: TextFieldWidget

    private lateinit var scrollBar: ScrollBarWidget
    private var lastScroll = 0

    override val baseTlKey: String
        get() = "container.slotlink.request"

    override fun init() {
        super.init()

        playerInventoryTitleX = Int.MIN_VALUE
        playerInventoryTitleY = Int.MIN_VALUE

        val x = x + 7
        val y = y + titleY + 11

        for (i in 0 until viewedHeight * 8) {
            addButton(MultiSlotWidget(handler, i, x + (i % 8) * 18, y + (i / 8) * 18))
        }

        addButton(CraftingResultSlotWidget(handler, x + 112, y + viewedHeight * 18 + 36))

        scrollBar = addButton(ScrollBarWidget(x + 4 + 8 * 18, y, viewedHeight * 18)).apply {
            hasKnob = { maxScroll > 0 }
            onUpdated = {
                val scroll = (it * maxScroll + 0.5).toInt()
                if (scroll != lastScroll) c2s(N.SCROLL, buf().writeVarInt(syncId).writeVarInt(scroll))
                lastScroll = scroll
            }
        }

        addButton(ButtonWidget(x + 4 + 8 * 18, y + 4 + viewedHeight * 18, 14, 14, tl("sort"))).apply {
            u = { 200 }
            v = { sort.ordinal * 14 }
            onPressed = {
                sort = sort.next()
                scrollBar.knob = 0f
                c2s(N.SORT, buf().writeVarInt(syncId).writeVarInt(sort.ordinal).writeString(filter))
            }
            onHovered = { matrices, x, y ->
                renderTooltip(matrices, tl("sort.$sort"), x, y)
            }
        }

        addButton(ButtonWidget(x + 13, y + 22 + viewedHeight * 18, 8, 8, tl("craft.clear"))).apply {
            background = false
            u = { 16 }
            v = { 46 }
            onPressed = {
                c2s(N.CLEAR_CRAFTING_GRID, buf().writeVarInt(syncId))
            }
            onHovered = { matrices, x, y ->
                renderTooltip(matrices, tl("craft.clear"), x, y)
            }
        }

        addButton(ButtonWidget(x + 9 * 18 - 8, y + viewedHeight * 18 + 71, 8, 8, tl("moveAll"))).apply {
            background = false
            u = { 0 }
            v = { 46 }
            onPressed = {
                c2s(N.MOVE, buf().writeVarInt(syncId))
            }
            onHovered = { matrices, x, y ->
                if (playerInventory.cursorStack.isEmpty) {
                    renderTooltip(matrices, tl("move.all"), x, y)
                } else {
                    renderTooltip(matrices, tl("move.type"), x, y)
                }
            }
        }

        addButton(ButtonWidget(x + 9 * 18 - 16, y + viewedHeight * 18 + 71, 8, 8, tl("restock"))).apply {
            background = false
            u = { 8 }
            v = { 46 }
            onPressed = {
                c2s(N.RESTOCK, buf().writeVarInt(syncId))
            }
            onHovered = { matrices, x, y ->
                if (playerInventory.cursorStack.isEmpty) {
                    renderTooltip(matrices, tl("restock.all"), x, y)
                } else {
                    renderTooltip(matrices, tl("restock.cursor"), x, y)
                }
            }
        }

        searchBar = addButton(TextFieldWidget(x, y + 4 + viewedHeight * 18, 144, 14, tl("search"))).apply {
            setMaxLength(50)
            placeholder = tl("search")
            text = filter
            tooltip.add(tl("search.tip1"))
            tooltip.add(tl("search.tip2"))
            tooltip.add(tl("search.tip3"))
            setChangedListener {
                if (it != filter) {
                    c2s(N.SORT, buf().writeVarInt(syncId).writeVarInt(sort.ordinal).writeString(it))
                    scrollBar.knob = 0f
                    filter = it
                    if (hasRei) REIHelper.getInstance().searchTextField?.text = filter
                }
            }
        }

        c2s(N.SORT, buf().writeVarInt(syncId).writeVarInt(sort.ordinal).writeString(filter))
    }

    /**
     * apparently this also called on resize
     */
    override fun init(client: MinecraftClient, width: Int, height: Int) {
        var viewedHeight = 3
        for (i in 3..6) if (height > (198 + (i * 18))) viewedHeight = i

        backgroundWidth = 9 * 18 + 14
        backgroundHeight = viewedHeight * 18 + 180

        handler.resize(viewedHeight)
        c2s(N.RESIZE, buf().writeVarInt(handler.syncId).writeVarInt(viewedHeight))

        super.init(client, width, height)
    }

    override fun drawBackground(matrices: MatrixStack, delta: Float, mouseX: Int, mouseY: Int) {
        super.drawBackground(matrices, delta, mouseX, mouseY)

        val result = handler.slots[0]
        drawNinePatch(matrices, x + result.x - 5, y + result.y - 5, 26, 26, 16f, 0f, 1, 14)

        drawTexture(matrices, x + 90, y + 58 + viewedHeight * 18, 0, 31, 22, 15)
    }

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        if (searchBar.isFocused) {
            if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
                searchBar.changeFocus(false)
            } else {
                searchBar.keyPressed(keyCode, scanCode, modifiers)
            }
            return true
        }
        return super.keyPressed(keyCode, scanCode, modifiers)
    }

    override fun mouseReleased(mouseX: Double, mouseY: Double, button: Int): Boolean {
        isDragging = false
        val element = hoveredElement(mouseX, mouseY).filter { it.mouseReleased(mouseX, mouseY, button) }
        if (!element.isPresent) {
            return super.mouseReleased(mouseX, mouseY, button)
        }
        return true
    }

    override fun mouseDragged(mouseX: Double, mouseY: Double, button: Int, deltaX: Double, deltaY: Double): Boolean {
        focused?.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)
    }

    override fun mouseScrolled(mouseX: Double, mouseY: Double, amount: Double): Boolean {
        if ((maxScroll > 0) and (mouseX >= (x + 7)) and (mouseX < (x + 169)) and (mouseY >= (y + 17)) and (mouseY < (y + 17 + viewedHeight * 18))) {
            scrollBar.knob = (scrollBar.knob - amount / maxScroll).toFloat().coerceIn(0f, 1f)
            c2s(N.SCROLL, buf().writeVarInt(syncId).writeVarInt((scrollBar.knob * maxScroll + 0.5).toInt()))
            return true
        }
        return super.mouseScrolled(mouseX, mouseY, amount)
    }

}
