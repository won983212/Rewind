package com.won983212.rewind.ui.component;

import com.mojang.blaze3d.vertex.PoseStack;

@SuppressWarnings("UnusedReturnValue")
public class TextLabel extends AbstractComponent {
    private String text;
    private float scale;
    private int foreground;


    public TextLabel(String text) {
        this.foreground = 0xffffffff;
        this.scale = 1;
        this.setText(text);
    }

    public TextLabel setText(String text) {
        if (text == null) {
            text = "";
        }
        this.text = text;
        this.setSize((int) (font.width(text) * scale), (int) (font.lineHeight * scale));
        return this;
    }

    public TextLabel setScale(float scale) {
        this.scale = scale;
        return this;
    }

    public TextLabel setForeground(int color) {
        this.foreground = color;
        return this;
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        if (scale != 1) {
            poseStack.pushPose();
            poseStack.scale(scale, scale, scale);
            font.draw(poseStack, text, x / scale, y / scale, foreground);
            poseStack.popPose();
        } else {
            font.draw(poseStack, text, x, y, foreground);
        }
    }
}
