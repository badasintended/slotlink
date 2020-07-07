package badasintended.slotlink.client.gui.screen

import badasintended.slotlink.client.gui.widget.*
import badasintended.slotlink.common.SortBy
import badasintended.slotlink.common.positionOf
import badasintended.slotlink.common.sizeOf
import badasintended.slotlink.common.slotAction
import badasintended.slotlink.network.NetworkRegistry
import badasintended.slotlink.screen.AbstractRequestScreenHandler
import io.netty.buffer.Unpooled
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry
import net.minecraft.client.MinecraftClient
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.item.ItemStack
import net.minecraft.network.PacketByteBuf
import net.minecraft.text.TranslatableText
import net.minecraft.util.registry.Registry
import spinnery.common.utility.StackUtilities.equalItemAndTag
import spinnery.widget.WPanel
import spinnery.widget.WSlot
import spinnery.widget.WVerticalSlider
import spinnery.widget.api.Action.PICKUP
import spinnery.widget.api.Action.QUICK_MOVE
import spinnery.widget.api.Position
import kotlin.math.sign
import kotlin.streams.toList
import spinnery.widget.WAbstractWidget as W

@Environment(EnvType.CLIENT)
abstract class AbstractRequestScreen<H : AbstractRequestScreenHandler>(c: H) : ModScreen<H>(c) {

    private val emptySlots = arrayListOf<WLinkedSlot>()
    private val filledSlots = arrayListOf<WLinkedSlot>()
    private val filledStacks = arrayListOf<ItemStack>()

    private var slotHeight = 0
    private var hideLabel = true

    private var slotActionPerformed = false
    private var lastScroll = 0
    private var lastFilter = ""

    private var stillSorting = false

    protected var lastSort = c.lastSort

    // lol wtf is this
    private val main: WPanel
    private val titleLabel: WTranslatableLabel
    private val craftingLabel: WTranslatableLabel
    private val playerInvLabel: WTranslatableLabel
    private val playerInvSlots = arrayListOf<WPlayerSlot>()
    private val scrollArea: WMouseArea
    private val scrollbar: WFakeScrollbar
    private val viewedSlots = arrayListOf<WMultiSlot>()
    private val slotArea: WSlotArea
    private val searchBar: WSearchBar

    init {
        updateSlotSize()
        val slotSize = slotHeight * 18

        main = root.createChild(
            { WPanel() },
            positionOf(0, 0, 0),
            sizeOf(176, 197 + (slotSize - (if (hideLabel) 17 else 0)))
        )
        main.setParent<W>(root)
        main.setOnAlign(W::center)
        main.center()
        root.add(main)

        // Storage Request title
        titleLabel = main.createChild(
            { WTranslatableLabel("container.slotlink.request") },
            positionOf(main, 8, 6)
        )

        // Crafting label
        craftingLabel = main.createChild(
            { WTranslatableLabel("container.crafting") },
            positionOf(titleLabel, 19, slotSize + 31 - (if (hideLabel) 8 else 0))
        )
        craftingLabel.setHidden<W>(hideLabel)

        // Crafting clear button
        main.createChild(
            { WPutButton({ drawClearTooltip() }, { onClearButtonClick() }) },
            positionOf(craftingLabel, -6, 10),
            sizeOf(6)
        )

        // Crafting Input slots
        WSlot.addArray(
            positionOf(craftingLabel, 1, 10),
            sizeOf(18),
            main, 0, 1, 3, 3
        )

        // Crafting Result slot
        val resultSlot = main.createChild(
            { WCraftingResultSlot { sort() } },
            positionOf(craftingLabel, 91, 24),
            sizeOf(26)
        )
        resultSlot.setInventoryNumber<WSlot>(2)
        resultSlot.setSlotNumber<WSlot>(0)

        // Crafting Arrow
        main.createChild(
            { WCraftingArrow() },
            positionOf(resultSlot, -29, 6)
        )

        // Player Inventory label
        playerInvLabel = main.createChild(
            { WTranslatableLabel("container.inventory") },
            positionOf(craftingLabel, -19, (66 - (if (hideLabel) 9 else 0)))
        )
        playerInvLabel.setHidden<W>(hideLabel)

        // that `move all to inventories` button
        main.createChild(
            { WPutButton({ drawPutAllTooltip() }, { onPutAllButtonClick() }) },
            positionOf(playerInvLabel, 155, 4),
            sizeOf(6)
        )

        for (i in 0 until 27) {
            val slot = main.createChild(
                { WPlayerSlot { sort() } },
                positionOf(playerInvLabel, (((i % 9) * 18) - 1), (((i / 9) * 18) + 11)),
                sizeOf(18)
            )
            slot.setInventoryNumber<WSlot>(0)
            slot.setSlotNumber<WSlot>(i + 9)
            playerInvSlots.add(slot)
        }

        for (i in 0 until 9) {
            val slot = main.createChild(
                { WPlayerSlot { sort() } },
                positionOf(playerInvLabel, (((i % 9) * 18) - 1), 70),
                sizeOf(18)
            )
            slot.setInventoryNumber<WSlot>(0)
            slot.setSlotNumber<WSlot>(i)
            playerInvSlots.add(slot)
        }

        scrollArea = main.createChild(
            { WMouseArea() },
            positionOf(titleLabel, -1, 11),
            sizeOf(162, slotSize)
        )
        scrollArea.onMouseScrolled = { scroll(lastScroll - sign(it).toInt()) }

        scrollbar = main.createChild(
            { WFakeScrollbar { scroll(it) } },
            positionOf(scrollArea, 148, 0),
            sizeOf(14, slotSize)
        )
        scrollbar.setMin<WFakeScrollbar>(0f)

        slotArea = main.createChild(
            { WSlotArea() },
            Position.of(scrollArea),
            sizeOf(144, slotSize)
        )
        slotArea.onMouseReleased = { onSlotAreaClick() }
        slotArea.setHidden<W>(true)

        for (i in 0 until 48) {
            val slot = main.createChild(
                { WMultiSlot({ slotActionPerformed = it }, { sort() }) },
                positionOf(scrollArea, ((i % 8) * 18), ((i / 8) * 18), 2),
                sizeOf(18)
            )
            slot.setInventoryNumber<WSlot>(-1)
            slot.setSlotNumber<WSlot>(i)
            slot.setHidden<WSlot>(true)
            viewedSlots.add(slot)
        }

        searchBar = main.createChild(
            { WSearchBar({ sort(lastSort, it) }, { drawSearchTooltip() }) },
            positionOf(scrollArea, 0, (slotSize + 3)),
            sizeOf(146, 18)
        )

        main.createChild(
            { WSortButton(lastSort.texture, { sort(lastSort.next(), lastFilter) }, { drawSortTooltip() }) },
            positionOf(searchBar, 148, 1),
            sizeOf(14)
        )

    }

