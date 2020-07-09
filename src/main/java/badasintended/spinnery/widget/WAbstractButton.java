/*
 * This code is taken from Spinnery project, with some modification
 * allowing slotlink mod to use old feature that broken on newer version.
 *
 * Spinnery is licensed under LGPLv3.0.
 * (https://github.com/vini2003/Spinnery)
 */

package badasintended.spinnery.widget;

/**
 * A WAbstractBar provides the basics necessary for a
 * general button-like widget, like a {@link WButton}
 * and {@link WTexturedButton}.
 */
public abstract class WAbstractButton extends WAbstractWidget {
	protected boolean lowered = false;
	protected int ticks = 0;
	protected int delayTicks = 1;

	/**
	 * Method called every tick, calculates whether the button is lowered or not.
	 */
	@Override
	public void tick() {
		lowered = ticks > 0;
		ticks -= ticks > 0 ? 1 : 0;
	}

	/**
	 * Method called when the mouse is clicked, sets the button as lowered and dispatches events.
	 */
	@Override
	public void onMouseClicked(float mouseX, float mouseY, int mouseButton) {
		setLowered(true);
		super.onMouseClicked(mouseX, mouseY, mouseButton);
	}

	/**
	 * Retrieves the delay, in ticks, during which this button stays lowered.
	 *
	 * @return The delay, in ticks, during which this button stays lowered.
	 */
	public int getDelay() {
		return delayTicks;
	}

	/**
	 * Sets the delay, in ticks, during which this button stays lowered.
	 *
	 * @param delayTicks Ticks to be used as delay.
	 */
	public <W extends WAbstractButton> W setDelay(int delayTicks) {
		this.delayTicks = delayTicks;
		return (W) this;
	}

	/**
	 * Asserts whether the button is lowered or not.
	 *
	 * @return True if lowered; false if not.
	 */
	public boolean isLowered() {
		return lowered;
	}

	/**
	 * Sets the button's lowered state.
	 *
	 * @param toggleState Boolean representing lowered (true) or not lowered (false).
	 */
	public <W extends WAbstractButton> W setLowered(boolean toggleState) {
		this.lowered = toggleState;
		this.ticks = toggleState ? getDelay() : 0;
		return (W) this;
	}
}
