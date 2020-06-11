package io.gitlab.intended.storagenetworks.gui.screen

import io.gitlab.intended.storagenetworks.block.BlockRegistry
import io.gitlab.intended.storagenetworks.gui.container.MasterContainer
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import spinnery.client.screen.BaseContainerScreen

@Environment(EnvType.CLIENT)
class MasterScreen(container: MasterContainer) : BaseContainerScreen<MasterContainer>(
    BlockRegistry.MASTER.name,
    container,
    container.player
)
