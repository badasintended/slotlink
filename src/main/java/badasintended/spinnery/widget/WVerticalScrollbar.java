/*
 * This code is taken from Spinnery project, with some modification
 * allowing slotlink mod to use old feature that broken on newer version.
 *
 * Spinnery is licensed under LGPLv3.0.
 * (https://github.com/vini2003/Spinnery)
 */

package badasintended.spinnery.widget;

import badasintended.spinnery.client.render.BaseRenderer;
import badasintended.spinnery.common.utility.MouseUtilities;
import badasintended.spinnery.widget.api.Color;
import badasintended.spinnery.widget.api.WVerticalScrollable;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;

public class WVerticalScrollbar extends WAbstractWidget {
	protected WVerticalScrollable scrollable;
	protected float clickMouseY;
	protected boolean dragging = false;
	protected boolean hasArrows = true;

	public WVerticalScrollbar setScrollable(WVerticalScrollable scrollable) {
		this.scrollable = scrollable;
		return this;
	}

	public WVerticalScrollable getScrollable() {
		return scrollable;
	}

	public boolean hasArrows() {
		return hasArrows;
	}

	public void setHasArrows(boolean hasArrows) {
		this.hasArrows = hasArrows;
	}

	@Override
	public void draw(MatrixStack matrices, VertexConsumerProvider.Immediate.Immediate provider) {
		if (isHidden()) {
			return;
		}

		BaseRenderer.drawBeveledPanel(matrices, provider, getX(), getY(), getZ(), getWidth(), getHeight(), getStyle().asColor("scroll_line.top_left"), getStyle().asColor("scroll_line.background"), getStyle().asColor("scroll_line.bottom_right"));

		Color scrollerColor = getStyle().asColor("scroller.background_default");

		if (MouseUtilities.mouseX > getX() + 1 && MouseUtilities.mouseX < getWideX() - 1
		&&  MouseUtilities.mouseY > getScrollerY() + 1 && MouseUtilities.mouseY < getScrollerY() + getScrollerHeight() - 1 && !isHeld()) {
			scrollerColor = getStyle().asColor("scroller.background_hovered");
		} else if (isHeld()) {
			scrollerColor = getStyle().asColor("scroller.background_held");
		}

		BaseRenderer.drawBeveledPanel(matrices, provider, getX() + 1, getScrollerY() + 1, getZ(), getWidth() - 2, Math.min(getHighY() - getScrollerY(), getScrollerHeight()) - 2, getStyle().asColor("scroller.top_left"), scrollerColor, getStyle().asColor("scroller.bottom_right"));
	}

	@Override
	public void onMouseClicked(float mouseX, float mouseY, int mouseButton) {
		if (mouseButton == 0) {
			if (isWithinBounds(mouseX, mouseY)) {
				if (mouseY >= getScrollerY() && mouseY <= getScrollerY() + getScrollerHeight()) {
					dragging = true;
					clickMouseY = mouseY - getScrollerY();
				} else {
					dragging = false;

					if (mouseY > getScrollerY()) {
						if (((WVerticalScrollableContainer) scrollable).hasSmoothing()) {
							((WVerticalScrollableContainer) scrollable).kineticScrollDelta -= 3.5;
						} else {
							scrollable.scroll(0, -50);
						}
					} else {
						if (((WVerticalScrollableContainer) scrollable).hasSmoothing()) {
							((WVerticalScrollableContainer) scrollable).kineticScrollDelta += 3.5;
						} else {
							scrollable.scroll(0, +50);
						}
					}
				}
			} else {
				dragging = false;
			}
		}

		super.onMouseClicked(mouseX, mouseY, mouseButton);
	}

	public float getScrollerY() {
		float outerHeight = scrollable.getVisibleHeight();
		float innerHeight = scrollable.getUnderlyingHeight();
		float topOffset = scrollable.getStartOffsetY();
		float percentToEnd = topOffset / (innerHeight - outerHeight);
		float maximumOffset = getHeight() - getScrollerHeight();
		return getY() + (maximumOffset * percentToEnd);
	}

	public float getScrollerHeight() {
		float outerHeight = getHeight();
		float innerHeight = scrollable.getUnderlyingHeight();
		float calculated = (outerHeight * (outerHeight / Math.max(innerHeight, outerHeight)));
		return Math.max(calculated, 4);
	}

	@Override
	public void onMouseDragged(float mouseX, float mouseY, int mouseButton, double deltaX, double deltaY) {
		if (mouseButton == 0) {
			if (dragging) {
				double scrollerOffsetY = getScrollerY() + clickMouseY - mouseY;

				scrollable.scroll(0, scrollerOffsetY);
			}
		}

		super.onMouseDragged(mouseX, mouseY, mouseButton, deltaX, deltaY);
	}
}
