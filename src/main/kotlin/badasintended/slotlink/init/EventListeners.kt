package badasintended.slotlink.init

import badasintended.slotlink.block.BlockAttackAware
import badasintended.slotlink.config.config
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.fabricmc.fabric.api.event.player.AttackBlockCallback
import net.minecraft.util.ActionResult

object EventListeners : Initializer {

    override fun main() {
        ServerLifecycleEvents.END_DATA_PACK_RELOAD.register { _, _, _ ->
            config.invalidate()
        }

        AttackBlockCallback.EVENT.register { player, world, hand, pos, direction ->
            val state = world.getBlockState(pos)
            val block = state.block
            if (block is BlockAttackAware) block.onBlockAttack(state, world, pos, player, hand, direction)
            else ActionResult.PASS
        }
    }

}