package com.won983212.rewind.ui.component.panel;

import com.won983212.rewind.ui.ComponentArea;
import com.won983212.rewind.ui.ComponentVec2;
import com.won983212.rewind.ui.component.AbstractComponent;

/**
 * StackPanel은 컴포넌트들을 한 방향으로 쌓아서 배치합니다.
 */
public class StackPanel extends Panel {
    private final Orientation orientation;


    public StackPanel() {
        this(Orientation.HORIZONTAL);
    }

    public StackPanel(Orientation orientation) {
        this.orientation = orientation;
    }

    @Override
    public ComponentVec2 measureMinSize() {
        ComponentVec2 size = new ComponentVec2();
        for (AbstractComponent obj : components) {
            ComponentVec2 componentSize = obj.getMinSizeWithMargin();
            if (orientation == Orientation.HORIZONTAL) {
                size.x += componentSize.x;
                size.y = Math.max(size.y, componentSize.y);
            } else {
                size.x = Math.max(size.x, componentSize.x);
                size.y += componentSize.y;
            }
        }
        return size;
    }

    @Override
    public void arrangeChildren(ComponentArea available) {
        float x = available.x;
        float y = available.y;
        for (AbstractComponent obj : components) {
            ComponentVec2 componentSize = obj.getMinSizeWithMargin();
            ComponentArea rect = new ComponentArea(x, y, componentSize.x, componentSize.y);
            if (orientation == Orientation.HORIZONTAL) {
                x += componentSize.x;
                rect.height = getHeight();
            } else {
                y += componentSize.y;
                rect.width = getWidth();
            }
            obj.arrange(rect);
        }
    }

    public enum Orientation {
        HORIZONTAL,
        VERTICAL
    }
}