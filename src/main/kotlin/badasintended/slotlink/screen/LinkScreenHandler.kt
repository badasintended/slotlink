package badasintended.slotlink.screen

import badasintended.slotlink.block.entity.ConnectorCableBlockEntity
import badasintended.slotlink.init.Screens
import badasintended.slotlink.util.readFilter
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.ItemStack
import net.minecraft.network.PacketByteBuf
import net.minecraft.screen.*
import net.minecraft.screen.slot.Slot

@Suppress("LeakingThis")
open class LinkScreenHandler(
    syncId: Int,
    val playerInv: PlayerInventory,
    var priority: Int,
    var blacklist: Boolean,
    val filter: MutableList<Pair<ItemStack, Boolean>>,
    protected val context: ScreenHandlerContext
) : ScreenHandler(null, syncId) {

    constructor(syncId: Int, playerInv: PlayerInventory, buf: PacketByteBuf) : this(
        syncId, playerInv, buf.readVarInt(), buf.readBoolean(), buf.readFilter(), ScreenHandlerContext.EMPTY
    )

    init {
        for (m in 0 until 3) for (l in 0 until 9) {
            addSlot(Slot(playerInv, l + m * 9 + 9, 8 + l * 18, 84 + m * 18))
        }
        for (m in 0 until 9) {
            addSlot(Slot(playerInv, m, 8 + m * 18, 142))
        }
    }

    fun filterSlotClick(i: Int, button: Int) {
        if (button !in 0..1) return
        val stack = playerInv.cursorStack.copy().apply { count = 1 }
        if (button != 1) stack.tag = null
        filter[i] = stack to ((button == 1) and !playerInv.cursorStack.isEmpty)
    }

    override fun close(player: PlayerEntity) {
        super.close(player)
        context.run { world, pos ->
            val be = world.getBlockEntity(pos)
            if (be is ConnectorCableBlockEntity) {
                be.priority = priority
                be.isBlackList = blacklist
                be.markDirty()
            }
        }
    }

    override fun canUse(player: PlayerEntity) = true

    override fun getType(): ScreenHandlerType<*> = Screens.LINK

}
