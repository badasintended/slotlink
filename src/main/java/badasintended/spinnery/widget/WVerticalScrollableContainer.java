/*
 * This code is taken from Spinnery project, with some modification
 * allowing slotlink mod to use old feature that broken on newer version.
 *
 * Spinnery is licensed under LGPLv3.0.
 * (https://github.com/vini2003/Spinnery)
 */

package badasintended.spinnery.widget;

import badasintended.spinnery.client.integration.SpinneryConfigurationScreen;
import badasintended.spinnery.client.render.BaseRenderer;
import badasintended.spinnery.client.utility.ScissorArea;
import badasintended.spinnery.common.utility.MouseUtilities;
import badasintended.spinnery.widget.api.*;
import com.google.common.collect.ImmutableSet;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import org.lwjgl.glfw.GLFW;

import java.util.*;

@SuppressWarnings({"UnusedReturnValue", "unchecked"})
public class WVerticalScrollableContainer extends WAbstractWidget implements WDrawableCollection, WModifiableCollection, WVerticalScrollable, WDelegatedEventListener {
	protected Set<WAbstractWidget> widgets = new HashSet<>();
	protected List<WLayoutElement> orderedWidgets = new ArrayList<>();

	protected WVerticalScrollbar scrollbar;

	protected WVerticalArrowUp verticalArrowUp;
	protected WVerticalArrowDown verticalArrowDown;

	protected float scrollbarWidth = 12;

	protected float divisionSpace = 2;
	protected int borderSpace = 0;
	protected int fadeSpace = 12;

	protected float lastScrollX = 0;
	protected float lastScrollY = 0;

	protected float lastDragScrollY = 0;

	protected long lastDragScrollMilliseconds = 0;

	protected float offsetY = 0;

	protected float kineticScrollDelta = 0;

	protected float kineticReductionCoefficient = SpinneryConfigurationScreen.kineticReductionCoefficient.getValue();
	protected float kineticAccelerationCoefficient = SpinneryConfigurationScreen.kineticAccelerationCoefficient.getValue();

	protected float dragScrollAccelerationCoefficient = SpinneryConfigurationScreen.dragScrollAccelerationCoefficient.getValue();

	protected boolean isDragScrolling = false;

	protected boolean hasFade = SpinneryConfigurationScreen.fading.getValue();

	protected boolean hasSmoothing = SpinneryConfigurationScreen.smoothing.getValue();

	protected boolean hasArrows = SpinneryConfigurationScreen.arrows.getValue();

	public WVerticalScrollableContainer() {
		scrollbar = new WVerticalScrollbar().setScrollable(this).setParent(this);
	}

	public boolean isScrollbarVisible() {
		return !scrollbar.isHidden();
	}

	public <W extends WVerticalScrollableContainer> W setScrollbarVisible(boolean visible) {
		scrollbar.setHidden(!visible);
		return (W) this;
	}

	public WVerticalScrollbar getScrollbar() {
		return scrollbar;
	}

	public WVerticalArrowUp getVerticalArrowUp() {
		return verticalArrowUp;
	}

	public void setVerticalArrowUp(WVerticalArrowUp verticalArrowUp) {
		this.verticalArrowUp = verticalArrowUp;
	}

	public WVerticalArrowDown getVerticalArrowDown() {
		return verticalArrowDown;
	}

	public <W extends WVerticalScrollableContainer> W setVerticalArrowDown(WVerticalArrowDown verticalArrowDown) {
		this.verticalArrowDown = verticalArrowDown;
		return (W) this;
	}

	public <W extends WVerticalScrollableContainer> W setScrollbar(WVerticalScrollbar scrollbar) {
		this.scrollbar = scrollbar;
		return (W) this;
	}

	public float getScrollbarWidth() {
		return scrollbarWidth;
	}

	public <W extends WVerticalScrollableContainer> W setScrollbarWidth(float scrollbarWidth) {
		this.scrollbarWidth = scrollbarWidth;
		return (W) this;
	}

	public float getDivisionSpace() {
		return divisionSpace;
	}

	public <W extends WVerticalScrollableContainer> W setDivisionSpace(float divisionSpace) {
		this.divisionSpace = divisionSpace;
		return (W) this;
	}

