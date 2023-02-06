package badasintended.slotlink.compat.trinkets

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.entity.player.PlayerEntity

@Environment(EnvType.CLIENT)
object TrinketsAccess {

    var tryOpenRemote = { _: PlayerEntity -> false }

}