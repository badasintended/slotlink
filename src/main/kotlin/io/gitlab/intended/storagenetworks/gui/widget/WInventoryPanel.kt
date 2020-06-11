package io.gitlab.intended.storagenetworks.gui.widget

import io.gitlab.intended.storagenetworks.Mod
import net.minecraft.util.registry.Registry
import spinnery.widget.*
import spinnery.widget.api.Position
import spinnery.widget.api.Size
import kotlin.math.sign

class WInventoryPanel(
    private val serverSlot: ArrayList<WSlot>
) : WPanel() {

    enum class SortBy {
        NAME,
        IDENTIFIER,
        COUNT;

        companion object {
            val values = values()
        }

        fun next(): SortBy {
            return values[(this.ordinal + 1) % values.size]
        }
    }

    private val slots = arrayListOf<WSlot>()

    private val sortButton = createChild(
        { WButton() },
        Position.of(this, (7 * 18f), ((6 * 18f) + 5f)),
        Size.of(18f)
    )

    private val sortImage = createChild(
        { WStaticImage() },
        Position.of(this, ((7 * 18f) + 1f), ((6 * 18f) + 6f), 4f),
        Size.of(16f)
    )

    private var prevScroll = 0
    private var prevSort = SortBy.COUNT

    fun init() {
        setSize<WPanel>(Size.of((9 * 18f), (7 * 18f)))

        val bg = createChild({ WStaticImage() }, Position.of(this), Size.of((8 * 18f), (6 * 18f)))
        bg.setTexture<WStaticImage>(Mod.id("textures/gui/bg.png"))

        for (i in 0..47) {
            val slot = createChild({ WSlot() }, Position.of(this, ((i % 8) * 18f), ((i / 8) * 18f), 2f), Size.of(18f))
            slots.add(slot)
        }

        val scrollBar = createChild(
            { WVerticalSlider() },
            Position.of(this, ((8 * 18f) + 5f), 0f),
            Size.of(12f, (6 * 18f))
        )
        scrollBar.setMin<WVerticalSlider>(0f)
        scrollBar.setMax<WVerticalSlider>(((serverSlot.size / 8f) - 5f).coerceAtLeast(0f))

        val searchBar = createChild(
            { WInventorySearchBar() },
            Position.of(this, 0f, ((6 * 18f) + 5f)),
            Size.of(((7 * 18f) + 1f), 18f)
        )

        sortButton.setOnMouseClicked<WButton> { _, _, _, _ -> sort(prevSort.next()) }

        sort(SortBy.COUNT)
    }

    private fun scroll(v: Int) {
        prevScroll = v.coerceIn(0, ((serverSlot.size / 8) - 5).coerceAtLeast(0))
        val offset = prevScroll * 8

        var i = 0
        for (j in 0..47) {
            if (j < (serverSlot.size - offset)) {
                slots[j].setInventoryNumber<WSlot>(serverSlot[j + offset].inventoryNumber)
                slots[j].setSlotNumber<WSlot>(serverSlot[j + offset].slotNumber)
                slots[j].setBlacklist<WSlot>()
                slots[j].setHidden<WSlot>(false)
                i++
            }
        }
        if (i < 48) for (j in i..47) {
            slots[j].setInventoryNumber<WSlot>(3)
            slots[j].setSlotNumber<WSlot>(0)
            slots[j].setWhitelist<WSlot>()
            slots[j].setHidden<WSlot>(true)
        }
    }

    private fun sort(sortBy: SortBy) {
        when (sortBy) {
            SortBy.NAME -> {
                val filled = arrayListOf<WSlot>()
                val empty = arrayListOf<WSlot>()
                serverSlot.forEach { if (it.stack.isEmpty) empty.add(it) else filled.add(it) }
                filled.sortBy { it.stack.name.asString() }
                serverSlot.clear()
                serverSlot.addAll(filled)
                serverSlot.addAll(empty)
                sortImage.setTexture<WStaticImage>(Mod.id("textures/gui/name.png"))
            }
            SortBy.IDENTIFIER -> {
                val filled = arrayListOf<WSlot>()
                val empty = arrayListOf<WSlot>()
                serverSlot.forEach { if (it.stack.isEmpty) empty.add(it) else filled.add(it) }
                filled.sortBy { Registry.ITEM.getId(it.stack.item).toString() }
                serverSlot.clear()
                serverSlot.addAll(filled)
                serverSlot.addAll(empty)
                sortImage.setTexture<WStaticImage>(Mod.id("textures/gui/identifier.png"))
            }
            SortBy.COUNT -> {
                serverSlot.sortByDescending { it.stack.count }
                sortImage.setTexture<WStaticImage>(Mod.id("textures/gui/count.png"))
            }
        }
        prevSort = sortBy
        scroll(prevScroll)
    }

    override fun draw() = orderedWidgets.forEach { it.draw() }

    override fun onMouseScrolled(mouseX: Float, mouseY: Float, deltaY: Double) {
        if (this.isWithinBounds(mouseX, mouseY)) scroll(prevScroll - sign(deltaY).toInt())
        super.onMouseScrolled(mouseX, mouseY, deltaY)
    }

}
