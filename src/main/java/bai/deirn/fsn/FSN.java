package bai.deirn.fsn;

import bai.deirn.fsn.block.FSNBlocks;
import bai.deirn.fsn.block.entity.FSNBlockEntities;
import bai.deirn.fsn.item.FSNItems;
import net.fabricmc.api.ModInitializer;

public class FSN implements ModInitializer {

    @Override
    public void onInitialize() {
        FSNBlocks.init();
        FSNBlockEntities.init();
        FSNItems.init();
    }

}
