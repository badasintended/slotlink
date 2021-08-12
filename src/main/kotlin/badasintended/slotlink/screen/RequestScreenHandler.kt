package badasintended.slotlink.screen

import badasintended.slotlink.block.entity.BlockEntityWatcher
import badasintended.slotlink.block.entity.MasterBlockEntity
import badasintended.slotlink.block.entity.RequestBlockEntity
import badasintended.slotlink.init.Packets.UPDATE_CURSOR
import badasintended.slotlink.init.Packets.UPDATE_MAX_SCROLL
import badasintended.slotlink.init.Packets.UPDATE_SLOT_NUMBERS
import badasintended.slotlink.init.Packets.UPDATE_VIEWED_STACK
import badasintended.slotlink.init.Screens
import badasintended.slotlink.inventory.FilteredInventory
import badasintended.slotlink.screen.slot.LockedSlot
import badasintended.slotlink.screen.view.ItemView
import badasintended.slotlink.screen.view.toView
import badasintended.slotlink.util.ObjIntPair
import badasintended.slotlink.util.actionBar
import badasintended.slotlink.util.allEmpty
import badasintended.slotlink.util.input
import badasintended.slotlink.util.int
import badasintended.slotlink.util.isItemAndTagEqual
import badasintended.slotlink.util.item
import badasintended.slotlink.util.merge
import badasintended.slotlink.util.nbt
import badasintended.slotlink.util.result
import badasintended.slotlink.util.s2c
import badasintended.slotlink.util.stack
import badasintended.slotlink.util.to
import java.util.*
import kotlin.collections.set
import kotlin.math.ceil
import kotlin.math.min
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.CraftingInventory
import net.minecraft.inventory.CraftingResultInventory
import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack
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
import net.minecraft.screen.slot.SlotActionType.CLONE
import net.minecraft.screen.slot.SlotActionType.PICKUP
import net.minecraft.screen.slot.SlotActionType.QUICK_MOVE
import net.minecraft.screen.slot.SlotActionType.SWAP
import net.minecraft.screen.slot.SlotActionType.THROW
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.registry.Registry

