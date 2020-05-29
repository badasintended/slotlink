package io.gitlab.intended.storagenetworks.block;

import com.google.common.collect.ImmutableList;
import io.gitlab.intended.storagenetworks.StorageNetworks;
import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

import java.util.List;

public abstract class ModBlock extends Block {

    public static final Settings SETTINGS = FabricBlockSettings
            .of(Material.STONE)
            .breakByHand(true)
            .breakByTool(FabricToolTags.PICKAXES)
            .hardness(5F);

    public final Identifier ID;

    public ModBlock(String id) {
        super(SETTINGS);
        ID = StorageNetworks.id(id);
    }

    public ModBlock(String id, Settings settings) {
        super(settings);
        ID = StorageNetworks.id(id);
    }

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

    public static List<BlockPos> getPosAround(BlockPos pos) {
        return ImmutableList.of(
                pos.north(),
                pos.south(),
                pos.east(),
                pos.west(),
                pos.up(),
                pos.down()
        );
    }

    @Override
    public void buildTooltip(ItemStack stack, BlockView view, List<Text> tooltip, TooltipContext options) {
        tooltip.add(new TranslatableText(stack.getTranslationKey()+".tooltip"));
    }

}
