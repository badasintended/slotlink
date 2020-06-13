package io.gitlab.intended.storagenetworks.gui.widget

import spinnery.widget.WSlot

class WViewedSlot(
    private val isDeleted: (Int) -> Boolean
) : WSlot() {

    override fun tick() {
        if (!isHidden) if (isDeleted.invoke(inventoryNumber)) {
            setHidden<WSlot>(true)
            setWhitelist<WSlot>()
        }
    }

}