	public int getBorderSpace() {
		return borderSpace;
	}

	public <W extends WVerticalScrollableContainer> W setBorderSpace(int borderSpace) {
		this.borderSpace = borderSpace;
		return (W) this;
	}

	public int getFadeSpace() {
		return fadeSpace;
	}

	public <W extends WVerticalScrollableContainer> W setFadeSpace(int fadeSpace) {
		this.fadeSpace = fadeSpace;
		return (W) this;
	}

	public float getLastScrollX() {
		return lastScrollX;
	}

	public <W extends WVerticalScrollableContainer> W setLastScrollX(float lastScrollX) {
		this.lastScrollX = lastScrollX;
		return (W) this;
	}

	public float getLastScrollY() {
		return lastScrollY;
	}

	public <W extends WVerticalScrollableContainer> W setLastScrollY(float lastScrollY) {
		this.lastScrollY = lastScrollY;
		return (W) this;
	}

	public float getLastDragScrollY() {
		return lastDragScrollY;
	}

	public <W extends WVerticalScrollableContainer> W setLastDragScrollY(float lastDragScrollY) {
		this.lastDragScrollY = lastDragScrollY;
		return (W) this;
	}

	public long getLastDragScrollMilliseconds() {
		return lastDragScrollMilliseconds;
	}

	public <W extends WVerticalScrollableContainer> W setLastDragScrollMilliseconds(long lastDragScrollMilliseconds) {
		this.lastDragScrollMilliseconds = lastDragScrollMilliseconds;
		return (W) this;
	}

	public float getOffsetY() {
		return offsetY;
	}

	public <W extends WVerticalScrollableContainer> W setOffsetY(float offsetY) {
		this.offsetY = offsetY;
		return (W) this;
	}

	public float getKineticScrollDelta() {
		return kineticScrollDelta;
	}

	public <W extends WVerticalScrollableContainer> W setKineticScrollDelta(float kineticScrollDelta) {
		this.kineticScrollDelta = kineticScrollDelta;
		return (W) this;
	}

	public float getKineticReductionCoefficient() {
		return kineticReductionCoefficient;
	}

	public <W extends WVerticalScrollableContainer> W setKineticReductionCoefficient(float kineticReductionCoefficient) {
		this.kineticReductionCoefficient = kineticReductionCoefficient;
		return (W) this;
	}

	public float getKineticAccelerationCoefficient() {
		return kineticAccelerationCoefficient;
	}

	public <W extends WVerticalScrollableContainer> W setKineticAccelerationCoefficient(float kineticAccelerationCoefficient) {
		this.kineticAccelerationCoefficient = kineticAccelerationCoefficient;
		return (W) this;
	}

	public float getDragScrollAccelerationCoefficient() {
		return dragScrollAccelerationCoefficient;
	}

	public <W extends WVerticalScrollableContainer> W setDragScrollAccelerationCoefficient(float dragScrollAccelerationCoefficient) {
		this.dragScrollAccelerationCoefficient = dragScrollAccelerationCoefficient;
		return (W) this;
	}

	public boolean isDragScrolling() {
		return isDragScrolling;
	}

	public <W extends WVerticalScrollableContainer> W setDragScrolling(boolean dragScrolling) {
		isDragScrolling = dragScrolling;
		return (W) this;
	}

	public boolean hasFade() {
		return hasFade;
	}

	public <W extends WVerticalScrollableContainer> W setHasFade(boolean hasFade) {
		this.hasFade = hasFade;
		return (W) this;
	}

	public boolean hasSmoothing() {
		return hasSmoothing;
	}

	public <W extends WVerticalScrollableContainer> W setHasSmoothing(boolean hasSmoothing) {
		this.hasSmoothing = hasSmoothing;
		return (W) this;
	}

	public boolean hasArrows() {
		return hasArrows;
	}

	public <W extends WVerticalScrollableContainer> W setHasArrows(boolean hasArrows) {
		this.hasArrows = hasArrows;
		return (W) this;
	}

	protected float getBottomWidgetY() {
		return (float) getWidgets().stream().mapToDouble(widget -> widget.getY() + widget.getHeight()).max().orElse(0);
	}

