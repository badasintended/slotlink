package badasintended.slotlink.client.gui.widget

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.text.TranslatableText
import sbinnery.widget.WStaticText

@Environment(EnvType.CLIENT)
class WTranslatableLabel(key: String, vararg args: Any) : WLabel() {

    init {
        setText<WStaticText>(TranslatableText(key, *args))
    }

}
