package com.won983212.rewind.ui.component.panel;

import com.won983212.rewind.ui.ComponentArea;
import com.won983212.rewind.ui.ComponentVec2;
import com.won983212.rewind.ui.component.AbstractComponent;

/**
 * StackPanel은 컴포넌트들을 한 방향으로 쌓아서 배치합니다.
 */
public class StackPanel extends Panel {
    private final Orientation orientation;
    private float gap = 2;


    public StackPanel() {
        this(Orientation.HORIZONTAL);
    }

    public StackPanel(Orientation orientation) {
        this.orientation = orientation;
    }

    public StackPanel setGap(float gap) {
        this.gap = gap;
        return this;
    }

    @Override
    public ComponentVec2 measureMinSize() {
        ComponentVec2 size = new ComponentVec2();
        int len = components.size();
        for (int i = 0; i < len; i++) {
            AbstractComponent component = components.get(i);
            ComponentVec2 componentSize = component.getMinSizeWithMargin();
            if (orientation == Orientation.HORIZONTAL) {
                size.x += componentSize.x + (i == len - 1 ? 0 : gap);
                size.y = Math.max(size.y, componentSize.y);
            } else {
                size.x = Math.max(size.x, componentSize.x);
                size.y += componentSize.y + (i == len - 1 ? 0 : gap);
            }
        }
        return size;
    }

    @Override
    public void arrangeChildren(ComponentArea available) {
        float x = available.x;
        float y = available.y;
        int len = components.size();
        for (AbstractComponent component : components) {
            ComponentVec2 componentSize = component.getMinSizeWithMargin();
            ComponentArea rect = new ComponentArea(x, y, componentSize.x, componentSize.y);
            if (orientation == Orientation.HORIZONTAL) {
                x += componentSize.x + gap;
                rect.height = available.height;
            } else {
                y += componentSize.y + gap;
                rect.width = available.width;
            }
            component.arrange(rect);
        }
    }

    public enum Orientation {
        HORIZONTAL,
        VERTICAL
    }
}