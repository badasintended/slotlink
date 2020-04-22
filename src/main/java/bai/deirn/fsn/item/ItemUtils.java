package bai.deirn.fsn.item;

import bai.deirn.fsn.FSN;
import bai.deirn.fsn.block.FSNBlocks;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public class ItemUtils {

    public static final ItemGroup ITEM_GROUP = FabricItemGroupBuilder.build(
            FSN.id("item_group"), () -> new ItemStack(FSNBlocks.CONTROLLER)
    );

}
