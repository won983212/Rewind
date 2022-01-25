package com.won983212.rewind.ui;

import com.won983212.rewind.util.UIUtils;

public enum VerticalArrange {
    TOP, MIDDLE, BOTTOM, STRETCH;


    /**
     * Calculate actual vertically aligned position of component.
     *
     * @param available       An area component can be placed.
     * @param componentHeight desired height of component
     */
    public float getArrangedY(ComponentArea available, float componentHeight) {
        return switch (this) {
            case BOTTOM -> available.y + available.height - componentHeight;
            case MIDDLE -> UIUtils.snapToPixel(available.y + (available.height - componentHeight) / 2);
            default -> available.y;
        };
    }

    /**
     * Calculate actual height of component when it aligned vertically.
     *
     * @param available       An area component can be placed.
     * @param componentHeight desired height of component
     */
    public float getArrangedHeight(ComponentArea available, float componentHeight) {
        if (this == VerticalArrange.STRETCH) {
            return available.height;
        }
        return componentHeight;
    }
}