package io.gitlab.intended.storagenetworks.item;

import io.gitlab.intended.storagenetworks.StorageNetworks;
import io.gitlab.intended.storagenetworks.registry.BlockRegistry;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.World;

import java.util.List;

public abstract class ModItem extends Item {

    public static final ItemGroup ITEM_GROUP = FabricItemGroupBuilder.build(StorageNetworks.id("item_group"), () -> new ItemStack(BlockRegistry.MASTER));

    public ModItem(Settings settings) {
        super(settings);
    }

    public static Settings getSettings() {
        return new Settings().group(ITEM_GROUP);
    }

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(new TranslatableText(stack.getTranslationKey() + ".tooltip"));
    }

}
