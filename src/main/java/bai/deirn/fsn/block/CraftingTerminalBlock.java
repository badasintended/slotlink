package bai.deirn.fsn.block;

import bai.deirn.fsn.block.entity.CraftingTerminalBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.world.BlockView;

public class CraftingTerminalBlock extends ChildBlock {

    public CraftingTerminalBlock(Settings settings) {
        super(settings);
    }

    @Override
    public BlockEntity createBlockEntity(BlockView view) {
        return new CraftingTerminalBlockEntity();
    }

}
