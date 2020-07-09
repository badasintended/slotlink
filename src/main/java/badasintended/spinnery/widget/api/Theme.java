/*
 * This code is taken from Spinnery project, with some modification
 * allowing slotlink mod to use old feature that broken on newer version.
 *
 * Spinnery is licensed under LGPLv3.0.
 * (https://github.com/vini2003/Spinnery)
 */

package badasintended.spinnery.widget.api;

import badasintended.spinnery.Spinnery;
import badasintended.spinnery.common.registry.ThemeRegistry;
import badasintended.spinnery.common.utility.JanksonUtilities;
import blue.endless.jankson.JsonArray;
import blue.endless.jankson.JsonElement;
import blue.endless.jankson.JsonObject;
import blue.endless.jankson.JsonPrimitive;
import com.google.common.collect.ImmutableMap;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.Level;

import java.util.HashMap;
import java.util.Map;

/**
 * A class that holds the data used by Spinnery for widget themes.
 * Most importantly, this contains Styles for all widgets in the theme.
 */
public class Theme {
	protected final Identifier id;
	protected final Identifier parent;
	protected final ImmutableMap<Identifier, Style> styles;

	protected Theme(Identifier id, Identifier parent, Map<Identifier, Style> styles) {
		this.id = id;
		this.parent = parent;
		this.styles = ImmutableMap.copyOf(styles);
	}

	private static JsonObject createObject(Map<String, JsonElement> map) {
		JsonObject obj = new JsonObject();
		obj.putAll(map);
		return obj;
	}

	// Dereference $ vars
	private static void processRefs(Map<String, JsonElement> style, Map<String, JsonElement> refs) {
		for (String property : style.keySet()) {
			JsonElement el = style.get(property);
			String strValue = JanksonUtilities.asString(el).orElse("");

			// Look up variables
			if (strValue.startsWith("$") && !strValue.startsWith("$$")) {
				style.put(property, refs.get(strValue));
				continue;
			}

			if (strValue.startsWith("$$")) {
				style.put(property, new JsonPrimitive(strValue.substring(1)));
			} else {
				style.put(property, el);
			}
		}
	}

	private static void flattenObject(Map<String, JsonElement> collector, String rootKey, JsonObject object) {
		String prefix = rootKey == null ? "" : rootKey + ".";
		for (String key : object.keySet()) {
			JsonElement element = object.get(key);
			if (element instanceof JsonObject) {
				flattenObject(collector, key, (JsonObject) element);
			} else {
				collector.put(prefix + key, element);
			}
		}
	}

	public static Theme of(Identifier themeId, JsonObject themeDef) {
		// Add parent logic
		JsonElement parentProp = themeDef.get("parent");
		Identifier parent = JanksonUtilities.asString(parentProp).map(Identifier::new).orElse(null);

		// Generic validation
		JsonObject themeProps = themeDef.getObject("theme");
		if (themeProps == null) {
			Spinnery.LOGGER.warn("Invalid theme definition for theme {}", themeId);
			return null;
		}
		Spinnery.LOGGER.log(Level.INFO, "Parsing theme {}", themeId);

		// Get variables
		JsonObject themeVars = themeDef.getObject("vars");
		Map<String, JsonElement> vars = new HashMap<>();
		if (themeVars != null) {
			for (String varKey : themeVars.keySet()) {
				vars.put("$" + varKey, themeVars.get(varKey));
			}
		}

		// Get prototypes
		JsonObject themePrototypes = themeDef.getObject("prototypes");
		Map<String, JsonObject> prototypes = new HashMap<>();
		if (themePrototypes != null) {
			for (String varKey : themePrototypes.keySet()) {
				Map<String, JsonElement> flatProto = new HashMap<>();
				JsonObject protoObj = themePrototypes.getObject(varKey);
				if (protoObj == null) continue;
				flattenObject(flatProto, null, protoObj);
				prototypes.put("$" + varKey, createObject(flatProto));
			}
		}

		// Process widget styles
		Map<Identifier, Style> styles = new HashMap<>();
		for (String widgetId : themeProps.keySet()) {
			Map<String, JsonElement> properties = new HashMap<>();
			JsonObject widgetProps = themeProps.getObject(widgetId);
			if (widgetProps == null) {
				Spinnery.LOGGER.warn("Invalid properties definition for widget {}, skipping", widgetId);
				continue;
			}

			// Apply prototypes
			JsonElement protoArray = widgetProps.remove("$extend");
			if (protoArray instanceof JsonArray) {
				for (JsonElement el : (JsonArray) protoArray) {
					String protoName = JanksonUtilities.asString(el).orElse("");
					JsonObject protoObj = prototypes.get(protoName);
					if (!protoName.isEmpty() && protoObj != null) {
						properties.putAll(protoObj);
					}
				}
			}

			// Apply actual values
			flattenObject(properties, null, widgetProps);
			processRefs(properties, vars);
			styles.put(new Identifier(widgetId), new Style(properties));
		}
		return new Theme(themeId, parent, styles);
	}

	/**
	 * Retrieves the Identifier of this theme.
	 *
	 * @return The Identifier of this theme.
	 */
	public Identifier getId() {
		return id;
	}

	/**
	 * Retrieves the Style for a give widget.
	 *
	 * @param widgetId Identifier of the widget, which must have been registered via WidgetRegistry.
	 * @return Style of the given widget.
	 */
	public Style getStyle(Identifier widgetId) {
		Style style = styles.get(widgetId);
		if (parent != null) {
			Style baseStyle = ThemeRegistry.getStyle(parent, widgetId);
			if (style == null) return baseStyle;
			style = Style.of(baseStyle).mergeFrom(style);
		}
		return style == null ? new Style() : style;
	}
}
