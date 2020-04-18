package deirn.minecraft.fsn.block;

import deirn.minecraft.fsn.FSN;
import net.fabricmc.fabric.api.block.FabricBlockSettings;
import net.fabricmc.fabric.api.tools.FabricToolTags;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.world.BlockView;

import java.util.List;

public class FSNBlock extends Block {

    private static final Settings SETTINGS = FabricBlockSettings
            .of(Material.STONE)
            .breakByHand(true)
            .breakByTool(FabricToolTags.PICKAXES)
            .hardness(5F)
            .build();

    private Identifier id;

    public FSNBlock(String id) {
        super(SETTINGS);
        this.id = FSN.id(id);
    }

    public FSNBlock(String id, Settings settings) {
        super(settings);
        this.id = FSN.id(id);
    }

    public Identifier getId(){
        return this.id;
    }

    @Override
    public void buildTooltip(ItemStack stack, BlockView view, List<Text> tooltip, TooltipContext options) {
        tooltip.add(new TranslatableText(stack.getTranslationKey()+".tooltip"));
    }
}
