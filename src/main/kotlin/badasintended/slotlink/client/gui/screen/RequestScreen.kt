package badasintended.slotlink.client.gui.screen

import badasintended.slotlink.client.config.config
import badasintended.slotlink.client.gui.widget.ButtonWidget
import badasintended.slotlink.client.gui.widget.CraftingResultSlotWidget
import badasintended.slotlink.client.gui.widget.MultiSlotWidget
import badasintended.slotlink.client.gui.widget.ScrollBarWidget
import badasintended.slotlink.client.gui.widget.SlotCountWidget
import badasintended.slotlink.client.gui.widget.TextFieldWidget
import badasintended.slotlink.client.util.c2s
import badasintended.slotlink.client.util.drawNinePatch
import badasintended.slotlink.init.Packets.CLEAR_CRAFTING_GRID
import badasintended.slotlink.init.Packets.MOVE
import badasintended.slotlink.init.Packets.RESIZE
import badasintended.slotlink.init.Packets.RESTOCK
import badasintended.slotlink.init.Packets.SCROLL
import badasintended.slotlink.init.Packets.SORT
import badasintended.slotlink.screen.RequestScreenHandler
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.MinecraftClient
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.text.Text
import org.lwjgl.glfw.GLFW

var reiSearchHandler: ((String) -> Unit)? = null

