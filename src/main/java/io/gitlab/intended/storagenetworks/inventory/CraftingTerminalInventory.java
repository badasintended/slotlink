package io.gitlab.intended.storagenetworks.inventory;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;
import spinnery.common.BaseInventory;
import spinnery.widget.WInterface;
import spinnery.widget.WSlot;

public class CraftingTerminalInventory extends ModInventory {

    public static final int INVENTORY = 1;

    public CraftingTerminalInventory(int syncId, Identifier id, PlayerEntity player, PacketByteBuf buf) {
        super(syncId, id, player, buf);

        WInterface mainInterface = getInterface();
        BaseInventory inventory = new BaseInventory(27);

        getInventories().put(INVENTORY, inventory);
        mainInterface.createChild(WSlot::new);
        WSlot.addHeadlessArray(mainInterface, 0, INVENTORY, 9, 3);
        WSlot.addHeadlessPlayerInventory(mainInterface);
    }

}
