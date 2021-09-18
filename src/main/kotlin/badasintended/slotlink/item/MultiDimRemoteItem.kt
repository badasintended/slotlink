package badasintended.slotlink.item

import badasintended.slotlink.block.entity.MasterBlockEntity
import badasintended.slotlink.init.Blocks
import badasintended.slotlink.network.ConnectionType
import badasintended.slotlink.network.Network
import badasintended.slotlink.screen.RemoteScreenHandler
import badasintended.slotlink.storage.FilterFlags
import badasintended.slotlink.util.actionBar
import badasintended.slotlink.util.toArray
import badasintended.slotlink.util.toPos
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory
import net.minecraft.client.item.TooltipContext
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.ItemStack
import net.minecraft.item.ItemUsageContext
import net.minecraft.network.PacketByteBuf
import net.minecraft.screen.ScreenHandler
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text
import net.minecraft.text.TranslatableText
import net.minecraft.util.ActionResult
import net.minecraft.util.Formatting
import net.minecraft.util.Hand
import net.minecraft.util.Hand.MAIN_HAND
import net.minecraft.util.Hand.OFF_HAND
import net.minecraft.util.Identifier
import net.minecraft.util.TypedActionResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.registry.Registry
import net.minecraft.util.registry.RegistryKey
import net.minecraft.world.World

open class MultiDimRemoteItem(id: String = "multi_dim_remote") : ModItem(id, SETTINGS.maxCount(1)) {

    protected val baseTlKey = "item.slotlink.remote"

    protected open fun use(
        world: World,
        player: PlayerEntity,
        stack: ItemStack,
        hand: Hand,
        masterPos: BlockPos,
        masterDim: RegistryKey<World>
    ) {
        if (!world.isClient) {
            val dim = world.server!!.getWorld(masterDim)
            if (dim == null) {
                player.actionBar("${baseTlKey}.invalidDim")
            } else {
                val network = Network.get(dim, masterPos)
                if (network == null || network.deleted) {
                    player.actionBar("${baseTlKey}.masterNotFound")
                } else {
                    network.get(ConnectionType.MASTER).firstOrNull()?.also {
                        player.openHandledScreen(ScreenHandlerFactory(dim, it, hand == OFF_HAND))
                    }
                }
            }
        }
    }

    override fun use(world: World, player: PlayerEntity, hand: Hand): TypedActionResult<ItemStack> {
        val stack = when (hand) {
            MAIN_HAND -> player.mainHandStack
            OFF_HAND -> player.offHandStack
        }

        val network = stack.getSubNbt("network")

        if (network == null) {
            player.actionBar("${baseTlKey}.hasNoMaster")
        } else {
            val pos = network.getIntArray("pos").toPos()
            val dim = RegistryKey.of(Registry.WORLD_KEY, Identifier(network.getString("dim")))
            use(world, player, stack, hand, pos, dim)
        }

        return TypedActionResult.success(stack)
    }

    override fun useOnBlock(context: ItemUsageContext): ActionResult {
        val player = context.player
        val stack = context.stack
        val world = context.world
        val pos = context.blockPos
        val block = world.getBlockState(pos).block

        if (block == Blocks.MASTER && player != null && player.isSneaking) {
            val dimId = world.registryKey.value.toString()
            val tag = stack.getOrCreateSubNbt("network")
            tag.putIntArray("pos", pos.toArray())
            tag.putString("dim", dimId)
            player.actionBar("${baseTlKey}.linked", pos.x, pos.y, pos.z, dimId)
            return ActionResult.SUCCESS
        }

        return ActionResult.PASS
    }

    override fun appendTooltip(stack: ItemStack, world: World?, tooltip: MutableList<Text>, context: TooltipContext) {
        super.appendTooltip(stack, world, tooltip, context)

        tooltip.add(TranslatableText("${baseTlKey}.useTooltip").formatted(Formatting.GRAY))

        val tag = stack.orCreateNbt
        if (tag.contains("network")) {
            val network = tag.getCompound("network")
            val pos = network.getIntArray("pos")
            val dim = network.getString("dim")
            tooltip.add(
                TranslatableText("${baseTlKey}.info", pos[0], pos[1], pos[2], dim).formatted(
                    Formatting.DARK_PURPLE
                )
            )
        }
    }

    class ScreenHandlerFactory(
        masterWorld: World,
        private val master: MasterBlockEntity,
        private val offHand: Boolean
    ) : ExtendedScreenHandlerFactory {

        private val storages = master.getStorages(masterWorld, FilterFlags.INSERT, true)

        override fun createMenu(syncId: Int, inv: PlayerInventory, player: PlayerEntity): ScreenHandler {
            val handler = RemoteScreenHandler(syncId, inv, storages, master, offHand)
            master.watchers.add(handler)
            master.markForcedChunks()
            return handler
        }

        override fun writeScreenOpeningData(player: ServerPlayerEntity, buf: PacketByteBuf) {
            buf.writeBoolean(offHand)
        }

        override fun getDisplayName() = TranslatableText("container.slotlink.request")

    }

}
