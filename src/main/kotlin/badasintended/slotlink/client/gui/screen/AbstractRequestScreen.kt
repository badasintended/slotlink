package badasintended.slotlink.client.gui.screen

import badasintended.slotlink.client.gui.widget.*
import badasintended.slotlink.common.SortBy
import badasintended.slotlink.common.texture
import badasintended.slotlink.screen.AbstractRequestScreenHandler
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.MinecraftClient
import net.minecraft.util.registry.Registry
import spinnery.widget.WPanel
import spinnery.widget.WSlot
import spinnery.widget.WStaticImage
import spinnery.widget.WVerticalSlider
import spinnery.widget.api.Position
import spinnery.widget.api.Size
import kotlin.math.sign
import spinnery.widget.WAbstractWidget as W

@Environment(EnvType.CLIENT)
abstract class AbstractRequestScreen<H : AbstractRequestScreenHandler>(c: H) : ModScreen<H>(c) {

    private val sortedSlots = arrayListOf<WSlot>()

    private var viewedSlotHeight = if (MinecraftClient.getInstance().window.scaledHeight < 300) 3 else 6

    private var lastScroll = 0
    private var lastFilter = ""

    private var lastSlotClick: Long = 0

    protected var lastSort = c.lastSort

    // lol wtf is this
    private val main: WPanel
    private val titleLabel: WTranslatableLabel
    private val craftingLabel: WTranslatableLabel
    private val playerInvLabel: WTranslatableLabel
    private val scrollArea: WMouseArea
    private val scrollBar: WFakeScrollBar
    private val viewedSlots = arrayListOf<WSlot>()
    private val slotArea: WMouseArea
    private val searchBar: WSearchBar
    private val sortImage: WStaticImage

    init {
        val small = viewedSlotHeight == 3
        val slotSize = viewedSlotHeight * 18f

        main = root.createChild(
            { WPanel() },
            Position.of(0f, 0f, 0f),
            Size.of(176f, 197f + (slotSize - (if (small) 17 else 0)))
        )
        main.setParent<W>(root)
        main.setOnAlign(W::center)
        main.center()
        root.add(main)

        // Storage Request title
        titleLabel = main.createChild(
            { WTranslatableLabel("container.slotlink.request") },
            Position.of(main, 8f, 6f)
        )

        // Crafting label
        craftingLabel = main.createChild(
            { WTranslatableLabel("container.crafting") },
            Position.of(titleLabel, 19f, slotSize + 31f - (if (small) 8 else 0))
        )
        craftingLabel.setHidden<W>(small)

        // Crafting Input slots
        WSlot.addArray(
            Position.of(craftingLabel, 1f, 10f),
            Size.of(18f),
            main, 0, 1, 3, 3
        )

        // Crafting Result slot
        val resultSlot = main.createChild(
            { WSlot() },
            Position.of(craftingLabel, 91f, 24f),
            Size.of(26f)
        )
        resultSlot.setInventoryNumber<WSlot>(2)
        resultSlot.setSlotNumber<WSlot>(0)

        // Player Inventory label
        playerInvLabel = main.createChild(
            { WTranslatableLabel("container.inventory") },
            Position.of(craftingLabel, -19f, 66f - (if (small) 9 else 0))
        )
        playerInvLabel.setHidden<W>(small)

        // Player Inventory slots
        WSlot.addPlayerInventory(
            Position.of(playerInvLabel, -1f, 11f),
            Size.of(18f),
            main
        )

        val playerInvArea = main.createChild(
            { WMouseArea() },
            Position.of(playerInvLabel, -1f, 11f),
            Size.of(162f, 76f)
        )
        playerInvArea.onMouseReleased = { onSlotClick() }

        scrollArea = main.createChild(
            { WMouseArea() },
            Position.of(titleLabel, -1f, 11f),
            Size.of(162f, slotSize)
        )
        scrollArea.onMouseScrolled = { scroll(lastScroll - sign(it).toInt()) }

        scrollBar = main.createChild(
            { WFakeScrollBar { scroll(it) } },
            Position.of(scrollArea, 148f, 0f),
            Size.of(14f, slotSize)
        )
        scrollBar.setMin<WFakeScrollBar>(0f)

        for (i in 0 until 48) {
            val slot = main.createChild(
                { WSlot() },
                Position.of(scrollArea, (i % 8) * 18f, (i / 8) * 18f, 2f),
                Size.of(18f)
            )
            slot.setHidden<WSlot>(true)
            viewedSlots.add(slot)
        }

        slotArea = main.createChild(
            { WMouseArea() },
            Position.of(scrollArea),
            Size.of(144f, slotSize)
        )
        slotArea.onMouseReleased = { onSlotClick() }

        searchBar = main.createChild(
            { WSearchBar { sort(lastSort, it) } },
            Position.of(scrollArea, 0f, slotSize + 3f, 1f),
            Size.of(146f, 18f)
        )

        val sortButton = main.createChild(
            { WSortButton { sort(lastSort.next(), lastFilter) } },
            Position.of(searchBar, 148f, 1f),
            Size.of(14f)
        )

        sortImage = main.createChild(
            { WStaticImage() },
            Position.of(sortButton, 0f, 0f, 1f),
            Size.of(14f)
        )
        sortImage.setTexture<WStaticImage>(texture("gui/name"))

        GlobalScope.launch {
            delay(100)
            sort(lastSort, lastFilter)
        }

    }

