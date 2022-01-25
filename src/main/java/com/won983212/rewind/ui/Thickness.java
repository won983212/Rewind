package com.won983212.rewind.ui;

public class Thickness {
    public final int top;
    public final int bottom;
    public final int left;
    public final int right;


    public Thickness() {
        this(0, 0, 0, 0);
    }

    public Thickness(int r) {
        this(r, r, r, r);
    }

    public Thickness(int horizontal, int vertical) {
        this(vertical, vertical, horizontal, horizontal);
    }

    public Thickness(int top, int bottom, int left, int right) {
        this.top = top;
        this.bottom = bottom;
        this.left = left;
        this.right = right;
    }

    /**
     * Calculate expanded size.
     */
    public ComponentVec2 toExpandedSize(ComponentVec2 contentSize) {
        return new ComponentVec2(contentSize.x + left + right, contentSize.y + top + bottom);
    }

    /**
     * Calculate actual content area.
     */
    public ComponentArea toContentRect(ComponentArea rect) {
        return new ComponentArea(rect.x + left, rect.y + top, rect.width - left - right, rect.height - top - bottom);
    }
}
