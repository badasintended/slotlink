/*
 * This code is taken from Spinnery project, with some modification
 * allowing slotlink mod to use old feature that broken on newer version.
 *
 * Spinnery is licensed under LGPLv3.0.
 * (https://github.com/vini2003/Spinnery)
 */

package badasintended.spinnery.widget;

import badasintended.spinnery.widget.api.Padding;
import badasintended.spinnery.widget.api.Position;
import badasintended.spinnery.widget.api.Size;
import badasintended.spinnery.widget.api.WPadded;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.lwjgl.glfw.GLFW;

import java.util.function.Consumer;

/**
 * A WAbstractSlider provides the basics necessary for a
 * general slider-like widget, like a {@link WHorizontalSlider}
 * and {@link WVerticalSlider}.
 */
@Environment(EnvType.CLIENT)
@SuppressWarnings("unchecked")
public abstract class WAbstractSlider extends WAbstractWidget implements WPadded {
	protected float min = 0;
	protected float max = 0;
	protected float progress = 0;
	protected float step = 1;
	protected boolean progressVisible = true;
	protected Consumer<WAbstractSlider> runnableOnProgressChange;

	/**
	 * Retrieves the minimum value for this slider.
	 *
	 * @return The minimum value for this slider.
	 */
	public float getMin() {
		return min;
	}

	/**
	 * Sets the minimum value for this slider.
	 *
	 * @param min The value to be set as the minimum value for this slider.
	 */
	public <W extends WAbstractSlider> W setMin(float min) {
		this.min = min;
		if (progress < min) {
			setProgress(min);
		}
		return (W) this;
	}

	/**
	 * Retrieves the maximum value for this slider.
	 *
	 * @return The maximum value for this slider.
	 */
	public float getMax() {
		return max;
	}

	/**
	 * Sets the maximum value for this slider.
	 *
	 * @param max The value to be set as the maximum value for this slider.
	 */
	public <W extends WAbstractSlider> W setMax(float max) {
		this.max = max;
		if (progress > max) {
			setProgress(max);
		}
		return (W) this;
	}

	/**
	 * Retrieves the step value for this slider, used for selecting a value with arrow keys.
	 *
	 * @return The step value for this slider.
	 */
	public float getStep() {
		return step;
	}

	/**
	 * Sets the step value for this slider, used for selecting a value with arrow keys.
	 *
	 * @param step The value to be used as the step value for this slider.
	 */
	public <W extends WAbstractSlider> W setStep(float step) {
		this.step = step;
		return (W) this;
	}

	/**
	 * Retrieves the progress value of this slider.
	 *
	 * @return The progress value of this slider.
	 */
	public float getProgress() {
		return progress;
	}

	/**
	 * Sets the progress value of this slider.
	 *
	 * @param progress The value to be used as the progress value for this slider.
	 */
	public <W extends WAbstractSlider> W setProgress(float progress) {
		float value = (float) (Math.floor(progress / step) * step);
		this.progress = Math.max(min, Math.min(max, value));
		if (runnableOnProgressChange != null) {
			runnableOnProgressChange.accept(this);
		}
		return (W) this;
	}

	/**
	 * Asserts whether the progress is visible or not.
	 *
	 * @return True if visible; false if not.
	 */
	public boolean isProgressVisible() {
		return progressVisible;
	}

	/**
	 * Sets whether the progress is visible or not.
	 *
	 * @param progressVisible Boolean representing visible (true) or not visible (false).
	 */
	public <W extends WAbstractSlider> W setProgressVisible(boolean progressVisible) {
		this.progressVisible = progressVisible;
		return (W) this;
	}

	/**
	 * Retrieves the progress value as a String.
	 *
	 * @return The progress value as a String.
	 */
	public String getFormattedProgress() {
		return String.valueOf(progress);
	}

	/**
	 * Retrieves the position at which the progress text should be rendered.
	 *
	 * @return The position at which the progress text should be rendered.
	 */
	abstract public Position getProgressTextAnchor();

	/**
	 * Retrieves the percentage completion of the progress value in relation to the minimum and maximum values.
	 *
	 * @return The percentage completion of the progress value in relation to the minimum and maximum values.
	 */
	public float getPercentComplete() {
		return (float) Math.max(0, (progress - min) / (max - min));
	}

	/**
	 * Retrieves the event dispatched when the progress value changes.
	 *
	 * @return The event dispatched when the progress value changes.
	 */
	public Consumer<WAbstractSlider> getOnProgressChange() {
		return runnableOnProgressChange;
	}

	/**
	 * Sets the event dispatched when the progress value changes.
	 *
	 * @param runnableOnProgressChange The event to be dispatched when the progress value changes.
	 */
	public <W extends WAbstractSlider> W setOnProgressChange(Consumer<WAbstractSlider> runnableOnProgressChange) {
		this.runnableOnProgressChange = runnableOnProgressChange;
		return (W) this;
	}

	/**
	 * Retrieves the size of this slider's progress indicator knob.
	 *
	 * @return The size of this slider's progress indicator knob.
	 */
	public abstract Size getKnobSize();

	@Override
	public Padding getPadding() {
		return getStyle().asPadding("padding");
	}

	@Override
	public boolean isFocusedMouseListener() {
		return true;
	}

	@Override
	public boolean isFocusedKeyboardListener() {
		return true;
	}

	@Override
	public void onKeyPressed(int keyCode, int character, int keyModifier) {
		if (keyCode == GLFW.GLFW_KEY_KP_ADD || keyCode == GLFW.GLFW_KEY_EQUAL) {
			progress = Math.min(progress + step, max);
		}
		if (keyCode == GLFW.GLFW_KEY_KP_SUBTRACT || keyCode == GLFW.GLFW_KEY_MINUS) {
			progress = Math.max(progress - step, min);
		}
		super.onKeyPressed(keyCode, character, keyModifier);
	}

	@Override
	public void onMouseClicked(float mouseX, float mouseY, int mouseButton) {
		updatePosition(mouseX, mouseY);
		super.onMouseClicked(mouseX, mouseY, mouseButton);
	}

	protected abstract void updatePosition(float mouseX, float mouseY);

	@Override
	public void onMouseDragged(float mouseX, float mouseY, int mouseButton, double deltaX, double deltaY) {
		updatePosition(mouseX, mouseY);
		super.onMouseDragged(mouseX, mouseY, mouseButton, deltaX, deltaY);
	}
}
