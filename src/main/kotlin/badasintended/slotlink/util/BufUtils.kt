@file:Suppress("HasPlatformType", "NOTHING_TO_INLINE")

package badasintended.slotlink.util

import net.minecraft.item.ItemStack
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

inline fun B.id(id: Identifier) = writeIdentifier(id)
inline val B.id get() = readIdentifier()
