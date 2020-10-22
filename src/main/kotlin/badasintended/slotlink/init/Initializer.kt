package badasintended.slotlink.init

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment

interface Initializer {

    fun main() {
    }

    @Environment(EnvType.CLIENT)
    fun client() {
    }

}