	protected float getBottomWidgetOffsetY() {
		return (float) getWidgets().stream().mapToDouble(widget -> widget.getOffsetY() + widget.getHeight() + getDivisionSpace()).max().orElse(0);
	}

	@Override
	public void scroll(double deltaX, double deltaY) {
		if (getWidgets().isEmpty()) {
			return;
		}

		if (getUnderlyingHeight() <= getVisibleHeight()) {
			scrollToStart();
			return;
		}

		float bottomY = getBottomWidgetOffsetY();

		boolean hitTop = offsetY < -getDivisionSpace();
		boolean hitBottom = bottomY < getHeight();

		if ((!hitTop && deltaY > 0) || (!hitBottom && deltaY < 0)) {
			offsetY = (float) Math.min(Math.max(0, offsetY - deltaY), bottomY - getHeight() + 1);

			kineticScrollDelta = offsetY - deltaY >= bottomY + (2 * getDivisionSpace()) ? 0 : kineticScrollDelta;

			updateChildren();
		}
	}

	public void updateChildren() {
		for (WAbstractWidget widget : getWidgets()) {
			widget.getPosition().setY(-offsetY + widget.getOffsetY() + getY());
			boolean startContained = isWithinBounds(widget.getX(), widget.getY(), 1) || isWithinBounds(widget.getX() + widget.getWidth(), widget.getY() + widget.getHeight(), 1);
			widget.setHidden(!startContained);
		}
	}

	public void updateChildrenFocus() {
		for (WAbstractWidget widget : getAllWidgets()) {
			if (widget.isWithinBounds(lastScrollX, lastScrollY)) {
				widget.onFocusGained();
			} else {
				widget.onFocusReleased();
			}
		}
	}

	public void scrollToStart() {
		offsetY = 0;
		updateChildren();
	}

	public void scrollToEnd() {
		offsetY = getBottomWidgetY();
		updateChildren();
	}

	public void updateScrollbar() {
		float scrollBarWidth = getScrollbarWidth();
		float scrollBarHeight = getHeight();

		float scrollBarOffsetX = getWidth() - scrollBarWidth - getBorderSpace();
		float scrollBarOffsetY = getBorderSpace();

		if (hasArrows) {
			scrollBarOffsetY += scrollbarWidth - 1;
			scrollBarHeight -= scrollbarWidth * 2;

			scrollBarHeight = Math.abs(scrollBarHeight);

			if (verticalArrowUp == null) verticalArrowUp =  new WVerticalArrowUp().setScrollable(this).setPosition(Position.of(this, scrollBarOffsetX, 0, 0)).setSize(Size.of(scrollBarWidth));
			else verticalArrowUp.setPosition(Position.of(this, scrollBarOffsetX, 0, 0)).setSize(Size.of(scrollBarWidth));
			if (verticalArrowDown == null) verticalArrowDown = new WVerticalArrowDown().setScrollable(this).setPosition(Position.of(this, scrollBarOffsetX, scrollBarHeight + scrollbarWidth - 2, 0)).setSize(Size.of(scrollBarWidth));
			else verticalArrowDown.setPosition(Position.of(this, scrollBarOffsetX, scrollBarHeight + scrollBarWidth - 2, 0)).setSize(Size.of(scrollBarWidth));
		} else {
			verticalArrowUp = null;
			verticalArrowDown = null;
		}

		scrollbar.setPosition(Position.of(this, scrollBarOffsetX, scrollBarOffsetY, 0));
		scrollbar.setSize(Size.of(scrollBarWidth, scrollBarHeight - (2 * getBorderSpace())));
	}

	@Override
	public Collection<? extends WEventListener> getEventDelegates() {
		if (hasArrows) {
			return ImmutableSet.<WAbstractWidget>builder().addAll(widgets).add(scrollbar).add(verticalArrowUp).add(verticalArrowDown).build();
		} else {
			return ImmutableSet.<WAbstractWidget>builder().addAll(widgets).add(scrollbar).build();
		}
	}

	@Override
	public Size getUnderlyingSize() {
		return Size.of(getVisibleWidth(), getBottomWidgetOffsetY());
	}

	@Override
	public Set<WAbstractWidget> getWidgets() {
		return widgets;
	}

	@Override
	public List<WLayoutElement> getOrderedWidgets() {
		return orderedWidgets;
	}

