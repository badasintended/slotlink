/*
 * This code is taken from Spinnery project, with some modification
 * allowing slotlink mod to use old feature that broken on newer version.
 *
 * Spinnery is licensed under LGPLv3.0.
 * (https://github.com/vini2003/Spinnery)
 */

package badasintended.spinnery.widget.api;

import blue.endless.jankson.JsonElement;
import blue.endless.jankson.JsonPrimitive;

/**
 * Data object representing a color. Components may be accessed via the fields
 * A, R, G, B; the color int may be accessed via the fields
 * RGB and ARGB.
 */
public class Color implements JanksonSerializable {
	public static final Color DEFAULT = Color.of(0xffffffff);

	public float A = 1.0f, R = 1.0f, G = 1.0f, B = 1.0f;

	public int ARGB = 0xffffffff;
	public int RGB = 0xffffff;

	public Color(int r, int g, int b, int a) {
		this(r / 255f, g / 255f, b / 255f, a / 255f);
	}

	public Color(float r, float g, float b, float a) {
		R = r;
		G = g;
		B = b;
		A = a;
		RGB = packRgb((int) Math.floor(r * 255.0F), (int) Math.floor(g * 255.0F), (int) Math.floor(b * 255.0F));
		ARGB = RGB + ((int) (a * 255) << 24);
	}

	public static int packRgb(int r, int g, int b) {
		int i = (r << 8) + g;
		i = (i << 8) + b;
		return i;
	}

	public Color(String ARGB) {
		if (ARGB.length() == 8) {
			R = Integer.decode("0x" + ARGB.substring(2, 4)) / 255f;
			G = Integer.decode("0x" + ARGB.substring(4, 6)) / 255f;
			B = Integer.decode("0x" + ARGB.substring(6, 8)) / 255f;
			this.RGB = Integer.decode(ARGB);
			this.ARGB = RGB + (0xFF << 24);
		} else if (ARGB.length() == 10) {
			int alpha = Integer.decode("0x" + ARGB.substring(2, 4));
			A = alpha / 255f;
			R = Integer.decode("0x" + ARGB.substring(4, 6)) / 255f;
			G = Integer.decode("0x" + ARGB.substring(6, 8)) / 255f;
			B = Integer.decode("0x" + ARGB.substring(8, 10)) / 255f;
			this.RGB = Integer.decode("0x" + ARGB.substring(4));
			this.ARGB = RGB + (alpha << 24);
		}
	}

	public static Color of(String ARGB) {
		return new Color(ARGB);
	}

	public static Color of(Number color) {
		int intColor = color.intValue();
		int a = (intColor >> 24) & 0xFF;
		int r = (intColor >> 16) & 0xFF;
		int g = (intColor >> 8) & 0xFF;
		int b = (intColor & 0xFF);
		return new Color(r, g, b, a);
	}

	@Override
	public JsonElement toJson() {
		return new JsonPrimitive(ARGB);
	}
}
