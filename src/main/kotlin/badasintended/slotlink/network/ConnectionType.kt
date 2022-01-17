package badasintended.slotlink.network

import badasintended.slotlink.block.entity.CableBlockEntity
import badasintended.slotlink.block.entity.ExportCableBlockEntity
import badasintended.slotlink.block.entity.ImportCableBlockEntity
import badasintended.slotlink.block.entity.InterfaceBlockEntity
import badasintended.slotlink.block.entity.LinkCableBlockEntity
import badasintended.slotlink.block.entity.MasterBlockEntity
import badasintended.slotlink.block.entity.RequestBlockEntity
import kotlin.reflect.KClass

class ConnectionType<T : Any>(
    val clazz: KClass<T>,
    val save: Boolean = true
) {

    companion object {

        private val map = arrayListOf<ConnectionType<*>>()

        val MASTER = ConnectionType(MasterBlockEntity::class, false)
        val CABLE = ConnectionType(CableBlockEntity::class)
        val REQUEST = ConnectionType(RequestBlockEntity::class)
        val LINK = ConnectionType(LinkCableBlockEntity::class)
        val EXPORT = ConnectionType(ExportCableBlockEntity::class)
        val IMPORT = ConnectionType(ImportCableBlockEntity::class)
        val INTERFACE = ConnectionType(InterfaceBlockEntity::class)

        operator fun get(i: Int) = map[i]

    }

    val index = map.size

    init {
        map.add(this)
    }

}