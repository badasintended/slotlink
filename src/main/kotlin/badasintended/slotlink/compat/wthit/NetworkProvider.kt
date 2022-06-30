package badasintended.slotlink.compat.wthit

import badasintended.slotlink.block.entity.ChildBlockEntity
import badasintended.slotlink.util.toArray
import mcp.mobius.waila.api.IBlockAccessor
import mcp.mobius.waila.api.IBlockComponentProvider
import mcp.mobius.waila.api.IPluginConfig
import mcp.mobius.waila.api.IServerAccessor
import mcp.mobius.waila.api.IServerDataProvider
import mcp.mobius.waila.api.ITooltip
import mcp.mobius.waila.api.component.PairComponent
import net.minecraft.nbt.NbtCompound
import net.minecraft.text.Text

private const val posKey = "pos"

object NetworkProvider : IBlockComponentProvider, IServerDataProvider<ChildBlockEntity> {

    override fun appendBody(tooltip: ITooltip, accessor: IBlockAccessor, config: IPluginConfig) {
        if (config.getBoolean(showNetwork) && accessor.serverData.contains(posKey)) {
            val pos = accessor.serverData.getIntArray(posKey)
            tooltip.addLine(
                PairComponent(
                    Text.translatable("waila.slotlink.network.key"),
                    Text.translatable("waila.slotlink.network.value", pos[0], pos[1], pos[2])
                )
            )
        }
    }

    override fun appendServerData(
        data: NbtCompound,
        accessor: IServerAccessor<ChildBlockEntity>,
        config: IPluginConfig
    ) {
        accessor.target.network?.also { network ->
            if (!network.deleted) data.putIntArray(posKey, network.masterPos.toArray())
        }
    }

}