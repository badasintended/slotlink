package badasintended.slotlink.client.gui.screen

import badasintended.slotlink.client.gui.widget.*
import badasintended.slotlink.common.registry.NetworkRegistry
import badasintended.slotlink.common.util.*
import badasintended.slotlink.gui.screen.TransferScreenHandler
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry
import net.minecraft.item.ItemStack
import net.minecraft.util.collection.DefaultedList
import net.minecraft.util.math.Direction
import spinnery.widget.*

class TransferScreen(c: TransferScreenHandler) : ModScreen<TransferScreenHandler>(c) {

    private val filterSlots = arrayListOf<WFilterSlot>()
    private var isBlackList = false
    private var side = Direction.DOWN

    init {
        val main = root.createChild(
            { WPanel() },
            positionOf(0, 0, 0),
            sizeOf(176, 166)
        )
        main.setParent<WAbstractWidget>(root)
        main.setOnAlign(WAbstractWidget::center)
        main.center()
        root.add(main)

        val label = main.createChild(
            { WTranslatableLabel("container.slotlink.transfer", c.pos.x, c.pos.y, c.pos.z) },
            positionOf(main, 0, 6)
        )
        label.centerX()

        for (i in 0 until 9) {
            val slot = main.createChild(
                { WFilterSlot(this::save) },
                positionOf(
                    main,
                    (((i % 3) * 18) + 61),
                    (((i / 3) * 18) + 16)
                ),
                sizeOf(18)
            )
            slot.setNumber<WSlot>(1, i)
            filterSlots.add(slot)
        }

        main.createChild(
            { WSideButton(this::side, this::save) },
            positionOf(main, 27, 36),
            sizeOf(14)
        )

        main.createChild(
            { WFilterButton(this::isBlackList, this::save) },
            positionOf(main, 135, 36),
            sizeOf(14)
        )

        val playerInvLabel = main.createChild(
            { WTranslatableLabel("container.inventory") },
            positionOf(main, 8, 72)
        )

        for (i in 0 until 27) {
            val slot = main.createChild(
                { WVanillaSlot() },
                positionOf(
                    playerInvLabel,
                    (((i % 9) * 18) - 1),
                    (((i / 9) * 18) + 11)
                ),
                sizeOf(18)
            )
            slot.setNumber<WSlot>(0, i + 9)
        }

        for (i in 0 until 9) {
            val slot = main.createChild(
                { WVanillaSlot() },
                positionOf(
                    playerInvLabel,
                    (((i % 9) * 18) - 1),
                    69
                ),
                sizeOf(18)
            )
            slot.setNumber<WSlot>(0, i)
        }
    }

    fun setMode(side: Direction, isBlackList: Boolean, filter: DefaultedList<ItemStack>) {
        this.side = side
        this.isBlackList = isBlackList
        filterSlots.forEachIndexed { i, s -> s.setStack<WSlot>(filter[i]) }
    }

    private fun save() {
        val buf = buf()
        buf.writeBlockPos(c.pos)
        buf.writeInt(side.id)
        buf.writeBoolean(isBlackList)
        filterSlots.forEach { buf.writeItemStack(it.stack) }

        ClientSidePacketRegistry.INSTANCE.sendToServer(NetworkRegistry.TRANSFER_WRITE, buf)
    }

}
