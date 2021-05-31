package badasintended.slotlink.client.gui.widget

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder
import net.minecraft.client.gui.widget.ClickableWidget
import net.minecraft.client.sound.SoundManager
import net.minecraft.text.LiteralText
import net.minecraft.text.Text

@Environment(EnvType.CLIENT)
abstract class NoSoundWidget(x: Int, y: Int, w: Int, h: Int, text: Text = LiteralText.EMPTY) :
    ClickableWidget(x, y, w, h, text) {

    override fun playDownSound(soundManager: SoundManager) {}

    override fun appendNarrations(builder: NarrationMessageBuilder) {}

}