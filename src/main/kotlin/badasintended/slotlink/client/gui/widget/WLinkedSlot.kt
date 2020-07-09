package badasintended.slotlink.client.gui.widget

import badasintended.spinnery.widget.WAbstractWidget
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.item.ItemStack

@Environment(EnvType.CLIENT)
class WLinkedSlot : WAbstractWidget() {

    var invNumber = 0
    var slotNumber = 0
    var stack: ItemStack = ItemStack.EMPTY
    var copiedStack: ItemStack = ItemStack.EMPTY

}
