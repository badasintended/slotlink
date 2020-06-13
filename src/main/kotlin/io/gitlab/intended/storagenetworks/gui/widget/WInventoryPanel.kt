package io.gitlab.intended.storagenetworks.gui.widget

import io.gitlab.intended.storagenetworks.Mod
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.minecraft.util.registry.Registry
import spinnery.widget.WPanel
import spinnery.widget.WSlot
import spinnery.widget.WStaticImage
import spinnery.widget.WVerticalSlider
import spinnery.widget.api.Position
import spinnery.widget.api.Size
import kotlin.math.sign

class WInventoryPanel(
    private val serverSlots: ArrayList<WSlot>,
    private var lastSort: SortBy,
    private val setLastSortFunction: (SortBy) -> Unit,
    private val isDeleted: (Int) -> Boolean
) : WPanel() {

    enum class SortBy {
        NAME,
        IDENTIFIER,
        COUNT;

        companion object {
            val values = values()
            fun of(i: Int) = values[i.coerceIn(0, 2)]
        }

        fun next(): SortBy {
            return values[(this.ordinal + 1) % values.size]
        }
    }

    private val sortedSlots = serverSlots
    private val viewedSlots = arrayListOf<WSlot>()

    private val sortImage = createChild(
        { WStaticImage() },
        Position.of(this, ((7 * 18f) + 1f), ((6 * 18f) + 5f), 4f),
        Size.of(16f)
    )

    private val scrollBar = createChild(
        { WFakeScrollBar { scroll(it) } },
        Position.of(this, ((8 * 18f) + 4f), 0f),
        Size.of(14f, (6 * 18f))
    )

    private var lastScroll = 0

    init {
        sortImage.setTexture<WStaticImage>(Mod.id("textures/gui/count.png"))
    }

    fun init() {
        setSize<WPanel>(Size.of((9 * 18f), (7 * 18f) + 4f))

        val scrollArea = createChild(
            { WMouseArea() },
            Position.of(this),
            Size.of((9 * 18f), (6 * 18f))
        )
        scrollArea.onMouseScrolled = { scroll(lastScroll - sign(it).toInt()) }

        if (serverSlots.size == 0) {
            val warning = createChild({ WStaticImage() }, Position.of(this, 56f, 38f, 4f), Size.of(32f))
            warning.setTexture<WStaticImage>(Mod.id("textures/gui/warning.png"))
        }

        for (i in 0..47) {
            val slot = createChild(
                { WViewedSlot(isDeleted) },
                Position.of(this, ((i % 8) * 18f), ((i / 8) * 18f), 2f),
                Size.of(18f)
            )
            slot.setWhitelist<WSlot>()
            slot.setHidden<WSlot>(true)
            viewedSlots.add(slot)
        }

        val slotArea = createChild(
            { WMouseArea() },
            Position.of(this),
            Size.of((8 * 18f), (6 * 18f))
        )
        slotArea.onMouseReleased = {
            GlobalScope.launch {
                delay(250)
                sort()
            }
        }

        scrollBar.setMin<WVerticalSlider>(0f)
        scrollBar.setMax<WVerticalSlider>(((serverSlots.size / 8) - 5f).coerceAtLeast(0f))

        val searchBar = createChild(
            { WInventorySearchBar() },
            Position.of(this, 0f, ((6 * 18f) + 4f)),
            Size.of(((7 * 18f) - 4f), 18f)
        )

        val sortButton = createChild(
            { WInventorySortButton { sort(lastSort.next()) } },
            Position.of(this, (7 * 18f), ((6 * 18f) + 4f)),
            Size.of(18f)
        )

        GlobalScope.launch {
            delay(50)
            sort()
        }
    }

    private fun scroll(v: Int) {
        val max = ((sortedSlots.size / 8) - 5).coerceAtLeast(0)
        lastScroll = v.coerceIn(0, max)
        scrollBar.setProgress<WVerticalSlider>((max - lastScroll).toFloat())
        val offset = lastScroll * 8

        var i = 0
        for (j in 0..47) {
            if (j < (sortedSlots.size - offset)) {
                viewedSlots[j].setInventoryNumber<WSlot>(sortedSlots[j + offset].inventoryNumber)
                viewedSlots[j].setSlotNumber<WSlot>(sortedSlots[j + offset].slotNumber)
                viewedSlots[j].setBlacklist<WSlot>()
                viewedSlots[j].setHidden<WSlot>(false)
                i++
            }
        }
        if (i < 48) for (j in i..47) {
            viewedSlots[j].setInventoryNumber<WSlot>(3)
            viewedSlots[j].setSlotNumber<WSlot>(0)
            viewedSlots[j].setWhitelist<WSlot>()
            viewedSlots[j].setHidden<WSlot>(true)
        }
    }

    private fun sort(sortBy: SortBy) {
        when (sortBy) {
            SortBy.NAME -> {
                val filled = arrayListOf<WSlot>()
                val empty = arrayListOf<WSlot>()
                serverSlots.forEach { if (it.stack.isEmpty) empty.add(it) else filled.add(it) }
                filled.sortBy { it.stack.name.asString() }
                sortedSlots.clear()
                sortedSlots.addAll(filled)
                sortedSlots.addAll(empty)
                sortImage.setTexture<WStaticImage>(Mod.id("textures/gui/name.png"))
            }
            SortBy.IDENTIFIER -> {
                val filled = arrayListOf<WSlot>()
                val empty = arrayListOf<WSlot>()
                serverSlots.forEach { if (it.stack.isEmpty) empty.add(it) else filled.add(it) }
                filled.sortBy { Registry.ITEM.getId(it.stack.item).toString() }
                sortedSlots.clear()
                sortedSlots.addAll(filled)
                sortedSlots.addAll(empty)
                sortImage.setTexture<WStaticImage>(Mod.id("textures/gui/identifier.png"))
            }
            SortBy.COUNT -> {
                val sorted = serverSlots.sortedByDescending { it.stack.count }
                sortedSlots.clear()
                sortedSlots.addAll(sorted)
                sortImage.setTexture<WStaticImage>(Mod.id("textures/gui/count.png"))
            }
        }
        if (lastSort != sortBy) scroll(0) else scroll(lastScroll)
        lastSort = sortBy
        setLastSortFunction.invoke(sortBy)
    }

    fun sort() = sort(lastSort)

    override fun draw() = orderedWidgets.forEach { it.draw() }

}
