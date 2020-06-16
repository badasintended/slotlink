package io.gitlab.intended.storagenetworks.gui.screen

import io.gitlab.intended.storagenetworks.block.BlockRegistry
import io.gitlab.intended.storagenetworks.common.SortBy
import io.gitlab.intended.storagenetworks.gui.container.RequestContainer
import io.gitlab.intended.storagenetworks.gui.widget.*
import io.gitlab.intended.storagenetworks.texture
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.util.registry.Registry
import spinnery.client.screen.BaseContainerScreen
import spinnery.widget.WInterface
import spinnery.widget.WSlot
import spinnery.widget.WStaticImage
import spinnery.widget.WVerticalSlider
import spinnery.widget.api.Position
import spinnery.widget.api.Size
import kotlin.math.sign
import spinnery.widget.WAbstractWidget as W

@Environment(EnvType.CLIENT)
class RequestScreen(container: RequestContainer) : BaseContainerScreen<RequestContainer>(
    BlockRegistry.REQUEST.name,
    container,
    container.player
) {
    private val c = container
    private val root: WInterface = `interface`
    private val sortedSlots = container.slotList
    private val viewedSlots = arrayListOf<WSlot>()

    private var lastScroll = 0

    private val scrollBar: WFakeScrollBar
    private val sortImage: WStaticImage

    init {

        val main = root.createChild(
            { WTexturedPanel(texture("gui/request")) },
            Position.of(0f, 0f, 0f),
            Size.of(176f, 310f)
        )
        main.setParent<W>(root)
        main.setOnAlign(W::center)
        main.center()
        root.add(main)

        // Storage Request title
        val title = main.createChild(
            { WTranslatableLabel("container.storagenetworks.request") },
            Position.of(main, 8f, 6f)
        )

        // Crafting label
        val craftingLabel = main.createChild(
            { WTranslatableLabel("container.crafting") },
            Position.of(title, 19f, 144f)
        )

        // Crafting Input slots
        val craftingSlots = WSlot.addArray(
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
        playerInvArea.onMouseReleased = { sort(c.lastSort) }

        // Inventory Panel (slot, scrollbar, searchbar, etc.)
        /*
        val invPanel = main.createChild(
            {
                WInventoryPanel(
                    c.slotList,
                    c.lastSort,
                    { c.saveLastSort(it) },
                    { c.isDeleted(it) })
            },
            Position.of(title, -1f, 11f)
        )
        invPanel.init()
         */

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
        slotArea.onMouseReleased = {
            GlobalScope.launch {
                delay(250)
                sort(c.lastSort)
            }
        }

        val searchBar = main.createChild(
            { WSearchBar() },
            Position.of(scrollArea, 0f, 111f, 1f),
            Size.of(146f, 18f)
        )

        val sortButton = main.createChild(
            { WInventorySortButton { sort(c.lastSort.next()) } },
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
            delay(50)
            sort(c.lastSort)
        }

        // root.recalculateCache()
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
                viewedSlot.setPreviewStack<WSlot>(sortedSlot.stack)
                viewedSlot.setBlacklist<WSlot>()
                viewedSlot.setHidden<WSlot>(c.isDeleted(sortedSlot.inventoryNumber))
                i++
            }
        }
        if (i < 48) for (j in i until 48) {
            viewedSlots[j].setHidden<WSlot>(true)
        }
    }

    private fun sort(sortBy: SortBy) {
        when (sortBy) {
            SortBy.NAME -> {
                val filled = arrayListOf<WSlot>()
                val empty = arrayListOf<WSlot>()
                c.slotList.forEach { if (it.stack.isEmpty) empty.add(it) else filled.add(it) }
                filled.sortBy { it.stack.name.asString() }
                sortedSlots.clear()
                sortedSlots.addAll(filled)
                sortedSlots.addAll(empty)
                sortImage.setTexture<WStaticImage>(texture("gui/name"))
            }
            SortBy.IDENTIFIER -> {
                val filled = arrayListOf<WSlot>()
                val empty = arrayListOf<WSlot>()
                c.slotList.forEach { if (it.stack.isEmpty) empty.add(it) else filled.add(it) }
                filled.sortBy { Registry.ITEM.getId(it.stack.item).toString() }
                sortedSlots.clear()
                sortedSlots.addAll(filled)
                sortedSlots.addAll(empty)
                sortImage.setTexture<WStaticImage>(texture("gui/identifier"))
            }
            SortBy.COUNT -> {
                val sorted = c.slotList.sortedByDescending { it.stack.count }
                sortedSlots.clear()
                sortedSlots.addAll(sorted)
                sortImage.setTexture<WStaticImage>(texture("gui/count"))
            }
        }
        scrollBar.setMax<WFakeScrollBar>(((sortedSlots.size / 8) - 5f).coerceAtLeast(0f))
        if (c.lastSort != sortBy) scroll(0) else scroll(lastScroll)
        c.lastSort = sortBy
        //c.saveLastSort(sortBy)
    }

}
