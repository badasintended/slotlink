package badasintended.slotlink.item

import badasintended.slotlink.block.MasterBlock
import badasintended.slotlink.block.entity.MasterBlockEntity
import badasintended.slotlink.screen.RemoteScreenHandler
import badasintended.slotlink.util.*
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory
import net.minecraft.client.item.TooltipContext
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.ItemStack
import net.minecraft.item.ItemUsageContext
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.PacketByteBuf
import net.minecraft.screen.ScreenHandler
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text
import net.minecraft.text.TranslatableText
import net.minecraft.util.*
import net.minecraft.util.Hand.MAIN_HAND
import net.minecraft.util.Hand.OFF_HAND
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
                val master = dim.getBlockEntity(masterPos)
                if (master !is MasterBlockEntity) {
                    player.actionBar("${baseTlKey}.masterNotFound")
                } else {
                    player.openHandledScreen(
                        ScreenHandlerFactory(
                            dim, master, Sort.of(stack.orCreateTag.getInt("lastSort")), hand == OFF_HAND
                        )
                    )
                }
            }
        }
    }

    override fun use(world: World, player: PlayerEntity, hand: Hand): TypedActionResult<ItemStack> {
        val stack = when (hand) {
            MAIN_HAND -> player.mainHandStack
            OFF_HAND -> player.offHandStack
        }

        val masterPosTag = stack.orCreateTag.getCompound("masterPos")
        val masterDim = RegistryKey.of(Registry.DIMENSION, Identifier(stack.orCreateTag.getString("masterDim")))

        if (masterPosTag == CompoundTag()) {
            player.actionBar("${baseTlKey}.hasNoMaster")
        } else use(world, player, stack, hand, masterPosTag.toPos(), masterDim)

        return TypedActionResult.success(stack)
    }

    override fun useOnBlock(context: ItemUsageContext): ActionResult {
        val player = context.player
        val stack = context.stack
        val world = context.world
        val pos = context.blockPos
        val dimId = world.registryKey.value.toString()

        val block = world.getBlockState(pos).block
        if (block is MasterBlock) {
            if (player != null) if (player.isSneaking) {
                stack.orCreateTag.put("masterPos", pos.toTag())
                stack.orCreateTag.putString("masterDim", dimId)
                player.actionBar("${baseTlKey}.linked", pos.x, pos.y, pos.z, dimId)
                return ActionResult.SUCCESS
            }
        }

        return ActionResult.PASS
    }

    override fun appendTooltip(stack: ItemStack, world: World?, tooltip: MutableList<Text>, context: TooltipContext) {
        super.appendTooltip(stack, world, tooltip, context)

        tooltip.add(TranslatableText("${baseTlKey}.useTooltip").formatted(Formatting.GRAY))

        val masterPosTag = stack.orCreateTag.getCompound("masterPos")
        if (masterPosTag != CompoundTag()) {
            val masterPos = masterPosTag.toPos()
            val masterDim = Identifier(stack.orCreateTag.getString("masterDim"))
            tooltip.add(
                TranslatableText("${baseTlKey}.info", masterPos.x, masterPos.y, masterPos.z, masterDim).formatted(
                    Formatting.DARK_PURPLE
                )
            )
        }
    }

    class ScreenHandlerFactory(
        masterWorld: World,
        private val master: MasterBlockEntity,
        private val lastSort: Sort,
        private val offHand: Boolean
    ) : ExtendedScreenHandlerFactory {

        private val inventories = master.getInventories(masterWorld, true)
        //private val inventories = master.getLinkedInventories(masterWorld, true)

        override fun createMenu(syncId: Int, inv: PlayerInventory, player: PlayerEntity): ScreenHandler? {
            val handler = RemoteScreenHandler(syncId, inv, inventories, lastSort, master, offHand)
            //val handler = RemoteScreenHandler(syncId, inv, inventories, lastSort, offHand, masterWorld, master)
            master.watchers.add(handler)
            master.markForcedChunks()
            return handler
        }

        override fun writeScreenOpeningData(player: ServerPlayerEntity, buf: PacketByteBuf) {
            //buf.writeBlockPos(BlockPos.ORIGIN)
            buf.writeVarInt(lastSort.ordinal)
            buf.writeBoolean(offHand)
        }

        override fun getDisplayName() = TranslatableText("container.slotlink.request")

    }

}
