package badasintended.slotlink.client.compatibility.rei

import badasintended.slotlink.client.gui.screen.ModScreen
import badasintended.slotlink.screen.ModScreenHandler
import me.shedaniel.math.Rectangle
import me.shedaniel.rei.api.DisplayHelper

class SlotlinkDisplayBoundsProvider : DisplayHelper.DisplayBoundsProvider<ModScreen<ModScreenHandler>> {

    override fun getBaseSupportedClass() = ModScreen::class.java

    override fun getScreenBounds(screen: ModScreen<ModScreenHandler>): Rectangle {
        val main = screen.main
        return Rectangle(main.x.toInt(), main.y.toInt(), main.width.toInt(), main.height.toInt())
    }

}
