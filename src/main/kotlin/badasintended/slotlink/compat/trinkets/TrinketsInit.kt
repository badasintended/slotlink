package badasintended.slotlink.compat.trinkets

import badasintended.slotlink.client.util.c2s
import badasintended.slotlink.init.Initializer
import badasintended.slotlink.init.Items
import badasintended.slotlink.init.Packets
import badasintended.slotlink.item.RemoteItem
import badasintended.slotlink.util.int
import badasintended.slotlink.util.log
import badasintended.slotlink.util.modId
import badasintended.slotlink.util.modLoaded
import badasintended.slotlink.util.string
import dev.emi.trinkets.api.TrinketsApi
import kotlin.jvm.optionals.getOrNull
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment

object TrinketsInit : Initializer {

    private val OPEN_REMOTE_PACKET = modId("open_remote_trinkets")

    override fun main() = modLoaded("trinkets") {
        log.info("[slotlink] Loaded trinkets compatibility")

        TrinketsApi.registerTrinket(Items.LIMITED_REMOTE, RemoteTrinket)
        TrinketsApi.registerTrinket(Items.UNLIMITED_REMOTE, RemoteTrinket)
        TrinketsApi.registerTrinket(Items.MULTI_DIM_REMOTE, RemoteTrinket)

        Packets.registerServerReceiver(OPEN_REMOTE_PACKET) { server, player, buf ->
            val slotGroup = buf.string
            val slotName = buf.string
            val slotIndex = buf.int

            server.execute r@{
                val component = TrinketsApi.getTrinketComponent(player).getOrNull() ?: return@r
                val inventory = component.inventory[slotGroup]?.get(slotName) ?: return@r
                val stack = inventory.getStack(slotIndex)
                val item = stack.item
                if (item is RemoteItem) {
                    item.use(player.world, player, stack, -1)
                }
            }
        }
    }

    @Environment(EnvType.CLIENT)
    override fun client() = modLoaded("trinkets") {
        TrinketsAccess.tryOpenRemote = { player ->
            player as RemoteTrinket.Holder

            val slot = player.remoteTrinketSlot
            if (slot != null && slot.stack.item is RemoteItem) {
                c2s(OPEN_REMOTE_PACKET) {
                    val type = slot.inventory.slotType
                    string(type.group)
                    string(type.name)
                    int(slot.index)
                }
                true
            } else false
        }
    }

}
