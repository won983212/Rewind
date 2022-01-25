package com.won983212.rewind.ui.component;

import com.mojang.blaze3d.vertex.PoseStack;
import com.won983212.rewind.ui.Theme;
import com.won983212.rewind.ui.Thickness;
import com.won983212.rewind.util.Color;
import com.won983212.rewind.util.UIUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;

public abstract class AbstractStyledComponent extends AbstractComponent {
    protected final Font font = Minecraft.getInstance().font;
    protected Color backgroundColor = Theme.TRANSPARENT;
    protected Color foregroundColor = Theme.FOREGROUND;
    protected Color borderColor = null;
    protected Thickness borderThickness = new Thickness(0);

    public AbstractStyledComponent setBackgroundColor(Color color) {
        this.backgroundColor = color;
        return this;
    }

    public AbstractStyledComponent setForegroundColor(Color color) {
        this.foregroundColor = color;
        return this;
    }

    public AbstractStyledComponent setBorderThickness(Thickness thickness) {
        this.borderThickness = thickness;
        return this;
    }

    public AbstractStyledComponent setBorderColor(Color borderColor) {
        this.borderColor = borderColor;
        return this;
    }

    @Override
    protected Thickness getPositionOffset() {
        return super.getPositionOffset().combine(borderThickness);
    }

    @Override
    protected void renderComponent(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        if (backgroundColor.getAlpha() > 0) {
            UIUtils.fillFloat(poseStack, 0, 0, getWidth(), getHeight(), backgroundColor.getArgb());
        }
    }
}