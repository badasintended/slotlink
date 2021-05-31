package badasintended.slotlink.util

import badasintended.slotlink.mixin.CraftingScreenHandlerAccessor
import badasintended.slotlink.mixin.TextFieldWidgetAccessor
import net.minecraft.client.gui.widget.TextFieldWidget
import net.minecraft.screen.CraftingScreenHandler

inline val CraftingScreenHandler.input get() = (this as CraftingScreenHandlerAccessor).input
inline val CraftingScreenHandler.result get() = (this as CraftingScreenHandlerAccessor).result

inline var TextFieldWidget.focusedTicks
    get() = (this as TextFieldWidgetAccessor).focusedTicks
    set(value) = (this as TextFieldWidgetAccessor).run { focusedTicks = value }