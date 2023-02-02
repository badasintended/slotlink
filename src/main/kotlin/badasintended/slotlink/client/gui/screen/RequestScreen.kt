package badasintended.slotlink.client.gui.screen

import badasintended.slotlink.client.gui.widget.ButtonWidget
import badasintended.slotlink.client.gui.widget.CraftingResultSlotWidget
import badasintended.slotlink.client.gui.widget.MultiSlotWidget
import badasintended.slotlink.client.gui.widget.ScrollBarWidget
import badasintended.slotlink.client.gui.widget.TextFieldWidget
import badasintended.slotlink.client.util.GuiTextures
import badasintended.slotlink.client.util.bind
import badasintended.slotlink.client.util.c2s
import badasintended.slotlink.compat.invsort.InventorySortButton
import badasintended.slotlink.compat.recipe.RecipeViewer
import badasintended.slotlink.config.config
import badasintended.slotlink.init.Packets.CLEAR_CRAFTING_GRID
import badasintended.slotlink.init.Packets.MOVE
import badasintended.slotlink.init.Packets.RESIZE
import badasintended.slotlink.init.Packets.RESTOCK
import badasintended.slotlink.init.Packets.SCROLL
import badasintended.slotlink.init.Packets.SORT
import badasintended.slotlink.screen.RequestScreenHandler
import badasintended.slotlink.screen.slot.LockedSlot
import badasintended.slotlink.util.bool
import badasintended.slotlink.util.enum
import badasintended.slotlink.util.int
import badasintended.slotlink.util.string
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.gui.Element
import net.minecraft.client.gui.Selectable
import net.minecraft.client.gui.widget.ClickableWidget
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.text.Text
import org.lwjgl.glfw.GLFW
import badasintended.slotlink.util.backgroundHeight as utilBackgroundHeight
import badasintended.slotlink.util.backgroundWidth as utilBackgroundWidth
import badasintended.slotlink.util.x as utilX
import badasintended.slotlink.util.y as utilY

