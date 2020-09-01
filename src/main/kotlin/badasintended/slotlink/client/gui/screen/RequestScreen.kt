package badasintended.slotlink.client.gui.screen

import badasintended.slotlink.client.gui.widget.*
import badasintended.slotlink.gui.screen.RequestScreenHandler
import badasintended.slotlink.registry.NetworkRegistry
import badasintended.slotlink.util.*
import badasintended.slotlink.util.Sort.*
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.MinecraftClient
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.item.ItemStack
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.ScreenHandlerListener
import net.minecraft.text.LiteralText
import net.minecraft.text.MutableText
import net.minecraft.util.Formatting
import net.minecraft.util.collection.DefaultedList
import net.minecraft.util.registry.Registry
import sbinnery.common.utility.StackUtilities.equalItemAndTag
import sbinnery.widget.*
import sbinnery.widget.api.Action.PICKUP
import sbinnery.widget.api.Action.QUICK_MOVE
import sbinnery.widget.api.Position
import kotlin.math.ceil
import kotlin.math.sign
import sbinnery.widget.WAbstractWidget as W

@Environment(EnvType.CLIENT)
open class RequestScreen<H : RequestScreenHandler>(c: H) : ModScreen<H>(c), ScreenHandlerListener {

    private val emptySlots = arrayListOf<WLinkedSlot>()
    private val filledSlots = arrayListOf<WLinkedSlot>()
    private val filledStacks = arrayListOf<ItemStack>()

    private var slotHeight = 0
    private var hideLabel = true

    private var shouldSort = false
    private var lastScroll = 0
    private var lastFilter = ""

    private var tick = 0
    private var firstSort = true

    protected var lastSort = c.lastSort

    // lol wtf is this
    private val main: WPanel
    private val titleLabel: WTranslatableLabel
    private val craftingLabel: WTranslatableLabel
    private val playerInvLabel: WTranslatableLabel
    protected val playerInvSlots = arrayListOf<WPlayerSlot>()
    private val scrollArea: WMouseArea
    private val scrollbar: WFakeScrollbar
    private val viewedSlots = arrayListOf<WMultiSlot>()
    private val slotArea: WMouseArea
    private val sortButton: WSlotButton
    private val searchBar: WSearchBar

    init {
        updateSlotSize()
        val slotSize = slotHeight * 18

        main = root.createChild(
            ::WPanel, positionOf(0, 0, 0), sizeOf(
                176, 197 + (slotSize - (if (hideLabel) 17 else 0))
            )
        )
        main.setParent<W>(root)
        main.setOnAlign(W::center)
        main.center()
        root.add(main)

        // Storage Request title
        titleLabel = main.createChild(
            { WTranslatableLabel("container.slotlink.request") }, positionOf(main, 8, 6)
        )

        // Crafting label
        craftingLabel = main.createChild(
            { WTranslatableLabel("container.crafting") },
            positionOf(titleLabel, 19, slotSize + 31 - (if (hideLabel) 8 else 0))
        )
        craftingLabel.setHidden<W>(hideLabel)


        for (i in 0 until 9) {
            val slot = main.createChild(
                ::WSlot, positionOf(craftingLabel, (((i % 3) * 18) + 1), (((i / 3) * 18) + 10)), sizeOf(18)
            )
            slot.setNumber<WSlot>(1, i)
        }

        // Crafting Result slot
        val resultSlot = main.createChild(::WCraftingResultSlot, positionOf(craftingLabel, 91, 24), sizeOf(26))
        resultSlot.setNumber<WSlot>(2, 0)

        // Crafting Arrow
        main.createChild(::WCraftingArrow, positionOf(resultSlot, -29, 6))

        // Player Inventory label
        playerInvLabel = main.createChild(
            { WTranslatableLabel("container.inventory") },
            positionOf(craftingLabel, -19, (66 - (if (hideLabel) 9 else 0)))
        )
        playerInvLabel.setHidden<W>(hideLabel)

        for (i in 0 until 27) {
            val slot = main.createChild(
                { WPlayerSlot(this::putSameItem) },
                positionOf(playerInvLabel, (((i % 9) * 18) - 1), (((i / 9) * 18) + 11)), sizeOf(18)
            )
            slot.setNumber<WSlot>(0, i + 9)
            playerInvSlots.add(slot)
        }

        for (i in 0 until 9) {
            val slot = main.createChild(
                { WPlayerSlot(this::putSameItem) }, positionOf(playerInvLabel, (((i % 9) * 18) - 1), 69), sizeOf(18)
            )
            slot.setNumber<WSlot>(0, i)
            playerInvSlots.add(slot)
        }

        scrollArea = main.createChild(::WMouseArea, positionOf(titleLabel, -1, 11), sizeOf(162, slotSize))
        scrollArea.onMouseScrolled = { scroll(lastScroll - sign(it).toInt()) }

        scrollbar = main.createChild(
            { WFakeScrollbar(this::scroll) }, positionOf(scrollArea, 148, 0), sizeOf(14, slotSize)
        )
        scrollbar.setMin<WFakeScrollbar>(0f)

        slotArea = main.createChild(::WMouseArea, Position.of(scrollArea), sizeOf(144, slotSize))
        slotArea.onMouseReleased = { onSlotAreaClick() }

        for (i in 0 until 48) {
            val slot = main.createChild(
                { WMultiSlot(this::shouldSort) }, positionOf(scrollArea, ((i % 8) * 18), ((i / 8) * 18), 2), sizeOf(18)
            )
            slot.setNumber<WSlot>(-1, i)
            slot.setHidden<WSlot>(true)
            viewedSlots.add(slot)
        }

        sortButton = main.createChild({
            WSlotButton()
                .tlKey { lastSort.translationKey }
                .texture { lastSort.texture }
                .onClick { sort(lastSort.next(), lastFilter) }
        }, positionOf(scrollArea, 148, (slotSize + 4)), sizeOf(14))

        searchBar = main.createChild(
            { WSearchBar { sort(lastSort, it) } }, positionOf(sortButton, -148, -1), sizeOf(146, 18)
        )

        // that `move all to inventories` button
        main.createChild(
            { WIconButton("block.slotlink.request.putAllTooltip", tex("gui/put"), this::putAllButtonClick) },
            positionOf(playerInvLabel, 155, 4), sizeOf(6)
        )

        // Crafting clear button
        main.createChild(
            { WIconButton("block.slotlink.request.craft.clearTooltip", tex("gui/put"), this::clearButtonClick) },
            positionOf(craftingLabel, -6, 10), sizeOf(6)
        )

        main.createChild(
            { WHelpTooltip("block.slotlink.request", 7) }, positionOf(scrollbar, 5, -10), sizeOf(8)
        )
    }

