package badasintended.slotlink.mixin;

import net.minecraft.client.gui.widget.TextFieldWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TextFieldWidget.class)
public interface TextFieldWidgetAccessor {

    @Accessor
    int getFocusedTicks();

    @Accessor
    void setFocusedTicks(int value);

}
