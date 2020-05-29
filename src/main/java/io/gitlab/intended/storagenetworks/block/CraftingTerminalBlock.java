package io.gitlab.intended.storagenetworks.block;

import io.gitlab.intended.storagenetworks.block.entity.CraftingTerminalBlockEntity;
import io.gitlab.intended.storagenetworks.inventory.ModInventory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class CraftingTerminalBlock extends ChildBlock {

    public CraftingTerminalBlock(String id) {
        super(id);
    }

    @Override
    public BlockEntity createBlockEntity(BlockView view) {
        return new CraftingTerminalBlockEntity();
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        return ModInventory.open(this, player);
    }

}
