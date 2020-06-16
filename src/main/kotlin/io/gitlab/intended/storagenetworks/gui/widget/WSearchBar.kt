package io.gitlab.intended.storagenetworks.gui.widget

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import spinnery.widget.WTextField

@Environment(EnvType.CLIENT)
class WSearchBar(
    private val setSearch: (String) -> Unit
) : WTextField() {

    override fun draw() {
        if (isHidden) return
        renderField()
    }

    override fun onKeyReleased(keyCode: Int, character: Int, keyModifier: Int) {
        super.onKeyReleased(keyCode, character, keyModifier)
        setSearch.invoke(text)
    }

}