    private fun onClearButtonClick() {
        c.clearCraft()
        ClientSidePacketRegistry.INSTANCE.sendToServer(NetworkRegistry.CRAFT_CLEAR, PacketByteBuf(Unpooled.buffer()))
        sort()
    }

    private fun onPutAllButtonClick() {
        val filledSlots = playerInvSlots.filterNot { it.stack.isEmpty }
        filledSlots.forEach { slot ->
            slotAction(c, slot.slotNumber, slot.inventoryNumber, 0, QUICK_MOVE, c.player)
        }
        sort()
    }

    private fun onSlotAreaClick() {
        if (!slotActionPerformed) {
            slotAction(c, 0, -2, 0, PICKUP, c.player)
            slotAction(c, 0, -2, 0, QUICK_MOVE, c.player)
            sort()
        }
        slotActionPerformed = false
    }

    private fun scroll(v: Int) {
        val slotSize = filledStacks.size + emptySlots.size
        val max = ((slotSize / 8) - slotHeight + 1).coerceAtLeast(0)
        lastScroll = v.coerceIn(0, max)
        scrollbar.setProgress<WVerticalSlider>((max - lastScroll).toFloat())
        val offset = lastScroll * 8
        val viewedSlotSize = slotHeight * 8

        viewedSlots.forEach { it.setHidden<W>(true) }

        var i = 0

        for (j in 0 until viewedSlotSize) {
            val viewedSlot = viewedSlots[j]
            if (j < (filledStacks.size - offset)) {
                val filledStack = filledStacks[j + offset]
                val serverSlots = arrayListOf<WLinkedSlot>()

                serverSlots.addAll(filledSlots.filter { slot ->
                    equalItemAndTag(filledStack, slot.copiedStack)
                })

                viewedSlot.setLinkedSlots(*serverSlots.toTypedArray())
                viewedSlot.setStack<WSlot>(filledStack)
                viewedSlot.setHidden<WSlot>(false)
                i++
            } else if (j < (emptySlots.size - offset + filledStacks.size)) {
                viewedSlot.setLinkedSlots(emptySlots[j + offset - filledStacks.size])
                viewedSlot.setStack<WSlot>(ItemStack.EMPTY)
                viewedSlot.setHidden<WSlot>(false)
                i++
            }
        }

        if (i < viewedSlotSize) for (j in i until viewedSlotSize) {
            viewedSlots[j].setHidden<WSlot>(true)
        }

    }

    fun sort() = sort(lastSort, lastFilter)

