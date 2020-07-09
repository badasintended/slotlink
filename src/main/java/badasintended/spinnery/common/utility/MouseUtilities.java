/*
 * This code is taken from Spinnery project, with some modification
 * allowing slotlink mod to use old feature that broken on newer version.
 *
 * Spinnery is licensed under LGPLv3.0.
 * (https://github.com/vini2003/Spinnery)
 */

package badasintended.spinnery.common.utility;

import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;

public class MouseUtilities {
	public static final long nanoDelay = 150000000;
	public static float mouseX = 0, mouseY = 0;
	public static long lastNanos = 0;

	/**
	 * Retrieve the interval in nanoseconds since the last update.
	 *
	 * @return Interval in nanoseconds since the last update.
	 */
	public static long nanoInterval() {
		return System.nanoTime() - lastNanos();
	}

	/**
	 * Retrieve the last time, in nanoseconds, when update was called.
	 *
	 * @return The last time, in nanoseconds, when update was called.
	 */
	public static long lastNanos() {
		return lastNanos;
	}

	/**
	 * Retrieve the delay, in nanoseconds, used for calculations.
	 *
	 * @return The delay, in nanoseconds.
	 */
	public static long nanoDelay() {
		return nanoDelay;
	}

	/**
	 * Update the last time, in nanoseconds, when update was called.
	 */
	public static void nanoUpdate() {
		lastNanos = System.nanoTime();
	}

	/**
	 * Set the cursor to the GLFW standard vertical resize cursor.
	 */
	public static void enableDragCursor() {
		GLFW.glfwSetCursor(MinecraftClient.getInstance().getWindow().getHandle(), GLFW.glfwCreateStandardCursor(GLFW.GLFW_VRESIZE_CURSOR));
	}

	/**
	 * Set the cursor to the GLFW standard arrow cursor.
	 */
	public static void enableArrowCursor() {
		GLFW.glfwSetCursor(MinecraftClient.getInstance().getWindow().getHandle(), GLFW.glfwCreateStandardCursor(GLFW.GLFW_ARROW_CURSOR));
	}
}
