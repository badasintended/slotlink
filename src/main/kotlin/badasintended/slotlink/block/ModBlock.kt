package badasintended.slotlink.block

import badasintended.slotlink.util.modId
import net.fabricmc.fabric.api.`object`.builder.v1.block.FabricBlockSettings
import net.minecraft.block.BlockRenderType
import net.minecraft.block.BlockState
import net.minecraft.block.BlockWithEntity
import net.minecraft.block.Material

abstract class ModBlock(id: String, settings: Settings = SETTINGS) : BlockWithEntity(settings) {

    companion object {

        val SETTINGS: Settings = FabricBlockSettings
            .of(Material.STONE)
            .breakByHand(true)
            .hardness(5f)

    }

    val id = modId(id)

    override fun getRenderType(state: BlockState?): BlockRenderType {
        return BlockRenderType.MODEL
    }

}
