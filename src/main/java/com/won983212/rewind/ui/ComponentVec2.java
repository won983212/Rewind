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
}