    private fun sort(sortBy: SortBy, filter: String): SortBy {
        if (stillSorting) return lastSort

        stillSorting = true

        emptySlots.clear()
        filledSlots.clear()

        c.linkedSlots.forEach { cSlot ->
            val slot = WLinkedSlot()
            slot.invNumber = cSlot.inventoryNumber
            slot.slotNumber = cSlot.slotNumber
            slot.stack = cSlot.stack
            if (!c.isDeleted(cSlot.inventoryNumber)) {
                if (cSlot.stack.isEmpty) emptySlots.add(slot) else filledSlots.add(slot)
            }
        }

        val trimmedFilter = filter.trim()

        if (trimmedFilter.isNotBlank()) {
            when (trimmedFilter.first()) {
                '@' -> filledSlots.removeIf {
                    !Registry.ITEM.getId(it.stack.item).toString().contains(trimmedFilter.drop(1), true)
                }
                '#' -> filledSlots.removeIf r@{ slot ->
                    val tag = trimmedFilter.drop(1)
                    val tags = c.world.tagManager.items().getTagsFor(slot.stack.item)
                    if (tags.isEmpty() and tag.isEmpty()) return@r false
                    else return@r tags.none { it.toString().contains(tag, true) }
                }
                else -> filledSlots.removeIf { !it.stack.name.string.contains(trimmedFilter.trim(), true) }
            }
        }

        filledStacks.clear()
        filledSlots.forEach { slot ->
            val copyStack = slot.stack.copy()
            when {
                filledStacks.none { equalItemAndTag(copyStack, it) } -> filledStacks.add(copyStack)
                else -> filledStacks.first { equalItemAndTag(copyStack, it) }.count += slot.stack.count
            }
            slot.copiedStack = copyStack
        }

        when (sortBy) {
            SortBy.NAME -> {
                filledStacks.sortBy { it.name.string }
            }
            SortBy.IDENTIFIER -> {
                filledStacks.sortBy { Registry.ITEM.getId(it.item).toString() }
            }
            SortBy.COUNT -> {
                filledStacks.sortByDescending { it.count }
            }
        }

        val slotSize = filledStacks.size + emptySlots.size

        scrollbar.setMax<WFakeScrollbar>(((slotSize / 8) - slotHeight + 1f).coerceAtLeast(0f))
        if (lastSort != sortBy) scroll(0) else scroll(lastScroll)
        lastSort = sortBy
        saveSort()

        lastFilter = filter

        stillSorting = false

        return lastSort
    }

    private fun drawSearchTooltip() = drawTooltip(
        "block.slotlink.request.search.tooltip1",
        "block.slotlink.request.search.tooltip2"
    )

    private fun drawSortTooltip() = drawTooltip(lastSort.translationKey)

    private fun drawClearTooltip() = drawTooltip("block.slotlink.request.craft.clearTooltip")

    private fun drawPutAllTooltip() = drawTooltip("block.slotlink.request.putAllTooltip")

    private fun drawTooltip(vararg translationKeys: String) {
        val client = MinecraftClient.getInstance()
        val mouse = client.mouse
        val factor = client.window.scaleFactor
        val x = (mouse.x / factor).toInt()
        val y = (mouse.y / factor).toInt()
        val text = translationKeys.asList().stream().map { TranslatableText(it) }.toList()
        renderTooltip(MatrixStack(), text, x, y)
    }

    private fun updateSlotSize() {
        val scaledHeight = MinecraftClient.getInstance().window.scaledHeight
        slotHeight = 0
        for (i in 3..6) if (scaledHeight > (207 + (i * 18))) slotHeight = i
        hideLabel = (slotHeight == 0)
        slotHeight = slotHeight.coerceAtLeast(3)
    }

    abstract fun saveSort()

    override fun resize(client: MinecraftClient, width: Int, height: Int) {
        updateSlotSize()
        val slotSize = slotHeight * 18

        main.setSize<W>(sizeOf(176, 197 + (slotSize - (if (hideLabel) 17 else 0))))
        craftingLabel.setPosition<W>(positionOf(titleLabel, 19, slotSize + 31 - (if (hideLabel) 8 else 0)))
        craftingLabel.setHidden<W>(hideLabel)
        playerInvLabel.setPosition<W>(positionOf(craftingLabel, -19, 66 - (if (hideLabel) 9 else 0)))
        playerInvLabel.setHidden<W>(hideLabel)
        scrollArea.setSize<W>(sizeOf(162, slotSize))
        scrollbar.setSize<W>(sizeOf(14, slotSize))
        slotArea.setSize<W>(sizeOf(144, slotSize))
        searchBar.setPosition<W>(positionOf(scrollArea, 0, slotSize + 3))

        sort()

        super.resize(client, width, height)
    }

    override fun keyPressed(keyCode: Int, character: Int, keyModifier: Int): Boolean {
        if (searchBar.isActive) searchBar.onKeyPressed(keyCode, character, keyModifier)
        else super.keyPressed(keyCode, character, keyModifier)
        return true
    }

}
