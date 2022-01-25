package com.won983212.rewind.ui.component;

import com.won983212.rewind.ui.Theme;
import com.won983212.rewind.util.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;

public abstract class AbstractStyledComponent extends AbstractComponent {
    protected final Font font = Minecraft.getInstance().font;
    protected Color backgroundColor = Theme.BACKGROUND;
    protected Color foregroundColor = Theme.FOREGROUND;
    protected Color borderColor = null;
    protected Color borderShadow = null;
    protected int roundRadius = 0;

    public AbstractStyledComponent setBackgroundColor(Color color) {
        this.backgroundColor = color;
        return this;
    }

    public AbstractStyledComponent setForegroundColor(Color color) {
        this.foregroundColor = color;
        return this;
    }

    public AbstractStyledComponent setBorderShadow(Color color) {
        this.borderShadow = color;
        return this;
    }

    public AbstractStyledComponent setBorder(Color borderColor) {
        this.borderColor = borderColor;
        return this;
    }

    public AbstractStyledComponent setRadius(int radius) {
        this.roundRadius = radius;
        return this;
    }
}