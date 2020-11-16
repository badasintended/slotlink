package badasintended.slotlink.screen

import badasintended.slotlink.block.entity.MasterBlockEntity
import badasintended.slotlink.block.entity.RequestBlockEntity
import badasintended.slotlink.init.Networks.UPDATE_CURSOR
import badasintended.slotlink.init.Networks.UPDATE_MAX_SCROLL
import badasintended.slotlink.init.Networks.UPDATE_VIEWED_STACK
import badasintended.slotlink.init.Screens
import badasintended.slotlink.inventory.FilteredInventory
import badasintended.slotlink.mixin.CraftingScreenHandlerAccessor
import badasintended.slotlink.mixin.SlotAccessor
import badasintended.slotlink.util.MasterWatcher
import badasintended.slotlink.util.Sort
import badasintended.slotlink.util.buf
import badasintended.slotlink.util.isItemAndTagEqual
import badasintended.slotlink.util.merge
import badasintended.slotlink.util.s2c
import badasintended.slotlink.util.stack
import java.util.Optional
import kotlin.collections.set
import kotlin.math.ceil
import kotlin.math.min
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.CraftingInventory
import net.minecraft.inventory.CraftingResultInventory
import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack
import net.minecraft.network.PacketByteBuf
import net.minecraft.network.packet.s2c.play.CloseScreenS2CPacket
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket
import net.minecraft.recipe.CraftingRecipe
import net.minecraft.recipe.Ingredient
import net.minecraft.recipe.Recipe
import net.minecraft.recipe.RecipeGridAligner
import net.minecraft.recipe.RecipeType
import net.minecraft.screen.CraftingScreenHandler
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.ScreenHandlerListener
import net.minecraft.screen.ScreenHandlerType
import net.minecraft.screen.slot.CraftingResultSlot
import net.minecraft.screen.slot.Slot
import net.minecraft.screen.slot.SlotActionType
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.collection.DefaultedList
import net.minecraft.util.registry.Registry