@Environment(EnvType.CLIENT)
class RequestScreen<H : RequestScreenHandler>(handler: H, inv: PlayerInventory, title: Text) :
    ModScreen<H>(handler, inv, title) {

    private val syncId get() = handler.syncId
    private val viewedHeight get() = handler.viewedHeight
    private val maxScroll get() = handler.maxScroll
    private val totalSlots get() = handler.totalSlotSize
    private val filledSlots get() = handler.filledSlotSize

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

        // Remaining slot display
        addButton(SlotCountWidget(x, y + titleY, backgroundWidth, this::totalSlots, this::filledSlots))

        val x = x + 7
        val y = y + titleY + 11

        val craftHeight = if (config.showCraftingGrid) 52 else 0

        // Linked slot view
        for (i in 0 until viewedHeight * 8) {
            addButton(MultiSlotWidget(handler, i, x + (i % 8) * 18, y + (i / 8) * 18))
        }

        // Liked slot scroll bar
        scrollBar = addButton(ScrollBarWidget(x + 4 + 8 * 18, y, viewedHeight * 18)).apply {
            hasKnob = { maxScroll > 0 }
            onUpdated = {
                val scroll = (it * maxScroll + 0.5).toInt()
                if (scroll != lastScroll) c2s(SCROLL) {
                    writeVarInt(syncId)
                    writeVarInt(scroll)
                }
                lastScroll = scroll
            }
        }

        // Sort button
        addButton(ButtonWidget(x + 4 + 8 * 18, y + 4 + viewedHeight * 18, 14, 14)).apply {
            u = { 200 }
            v = { config.sort.ordinal * 14 }
            onPressed = {
                config.sort = config.sort.next()
                scrollBar.knob = 0f
                c2s(SORT) {
                    writeVarInt(syncId)
                    writeVarInt(config.sort.ordinal)
                    writeString(filter)
                }
            }
            onHovered = { matrices, x, y ->
                renderTooltip(matrices, tl("sort.${config.sort}"), x, y)
            }
        }

        // Toggle crafting grid button
        addButton(ButtonWidget(x + 4 + 7 * 18, y + 4 + viewedHeight * 18, 14, 14)).apply {
            u = { 172 }
            v = { if (config.showCraftingGrid) 0 else 14 }
            onPressed = {
                config.showCraftingGrid = !config.showCraftingGrid
                init(client!!, client!!.window.scaledWidth, client!!.window.scaledHeight)
            }
            onHovered = { matrices, x, y ->
                renderTooltip(matrices, tl("craft.${config.showCraftingGrid}"), x, y)
            }
        }

        if (config.showCraftingGrid) {
            // Crafting output slot
            addButton(CraftingResultSlotWidget(handler, x + 112, y + viewedHeight * 18 + 36))

            // Clear crafting grid button
            addButton(ButtonWidget(x + 13, y + 22 + viewedHeight * 18, 8, 8)).apply {
                background = false
                u = { 16 }
                v = { 46 }
                onPressed = {
                    c2s(CLEAR_CRAFTING_GRID) {
                        writeVarInt(syncId)
                    }
                }
                onHovered = { matrices, x, y ->
                    renderTooltip(matrices, tl("craft.clear"), x, y)
                }
            }
        }

        // Move all to network button
        addButton(ButtonWidget(x + 9 * 18 - 8, y + viewedHeight * 18 + 19 + craftHeight, 8, 8)).apply {
            background = false
            u = { 0 }
            v = { 46 }
            onPressed = {
                c2s(MOVE) {
                    writeVarInt(syncId)
                }
            }
            onHovered = { matrices, x, y ->
                if (playerInventory.cursorStack.isEmpty) {
                    renderTooltip(matrices, tl("move.all"), x, y)
                } else {
                    renderTooltip(matrices, tl("move.type"), x, y)
                }
            }
        }

        // Restock player inventory button
        addButton(ButtonWidget(x + 9 * 18 - 16, y + viewedHeight * 18 + 19 + craftHeight, 8, 8)).apply {
            background = false
            u = { 8 }
            v = { 46 }
            onPressed = {
                c2s(RESTOCK) {
                    writeVarInt(syncId)
                }
            }
            onHovered = { matrices, x, y ->
                if (playerInventory.cursorStack.isEmpty) {
                    renderTooltip(matrices, tl("restock.all"), x, y)
                } else {
                    renderTooltip(matrices, tl("restock.cursor"), x, y)
                }
            }
        }

        // Sync to rei button
        if (reiSearchHandler != null) addButton(ButtonWidget(x + 4 + 6 * 18, y + 4 + viewedHeight * 18, 14, 14)).apply {
            u = { 158 }
            v = { if (config.syncReiSearch) 0 else 14 }
            onPressed = {
                config.syncReiSearch = !config.syncReiSearch
            }
            onHovered = { matrices, x, y ->
                renderTooltip(matrices, tl("rei.${config.syncReiSearch}"), x, y)
            }
        }

        // Search bar
        searchBar = addButton(
            TextFieldWidget(x, y + 4 + viewedHeight * 18, 18 * if (reiSearchHandler != null) 6 else 7, 14, tl("search"))
        ).apply {
            setMaxLength(50)
            placeholder = tl("search")
            text = filter
            tooltip.add(tl("search.tip1"))
            tooltip.add(tl("search.tip2"))
            tooltip.add(tl("search.tip3"))
            setChangedListener {
                if (it != filter) {
                    c2s(SORT) {
                        writeVarInt(syncId)
                        writeVarInt(config.sort.ordinal)
                        writeString(it)
                    }
                    scrollBar.knob = 0f
                    filter = it
                    if (config.syncReiSearch) reiSearchHandler?.invoke(filter)
                }
            }
        }

        c2s(SORT) {
            writeVarInt(syncId)
            writeVarInt(config.sort.ordinal)
            writeString(filter)
        }
    }

    /**
     * apparently this also called on resize
     */
    override fun init(client: MinecraftClient, width: Int, height: Int) {
        val craftHeight = if (config.showCraftingGrid) 52 else 0

        var viewedHeight = 3
        for (i in 3..6) if (height > (128 + craftHeight + (i * 18))) viewedHeight = i

        backgroundWidth = 9 * 18 + 14
        backgroundHeight = viewedHeight * 18 + 128 + craftHeight

        handler.resize(viewedHeight, config.showCraftingGrid)
        c2s(RESIZE) {
            writeVarInt(handler.syncId)
            writeVarInt(viewedHeight)
            writeBoolean(config.showCraftingGrid)
        }

        super.init(client, width, height)
    }

    override fun drawBackground(matrices: MatrixStack, delta: Float, mouseX: Int, mouseY: Int) {
        super.drawBackground(matrices, delta, mouseX, mouseY)

        if (config.showCraftingGrid) {
            val result = handler.slots[0]
            drawNinePatch(matrices, x + result.x - 5, y + result.y - 5, 26, 26, 16f, 0f, 1, 14)
            drawTexture(matrices, x + 90, y + 58 + viewedHeight * 18, 0, 31, 22, 15)
        }
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

    override fun mouseScrolled(mouseX: Double, mouseY: Double, amount: Double): Boolean {
        if ((maxScroll > 0) and (mouseX >= (x + 7)) and (mouseX < (x + 169)) and (mouseY >= (y + 17)) and (mouseY < (y + 17 + viewedHeight * 18))) {
            scrollBar.knob = (scrollBar.knob - amount / maxScroll).toFloat().coerceIn(0f, 1f)
            c2s(SCROLL) {
                writeVarInt(syncId)
                writeVarInt((scrollBar.knob * maxScroll + 0.5).toInt())
            }
            return true
        }
        return super.mouseScrolled(mouseX, mouseY, amount)
    }

    override fun onClose() {
        config.save()
        super.onClose()
    }

}
