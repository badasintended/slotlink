package bai.deirn.fsn.block;

import bai.deirn.fsn.block.entity.ProcessingTerminalBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.world.BlockView;

public class ProcessingTerminalBlock extends ChildBlock {

    public ProcessingTerminalBlock(Settings settings) {
        super(settings);
    }

    @Override
    public BlockEntity createBlockEntity(BlockView view) {
        return new ProcessingTerminalBlockEntity();
    }

}
