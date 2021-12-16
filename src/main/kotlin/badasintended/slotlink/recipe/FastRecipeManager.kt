package badasintended.slotlink.recipe

import badasintended.slotlink.util.callGetAllOfType
import badasintended.slotlink.util.recipes
import java.util.*
import net.minecraft.inventory.Inventory
import net.minecraft.recipe.Recipe
import net.minecraft.recipe.RecipeManager
import net.minecraft.recipe.RecipeType
import net.minecraft.server.MinecraftServer
import net.minecraft.util.Identifier
import net.minecraft.world.World

private val holder = WeakHashMap<RecipeManager, FastRecipeManager>()

val World.fastRecipeManager get() = holder.getOrPut(recipeManager) { FastRecipeManager(recipeManager) }!!
val MinecraftServer.fastRecipeManager get() = holder.getOrPut(recipeManager) { FastRecipeManager(recipeManager) }!!

/**
 * An attempt to optimize [RecipeManager]'s unnecessarily expensive filtering method
 * by caching previous results and using saner way to filter.
 */
class FastRecipeManager(
    private val delegate: RecipeManager
) : RecipeManager() {

    private val firstMatchCache = WeakHashMap<Inventory, Recipe<Inventory>>()
    private val getCache = WeakHashMap<Identifier, Recipe<*>>()

    @Synchronized
    @Suppress("UNCHECKED_CAST")
    override fun <C : Inventory, T : Recipe<C>> getFirstMatch(
        type: RecipeType<T>,
        inventory: C,
        world: World
    ): Optional<T> {
        val cache = firstMatchCache[inventory]
        if (cache != null && cache.type == type && cache.matches(inventory, world)) {
            return Optional.of(cache as T)
        } else {
            firstMatchCache.remove(inventory)
        }

        val result = delegate.callGetAllOfType(type).values.firstOrNull { it.matches(inventory, world) }
        if (result != null) {
            firstMatchCache[inventory] = result as Recipe<Inventory>
            return Optional.of(result as T)
        }

        return Optional.empty()
    }

    @Synchronized
    override fun get(id: Identifier): Optional<out Recipe<*>> {
        return Optional.ofNullable(getCache.getOrPut(id) { delegate.recipes.values.firstNotNullOfOrNull { it[id] } })
    }

}