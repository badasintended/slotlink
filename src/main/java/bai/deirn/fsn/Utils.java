package bai.deirn.fsn;

import bai.deirn.fsn.block.FSNBlocks;
import com.google.common.collect.ImmutableList;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;

import java.util.List;
import java.util.function.Supplier;
import java.util.logging.Logger;

public abstract class Utils {

    public static final String MOD_ID = "fsn";
    public static final Logger LOGGER = Logger.getLogger(MOD_ID);

    private static final ItemGroup ITEM_GROUP = FabricItemGroupBuilder.build(
            id("item_group"), () -> new ItemStack(FSNBlocks.MASTER)
    );

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

    public static Item.Settings getItemSettings() {
        return new Item.Settings().group(ITEM_GROUP);
    }

    // Block registry
    public static void register(String id, Block block) {
        Registry.register(Registry.BLOCK, id(id), block);
        register(id, new BlockItem(block, getItemSettings()));
    }

    // BlockEntityType registry
    public static void register(String id, BlockEntityType<? extends BlockEntity> blockEntityType) {
        Registry.register(Registry.BLOCK_ENTITY_TYPE, id(id), blockEntityType);
    }

    // Item registry
    public static void register(String id, Item item) {
        Registry.register(Registry.ITEM, id(id), item);
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

    public static Identifier id(String path) {
        return new Identifier(MOD_ID, path);
    }

}
