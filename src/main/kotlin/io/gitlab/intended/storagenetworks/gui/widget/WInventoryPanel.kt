package io.gitlab.intended.storagenetworks.gui.widget

import net.minecraft.inventory.Inventory
import spinnery.widget.WSlot
import spinnery.widget.WVerticalScrollableContainer
import spinnery.widget.api.Position
import spinnery.widget.api.Size
import kotlin.math.ceil
import spinnery.widget.WAbstractWidget as W

class WInventoryPanel(
    private val invMap: HashMap<Int, Inventory>
) : WVerticalScrollableContainer() {

    fun init() {

        setHasFade<WInventoryPanel>(true)
        val emptyList = arrayListOf<WInventorySlot>()
        val filledList = arrayListOf<WInventorySlot>()
        //val this: WPanel = createChild({ WPanel() }, Position.of(this, 0f, 0f, 1f))
        //val bg: WStaticImage = this.createChild { WStaticImage() }

        /*
        invMap.forEach { (invN, inv) ->
            for (slotN in 0 until (inv.invSize)) {
                val slot = panel.createChild { WInventorySlot(emptyList, filledList) }
                slot.setSize<W>(Size.of(18))
                slot.setInventoryNumber<WSlot>(invN)
                slot.setSlotNumber<WSlot>(slotN)
                slot.check()
            }
        }

        emptyList.sortedBy { it.inventoryNumber * (it.slotNumber + 1) }
        filledList.sortedBy { it.inventoryNumber * (it.slotNumber + 1) }

        var prev = 0
        emptyList.forEach {
            it.setPosition<W>(Position.of(panel, ((prev % 8) * 18), ((prev / 8) * 18), 2))
            prev++
        }
        filledList.forEach {
            it.setPosition<W>(Position.of(panel, ((prev % 8) * 18), ((prev / 8) * 18), 2))
            prev++
        }

        val panelY = ceil(prev / 8f).toInt().coerceAtLeast(6)

        panel.setSize<W>(Size.of((8 * 18), (panelY * 18)))
         */
        //bg.setTexture<WStaticImage>(Mod.id("textures/gui/bg.png"))
        /*
        bg.setSize<W>(Size.of(panel))
        bg.setPosition<W>(Position.of(panel, 0,0,0))
         */
        //bg.setPosition<W>(Position.of(this, 0f, 0f, 0f))
        refresh(emptyList, filledList, this, true)
    }

    private fun refresh(
        emptyList: ArrayList<WInventorySlot>,
        filledList: ArrayList<WInventorySlot>,
        panel: WInventoryPanel,
        //bg: WStaticImage,
        init: Boolean = false
    ) {
        if (!init) {
            emptyList.forEach { panel.remove(it) }
            filledList.forEach { panel.remove(it) }
            emptyList.clear()
            filledList.clear()
        }

        invMap.forEach { (invN, inv) ->
            for (slotN in 0 until (inv.invSize)) {
                val slot = panel.createChild {
                    WInventorySlot(emptyList, filledList, { refresh(emptyList, filledList, panel) })
                }
                slot.setSize<W>(Size.of(18f))
                slot.setInventoryNumber<WSlot>(invN)
                slot.setSlotNumber<WSlot>(slotN)
                slot.check()
            }
        }

        emptyList.sortedBy { it.inventoryNumber * (it.slotNumber + 1) }
        filledList.sortedByDescending { it.stack.count }
        var prev = 0
        filledList.forEach {
            it.setPosition<W>(Position.of(panel, ((prev % 8) * 18f), ((prev / 8) * 18f), 3f))
            prev++
        }
        emptyList.forEach {
            it.setPosition<W>(Position.of(panel, ((prev % 8) * 18f), ((prev / 8) * 18f), 3f))
            prev++
        }
        val panelY = ceil(prev / 8f).coerceAtLeast(6f)
        //panel.setSize<W>(Size.of((8f * 18f), (panelY * 18f)))
        //bg.setSize<W>(Size.of((8f * 18f), (panelY * 18f)))
        //`interface`.container.sendContentUpdates()
        onLayoutChange()
    }

}
