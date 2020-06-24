package badasintended.slotlink.client.gui.widget

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import spinnery.widget.WAbstractButton
import spinnery.widget.WButton
import java.util.function.Supplier

@Environment(EnvType.CLIENT)
class WSortButton(
    private val sortFunction: () -> Unit
) : WButton() {

    override fun <W : WAbstractButton> setLowered(toggleState: Boolean): W {
        if (toggleState) Supplier(sortFunction).get()
        return super.setLowered(toggleState)
    }

}
