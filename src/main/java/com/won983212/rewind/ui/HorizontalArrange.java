package com.won983212.rewind.ui;

public enum HorizontalArrange {
    LEFT, CENTER, RIGHT, STRETCH;


    /**
     * Calculate actual horizontally aligned position of component.
     *
     * @param available      An area component can be placed.
     * @param componentWidth desired width of component
     */
    public float getArrangedX(ComponentArea available, float componentWidth) {
        return switch (this) {
            case RIGHT -> available.x + available.width - componentWidth;
            case CENTER -> available.x + (available.width - componentWidth) / 2;
            default -> available.x;
        };
    }

    /**
     * Calculate actual width of component when it aligned horizontally.
     *
     * @param available      An area component can be placed.
     * @param componentWidth desired width of component
     */
    public float getArrangedWidth(ComponentArea available, float componentWidth) {
        if (this == HorizontalArrange.STRETCH) {
            return available.width;
        }
        return componentWidth;
    }
}