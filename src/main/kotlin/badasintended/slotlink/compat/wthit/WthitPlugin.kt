package badasintended.slotlink.compat.wthit

import badasintended.slotlink.block.entity.ChildBlockEntity
import badasintended.slotlink.util.modId
import mcp.mobius.waila.api.IRegistrar
import mcp.mobius.waila.api.IWailaPlugin
import mcp.mobius.waila.api.TooltipPosition

val showNetwork = modId("show_network")

@Suppress("unused")
class WthitPlugin : IWailaPlugin {

    override fun register(registrar: IRegistrar) {
        registrar.addSyncedConfig(showNetwork, true)
        registrar.addComponent(NetworkProvider, TooltipPosition.BODY, ChildBlockEntity::class.java)
        registrar.addBlockData(NetworkProvider, ChildBlockEntity::class.java)
    }

}