@Environment(EnvType.CLIENT)
class RequestScreen<H : RequestScreenHandler>(handler: H, inv: PlayerInventory, title: Text) :
    ModScreen<H>(handler, inv, title) {

    var craftingGrid by config::showCraftingGrid

    var arrowX = -1
    var arrowY = -1

    private val syncId by handler::syncId
    private val maxScroll by handler::maxScroll
    private val totalSlots by handler::totalSlotSize
    private val filledSlots by handler::filledSlotSize
    private val viewedHeight by handler::viewedHeight

    private val titleWidth by lazy { textRenderer.getWidth(title) }
    private val craftingText = Text.translatable("container.crafting")

    private lateinit var scrollBar: ScrollBarWidget
    private lateinit var searchBar: TextFieldWidget

    private var lastScroll = 0
    private var filter = ""

    private var skipChar = false

    private var inventorySortButton: ClickableWidget? = null

    override val baseTlKey: String
        get() = "container.slotlink.request"

    override fun init() {
        val craftHeight = if (craftingGrid) 67 else 0

        var viewedHeight = 3
        for (i in 3..6) if (height > (119 + craftHeight + (i * 18))) viewedHeight = i

        backgroundWidth = 9 * 18 + 14
        backgroundHeight = viewedHeight * 18 + 114 + craftHeight

        handler.resize(viewedHeight, craftingGrid)
        c2s(RESIZE) {
            int(syncId)
            int(viewedHeight)
            bool(craftingGrid)
        }

        super.init()

        playerInventoryTitleY = backgroundHeight - 94

        val x = x + 7
        val y = y + titleY + 11

        // Linked slot view
        for (i in 0 until viewedHeight * 9) {
            add(MultiSlotWidget(handler, i, x + (i % 9) * 18, y + (i / 9) * 18))
        }

        // Linked slot scroll bar
        scrollBar = add(ScrollBarWidget(x + 4 + 9 * 18, y, viewedHeight * 18)) {
            hasKnob = { maxScroll > 0 }
            onUpdated = {
                val scroll = (it * maxScroll + 0.5).toInt()
                if (scroll != lastScroll) c2s(SCROLL) {
                    int(syncId)
                    int(scroll)
                }
                lastScroll = scroll
            }
        }

        // Sort button
        add(ButtonWidget(x - 29, y, 20)) {
            allowSpectator = true
            texture = GuiTextures.REQUEST
            bgU = 216
            bgV = 32
            u = { 228 }
            v = { config.sort.ordinal * 14 + 52 }
            padding(3)
            onPressed = {
                config.sort = config.sort.next()
                scrollBar.knob = 0f
                sort()
            }
            onHovered = { matrices, x, y ->
                renderTooltip(matrices, tl("sort.${config.sort}"), x, y)
            }
        }

        // Toggle crafting grid button
        add(ButtonWidget(x - 29, y + 22, 20)) {
            allowSpectator = true
            texture = GuiTextures.REQUEST
            bgU = 216
            bgV = 32
            u = { 200 }
            v = { if (craftingGrid) 52 else 66 }
            padding(3)
            onPressed = {
                craftingGrid = !craftingGrid
                init(client!!, client!!.window.scaledWidth, client!!.window.scaledHeight)
            }
            onHovered = { matrices, x, y ->
                renderTooltip(matrices, tl("craft.${craftingGrid}"), x, y)
            }
        }

        if (craftingGrid) {
            // Crafting output slot
            add(CraftingResultSlotWidget(handler, x + 108, y + viewedHeight * 18 + 27))

            // Clear crafting grid button
            add(ButtonWidget(x + 13, y + 18 + viewedHeight * 18, 8)) {
                texture = GuiTextures.REQUEST
                background = false
                u = { 210 }
                v = { 16 }
                onPressed = {
                    c2s(CLEAR_CRAFTING_GRID) {
                        int(syncId)
                    }
                }
                onHovered = { matrices, x, y ->
                    renderTooltip(matrices, tl("craft.clear"), x, y)
                }
            }

            arrowX = x + 83
            arrowY = y + 32 + viewedHeight * 18
        }

        val invSorterW = if (inventorySortButton == null) 0 else 16

        // Move all to network button
        add(ButtonWidget(x + 9 * 18 - 8 - invSorterW, y + viewedHeight * 18 + 3 + craftHeight, 8)) {
            texture = GuiTextures.REQUEST
            background = false
            u = { 194 }
            v = { 16 }
            onPressed = {
                c2s(MOVE) {
                    int(syncId)
                }
            }
            onHovered = { matrices, x, y ->
                if (handler.cursorStack.isEmpty) {
                    renderTooltip(matrices, tl("move.all"), x, y)
                } else {
                    renderTooltip(matrices, tl("move.clazz"), x, y)
                }
            }
        }

        // Restock player inventory button
        add(ButtonWidget(x + 9 * 18 - 16 - invSorterW, y + viewedHeight * 18 + 3 + craftHeight, 8)) {
            texture = GuiTextures.REQUEST
            background = false
            u = { 202 }
            v = { 16 }
            onPressed = {
                c2s(RESTOCK) {
                    int(syncId)
                }
            }
            onHovered = { matrices, x, y ->
                if (handler.cursorStack.isEmpty) {
                    renderTooltip(matrices, tl("restock.all"), x, y)
                } else {
                    renderTooltip(matrices, tl("restock.cursor"), x, y)
                }
            }
        }

        // Search bar auto focus button
        add(ButtonWidget(x - 29, y + 44, 20)) {
            allowSpectator = true
            texture = GuiTextures.REQUEST
            bgU = 216
            bgV = 32
            u = { 242 }
            v = { if (config.autoFocusSearchBar) 52 else 66 }
            padding(3)
            onPressed = {
                config.autoFocusSearchBar = !config.autoFocusSearchBar
            }
            onHovered = { matrices, x, y ->
                renderTooltip(matrices, tl("autoFocus.${config.autoFocusSearchBar}"), x, y)
            }
        }

        // Sync to rei button
        RecipeViewer.instance?.also { recipeViewer ->
            add(ButtonWidget(x - 29, y + 66, 20)) {
                allowSpectator = true
                texture = GuiTextures.REQUEST
                bgU = 216
                bgV = 32
                u = { 214 }
                v = { recipeViewer.textureV + (if (config.syncRecipeViewerSearch) 0 else 14) }
                padding(3)
                onPressed = {
                    config.syncRecipeViewerSearch = !config.syncRecipeViewerSearch
                }
                onHovered = { matrices, x, y ->
                    renderTooltip(matrices, tl("searchSync.${config.syncRecipeViewerSearch}", recipeViewer.modName), x, y)
                }
            }
        }

        // Inventory Sorter's sort button
        inventorySortButton?.apply { add(this) }

        // Search bar
        searchBar = add(TextFieldWidget(x + 9 * 18 - 90, y - 13, 90, 12, tl("search"))) {
            setMaxLength(50)
            text = filter
            tooltip.add(tl("search.tip1"))
            tooltip.add(tl("search.tip2"))
            tooltip.add(tl("search.tip3"))
            setChangedListener {
                if (it != filter) {
                    scrollBar.knob = 0f
                    filter = it
                    sort()
                    if (config.syncRecipeViewerSearch) RecipeViewer.instance?.search(filter)
                }
            }
            if (config.autoFocusSearchBar) {
                grab = true
            }
        }

        sort()
    }

    inline fun <T> bounds(action: (Int, Int, Int, Int) -> T): T {
        return action(utilX - 22, utilY, utilBackgroundWidth + 40, utilBackgroundHeight)
    }

    private fun sort() {
        c2s(SORT) {
            int(syncId)
            enum(config.sort)
            string(filter)
        }
    }

    override fun <T> addSelectableChild(child: T): T where T : Element?, T : Selectable? {
        if (child is ClickableWidget && child is InventorySortButton && !child.initialized) {
            child.initialized = true
            inventorySortButton = child
            return child
        }
        return super.addSelectableChild(child)
    }

    override fun handledScreenTick() {
        super.handledScreenTick()
        if (searchBar.grab) searchBar.tick()
    }

    override fun drawBackground(matrices: MatrixStack, delta: Float, mouseX: Int, mouseY: Int) {
        super.drawBackground(matrices, delta, mouseX, mouseY)

        GuiTextures.REQUEST.bind()
        val viewedH = viewedHeight * 18
        drawTexture(matrices, x, y, 0, 0, 194, viewedH - 18 + 17)
        drawTexture(matrices, x, y + viewedH - 18 + 17, 0, 107, 194, 115)

        if (craftingGrid) {
            GuiTextures.CRAFTING.bind()
            drawTexture(matrices, x, y + viewedH + 17 + 7, 0, 0, 176, 157)
        }
    }

    override fun drawForeground(matrices: MatrixStack, mouseX: Int, mouseY: Int) {
        super.drawForeground(matrices, mouseX, mouseY)

        GuiTextures.REQUEST.bind()
        handler.slots.forEach {
            if (it is LockedSlot) {
                drawTexture(matrices, it.x, it.y, 240, 0, 16, 16)
            }
        }

        if (craftingGrid) {
            textRenderer.draw(matrices, craftingText, titleX + 21f, playerInventoryTitleY - 67f, 0x404040)
        }

        if (x + titleX < mouseX && mouseX <= x + titleX + titleWidth && y + titleY < mouseY && mouseY <= y + titleY + textRenderer.fontHeight) {
            renderTooltip(matrices, tl("slotCount", filledSlots, totalSlots), mouseX - x, mouseY - y)
        }
    }

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        return if (searchBar.grab) {
            if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
                searchBar.grab = false
                true
            } else {
                searchBar.keyPressed(keyCode, scanCode, modifiers)
            }
        } else if (client!!.options.chatKey.matchesKey(keyCode, scanCode)) {
            skipChar = true
            searchBar.grab = true
            true
        } else {
            super.keyPressed(keyCode, scanCode, modifiers)
        }
    }

    override fun keyReleased(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        skipChar = false
        return super.keyReleased(keyCode, scanCode, modifiers)
    }

    override fun charTyped(char: Char, modifiers: Int): Boolean {
        return if (skipChar) false else super.charTyped(char, modifiers)
    }

    override fun mouseScrolled(mouseX: Double, mouseY: Double, amount: Double): Boolean {
        if (maxScroll > 0 && mouseX >= x + 7 && mouseX < x + 169 && mouseY >= y + 17 && mouseY < y + 17 + viewedHeight * 18) {
            scrollBar.knob = (scrollBar.knob - amount / maxScroll).toFloat().coerceIn(0f, 1f)
            c2s(SCROLL) {
                int(syncId)
                int((scrollBar.knob * maxScroll + 0.5).toInt())
            }
            return true
        }
        return super.mouseScrolled(mouseX, mouseY, amount)
    }

    override fun close() {
        config.save()
        super.close()
    }

}