@Suppress("LeakingThis")
open class RequestScreenHandler(
    syncId: Int,
    val playerInventory: PlayerInventory,
    private val inventories: Set<FilteredInventory>,
) : CraftingScreenHandler(syncId, playerInventory),
    MasterBlockEntity.Watcher,
    BlockEntityWatcher<RequestBlockEntity>,
    RecipeGridAligner<Ingredient> {

    companion object {

        private val whitespaceRegex = Regex("\\s+")

    }

    val player: PlayerEntity = playerInventory.player
    private val emptySlots = linkedSetOf<ObjIntPair<Inventory>>()
    private val filledSlots = linkedSetOf<ObjIntPair<Inventory>>()

    private val filledStacks = arrayListOf<ItemStack>()

    private val trackedViews = ArrayList<ItemView>(54)
    val itemViews = ArrayList<ItemView>(54)

    private var lastSortData = SortData(SortMode.NAME, "")
    private var scheduledSortData: SortData? = null

    var viewedHeight = 0

    var maxScroll = 0
    private var lastScroll = 0

    private var request: RequestBlockEntity? = null
    private var master: MasterBlockEntity? = null

    private val cache = hashMapOf<Inventory, List<ItemView>>()

    var totalSlotSize = 0
    var filledSlotSize = 0

    init {
        for (i in 0 until 54) {
            itemViews.add(ItemStack.EMPTY.toView())
        }
    }

    /** Client side **/
    constructor(syncId: Int, playerInventory: PlayerInventory) : this(
        syncId, playerInventory, emptySet()
    )

    /** Server side **/
    constructor(
        syncId: Int,
        playerInventory: PlayerInventory,
        inventories: Set<FilteredInventory>,
        request: RequestBlockEntity?,
        master: MasterBlockEntity
    ) : this(syncId, playerInventory, inventories) {
        this.request = request
        this.master = master
        inventories.forEach {
            val views = ArrayList<ItemView>(it.size())
            for (i in 0 until it.size()) {
                views.add(it.getStack(i).toView())
            }
            cache[it] = views
        }

        for (i in 0 until 54) {
            trackedViews.add(ItemStack.EMPTY.toView())
        }

        addListener(object : ScreenHandlerListener {
            override fun onPropertyUpdate(handler: ScreenHandler, property: Int, value: Int) {}

            override fun onSlotUpdate(handler: ScreenHandler, slotId: Int, stack: ItemStack) {
                s2c(player, ScreenHandlerSlotUpdateS2CPacket(syncId, nextRevision(), slotId, stack))
            }
        })
    }

    fun scheduleSort(mode: SortMode, filter: String) {
        scheduledSortData = SortData(mode, filter)
    }

    fun scroll(amount: Int) {
        val scroll = amount.coerceIn(0, maxScroll)

        for (i in 0 until viewedHeight * 9) {
            val stack = filledStacks.getOrElse(i + 9 * scroll) { ItemStack.EMPTY }
            itemViews[i].update(stack)
        }

        lastScroll = scroll
    }

    /** server only **/
    fun multiSlotAction(i: Int, data: Int, type: SlotActionType) {
        val view = itemViews[i]
        var cursor = cursorStack

        if (cursor.isEmpty) {
            if (type == CLONE) {
                if (player.abilities.creativeMode && cursor.isEmpty) cursor = view.toStack().apply { count = maxCount }
            } else {
                if (type == THROW && data == 0) {
                    val slot = filledSlots.first { view.isItemAndTagEqual(it.stack) }
                    player.dropItem(slot.stack.copy().apply { count = 1 }, true)
                    slot.stack.decrement(1)
                } else if (type != SWAP || !view.isItemAndTagEqual(playerInventory.getStack(data))) {
                    var stack = if (type == SWAP) playerInventory.getStack(data).copy() else ItemStack.EMPTY
                    filledSlots.any {
                        if (view.isItemAndTagEqual(it.stack)) {
                            val merged = stack.merge(it.stack)
                            stack = merged.first
                            it.stack = merged.second
                        }
                        if (stack.isEmpty) false else stack.count < stack.maxCount
                    }

                    when (type) {
                        SWAP -> playerInventory.setStack(data, stack)
                        THROW -> player.dropItem(stack, true)
                        else -> {
                            if (type == QUICK_MOVE) slots
                                .filter { (it.inventory is PlayerInventory) && it.canInsert(stack) }
                                .sortedBy { it.index }
                                .sortedByDescending { it.stack.count }
                                .forEach {
                                    val merged = it.stack.merge(stack)
                                    it.stack = merged.first
                                    stack = merged.second
                                }
                            cursor = stack
                        }
                    }
                }
            }
        } else if (type == PICKUP) {
            cursor = moveStack(cursor)
        }

        updateCursor(cursor)
    }

    fun craftingResultSlotClick(button: Int, quickMove: Boolean) {
        if (button !in 0..2) return

        var cursor = cursorStack
        val resultStack = result.getStack(0)

        if (button == 2) {
            if (player.abilities.creativeMode && cursor.isEmpty) cursor = resultStack.copy().apply { count = maxCount }
        } else {
            while (true) {
                val merged = cursor.merge(resultStack)
                if (!merged.second.isEmpty || merged.allEmpty()) break

                cursor = merged.first
                resultStack.onCraft(player.world, player, resultStack.count)

                val remainingStacks =
                    player.world.recipeManager.getRemainingStacks(RecipeType.CRAFTING, input, player.world)

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
                                    continue
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
                if (!quickMove || finished) break
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
            alignRecipeToGrid(3, 3, -1, recipe, recipe.ingredients.iterator(), 0)
        }
    }

    fun clearCraftingGrid(toPlayerInventory: Boolean = false) {
        for (i in 1..9) slots[i].apply {
            if (toPlayerInventory) insertItem(stack, 10, 46, false)
            stack = moveStack(stack)
        }
    }

    fun move() {
        var cursor = cursorStack
        slots.forEach {
            if (it.inventory is PlayerInventory
                && it.index >= 9
                && it.canTakeItems(player)
                && (cursor.isEmpty || cursor.isItemEqual(it.stack))
            ) {
                it.stack = moveStack(it.stack)
            }
        }

        if (!cursor.isEmpty) {
            cursor = moveStack(cursor)
        }

        updateCursor(cursor)
    }

    fun restock() {
        var cursor = cursorStack
        if (cursor.isEmpty) slots.filter { it.inventory is PlayerInventory }.forEach {
            it.stack = it.stack.restock()
        } else {
            cursor = cursor.restock()
        }

        updateCursor(cursor)
    }

    open fun resize(viewedHeight: Int, craft: Boolean) {
        val coerced = viewedHeight.coerceIn(3, 6)
        val h = coerced * 18 + 23

        val craftH = if (craft) 67 else 0

        slots.clear()

        addSlotOnly(CraftingResultSlot(playerInventory.player, this.input, this.result, 0, -999999, -999999 + h))

        for (m in 0 until 3) for (l in 0 until 3) addSlotOnly(
            if (craft) Slot(input, l + m * 3, 30 + l * 18, 8 + m * 18 + h)
            else LockedSlot(input, l + m * 3)
        )

        for (m in 0 until 3) for (l in 0 until 9) addSlotOnly(
            Slot(playerInventory, l + m * 9 + 9, 8 + l * 18, 9 + craftH + m * 18 + h)
        )

        for (m in 0 until 9) addSlotOnly(
            Slot(playerInventory, m, 8 + m * 18, 67 + craftH + h)
        )

        this.viewedHeight = coerced
    }

    private fun addSlotOnly(slot: Slot): Slot {
        slot.id = slots.size
        slots.add(slot)
        return slot
    }

    private fun updateCursor(stack: ItemStack) {
        cursorStack = stack
        s2c(player, UPDATE_CURSOR) {
            stack(stack)
        }
    }

    private fun moveStack(stack: ItemStack): ItemStack {
        if (stack.isEmpty) return stack
        var result = stack
        val pairs = filledSlots.filter { it.stack.isItemAndTagEqual(result) && it.stack.count < it.stack.maxCount }
        pairs.forEach {
            val merged = it.stack.merge(result)
            if (it.first.isValid(it.second, merged.first)) {
                it.stack = merged.first
                result = merged.second
            }
        }
        if (!result.isEmpty || pairs.isEmpty()) {
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

    private fun onRemoved(key: String) {
        if (player is ServerPlayerEntity) {
            s2c(player, CloseScreenS2CPacket(syncId))
            player.actionBar("container.slotlink.request.$key")
        }
    }

    private fun sort(sortData: SortData) {
        sortData.mode.sorter.invoke(filledStacks)

        if (lastSortData != sortData) scroll(0) else scroll(lastScroll)
        lastSortData = sortData

        s2c(player, UPDATE_SLOT_NUMBERS) {
            int(syncId)
            int(totalSlotSize)
            int(filledSlotSize)
        }
    }

    override fun onSlotClick(i: Int, j: Int, actionType: SlotActionType, playerEntity: PlayerEntity) {
        if (playerEntity !is ServerPlayerEntity) return
        super.onSlotClick(i, j, actionType, playerEntity)
        s2c(playerEntity, UPDATE_CURSOR) {
            stack(cursorStack)
        }
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

    override fun acceptAlignedInput(inputs: Iterator<Ingredient>, slot: Int, amount: Int, gridX: Int, gridY: Int) {
        val ingredient = inputs.next()
        if (ingredient.isEmpty) return

        val pair = filledSlots.firstOrNull { ingredient.test(it.stack) }
        val stack = if (pair == null) {
            slots
                .firstOrNull { it.inventory is PlayerInventory && it.canTakeItems(player) && ingredient.test(it.stack) }
                ?.takeStack(1) ?: return
        } else {
            pair.first.removeStack(pair.second, 1)
        }

        input.setStack(slot, stack)
    }

    override fun onContentChanged(inventory: Inventory) {
        if (inventory is CraftingInventory) if (player is ServerPlayerEntity) {
            var stack = ItemStack.EMPTY
            val optional: Optional<CraftingRecipe> =
                player.server.recipeManager.getFirstMatch(RecipeType.CRAFTING, input, player.world)
            if (optional.isPresent) {
                val craftingRecipe = optional.get()
                if (result.shouldCraftRecipe(player.world, player, craftingRecipe)) {
                    stack = craftingRecipe.craft(input)
                }
            }
            result.setStack(0, stack)
            s2c(player, ScreenHandlerSlotUpdateS2CPacket(syncId, nextRevision(), 0, stack))
        }
    }

    override fun sendContentUpdates() {
        super.sendContentUpdates()

        if (player !is ServerPlayerEntity) return

        var resort = false
        cache.forEach { entry ->
            val inventory = entry.key
            val views = entry.value

            for (i in 0 until inventory.size()) {
                val view = views[i]
                val stack = inventory.getStack(i)
                if (!view.isItemAndTagEqual(stack)) {
                    if (view.isEmpty && !stack.isEmpty) {
                        filledSlots.add(inventory to i)
                        emptySlots.remove(inventory to i)
                        filledSlotSize++
                    } else if (!view.isEmpty && stack.isEmpty) {
                        filledSlots.remove(inventory to i)
                        emptySlots.add(inventory to i)
                        filledSlotSize--
                    }

                    val beforeId = filledStacks.indexOfFirst { view.isItemAndTagEqual(it) }
                    if (beforeId >= 0) {
                        val beforeMatch = filledStacks[beforeId]
                        beforeMatch.count -= view.count
                        if (beforeMatch.isEmpty) {
                            filledStacks.removeAt(beforeId)
                        }
                    }

                    if (!stack.isEmpty && lastSortData.filters.all { it.match(stack) }) {
                        val afterMatch = filledStacks.firstOrNull { it.isItemAndTagEqual(stack) }
                        if (afterMatch == null) {
                            filledStacks.add(stack.copy())
                        } else {
                            afterMatch.count += stack.count
                        }
                    }

                    resort = true
                    view.update(stack)
                } else if (view.count != stack.count) {
                    val filled = filledStacks.firstOrNull { view.isItemAndTagEqual(it) }
                    if (filled != null) {
                        filled.count -= view.count - stack.count
                        view.update(stack)
                    }
                    resort = true
                }
            }
        }

        if (resort) sort(lastSortData)

        scheduledSortData?.let { sortData ->
            scheduledSortData = null
            emptySlots.clear()
            filledSlots.clear()
            totalSlotSize = 0
            filledSlotSize = 0

            inventories.forEach { inv ->
                for (slot in 0 until inv.size()) {
                    totalSlotSize++
                    val stack = inv.getStack(slot)
                    if (stack.isEmpty) {
                        emptySlots.add(inv to slot)
                    } else {
                        filledSlots.add(inv to slot)
                        filledSlotSize++
                    }
                }
            }

            filledSlots.removeIf { slot ->
                sortData.filters.any { !it.match(slot.stack) }
            }

            filledStacks.clear()

            filledSlots.forEach { slot ->
                val stack = slot.stack
                val match = filledStacks.firstOrNull { it.isItemAndTagEqual(stack) }
                if (match == null) {
                    filledStacks.add(stack.copy())
                } else {
                    match.count += stack.count
                }
            }

            sort(sortData)
        }

        itemViews.forEachIndexed { i, after ->
            val before = trackedViews[i]
            if (before != after) {
                s2c(player, UPDATE_VIEWED_STACK) {
                    int(syncId)
                    int(i)
                    item(after.item)
                    nbt(after.nbt)
                    int(after.count)
                }
                before.update(after)
            }
        }

        val max = ceil((filledStacks.size / 9f) - viewedHeight).toInt().coerceAtLeast(0)
        if (maxScroll != max) {
            s2c(player, UPDATE_MAX_SCROLL) {
                int(syncId)
                int(max)
            }
            maxScroll = max
            scroll(0)
        }
    }

    override fun canUse(player: PlayerEntity?) = true

    override fun getType(): ScreenHandlerType<*> = Screens.REQUEST

    override fun close(player: PlayerEntity) {
        slots.filter { it.inventory is CraftingInventory }.forEach {
            it.stack = moveStack(it.stack)
        }
        dropInventory(player, input)
        request?.watchers?.remove(this)
        request?.markDirty()
        master?.watchers?.remove(this)
        master?.unmarkForcedChunks()
    }

    override fun onMasterRemoved() = onRemoved("brokenMaster")

    override fun onRemoved() = onRemoved("brokenSelf")

    private inner class SortData(
        val mode: SortMode,
        filter: String,
    ) {

        val filters by lazy { filter.trim().split(whitespaceRegex).map { Filter(it) } }

    }

    private inner class Filter(string: String) {

        val first = string.getOrElse(0) { 'w' }
        val term = when (first) {
            '@', '#' -> string.drop(1)
            else -> string
        }

        fun match(stack: ItemStack): Boolean = term.isBlank() || when (first) {
            '@' -> Registry.ITEM.getId(stack.item).toString().contains(term, true)
            '#' -> player.world.tagManager
                .getOrCreateTagGroup(Registry.ITEM_KEY)
                .tags.filterValues { it.contains(stack.item) }.keys
                .any { it.toString().contains(term, true) }
            else -> stack.name.string.contains(term, true)
        }

    }

    @Suppress("unused")
    enum class SortMode(
        private val id: String,
        val sorter: (ArrayList<ItemStack>) -> Any
    ) {

        NAME("name", { it -> it.sortBy { it.name.string } }),
        NAME_DESC("name_desc", { it -> it.sortByDescending { it.name.string } }),

        ID("id", { it -> it.sortBy { Registry.ITEM.getId(it.item).toString() } }),
        ID_DESC("id_desc", { it -> it.sortByDescending { Registry.ITEM.getId(it.item).toString() } }),

        COUNT("count", { it -> it.sortBy { it.count } }),
        COUNT_DESC("count_desc", { it -> it.sortByDescending { it.count } });

        companion object {

            val values = values()

        }

        fun next(): SortMode {
            return values[(ordinal + 1) % values.size]
        }

        override fun toString() = id

    }

}
