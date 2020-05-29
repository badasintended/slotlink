package io.gitlab.intended.storagenetworks.client.gui.screen;

import io.gitlab.intended.storagenetworks.StorageNetworks;
import io.gitlab.intended.storagenetworks.inventory.CraftingTerminalInventory;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Window;
import spinnery.common.BaseContainerScreen;
import spinnery.widget.*;
import spinnery.widget.api.Position;
import spinnery.widget.api.Size;

public class CraftingTerminalScreen extends BaseContainerScreen<CraftingTerminalInventory> {

    public CraftingTerminalScreen(CraftingTerminalInventory container) {
        super(container.name, container, container.player);

        WInterface mainInterface = getInterface();

        Position position;
        Size size;

        Window window = MinecraftClient.getInstance().getWindow();
        /*
        position = Position.of(0, 0, -1);

        size = Size.of(window.getScaledWidth(), window.getScaledHeight());

        WStaticImage background = mainInterface.createChild(WStaticImage::new, position, size);
        background.setTexture(StorageNetworks.id("gui/background"));
        background.setParent(mainInterface);
        background.setOnAlign(WAbstractWidget::center);
        background.center();
         */

        position = Position.of(0, 0, 0);

        size = Size.of(9 * 18 + 8, 3 * 18 + 108);

        WPanel mainPanel = mainInterface.createChild(WPanel::new, position, size).setParent(mainInterface);

        mainPanel.setLabel(container.name);
        mainPanel.setOnAlign(WAbstractWidget::center);
        mainPanel.center();
        mainInterface.add(mainPanel);

        position = Position.of(mainPanel, ((mainPanel.getWidth()) / 2) - (int) (18 * 4.5f), 3 * 18 + 24, 1);
        size = Size.of(18, 18);
        WSlot.addPlayerInventory(position, size, mainInterface);

        position = Position.of(mainPanel, 4, 19, 1);
        WSlot.addArray(position, size, mainInterface, 0, CraftingTerminalInventory.INVENTORY, 9, 3);

    }

    @Override
    public void render(int mouseX, int mouseY, float tickDelta) {
        super.render(mouseX, mouseY, tickDelta);
    }

}
