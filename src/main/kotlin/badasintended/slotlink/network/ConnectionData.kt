package badasintended.slotlink.network

import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction

class ConnectionData(
    pos: BlockPos,
    val type: ConnectionType<*>,
    val sides: HashSet<Direction> = hashSetOf()
) {

    val pos: BlockPos = pos.toImmutable()

    var sideBits: Int
        get() {
            var value = 0
            sides.forEach {
                value += 1 shl it.id
            }
            return value
        }
        set(value) {
            sides.clear()
            for (i in 0 until 6) {
                if (((value shr i) and 1) != 0) {
                    sides.add(Direction.byId(i))
                }
            }
        }

    override fun equals(other: Any?): Boolean {
        if (other !is ConnectionData) return false
        if (other === this) return true

        return pos == other.pos && type == other.type
    }

    override fun hashCode(): Int {
        var result = pos.hashCode()
        result = 31 * result + type.hashCode()
        return result
    }

}
