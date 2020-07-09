/*
 * This code is taken from Spinnery project, with some modification
 * allowing slotlink mod to use old feature that broken on newer version.
 *
 * Spinnery is licensed under LGPLv3.0.
 * (https://github.com/vini2003/Spinnery)
 */

package badasintended.spinnery.client.screen;

import badasintended.spinnery.widget.WInterface;
import net.minecraft.client.gui.hud.InGameHud;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InGameHudScreen {
	protected static WInterface hudInterface = null;
	protected static InGameHud inGameHudCache = null;
	protected static List<Runnable> onInitialize = new ArrayList<>();

	public static void onInitialize(InGameHud inGameHud) {
		inGameHudCache = inGameHud;
		hudInterface = ((Accessor) inGameHud).cursed_getInterface();
		for (Runnable runnable : onInitialize) {
			runnable.run();
		}
	}

	public static void addOnInitialize(Runnable... r) {
		onInitialize.addAll(Arrays.asList(r));
	}

	public static void removeOnInitialize(Runnable... r) {
		onInitialize.removeAll(Arrays.asList(r));
	}

	public static WInterface getInterface() {
		return hudInterface;
	}

	public static InGameHud getInGameHud() {
		return inGameHudCache;
	}

	public interface Accessor {
		WInterface cursed_getInterface();

		InGameHud cursed_getInGameHud();
	}
}