	@Override
	public boolean contains(WAbstractWidget... widgetArray) {
		return widgets.containsAll(Arrays.asList(widgetArray));
	}

	@Override
	public void add(WAbstractWidget... widgetArray) {
		widgets.addAll(Arrays.asList(widgetArray));

		onLayoutChange();
	}

	public void addRow(WAbstractWidget... widgetArray) {
		float maxY = 0;
		float maxX = 0;

		for (WAbstractWidget widget : getWidgets()) {
			if (widget.getOffsetY() > maxY) {
				maxY = widget.getOffsetY() + widget.getHeight();
			}
		}

		for (WAbstractWidget widget : widgetArray) {
			widget.setParent(this);
			widget.setInterface(getInterface());
			widget.setPosition(Position.of(this));

			widget.getPosition().setOffsetX(maxX + getDivisionSpace());
			widget.getPosition().setOffsetY(maxY + getDivisionSpace());

			maxX += widget.getWidth() + getDivisionSpace();
		}

		widgets.addAll(Arrays.asList(widgetArray));

		onLayoutChange();
	}

	@Override
	public void remove(WAbstractWidget... widgetArray) {
		widgets.removeAll(Arrays.asList(widgetArray));

		for (WAbstractWidget widgetA : widgetArray) {
			if (widgets.stream().noneMatch(widgetB -> widgetA != widgetB && widgetA.getY() == widgetB.getY())) {
				for (WAbstractWidget widgetC : widgets) {
					if (widgetC.getOffsetY() > widgetA.getOffsetY()) {
						widgetC.getPosition().setOffsetY(widgetC.getOffsetY() - widgetC.getHeight() - getDivisionSpace());
					}
				}
			}

			float bottomY = getBottomWidgetOffsetY();

			if (offsetY + getHeight() > bottomY) {
				offsetY = bottomY - getHeight();
			}

			updateChildren();
			updateChildrenFocus();
		}

		onLayoutChange();
	}

	@Override
	public Size getVisibleSize() {
		return Size.of(getWidth() - (!scrollbar.isHidden() ? scrollbar.getWidth() : 0), getHeight());
	}

	@Override
	public float getStartAnchorY() {
		return getY();
	}

	@Override
	public float getEndAnchorY() {
		if (getVisibleHeight() > getUnderlyingHeight()) return getStartAnchorY();
		return getStartAnchorY() - (getUnderlyingHeight() - getVisibleHeight());
	}

	@Override
	public float getStartOffsetY() {
		return offsetY;
	}

	@Override
	public void onLayoutChange() {
		super.onLayoutChange();

		updateScrollbar();
		recalculateCache();
	}

	@Override
	public void recalculateCache() {
		orderedWidgets = new ArrayList<>(getWidgets());

		Collections.sort(orderedWidgets);
		Collections.reverse(orderedWidgets);
	}

	@Override
	public boolean updateFocus(float positionX, float positionY) {
		setFocus(isWithinBounds(positionX, positionY) && getWidgets().stream().noneMatch((WAbstractWidget::isFocused)));

		return isFocused();
	}

	@Override
	public void onKeyPressed(int keyCode, int character, int keyModifier) {
		if (isWithinBounds(MouseUtilities.mouseX, MouseUtilities.mouseY)) {
			if (keyCode == GLFW.GLFW_KEY_UP) {
				if (hasSmoothing()) {
					kineticScrollDelta += 0.75;
				} else {
					scroll(0, 2.5);
				}
			} else if (keyCode == GLFW.GLFW_KEY_DOWN) {
				if (hasSmoothing()) {
					kineticScrollDelta -= 0.75;
				} else {
					scroll(0, -2.5);
				}
			}
		}

		super.onKeyPressed(keyCode, character, keyModifier);
	}

	@Override
	public void onMouseClicked(float mouseX, float mouseY, int mouseButton) {
		if (isWithinBounds(mouseX, mouseY)) {
			if (mouseButton == 2) {
				isDragScrolling = true;

				lastDragScrollY = mouseY;
				lastDragScrollMilliseconds = System.currentTimeMillis();

				MouseUtilities.enableDragCursor();
			}
		}

		super.onMouseClicked(mouseX, mouseY, mouseButton);
	}

