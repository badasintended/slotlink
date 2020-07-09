/*
 * This code is taken from Spinnery project, with some modification
 * allowing slotlink mod to use old feature that broken on newer version.
 *
 * Spinnery is licensed under LGPLv3.0.
 * (https://github.com/vini2003/Spinnery)
 */

package badasintended.spinnery.widget.api;

/**
 * Generic interface providing standard event hook methods.
 */
public interface WEventListener {
	/**
	 * Event dispatched when a keyboard key is pressed.
	 *
	 * @param keyCode     Keycode associated with pressed key.
	 * @param character   Character associated with pressed key.
	 * @param keyModifier Modifier(s) associated with pressed key.
	 */
	void onKeyPressed(int keyCode, int character, int keyModifier);

	/**
	 * Event dispatched when a keyboard key is released.
	 *
	 * @param keyCode     Keycode associated with pressed key.
	 * @param character   Character associated with pressed key.
	 * @param keyModifier Modifier(s) associated with pressed key.
	 */
	void onKeyReleased(int keyCode, int character, int keyModifier);

	/**
	 * Event dispatched when a key with a valid associated character is called.
	 *
	 * @param character Character associated with key pressed.
	 * @param keyCode   Keycode associated with key pressed.
	 */
	void onCharTyped(char character, int keyCode);

	/**
	 * Event dispatched when a widget gains focus.
	 */
	void onFocusGained();

	/**
	 * Event dispatched when a widget loses focus.
	 */
	void onFocusReleased();

	/**
	 * Event dispatched when the mouse is released.
	 *
	 * @param mouseX      Horizontal position of mouse cursor.
	 * @param mouseY      Vertical position of mouse cursor.
	 * @param mouseButton Mouse button released.
	 */
	void onMouseReleased(float mouseX, float mouseY, int mouseButton);

	/**
	 * Event dispatched when the mouse is clicked.
	 *
	 * @param mouseX      Horizontal position of mouse cursor.
	 * @param mouseY      Vertical position of mouse cursor.
	 * @param mouseButton Mouse button clicked.
	 */
	void onMouseClicked(float mouseX, float mouseY, int mouseButton);

	/**
	 * Event dispatched when the mouse is dragged.
	 *
	 * @param mouseX      Horizontal position of mouse cursor.
	 * @param mouseY      Vertical position of mouse cursor.
	 * @param mouseButton Mouse button dragged.
	 * @param deltaX      Horizontal delta of mouse drag.
	 * @param deltaY      Vertical delta of mouse drag.
	 */
	void onMouseDragged(float mouseX, float mouseY, int mouseButton, double deltaX, double deltaY);

	/**
	 * Event dispatched when the mouse is moved.
	 *
	 * @param mouseX Horizontal position of mouse cursor.
	 * @param mouseY Vertical position of mouse cursor.
	 */
	void onMouseMoved(float mouseX, float mouseY);

	/**
	 * Event dispatched when the mouse wheel is scrolled.
	 *
	 * @param mouseX Horizontal position of the mouse cursor.
	 * @param mouseY Vertical position of the mouse cursor.
	 * @param deltaY Vertical delta of mouse scroll.
	 */
	void onMouseScrolled(float mouseX, float mouseY, double deltaY);

	/**
	 * Event dispatched when a tooltip should be drawn, however, currently not implemented.
	 *
	 * @param mouseX Horizontal position of mouse cursor.
	 * @param mouseY Vertical position of mouse cursor.
	 */
	void onDrawTooltip(float mouseX, float mouseY);

	/**
	 * Event dispatched on game GUI alignment, e.g. when the game window is resized.
	 */
	void onAlign();
}