    private fun clearButtonClick() {
        c.clearCraft()
        c2s(NetworkRegistry.CRAFT_CLEAR, buf())
    }

    private fun putAllButtonClick() {
        putInternal(playerInvSlots.filterNot { it.stack.isEmpty })
    }

    private fun putSameItem(stack: ItemStack) {
        putInternal(playerInvSlots.filterNot { it.stack.isEmpty }.filter { it.stack.item == stack.item })
    }

    private fun putInternal(slots: List<WPlayerSlot>) {
        slots.forEach { slotAction(c, it.slotNumber, it.inventoryNumber, 0, QUICK_MOVE, c.player) }
    }

    private fun onSlotAreaClick() {
        if (shouldSort) {
            slotAction(c, 0, -2, 0, PICKUP, c.player)
            slotAction(c, 0, -2, 0, QUICK_MOVE, c.player)
        } else {
            shouldSort = true
            sort()
        }
    }

    private fun scroll(v: Int) {
        val slotSize = filledStacks.size
        val max = (ceil(slotSize / 8f) - slotHeight).coerceAtLeast(0f)
        lastScroll = v.coerceIn(0, max.toInt())
        scrollbar.setProgress<WVerticalSlider>(max - lastScroll)
        val offset = lastScroll * 8
        val viewedSlotSize = slotHeight * 8

        viewedSlots.forEach { it.setHidden<W>(true) }

        for (j in 0 until viewedSlotSize) {
            viewedSlots[j].apply {
                setHidden<WSlot>(false)
                if (j < (filledStacks.size - offset)) {
                    val filledStack = filledStacks[j + offset]
                    val serverSlots = arrayListOf<WLinkedSlot>()

                    serverSlots.addAll(filledSlots.filter { slot ->
                        equalItemAndTag(filledStack, slot.copiedStack)
                    })

                    setLinkedSlots(*serverSlots.toTypedArray())
                    setStack<WSlot>(filledStack)
                } else {
                    setStack<WSlot>(ItemStack.EMPTY)
                    setLinkedSlots()
                }
            }
        }
    }

    open fun saveSort() {
        val buf = buf()
        buf.apply {
            writeBlockPos(c.requestPos)
            writeInt(lastSort.ordinal)
        }
        c2s(NetworkRegistry.REQUEST_SAVE, buf)
    }

