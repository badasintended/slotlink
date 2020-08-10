package badasintended.slotlink.client.gui.screen

import badasintended.slotlink.client.gui.widget.*
import badasintended.slotlink.common.util.*
import badasintended.slotlink.gui.screen.LinkScreenHandler
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import spinnery.widget.*

@Environment(EnvType.CLIENT)
open class LinkScreen<H : LinkScreenHandler>(c: H) : ModScreen<H>(c) {

    protected val main: WPanel

    protected val filterButton: WSlotButton

    init {
        main = root.createChild(::WPanel, positionOf(0, 0, 0), sizeOf(176, 166))
        main.setParent<WAbstractWidget>(root)
        main.setOnAlign(WAbstractWidget::center)
        main.center()
        root.add(main)

        val label = main.createChild(
            { WTranslatableLabel("container.slotlink.cable", c.pos.x, c.pos.y, c.pos.z) }, positionOf(main, 0, 6)
        )
        label.centerX()

        main.createChild({ WHelpTooltip("container.slotlink.cable", 12) }, positionOf(main, 160, 6), sizeOf(8))

        for (i in 0 until 9) {
            val slot = main.createChild(
                { WFilterSlot { c.filter.set(i, it) } }, positionOf(main, (((i % 3) * 18) + 61), (((i / 3) * 18) + 16)),
                sizeOf(18)
            )
            slot.setNumber<WSlot>(1, i)
            slot.setStack<WSlot>(c.filter[i])
        }

        val priorityUp = main.createChild({
            WSlotButton()
                .tlKey("container.slotlink.cable.priority")
                .texture(tex("gui/triangle_up"))
                .onClick { c.priority += 1 }
        }, positionOf(main, 43, 18), sizeOf(14))

        main.createChild({ WDynamicLabel { "${c.priority}" } }, positionOf(priorityUp, 0, 23))

        main.createChild({
            WSlotButton()
                .tlKey("container.slotlink.cable.priority")
                .texture(tex("gui/triangle_down"))
                .onClick { c.priority -= 1 }
        }, positionOf(priorityUp, 0, 36), sizeOf(14))

        filterButton = main.createChild({
            WSlotButton()
                .tlKey { "container.slotlink.cable.${if (c.isBlacklist) "black" else "white"}list" }
                .texture { tex("gui/${if (c.isBlacklist) "black" else "white"}list") }
                .onClick { c.isBlacklist = !c.isBlacklist }
        }, positionOf(main, 119, 36), sizeOf(14))

        val playerInvLabel = main.createChild(
            { WTranslatableLabel("container.inventory") }, positionOf(main, 8, 72)
        )

        for (i in 0 until 27) {
            val slot = main.createChild(
                ::WVanillaSlot, positionOf(playerInvLabel, (((i % 9) * 18) - 1), (((i / 9) * 18) + 11)), sizeOf(18)
            )
            slot.setNumber<WSlot>(0, i + 9)
        }

        for (i in 0 until 9) {
            val slot = main.createChild(
                ::WVanillaSlot, positionOf(playerInvLabel, (((i % 9) * 18) - 1), 69), sizeOf(18)
            )
            slot.setNumber<WSlot>(0, i)
        }
    }

}
