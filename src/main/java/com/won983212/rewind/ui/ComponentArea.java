package com.won983212.rewind.ui;

public class ComponentArea {
    public float x;
    public float y;
    public float width;
    public float height;


    public ComponentArea() {
        this(0, 0, 0, 0);
    }

    public ComponentArea(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    @Override
    public String toString() {
        return String.format("ComponentArea[x=%f, y=%f, width=%f, height=%f", x, y, width, height);
    }
}
