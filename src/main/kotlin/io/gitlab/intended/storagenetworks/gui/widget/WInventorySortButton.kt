package io.gitlab.intended.storagenetworks.gui.widget

import spinnery.widget.WAbstractButton
import spinnery.widget.WButton
import java.util.function.Supplier

class WInventorySortButton(
    private val sortFunction: () -> Unit
) : WButton() {

    override fun <W : WAbstractButton> setLowered(toggleState: Boolean): W {
        if (toggleState) Supplier(sortFunction).get()
        return super.setLowered(toggleState)
    }

}
