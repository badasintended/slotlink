package io.gitlab.intended.storagenetworks.block

import io.gitlab.intended.storagenetworks.Mod
import net.fabricmc.fabric.api.`object`.builder.v1.block.FabricBlockSettings
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags
import net.minecraft.block.Block
import net.minecraft.block.Material
import net.minecraft.client.item.TooltipContext
import net.minecraft.item.ItemStack
import net.minecraft.text.Text
import net.minecraft.text.TranslatableText
import net.minecraft.util.Identifier
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes
import net.minecraft.world.BlockView

abstract class ModBlock(id: String, settings: Settings = SETTINGS) : Block(settings) {

    companion object {
        val SETTINGS: Settings =
            FabricBlockSettings.of(Material.STONE).breakByHand(true).breakByTool(FabricToolTags.PICKAXES).hardness(5f)

        fun cuboid(xPos: Int, yPos: Int, zPos: Int, xSize: Int, ySize: Int, zSize: Int): VoxelShape {
            val xMin = xPos / 16.0
            val yMin = yPos / 16.0
            val zMin = zPos / 16.0
            val xMax = (xPos + xSize) / 16.0
            val yMax = (yPos + ySize) / 16.0
            val zMax = (zPos + zSize) / 16.0
            return VoxelShapes.cuboid(xMin, yMin, zMin, xMax, yMax, zMax)
        }

    }

    val id: Identifier = Mod.id(id)

    override fun buildTooltip(stack: ItemStack, view: BlockView?, tooltip: MutableList<Text>, options: TooltipContext) {
        tooltip.add(TranslatableText("${translationKey}.tooltip"))
    }

}