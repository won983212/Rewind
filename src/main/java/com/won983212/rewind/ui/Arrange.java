package com.won983212.rewind.ui;

public enum Arrange {
    LEFT_TOP(HorizontalArrange.LEFT, VerticalArrange.TOP),
    LEFT_MIDDLE(HorizontalArrange.LEFT, VerticalArrange.MIDDLE),
    LEFT_BOTTOM(HorizontalArrange.LEFT, VerticalArrange.BOTTOM),
    LEFT_STRETCH(HorizontalArrange.LEFT, VerticalArrange.STRETCH),
    CENTER_TOP(HorizontalArrange.CENTER, VerticalArrange.TOP),
    CENTER_MIDDLE(HorizontalArrange.CENTER, VerticalArrange.MIDDLE),
    CENTER_BOTTOM(HorizontalArrange.CENTER, VerticalArrange.BOTTOM),
    CENTER_STRETCH(HorizontalArrange.CENTER, VerticalArrange.STRETCH),
    RIGHT_TOP(HorizontalArrange.RIGHT, VerticalArrange.TOP),
    RIGHT_MIDDLE(HorizontalArrange.RIGHT, VerticalArrange.MIDDLE),
    RIGHT_BOTTOM(HorizontalArrange.RIGHT, VerticalArrange.BOTTOM),
    RIGHT_STRETCH(HorizontalArrange.RIGHT, VerticalArrange.STRETCH),
    STRETCH_TOP(HorizontalArrange.STRETCH, VerticalArrange.TOP),
    STRETCH_MIDDLE(HorizontalArrange.STRETCH, VerticalArrange.MIDDLE),
    STRETCH_BOTTOM(HorizontalArrange.STRETCH, VerticalArrange.BOTTOM),
    STRETCH_STRETCH(HorizontalArrange.STRETCH, VerticalArrange.STRETCH);

    public final HorizontalArrange horizontal;
    public final VerticalArrange vertical;


    Arrange(HorizontalArrange horizontal, VerticalArrange vertical) {
        this.horizontal = horizontal;
        this.vertical = vertical;
    }

    /**
     * Calculate actual aligned position of component.
     *
     * @param available An area component can be placed.
     * @param size      desired size of component
     */
    public ComponentVec2 getArrangedPosition(ComponentArea available, ComponentVec2 size) {
        float x = horizontal.getArrangedX(available, size.x);
        float y = vertical.getArrangedY(available, size.y);
        return new ComponentVec2(x, y);
    }

    /**
     * Calculate actual aligned size of component.
     *
     * @param available An area component can be placed.
     * @param size      desired size of component
     */
    public ComponentVec2 getArrangedSize(ComponentArea available, ComponentVec2 size) {
        float width = horizontal.getArrangedWidth(available, size.x);
        float height = vertical.getArrangedHeight(available, size.y);
        return new ComponentVec2(width, height);
    }
}