	@Override
	public void onMouseReleased(float mouseX, float mouseY, int mouseButton) {
		if (mouseButton == 2) {
			isDragScrolling = false;

			lastDragScrollY = 0;
			lastDragScrollMilliseconds = 0;

			MouseUtilities.enableArrowCursor();
		}

		super.onMouseReleased(mouseX, mouseY, mouseButton);
	}

	@Override
	public void onMouseScrolled(float mouseX, float mouseY, double deltaY) {
		if (isWithinBounds(mouseX, mouseY)) {
			if (hasSmoothing()) {
				kineticScrollDelta += deltaY;
				scroll(0, deltaY);
			} else {
				scroll(0, deltaY * 5);
			}


			lastScrollX = mouseX;
			lastScrollY = mouseY;
		}

		super.onMouseScrolled(mouseX, mouseY, deltaY);
	}

	@Override
	public void draw(MatrixStack matrices, VertexConsumerProvider.Immediate provider) {
		if (isHidden()) {
			return;
		}

		if (isDragScrolling()) {
			scroll(0, Math.pow(5, Math.abs(((MouseUtilities.mouseY - lastDragScrollY) / 100))) * ((System.currentTimeMillis() - lastDragScrollMilliseconds) * dragScrollAccelerationCoefficient) * (lastDragScrollY - MouseUtilities.mouseY > 0 ? 1 : -1));
		}

		if (kineticScrollDelta > 0.05 || kineticScrollDelta < -0.05) {
			kineticScrollDelta = kineticScrollDelta / getKineticReductionCoefficient();

			scroll(0, kineticScrollDelta * kineticReductionCoefficient * getKineticAccelerationCoefficient());

			updateChildrenFocus();
		} else {
			kineticScrollDelta = 0;

			lastScrollX = 0;
			lastScrollY = 0;
		}

		ScissorArea area = new ScissorArea(this);

		for (WAbstractWidget widget : getWidgets()) {
			widget.draw(matrices, provider);
		}

		area.destroy();

		if (hasFade()) {
			Color fadeOut = getStyle().asColor("background");
			fadeOut = Color.of("0x00" + Integer.toHexString((int) (fadeOut.R * 255)) + Integer.toHexString((int) (fadeOut.G * 255)) + Integer.toHexString((int) (fadeOut.B * 255)));

			if (offsetY > 1) {
				BaseRenderer.drawGradientQuad(matrices, provider, getX(), getY() - 1, getWideX() - getScrollbarWidth(), getY() + getFadeSpace() - 6, getZ(), getStyle().asColor("background"), fadeOut);
				BaseRenderer.drawGradientQuad(matrices, provider, getX(), getY() - 1, getWideX() - getScrollbarWidth(), getY() + getFadeSpace() - 3, getZ(), getStyle().asColor("background"), fadeOut);
				BaseRenderer.drawGradientQuad(matrices, provider, getX(), getY() - 1, getWideX() - getScrollbarWidth(), getY() + getFadeSpace(), getZ(), getStyle().asColor("background"), fadeOut);
			}

			if (getBottomWidgetY() > getHighY()) {
				BaseRenderer.drawGradientQuad(matrices, provider, getX(), getHighY() - getFadeSpace() + 6 , getWideX() - getScrollbarWidth(), getHighY() + 1, getZ(), fadeOut, getStyle().asColor("background"));
				BaseRenderer.drawGradientQuad(matrices, provider, getX(), getHighY() - getFadeSpace() + 3, getWideX() - getScrollbarWidth(), getHighY() + 1, getZ(), fadeOut, getStyle().asColor("background"));
				BaseRenderer.drawGradientQuad(matrices, provider, getX(), getHighY() - getFadeSpace() , getWideX() - getScrollbarWidth(), getHighY() + 1, getZ(), fadeOut, getStyle().asColor("background"));
			}
		}

		scrollbar.draw(matrices, provider);

		if (hasArrows()) {
			verticalArrowUp.draw(matrices, provider);
			verticalArrowDown.draw(matrices, provider);
		}
	}

	@Override
	public void tick() {
		if (hasArrows) {
			verticalArrowUp.tick();
			verticalArrowDown.tick();
		}
	}
}
