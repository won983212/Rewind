package com.won983212.rewind.util;

public class Color {
    private final int argb;

    private Color(int argb) {
        this.argb = argb;
    }

    public static Color of(int argb) {
        return new Color(argb);
    }

    public static Color of(int a, int r, int g, int b) {
        return new Color((a << 24) | (r << 16) | (g << 8) | b);
    }

    public Color alpha(int alpha) {
        return new Color((argb & 0x00FFFFFF) | (alpha << 24));
    }

    public Color red(int red) {
        return new Color((argb & 0xFF00FFFF) | (red << 16));
    }

    public Color green(int green) {
        return new Color((argb & 0xFFFF00FF) | (green << 8));
    }

    public Color blue(int blue) {
        return new Color((argb & 0xFFFFFF00) | blue);
    }

    public int getArgb() {
        return argb;
    }

    public int getAlpha() {
        return (argb >> 24) & 0xff;
    }

    public int getRed() {
        return (argb >> 16) & 0xff;
    }

    public int getGreen() {
        return (argb >> 8) & 0xff;
    }

    public int getBlue() {
        return argb & 0xff;
    }
}
