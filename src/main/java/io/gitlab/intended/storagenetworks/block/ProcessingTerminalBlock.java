package io.gitlab.intended.storagenetworks.block;

import io.gitlab.intended.storagenetworks.block.entity.ProcessingTerminalBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.world.BlockView;

public class ProcessingTerminalBlock extends ChildBlock {

    public ProcessingTerminalBlock(String id) {
        super(id);
    }

    @Override
    public BlockEntity createBlockEntity(BlockView view) {
        return new ProcessingTerminalBlockEntity();
    }

}
