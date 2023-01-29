@file:Suppress("HasPlatformType", "NOTHING_TO_INLINE")

package badasintended.slotlink.util

import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.registry.Registries
import net.minecraft.util.Identifier
import net.minecraft.network.PacketByteBuf as B

inline fun B.bool(boolean: Boolean) = writeBoolean(boolean)
inline val B.bool get() = readBoolean()

inline fun B.int(int: Int) = writeVarInt(int)
inline val B.int get() = readVarInt()

inline fun B.string(string: String) = writeString(string)
inline val B.string get() = readString(32767)

inline fun B.stack(stack: ItemStack) = writeItemStack(stack)
inline val B.stack get() = readItemStack()

inline fun B.item(item: Item) = writeVarInt(Registries.ITEM.getRawId(item))
inline val B.item get() = Registries.ITEM[readVarInt()]

inline fun B.nbt(nbt: NbtCompound?) = writeNbt(nbt)
inline val B.nbt get() = readNbt()

inline fun B.id(id: Identifier) = writeIdentifier(id)
inline val B.id get() = readIdentifier()

inline fun B.enum(enum: Enum<*>) = writeVarInt(enum.ordinal)
inline fun <reified T : Enum<T>> B.enum() = enumValues<T>()[int]
