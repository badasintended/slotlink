package io.gitlab.intended.storagenetworks.block

import io.gitlab.intended.storagenetworks.block.entity.CraftingTerminalBlockEntity
import io.gitlab.intended.storagenetworks.inventory.ModInventory
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.BlockView
import net.minecraft.world.World

class CraftingTerminalBlock(id: String) : ChildBlock(id) {

    override fun createBlockEntity(view: BlockView): BlockEntity = CraftingTerminalBlockEntity()

    override fun onUse(
        state: BlockState,
        world: World,
        pos: BlockPos,
        player: PlayerEntity,
        hand: Hand,
        hit: BlockHitResult
    ): ActionResult = ModInventory.open(this, player)

}