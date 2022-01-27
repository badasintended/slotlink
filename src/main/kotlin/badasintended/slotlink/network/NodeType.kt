package badasintended.slotlink.network

import badasintended.slotlink.block.entity.CableBlockEntity
import badasintended.slotlink.block.entity.ExportCableBlockEntity
import badasintended.slotlink.block.entity.ImportCableBlockEntity
import badasintended.slotlink.block.entity.InterfaceBlockEntity
import badasintended.slotlink.block.entity.LinkCableBlockEntity
import badasintended.slotlink.block.entity.MasterBlockEntity
import badasintended.slotlink.block.entity.RequestBlockEntity
import kotlin.reflect.KClass

class NodeType<T : Node>(
    val clazz: KClass<T>,
    val save: Boolean = true
) {

    companion object {

        private val map = arrayListOf<NodeType<*>>()

        val MASTER = NodeType(MasterBlockEntity::class, false)
        val CABLE = NodeType(CableBlockEntity::class)
        val REQUEST = NodeType(RequestBlockEntity::class)
        val LINK = NodeType(LinkCableBlockEntity::class)
        val EXPORT = NodeType(ExportCableBlockEntity::class)
        val IMPORT = NodeType(ImportCableBlockEntity::class)
        val INTERFACE = NodeType(InterfaceBlockEntity::class)

        operator fun get(i: Int) = map[i]

    }

    val index = map.size

    init {
        map.add(this)
    }

}