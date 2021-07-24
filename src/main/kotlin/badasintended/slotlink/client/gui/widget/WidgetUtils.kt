package badasintended.slotlink.client.gui.widget

interface KeyGrabber {

    fun onKey(keyCode: Int, scanCode: Int, modifiers: Int): Boolean

}

interface CharGrabber {

    fun onChar(chr: Char, modifiers: Int): Boolean

}