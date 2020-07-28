@file:Suppress("DEPRECATION")

package badasintended.slotlink.block

import badasintended.slotlink.block.entity.MasterBlockEntity
import badasintended.slotlink.common.*
import net.fabricmc.fabric.api.util.NbtType
import net.minecraft.block.Block
import net.minecraft.block.BlockEntityProvider
import net.minecraft.block.BlockState
import net.minecraft.block.InventoryProvider
import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.Inventory
import net.minecraft.item.BlockItem
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.BlockView
import net.minecraft.world.World

class MasterBlock : ModBlock("master"), BlockEntityProvider {

    override fun createBlockEntity(view: BlockView): BlockEntity = MasterBlockEntity()

    override fun onPlaced(world: World, pos: BlockPos, state: BlockState, placer: LivingEntity?, itemStack: ItemStack) {
        super.onPlaced(world, pos, state, placer, itemStack)

        val blockEntity = world.getBlockEntity(pos)
        val nbt = blockEntity!!.toTag(CompoundTag())

        nbt.put("storagePos", ListTag())
        blockEntity.fromTag(state, nbt)
        blockEntity.markDirty()
    }

    override fun neighborUpdate(
        state: BlockState,
        world: World,
        pos: BlockPos,
        block: Block,
        neighborPos: BlockPos,
        moved: Boolean
    ) {
        super.neighborUpdate(state, world, pos, block, neighborPos, moved)

        val neighborState = world.getBlockState(neighborPos)
        val neighborBlock = neighborState.block

        if (neighborBlock is ChildBlock) {
            val neighborBlockEntity = world.getBlockEntity(neighborPos)
            val neighborNbt = neighborBlockEntity!!.toTag(CompoundTag())
            val neighborHasMaster = neighborNbt.getBoolean("hasMaster")
            if (!neighborHasMaster) {
                val masterPos = pos.toTag()
                neighborNbt.put("masterPos", masterPos)
                neighborNbt.putBoolean("hasMaster", true)
                neighborBlockEntity.fromTag(neighborState, neighborNbt)
                neighborBlockEntity.markDirty()
                world.updateNeighbors(neighborPos, neighborBlock)
            }
        }
    }

    override fun onUse(
        state: BlockState,
        world: World,
        pos: BlockPos,
        player: PlayerEntity,
        hand: Hand,
        hit: BlockHitResult
    ): ActionResult {
        if (!world.isClient) openScreen("master", player) { buf ->
            val nbt = world.getBlockEntity(pos)!!.toTag(CompoundTag())
            val linkCables = nbt.getList("linkCables", NbtType.COMPOUND)

            buf.writeInt(linkCables.size) // total link cables in network

            val inventories = hashMapOf<Item, Int>() // inventory block items
            var totalSlot = 0
            var maxCount = 0
            linkCables.forEach { tag ->
                tag as CompoundTag
                val linkCableNbt = world.getBlockEntity(tag.toPos())!!.toTag(CompoundTag())
                val linkedPos = linkCableNbt.getCompound("linkedPos").toPos()
                val linkedState = world.getBlockState(linkedPos)
                val linkedBlock = linkedState.block
                val linkedBlockEntity = world.getBlockEntity(linkedPos)
                val linkedBlockItem = BlockItem.fromBlock(linkedBlock)

                var inventory: Inventory? = null

                if (!world.isBlockIgnored(linkedBlock)) {
                    if (linkedBlock.isInvProvider()) {
                        linkedBlock as InventoryProvider
                        inventory = linkedBlock.getInventory(linkedState, world, linkedPos)
                    } else if (linkedBlockEntity.hasInv()) {
                        linkedBlockEntity as Inventory
                        inventory = linkedBlockEntity
                    }
                }

                if (inventory != null) {
                    if (linkedBlockItem in inventories.keys) {
                        inventories[linkedBlockItem] = inventories[linkedBlockItem]!! + 1
                    } else {
                        inventories[linkedBlockItem] = 1
                    }
                    totalSlot += inventory.size()
                    maxCount += inventory.maxCountPerStack * inventory.size()
                }
            }

            buf.writeInt(totalSlot)
            buf.writeInt(maxCount)
            buf.writeInt(inventories.size)
            inventories.forEach { (item, count) ->
                buf.writeItemStack(ItemStack(item, count))
            }
        }
        return ActionResult.SUCCESS
    }

}
