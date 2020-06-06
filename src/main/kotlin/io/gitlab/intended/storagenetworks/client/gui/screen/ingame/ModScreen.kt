package io.gitlab.intended.storagenetworks.client.gui.screen.ingame

import io.github.cottonmc.cotton.gui.client.CottonInventoryScreen
import io.gitlab.intended.storagenetworks.container.MasterContainer
import io.gitlab.intended.storagenetworks.container.RequestContainer
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment

@Environment(EnvType.CLIENT)
class RequestScreen(container: RequestContainer) :
    CottonInventoryScreen<RequestContainer>(container, container.player)

@Environment(EnvType.CLIENT)
class MasterScreen(container: MasterContainer) :
    CottonInventoryScreen<MasterContainer>(container, container.player)