    fun sort() {
        if (shouldSort) sort(lastSort, lastFilter)
    }

    private fun sort(sort: Sort, filter: String): Sort {
        emptySlots.clear()
        filledSlots.clear()

        c.validateInventories()

        c.linkedSlots.forEach {
            val slot = WLinkedSlot().apply {
                invNumber = it.inventoryNumber
                slotNumber = it.slotNumber
                stack = it.stack
            }
            if (it.stack.isEmpty) emptySlots.add(slot) else filledSlots.add(slot)
        }

        val trimmedFilter = filter.trim()

        if (trimmedFilter.isNotBlank()) {
            when (trimmedFilter.first()) {
                '@' -> filledSlots.removeIf {
                    !Registry.ITEM.getId(it.stack.item).toString().contains(trimmedFilter.drop(1), true)
                }
                '#' -> filledSlots.removeIf r@{ slot ->
                    val tag = trimmedFilter.drop(1)
                    val tags = c.world.tagManager.items.getTagsFor(slot.stack.item)
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

        when (sort) {
            NAME -> filledStacks.sortBy { it.name.string }
            NAME_DESC -> filledStacks.sortByDescending { it.name.string }
            ID -> filledStacks.sortBy { Registry.ITEM.getId(it.item).toString() }
            ID_DESC -> filledStacks.sortByDescending { Registry.ITEM.getId(it.item).toString() }
            COUNT -> filledStacks.sortBy { it.count }
            COUNT_DESC -> filledStacks.sortByDescending { it.count }
        }

        scrollbar.setMax<WFakeScrollbar>((ceil(filledStacks.size / 8f) - slotHeight).coerceAtLeast(0f))
        if ((lastSort != sort) or (lastFilter != filter)) scroll(0) else scroll(lastScroll)
        lastSort = sort
        saveSort()

        lastFilter = filter
        return lastSort
    }

    private fun updateSlotSize() {
        val scaledHeight = getClient().window.scaledHeight
        slotHeight = 0
        for (i in 3..6) if (scaledHeight > (207 + (i * 18))) slotHeight = i
        hideLabel = (slotHeight == 0)
        slotHeight = slotHeight.coerceAtLeast(3)
    }

    override fun init() {
        super.init()
        val buf = buf()
        buf.writeVarInt(c.syncId)
        c2s(NetworkRegistry.REQUEST_INIT_SERVER, buf)
        c.addListener(this)
    }

    override fun removed() {
        super.removed()
        c.removeListener(this)
    }

    override fun resize(client: MinecraftClient, width: Int, height: Int) {
        updateSlotSize()
        viewedSlots.forEach { it.setHidden<WSlot>(true) }

        val slotSize = slotHeight * 18

        main.setSize<W>(sizeOf(176, 197 + (slotSize - (if (hideLabel) 17 else 0))))
        craftingLabel.setPosition<W>(positionOf(titleLabel, 19, slotSize + 31 - (if (hideLabel) 8 else 0)))
        craftingLabel.setHidden<W>(hideLabel)
        playerInvLabel.setPosition<W>(positionOf(craftingLabel, -19, 66 - (if (hideLabel) 9 else 0)))
        playerInvLabel.setHidden<W>(hideLabel)
        scrollArea.setSize<W>(sizeOf(162, slotSize))
        scrollbar.setSize<W>(sizeOf(14, slotSize))
        slotArea.setSize<W>(sizeOf(144, slotSize))
        sortButton.setPosition<W>(positionOf(scrollArea, 148, slotSize + 4))

        sort()

        super.resize(client, width, height)
    }

    override fun renderTooltip(matrices: MatrixStack, stack: ItemStack, x: Int, y: Int) {
        val list = getTooltipFromItem(stack)
        if (slotArea.isWithinBounds(x.toFloat(), y.toFloat())) {
            (list[0] as MutableText).append(LiteralText(" (${stack.count})").formatted(Formatting.GOLD))
        }
        renderTooltip(matrices, list, x, y)
    }

    override fun tick() {
        tick++
        if (tick == 1) {
            tick = 0
            if (firstSort) {
                firstSort = false
                shouldSort = true
                sort()
            }
        }
    }

    override fun onHandlerRegistered(handler: ScreenHandler, stacks: DefaultedList<ItemStack>) {}
    override fun onPropertyUpdate(handler: ScreenHandler, property: Int, value: Int) {}
    override fun onSlotUpdate(handler: ScreenHandler, slotId: Int, stack: ItemStack) {}

}
