package io.gitlab.intended.storagenetworks.container

import io.github.cottonmc.cotton.gui.widget.WGridPanel
import io.github.cottonmc.cotton.gui.widget.WItemSlot
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.PacketByteBuf

class MasterContainer(syncId: Int, player: PlayerEntity, buf: PacketByteBuf) : ModContainer(syncId, player, buf) {

    init {
        val root = WGridPanel()
        setRootPanel(root)
        root.setSize(300, 200)

        val itemSlot = WItemSlot.of(blockInventory, 0)
        root.add(itemSlot, 4, 1)

        root.add(createPlayerInventoryPanel(), 0, 3)
        root.validate(this)
    }

}
