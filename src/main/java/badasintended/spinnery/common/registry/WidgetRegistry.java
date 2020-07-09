/*
 * This code is taken from Spinnery project, with some modification
 * allowing slotlink mod to use old feature that broken on newer version.
 *
 * Spinnery is licensed under LGPLv3.0.
 * (https://github.com/vini2003/Spinnery)
 */

package badasintended.spinnery.common.registry;

import badasintended.spinnery.client.configuration.widget.WOptionField;
import badasintended.spinnery.widget.*;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import net.minecraft.util.Identifier;

/**
 * Registers all of Spinnery's widgets with Spinnery.
 */
public class WidgetRegistry {
	private static BiMap<Identifier, Class<? extends WAbstractWidget>> widgetMap = HashBiMap.create();

	public static Class<? extends WAbstractWidget> get(String className) {
		for (Class<? extends WAbstractWidget> widgetClass : widgetMap.values()) {
			if (widgetClass.getName().equals(className)) {
				return widgetClass;
			}
		}
		return null;
	}

	public static Class<? extends WAbstractWidget> get(Identifier id) {
		return widgetMap.get(id);
	}

	public static Identifier getId(Class<? extends WAbstractWidget> wClass) {
		return widgetMap.inverse().get(wClass);
	}

	public static void initialize() {
		register(new Identifier("spinnery", "widget"), WAbstractWidget.class);
		register(new Identifier("spinnery", "button"), WButton.class);
		register(new Identifier("spinnery", "panel"), WPanel.class);
		register(new Identifier("spinnery", "slot"), WSlot.class);
		register(new Identifier("spinnery", "static_text"), WStaticText.class);
		register(new Identifier("spinnery", "vertical_slider"), WVerticalSlider.class);
		register(new Identifier("spinnery", "vertical_scrollbar"), WVerticalScrollbar.class);
		register(new Identifier("spinnery", "vertical_scrollable_container"), WVerticalScrollableContainer.class);
		register(new Identifier("spinnery", "text_field"), WTextField.class);
		register(new Identifier("spinnery", "item"), WItem.class);
		register(new Identifier("spinnery", "vertical_arrow_up"), WVerticalArrowUp.class);
		register(new Identifier("spinnery", "vertical_arrow_down"), WVerticalArrowDown.class);
		register(new Identifier("spinnery", "option_field"), WOptionField.class);
	}

	public static void register(Identifier id, Class<? extends WAbstractWidget> wClass) {
		widgetMap.put(id, wClass);
	}
}