@Suppress("LeakingThis")
open class RequestScreenHandler(
    syncId: Int,
    val playerInventory: PlayerInventory,
    private val inventories: List<FilteredInventory>,
    var lastSort: Sort,
) : CraftingScreenHandler(syncId, playerInventory), MasterWatcher, RecipeGridAligner<Ingredient> {

    val player: PlayerEntity = playerInventory.player
    private val emptySlots = arrayListOf<Pair<Inventory, Int>>()
    private val filledSlots = arrayListOf<Pair<Inventory, Int>>()

    private val filledStacks = arrayListOf<ItemStack>()

    private val trackedStacks: DefaultedList<Pair<ItemStack, Int>> = DefaultedList.ofSize(48, ItemStack.EMPTY to 0)
    val viewedStacks: DefaultedList<Pair<ItemStack, Int>> = DefaultedList.ofSize(48, ItemStack.EMPTY to 0)

    private var lastFilter = ""

    var viewedHeight = 0

    var maxScroll = 0
    private var lastScroll = 0

    private var request: RequestBlockEntity? = null
    private var master: MasterBlockEntity? = null

    private val cache = hashMapOf<Inventory, DefaultedList<ItemStack>>()

    /** Client side **/
    constructor(syncId: Int, playerInventory: PlayerInventory, buf: PacketByteBuf) : this(
        syncId, playerInventory, emptyList(), Sort.of(buf.readVarInt())
    )

    /** Server side **/
    constructor(
        syncId: Int,
        playerInventory: PlayerInventory,
        inventories: List<FilteredInventory>,
        lastSort: Sort,
        request: RequestBlockEntity?,
        master: MasterBlockEntity
    ) : this(syncId, playerInventory, inventories, lastSort) {
        this.request = request
        this.master = master
        inventories.forEach {
            val stacks = DefaultedList.ofSize(it.size(), ItemStack.EMPTY)
            for (i in 0 until it.size()) {
                stacks[i] = it.getStack(i).copy()
            }
            cache[it] = stacks
        }

        addListener(object : ScreenHandlerListener {
            override fun onHandlerRegistered(handler: ScreenHandler, stacks: DefaultedList<ItemStack>) {}
            override fun onPropertyUpdate(handler: ScreenHandler, property: Int, value: Int) {}

            override fun onSlotUpdate(handler: ScreenHandler, slotId: Int, stack: ItemStack) {
                s2c(player, ScreenHandlerSlotUpdateS2CPacket(syncId, slotId, stack))
            }
        })
    }

    fun sort(sort: Sort, filter: String) {
        emptySlots.clear()
        filledSlots.clear()

        inventories.forEach { inv ->
            for (slot in 0 until inv.size()) {
                val stack = inv.getStack(slot)
                if (stack.isEmpty) {
                    emptySlots.add(inv to slot)
                } else {
                    filledSlots.add(inv to slot)
                }
            }
        }

        val trimmedFilter = filter.trim()

        if (trimmedFilter.isNotBlank()) {
            when (trimmedFilter.first()) {
                '@' -> filledSlots.removeIf {
                    !Registry.ITEM.getId(it.stack.item).toString().contains(trimmedFilter.drop(1), true)
                }
                '#' -> filledSlots.removeIf r@{ entry ->
                    val tag = trimmedFilter.drop(1)
                    val tags = player.world.tagManager.items.tags.filterValues { it.contains(entry.stack.item) }.keys
                    if (tags.isEmpty() and tag.isBlank()) return@r false
                    else return@r tags.none { it.toString().contains(tag, true) }
                }
                else -> filledSlots.removeIf { !it.stack.name.string.contains(trimmedFilter.trim(), true) }
            }
        }

        filledStacks.clear()

        filledSlots.forEach { slot ->
            val match = filledStacks.firstOrNull { it.isItemAndTagEqual(slot.stack) }
            if (match == null) {
                filledStacks.add(slot.stack.copy())
            } else {
                match.count += slot.stack.count
            }
        }

        sort.sorter.invoke(filledStacks)

        if ((lastSort != sort) or (lastFilter != filter)) scroll(0) else scroll(lastScroll)

        lastSort = sort
        lastFilter = filter
    }

    fun scroll(amount: Int) {
        viewedStacks.clear()

        val scroll = amount.coerceIn(0, maxScroll)

        for (i in 0 until viewedHeight * 8) {
            val stack = filledStacks.getOrElse(i + 8 * scroll) { ItemStack.EMPTY }
            viewedStacks[i] = stack.copy().apply { count = 1 } to stack.count
        }

        lastScroll = scroll
    }

    /** server only **/
    fun multiSlotClick(i: Int, button: Int, quickMove: Boolean) {
        var cursor = playerInventory.cursorStack
        if (cursor.isEmpty) {
            if ((button == 0) or (button == 1)) {
                val pairs = filledSlots.filter { it.stack.isItemAndTagEqual(viewedStacks[i].first) }
                pairs.forEach {
                    if (cursor.count < cursor.maxCount) {
                        val merged = cursor.merge(it.stack)
                        cursor = merged.first
                        it.stack = merged.second
                    }
                }
                if (quickMove) slots
                    .filter { (it.inventory is PlayerInventory) and it.canInsert(cursor) }
                    .sortedBy { (it as SlotAccessor).index }
                    .sortedByDescending { it.stack.count }
                    .forEach {
                        val stack = it.stack
                        val merged = stack.merge(cursor)
                        it.stack = merged.first
                        cursor = merged.second
                    }
            } else if (button == 2) {
                cursor = viewedStacks[i].first.copy().apply { count = maxCount }
            }
        } else if (button == 0) {
            cursor = moveStack(cursor)
        }

        updateCursor(cursor)
    }

    fun craftingResultSlotClick(button: Int, quickMove: Boolean) {
        if (button !in 0..2) return

        this as CraftingScreenHandlerAccessor

        var cursor = playerInventory.cursorStack
        val resultStack = result.getStack(0)

        if (button == 2) {
            cursor = resultStack.copy().apply { count = maxCount }
        } else {
            while (true) {
                val merged = cursor.merge(resultStack)
                if (!merged.second.isEmpty) break

                cursor = merged.first
                resultStack.onCraft(player.world, player, resultStack.count)

                val remainingStacks = player.world.recipeManager.getRemainingStacks(RecipeType.CRAFTING, input, player.world)

                var finished = false
                for (i in remainingStacks.indices) {
                    val remainingStack = remainingStacks[i]
                    val inputStack = input.getStack(i)
                    if (!inputStack.isEmpty) {
                        if (remainingStack.isEmpty) {
                            if (inputStack.count != 1) {
                                inputStack.decrement(1)
                            } else {
                                val slot = filledSlots.firstOrNull { it.stack.isItemAndTagEqual(inputStack) }
                                if (slot == null) {
                                    inputStack.decrement(1)
                                    finished = true
                                    break
                                } else {
                                    slot.first.removeStack(slot.second, 1)
                                }
                            }
                        } else {
                            input.setStack(i, remainingStack)
                        }
                    }
                }
                result.unlockLastRecipe(player)
                result.setStack(0, ItemStack.EMPTY)
                onContentChanged(input)
                if (!quickMove or finished) break
            }
            if (quickMove) {
                insertItem(cursor, 10, 46, true)
                if (!cursor.isEmpty) {
                    val stack = moveStack(cursor)
                    player.dropItem(stack, true)
                    cursor = ItemStack.EMPTY
                }
            }
        }
        updateCursor(cursor)
    }

    fun applyRecipe(recipe: Recipe<*>) {
        if (recipe.type == RecipeType.CRAFTING) {
            clearCraftingGrid()
            sendContentUpdates()
            alignRecipeToGrid(3, 3, -1, recipe, recipe.previewInputs.iterator(), 0)
        }
    }

    fun clearCraftingGrid(toPlayerInventory: Boolean = false) {
        for (i in 1..9) slots[i].apply {
            if (toPlayerInventory) insertItem(stack, 10, 46, false)
            stack = moveStack(stack)
        }
    }

    fun move() {
        var cursor = playerInventory.cursorStack
        slots
            .filter { (it.inventory is PlayerInventory) and it.canTakeItems(player) and if (cursor.isEmpty) true else cursor.isItemEqual(it.stack) }
            .forEach { it.stack = moveStack(it.stack) }

        if (!cursor.isEmpty) {
            cursor = moveStack(cursor)
        }

        updateCursor(cursor)
    }

    fun restock() {
        var cursor = playerInventory.cursorStack
        if (cursor.isEmpty) slots.filter { it.inventory is PlayerInventory }.forEach {
            it.stack = it.stack.restock()
        } else {
            cursor = cursor.restock()
        }

        updateCursor(cursor)
    }

    open fun resize(viewedHeight: Int) {
        this as CraftingScreenHandlerAccessor

        val coerced = viewedHeight.coerceIn(3, 6)
        val h = coerced * 18 + 23

        slots.clear()

        addSlot(CraftingResultSlot(playerInventory.player, this.input, this.result, 0, -999999, -999999 + h))

        for (m in 0 until 3) for (l in 0 until 3) {
            addSlot(Slot(input, l + m * 3, 30 + l * 18, 17 + m * 18 + h))
        }

        for (m in 0 until 3) for (l in 0 until 9) {
            addSlot(Slot(playerInventory, l + m * 9 + 9, 8 + l * 18, 75 + m * 18 + h))
        }

        for (m in 0 until 9) {
            addSlot(Slot(playerInventory, m, 8 + m * 18, 133 + h))
        }

        this.viewedHeight = coerced
    }

    private fun updateCursor(stack: ItemStack) {
        playerInventory.cursorStack = stack
        s2c(player, UPDATE_CURSOR, buf().writeItemStack(stack))
    }

    private fun moveStack(stack: ItemStack): ItemStack {
        if (stack.isEmpty) return stack
        var result = stack
        val pairs = filledSlots.filter { it.stack.isItemAndTagEqual(result) and (it.stack.count < it.stack.maxCount) }
        pairs.forEach {
            val merged = it.stack.merge(result)
            if (it.first.isValid(it.second, merged.first)) {
                it.stack = merged.first
                result = merged.second
            }
        }
        if (!result.isEmpty or pairs.isEmpty()) {
            val pair = emptySlots.firstOrNull { it.first.isValid(it.second, result) }
            if (pair != null) {
                pair.stack = result
                filledSlots.add(pair)
                emptySlots.remove(pair)
                result = ItemStack.EMPTY
            }
        }
        return result
    }

    private fun ItemStack.restock(max: Int = 64): ItemStack {
        if (isEmpty) return ItemStack.EMPTY

        var stack = copy()
        val pairs = filledSlots.filter { it.stack.isItemAndTagEqual(stack) }
        pairs.forEach {
            if (stack.count < min(stack.maxCount, max)) {
                val merged = stack.merge(it.stack)
                stack = merged.first
                it.stack = merged.second
            }
        }
        return stack
    }

    override fun onSlotClick(i: Int, j: Int, actionType: SlotActionType, playerEntity: PlayerEntity): ItemStack {
        if (playerEntity !is ServerPlayerEntity) return ItemStack.EMPTY
        val result = super.onSlotClick(i, j, actionType, playerEntity)
        s2c(playerEntity, UPDATE_CURSOR, buf().writeItemStack(playerEntity.inventory.cursorStack))
        return result
    }

    override fun transferSlot(player: PlayerEntity, index: Int): ItemStack {
        val inventory = slots[index].inventory
        var stack = ItemStack.EMPTY
        when (inventory) {
            is CraftingResultInventory -> {
                stack = super.transferSlot(player, index)
            }
            is CraftingInventory -> {
                super.transferSlot(player, index)
                stack = moveStack(slots[index].stack)
            }
            is PlayerInventory -> {
                stack = moveStack(slots[index].stack)
                slots[index].stack = stack
                stack = super.transferSlot(player, index)
            }
        }
        return stack
    }

    override fun acceptAlignedInput(inputs: MutableIterator<Ingredient>, slot: Int, amount: Int, gridX: Int, gridY: Int) {
        this as CraftingScreenHandlerAccessor
        val ingredient = inputs.next()
        if (ingredient.isEmpty) return

        val pair = filledSlots.firstOrNull { ingredient.test(it.stack) }
        val stack = if (pair == null) {
            slots
                .firstOrNull { (it.inventory is PlayerInventory) and it.canTakeItems(player) and (ingredient.test(it.stack)) }
                ?.takeStack(1) ?: return
        } else {
            pair.first.removeStack(pair.second, 1)
        }

        input.setStack(slot, stack)
    }

    override fun onContentChanged(inventory: Inventory) {
        this as CraftingScreenHandlerAccessor

        if (inventory is CraftingInventory) if (player is ServerPlayerEntity) {
            var stack = ItemStack.EMPTY
            val optional: Optional<CraftingRecipe> = player.server.recipeManager.getFirstMatch(RecipeType.CRAFTING, input, player.world)
            if (optional.isPresent) {
                val craftingRecipe = optional.get()
                if (result.shouldCraftRecipe(player.world, player, craftingRecipe)) {
                    stack = craftingRecipe.craft(input)
                }
            }
            result.setStack(0, stack)
            s2c(player, ScreenHandlerSlotUpdateS2CPacket(syncId, 0, stack))
        }
    }

    override fun sendContentUpdates() {
        super.sendContentUpdates()

        if (player !is ServerPlayerEntity) return

        var resort = false
        cache.forEach {
            val inventory = it.key
            val stacks = it.value

            for (i in 0 until inventory.size()) {
                val before = stacks[i]
                val after = inventory.getStack(i)
                if (!before.isItemAndTagEqual(after) or (before.count != after.count)) {
                    resort = true
                    stacks[i] = after.copy()
                }
            }
        }

        if (resort) sort(lastSort, lastFilter)

        viewedStacks.forEachIndexed { i, after ->
            val before = trackedStacks[i]
            if (!before.first.isItemAndTagEqual(after.first) or (before.second != after.second)) {
                val buf = buf().apply {
                    writeVarInt(syncId)
                    writeVarInt(i)
                    writeItemStack(after.first)
                    writeVarInt(after.second)
                }
                s2c(player, UPDATE_VIEWED_STACK, buf)
                trackedStacks[i] = after.first.copy() to after.second
            }
        }

        val max = ceil((filledStacks.size / 8f) - viewedHeight).toInt().coerceAtLeast(0)
        if (maxScroll != max) {
            s2c(player, UPDATE_MAX_SCROLL, buf().writeVarInt(syncId).writeVarInt(max))
            maxScroll = max
            scroll(0)
        }
    }

    override fun canUse(player: PlayerEntity?) = true

    override fun getType(): ScreenHandlerType<*> = Screens.REQUEST

    override fun close(player: PlayerEntity) {
        this as CraftingScreenHandlerAccessor
        slots.filter { it.inventory is CraftingInventory }.forEach {
            it.stack = moveStack(it.stack)
        }
        dropInventory(player, player.world, input)
        request?.lastSort = lastSort
        request?.markDirty()
        master?.watchers?.remove(this)
        master?.unmarkForcedChunks()
    }

    override fun onMasterRemoved() {
        if (player is ServerPlayerEntity) s2c(player, CloseScreenS2CPacket(syncId))
    }

}
