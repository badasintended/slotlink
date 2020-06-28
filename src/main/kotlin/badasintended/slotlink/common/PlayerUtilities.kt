package badasintended.slotlink.common

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.text.TranslatableText
import net.minecraft.world.World

fun sendActionBar(world: World, player: PlayerEntity, key: String, vararg args: Any) {
    if (!world.isClient) player.sendMessage(TranslatableText(key, *args), true)
}
