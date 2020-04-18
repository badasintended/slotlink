package deirn.minecraft.fsn.block;

import deirn.minecraft.fsn.block.FSNBlock;
import deirn.minecraft.fsn.item.ItemUtils;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;

public class BlockUtils {

    // Just so i could make shape with the values blockbench shows.
    public static VoxelShape cuboid(double xPos, double yPos, double zPos, double xSize, double ySize, double zSize) {
        double xMin = xPos / 16d;
        double yMin = yPos / 16d;
        double zMin = zPos / 16d;
        double xMax = (xPos + xSize) / 16d;
        double yMax = (yPos + ySize) / 16d;
        double zMax = (zPos + zSize) / 16d;

        return VoxelShapes.cuboid(xMin, yMin, zMin, xMax, yMax, zMax);
    }

    public static void register(FSNBlock... blocks) {
        for (FSNBlock block : blocks) {
            Registry.register(Registry.BLOCK, block.getId(), block);
            Registry.register(Registry.ITEM, block.getId(), new BlockItem(block, new Item.Settings().group(ItemUtils.ITEM_GROUP)));
        }
    }

}