    private fun onSlotClick() {
        GlobalScope.launch {
            lastSlotClick = System.currentTimeMillis()
            delay(250)
            if (System.currentTimeMillis() - lastSlotClick >= 250.toLong()) sort(lastSort, lastFilter)
        }
    }

    private fun scroll(v: Int) {
        val max = ((sortedSlots.size / 8) - viewedSlotHeight + 1).coerceAtLeast(0)
        lastScroll = v.coerceIn(0, max)
        scrollBar.setProgress<WVerticalSlider>((max - lastScroll).toFloat())
        val offset = lastScroll * 8

        viewedSlots.forEach { it.setHidden<W>(true) }

        var i = 0
        for (j in 0 until viewedSlotHeight * 8) {
            if (j < (sortedSlots.size - offset)) {
                val viewedSlot = viewedSlots[j]
                val sortedSlot = sortedSlots[j + offset]
                viewedSlot.setInventoryNumber<WSlot>(sortedSlot.inventoryNumber)
                viewedSlot.setSlotNumber<WSlot>(sortedSlot.slotNumber)
                viewedSlot.setBlacklist<WSlot>()
                viewedSlot.setHidden<WSlot>(c.isDeleted(sortedSlot.inventoryNumber))
                i++
            }
        }
        if (i < viewedSlotHeight * 8) for (j in i until viewedSlotHeight * 8) {
            viewedSlots[j].setHidden<WSlot>(true)
        }

    }

    private fun sort(sortBy: SortBy, filter: String) {
        val filled = arrayListOf<WSlot>()
        val empty = arrayListOf<WSlot>()
        c.slotList.forEach { slot ->
            if (slot.stack.isEmpty) empty.add(slot) else filled.add(slot)
        }

        when (sortBy) {
            SortBy.NAME -> {
                filled.sortBy { it.stack.name.asString() }
                sortImage.setTexture<WStaticImage>(texture("gui/name"))
            }
            SortBy.IDENTIFIER -> {
                filled.sortBy { Registry.ITEM.getId(it.stack.item).toString() }
                sortImage.setTexture<WStaticImage>(texture("gui/identifier"))
            }
            SortBy.COUNT -> {
                filled.sortByDescending { it.stack.count }
                sortImage.setTexture<WStaticImage>(texture("gui/count"))
            }
        }

        val trimmedFilter = filter.trim()

        if (trimmedFilter.isNotBlank()) {
            when (trimmedFilter.first()) {
                '@' -> filled.removeIf {
                    !Registry.ITEM.getId(it.stack.item).toString().contains(trimmedFilter.drop(1).trim(), true)
                }
                '#' -> filled.removeIf { slot ->
                    val tag = trimmedFilter.drop(1).trim()
                    val tags = c.world.tagManager.items().getTagsFor(slot.stack.item)
                    if (tags.isEmpty() and tag.isEmpty()) return@removeIf false
                    else tags.none { it.toString().contains(tag, true) }
                }
                else -> filled.removeIf { !it.stack.item.name.asString().contains(trimmedFilter.trim(), true) }
            }
        }

        sortedSlots.clear()
        sortedSlots.addAll(filled)
        sortedSlots.addAll(empty)

        scrollBar.setMax<WFakeScrollBar>(((sortedSlots.size / 8) - viewedSlotHeight + 1f).coerceAtLeast(0f))
        if (lastSort != sortBy) scroll(0) else scroll(lastScroll)
        lastSort = sortBy
        saveSort()

        lastFilter = filter
    }

    abstract fun saveSort()

    override fun resize(client: MinecraftClient, width: Int, height: Int) {
        viewedSlotHeight = if (client.window.scaledHeight < 300) 3 else 6

        val small = viewedSlotHeight == 3
        val slotSize = viewedSlotHeight * 18f

        main.setSize<W>(Size.of(176f, 197f + (slotSize - (if (small) 17 else 0))))
        craftingLabel.setPosition<W>(Position.of(titleLabel, 19f, slotSize + 31f - (if (small) 8 else 0)))
        craftingLabel.setHidden<W>(small)
        playerInvLabel.setPosition<W>(Position.of(craftingLabel, -19f, 66f - (if (small) 9 else 0)))
        playerInvLabel.setHidden<W>(small)
        scrollArea.setSize<W>(Size.of(162f, slotSize))
        scrollBar.setSize<W>(Size.of(14f, slotSize))
        slotArea.setSize<W>(Size.of(144f, slotSize))
        searchBar.setPosition<W>(Position.of(scrollArea, 0f, slotSize + 3f, 1f))

        sort(lastSort, lastFilter)

        super.resize(client, width, height)
    }

}
