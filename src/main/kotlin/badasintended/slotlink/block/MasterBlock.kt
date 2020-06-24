@file:Suppress("DEPRECATION")

package badasintended.slotlink.block

import badasintended.slotlink.block.entity.MasterBlockEntity
import badasintended.slotlink.common.hasInventory
import badasintended.slotlink.common.openScreen
import badasintended.slotlink.common.pos2Tag
import badasintended.slotlink.common.tag2Pos
import net.fabricmc.fabric.api.util.NbtType
import net.minecraft.block.Block
import net.minecraft.block.BlockEntityProvider
import net.minecraft.block.BlockState
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
        blockEntity.fromTag(nbt)
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
                val masterPos = pos2Tag(pos)
                neighborNbt.put("masterPos", masterPos)
                neighborNbt.putBoolean("hasMaster", true)
                neighborBlockEntity.fromTag(neighborNbt)
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
                val linkCableNbt = world.getBlockEntity(tag2Pos(tag))!!.toTag(CompoundTag())
                val linkedPos = tag2Pos(linkCableNbt.getCompound("linkedPos"))
                val linkedBlock = world.getBlockState(linkedPos).block
                if (linkedBlock.hasBlockEntity()) {
                    val linkedBlockEntity = world.getBlockEntity(linkedPos)
                    if (hasInventory(linkedBlockEntity)) {
                        linkedBlockEntity as Inventory
                        val linkedBlockId = BlockItem.fromBlock(world.getBlockState(linkedPos).block)
                        if (linkedBlockId in inventories.keys) {
                            inventories[linkedBlockId] = inventories[linkedBlockId]!! + 1
                        } else {
                            inventories[linkedBlockId] = 1
                        }
                        totalSlot += linkedBlockEntity.invSize
                        maxCount += linkedBlockEntity.invMaxStackAmount * linkedBlockEntity.invSize
                    }
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
