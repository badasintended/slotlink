package badasintended.slotlink.item

import badasintended.slotlink.block.entity.MasterBlockEntity
import badasintended.slotlink.init.Blocks
import badasintended.slotlink.screen.RemoteScreenHandler
import badasintended.slotlink.storage.FilterFlags
import badasintended.slotlink.util.actionBar
import badasintended.slotlink.util.int
import badasintended.slotlink.util.toArray
import badasintended.slotlink.util.toPos
import it.unimi.dsi.fastutil.ints.IntSet
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory
import net.minecraft.client.item.TooltipContext
import net.minecraft.entity.Entity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.ItemStack
import net.minecraft.item.ItemUsageContext
import net.minecraft.network.PacketByteBuf
import net.minecraft.screen.ScreenHandler
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text
import net.minecraft.util.ActionResult
import net.minecraft.util.Formatting
import net.minecraft.util.Hand
import net.minecraft.util.Identifier
import net.minecraft.util.TypedActionResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.registry.Registry
import net.minecraft.util.registry.RegistryKey
import net.minecraft.world.World

abstract class RemoteItem(id: String) : ModItem(id, SETTINGS.maxCount(1)) {

    abstract val level: Int

    protected val baseTlKey = "item.slotlink.remote"

    fun use(world: World, player: PlayerEntity, stack: ItemStack, remoteSlot: Int) {
        val network = stack.getSubNbt("network")

        if (network == null) {
            player.actionBar("${baseTlKey}.hasNoMaster")
        } else {
            val pos = network.getIntArray("pos").toPos()
            val dim = RegistryKey.of(Registry.WORLD_KEY, Identifier(network.getString("dim")))
            use(world, player, stack, remoteSlot, pos, dim)
        }
    }

    protected abstract fun use(
        world: World,
        player: PlayerEntity,
        stack: ItemStack,
        remoteSlot: Int,
        masterPos: BlockPos,
        masterDim: RegistryKey<World>
    )

    override fun use(world: World, player: PlayerEntity, hand: Hand): TypedActionResult<ItemStack> {
        val stack = when (hand) {
            Hand.MAIN_HAND -> player.mainHandStack
            Hand.OFF_HAND -> player.offHandStack
        }

        val slot = when (hand) {
            Hand.MAIN_HAND -> player.inventory.selectedSlot
            Hand.OFF_HAND -> PlayerInventory.OFF_HAND_SLOT
        }

        use(world, player, stack, slot)
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

        tooltip.add(Text.translatable("${baseTlKey}.useTooltip").formatted(Formatting.GRAY))

        val tag = stack.orCreateNbt
        if (tag.contains("network")) {
            val network = tag.getCompound("network")
            val pos = network.getIntArray("pos")
            val dim = network.getString("dim")
            tooltip.add(
                Text.translatable("${baseTlKey}.info", pos[0], pos[1], pos[2], dim).formatted(
                    Formatting.DARK_PURPLE
                )
            )
        }
    }

    override fun inventoryTick(stack: ItemStack, world: World, entity: Entity, slot: Int, selected: Boolean) {
        if (entity is Holder) {
            entity.possibleRemoteSlots.add(slot)
        }
    }

    interface Holder {

        @Suppress("INAPPLICABLE_JVM_NAME")
        @get:JvmName("slotlink\$getPossibleRemoteSlots")
        val possibleRemoteSlots: IntSet

    }

    class ScreenHandlerFactory(
        masterWorld: World,
        private val master: MasterBlockEntity,
        private val remoteSlot: Int
    ) : ExtendedScreenHandlerFactory {

        private val storages = master.getStorages(masterWorld, FilterFlags.INSERT, true)

        override fun createMenu(syncId: Int, inv: PlayerInventory, player: PlayerEntity): ScreenHandler {
            val handler = RemoteScreenHandler(syncId, inv, storages, master, remoteSlot)
            master.watchers.add(handler)
            master.markForcedChunks()
            return handler
        }

        override fun writeScreenOpeningData(player: ServerPlayerEntity, buf: PacketByteBuf) {
            buf.int(remoteSlot)
        }

        override fun getDisplayName() = Text.translatable("container.slotlink.request")!!

    }

}