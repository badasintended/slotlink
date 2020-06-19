package badasintended.slotlink.gui.screen

import badasintended.slotlink.common.SortBy
import badasintended.slotlink.gui.container.RequestContainer
import badasintended.slotlink.gui.widget.*
import badasintended.slotlink.texture
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.util.registry.Registry
import spinnery.widget.WSlot
import spinnery.widget.WStaticImage
import spinnery.widget.WVerticalSlider
import spinnery.widget.api.Position
import spinnery.widget.api.Size
import kotlin.math.sign
import spinnery.widget.WAbstractWidget as W

@Environment(EnvType.CLIENT)
class RequestScreen(c: RequestContainer) : ModScreen<RequestContainer>(c) {

    private val sortedSlots = arrayListOf<WSlot>()
    private val viewedSlots = arrayListOf<WSlot>()

    private val searchBar: WSearchBar
    private val scrollBar: WFakeScrollBar
    private val sortImage: WStaticImage

    private var lastScroll = 0
    private var lastFilter = ""

    private var lastSlotClick: Long = 0

    init {
        val main = root.createChild(
            { WTexturedPanel("request") },
            Position.of(0f, 0f, 0f),
            Size.of(176f, 310f)
        )
        main.setParent<W>(root)
        main.setOnAlign(W::center)
        main.center()
        root.add(main)

        // Storage Request title
        val title = main.createChild(
            { WTranslatableLabel("container.slotlink.request") },
            Position.of(main, 8f, 6f)
        )

        // Crafting label
        val craftingLabel = main.createChild(
            { WTranslatableLabel("container.crafting") },
            Position.of(title, 19f, 144f)
        )

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
        val playerInvLabel = main.createChild(
            { WTranslatableLabel("container.inventory") },
            Position.of(craftingLabel, -19f, 66f)
        )

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

        val scrollArea = main.createChild(
            { WMouseArea() },
            Position.of(title, -1f, 11f),
            Size.of(162f, 108f)
        )
        scrollArea.onMouseScrolled = { scroll(lastScroll - sign(it).toInt()) }

        scrollBar = main.createChild(
            { WFakeScrollBar { scroll(it) } },
            Position.of(scrollArea, 148f, 0f),
            Size.of(14f, 108f)
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

        val slotArea = main.createChild(
            { WMouseArea() },
            Position.of(scrollArea),
            Size.of(144f, 108f)
        )
        slotArea.onMouseReleased = { onSlotClick() }

        searchBar = main.createChild(
            { WSearchBar { sort(c.lastSort, it) } },
            Position.of(scrollArea, 0f, 111f, 1f),
            Size.of(146f, 18f)
        )

        val sortButton = main.createChild(
            { WSortButton { sort(c.lastSort.next(), lastFilter) } },
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
            sort(c.lastSort, lastFilter)
        }

    }

    private fun onSlotClick() {
        GlobalScope.launch {
            lastSlotClick = System.currentTimeMillis()
            delay(250)
            if (System.currentTimeMillis() - lastSlotClick >= 250.toLong()) sort(c.lastSort, lastFilter)
        }
    }

    private fun scroll(v: Int) {
        val max = ((sortedSlots.size / 8) - 5).coerceAtLeast(0)
        lastScroll = v.coerceIn(0, max)
        scrollBar.setProgress<WVerticalSlider>((max - lastScroll).toFloat())
        val offset = lastScroll * 8

        var i = 0
        for (j in 0 until 48) {
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
        if (i < 48) for (j in i until 48) {
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

        if (filter.isNotBlank()) {
            when (filter.first().toString()) {
                "@" -> filled.removeIf { !Registry.ITEM.getId(it.stack.item).toString().contains(filter.drop(1), true) }
                "#" -> filled.removeIf { slot ->
                    val tag = filter.drop(1)
                    val tags = c.world.tagManager.items().getTagsFor(slot.stack.item)
                    if (tags.isEmpty() and tag.isEmpty()) return@removeIf false
                    else tags.none { it.toString().contains(tag, true) }
                }
                else -> filled.removeIf { !it.stack.item.name.asString().contains(filter, true) }
            }
        }

        sortedSlots.clear()
        sortedSlots.addAll(filled)
        sortedSlots.addAll(empty)

        scrollBar.setMax<WFakeScrollBar>(((sortedSlots.size / 8) - 5f).coerceAtLeast(0f))
        if (c.lastSort != sortBy) scroll(0) else scroll(lastScroll)
        c.lastSort = sortBy

        lastFilter = filter
    }

}
