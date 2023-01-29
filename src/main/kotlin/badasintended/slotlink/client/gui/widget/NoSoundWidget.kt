package badasintended.slotlink.client.gui.widget

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder
import net.minecraft.client.gui.widget.ClickableWidget
import net.minecraft.client.sound.SoundManager
import net.minecraft.screen.ScreenTexts
import net.minecraft.text.Text

@Environment(EnvType.CLIENT)
abstract class NoSoundWidget(x: Int, y: Int, w: Int, h: Int, text: Text = ScreenTexts.EMPTY) :
    ClickableWidget(x, y, w, h, text) {

    override fun playDownSound(soundManager: SoundManager) {}

    override fun appendClickableNarrations(builder: NarrationMessageBuilder?) {}

}