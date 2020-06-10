package io.gitlab.intended.storagenetworks.gui.widget

import spinnery.widget.WSlot
import spinnery.widget.api.Action
import java.util.function.Supplier

class WInventorySlot(
    private val emptyList: ArrayList<WInventorySlot>? = null,
    private val filledList: ArrayList<WInventorySlot>? = null,
    private val refreshFunction: (() -> Unit)? = null,
    private val craftingSlots: HashSet<WCraftingInputSlot>? = null
) : WSlot() {

    constructor(craftingSlots: HashSet<WCraftingInputSlot>?) : this(null, null, null, craftingSlots)

    fun check() {
        if (stack.isEmpty) {
            filledList!!.remove(this)
            emptyList!!.add(this)
        } else {
            emptyList!!.remove(this)
            filledList!!.add(this)
        }
        emptyList.sortedBy { it.inventoryNumber * (it.slotNumber + 1) }
        filledList.sortedBy { it.inventoryNumber * (it.slotNumber + 1) }
    }

    override fun consume(action: Action?, subtype: Action.Subtype?) {
        if (refreshFunction != null) Supplier(refreshFunction).get()
        super.consume(action, subtype)
    }

}
