package badasintended.slotlink.compat.wthit

import badasintended.slotlink.block.entity.ChildBlockEntity
import badasintended.slotlink.util.toArray
import mcp.mobius.waila.api.IBlockAccessor
import mcp.mobius.waila.api.IBlockComponentProvider
import mcp.mobius.waila.api.IPluginConfig
import mcp.mobius.waila.api.IServerDataProvider
import mcp.mobius.waila.api.ITooltip
import net.minecraft.nbt.NbtCompound
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.TranslatableText
import net.minecraft.util.Formatting
import net.minecraft.world.World

private const val posKey = "pos"

object NetworkProvider : IBlockComponentProvider, IServerDataProvider<ChildBlockEntity> {

    override fun appendBody(tooltip: ITooltip, accessor: IBlockAccessor, config: IPluginConfig) {
        if (config.getBoolean(showNetwork) && accessor.serverData.contains(posKey)) {
            val pos = accessor.serverData.getIntArray(posKey)
            tooltip.addPair(
                TranslatableText("waila.slotlink.network.key").formatted(Formatting.GRAY),
                TranslatableText("waila.slotlink.network.value", pos[0], pos[1], pos[2]).formatted(Formatting.GRAY)
            )
        }
    }

    override fun appendServerData(data: NbtCompound, player: ServerPlayerEntity, world: World, node: ChildBlockEntity) {
        node.network?.also { network ->
            data.putIntArray(posKey, network.masterPos.toArray())
        }
    }

}