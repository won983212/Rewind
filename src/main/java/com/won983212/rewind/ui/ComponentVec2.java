package com.won983212.rewind.ui;

public class ComponentVec2 {
    public float x;
    public float y;


    public ComponentVec2() {
        this(0, 0);
    }

    public ComponentVec2(ComponentVec2 dest) {
        this(dest.x, dest.y);
    }

    public ComponentVec2(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public ComponentVec2 add(float x, float y) {
        return new ComponentVec2(this.x + x, this.y + y);
    }

    @Override
    public String toString() {
        return String.format("ComponentVec2[x=%f, y=%f", x, y);
    }
}
