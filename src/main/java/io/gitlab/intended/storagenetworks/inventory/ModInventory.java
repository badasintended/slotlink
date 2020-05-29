package io.gitlab.intended.storagenetworks.inventory;

import io.gitlab.intended.storagenetworks.block.ModBlock;
import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.world.World;
import spinnery.common.BaseContainer;

public class ModInventory extends BaseContainer {

    public Text name;
    public PlayerEntity player;

    public ModInventory(int syncId, Identifier id, PlayerEntity player, PacketByteBuf buf) {
        super(syncId, player.inventory);

        name = buf.readText();
        this.player = player;
    }

    public static ActionResult open(ModBlock block, PlayerEntity player) {
        if (!player.world.isClient) {
            ContainerProviderRegistry.INSTANCE.openContainer(block.ID, player, buf -> buf.writeText(new TranslatableText(block.getTranslationKey())));
            return ActionResult.SUCCESS;
        } else {
            return ActionResult.PASS;
        }
    }

}
