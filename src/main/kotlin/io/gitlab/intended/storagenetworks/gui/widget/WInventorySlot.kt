package io.gitlab.intended.storagenetworks.gui.widget

import spinnery.widget.WSlot
import spinnery.widget.api.Action

class WInventorySlot : WSlot() {

    override fun consume(action: Action?, subtype: Action.Subtype?) {
        `interface`.container.inventories[inventoryNumber]?.markDirty()
        super.consume(action, subtype)
    }

}
