package io.gitlab.intended.storagenetworks.gui.widget

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import spinnery.widget.WTextField

@Environment(EnvType.CLIENT)
class WSearchBar : WTextField() {

    override fun draw() {
        if (isHidden) return
        renderField()
    }

}
