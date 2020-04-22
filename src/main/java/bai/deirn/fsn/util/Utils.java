package bai.deirn.fsn.util;

import bai.deirn.fsn.FSN;
import bai.deirn.fsn.item.ItemUtils;
import com.google.common.collect.ImmutableList;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;

import java.util.List;
import java.util.function.Supplier;

public class Utils {

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

    // Block registry
    public static void register(String id, Block block) {
        Registry.register(Registry.BLOCK, FSN.id(id), block);
        Registry.register(Registry.ITEM, FSN.id(id), new BlockItem(block, new Item.Settings().group(ItemUtils.ITEM_GROUP)));
    }

    // BlockEntityType registry
    public static void register(String id, BlockEntityType<? extends BlockEntity> blockEntityType) {
        Registry.register(Registry.BLOCK_ENTITY_TYPE, FSN.id(id), blockEntityType);
    }

    public static List<BlockPos> getPosAround(BlockPos pos){
        return ImmutableList.of(
                pos.north(),
                pos.south(),
                pos.east(),
                pos.west(),
                pos.up(),
                pos.down()
        );
    }

    public static BlockEntityType<?> createBlockEntity(Supplier<? extends BlockEntity> supplier, Block block) {
        return BlockEntityType.Builder.create(supplier, block).build(null);
    }

}
