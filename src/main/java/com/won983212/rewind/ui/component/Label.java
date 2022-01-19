package com.won983212.rewind.ui.component;

import com.mojang.blaze3d.vertex.PoseStack;
import com.won983212.rewind.mixin.MixinMinecraft;
import net.minecraft.client.Minecraft;

@SuppressWarnings("UnusedReturnValue")
public class Label extends AbstractComponent {
    private String text;
    private float scale;
    private int foreground;
    private float maxWidth = -1;
    private boolean useDefaultFont = false;


    public Label(String text) {
        this.foreground = 0xffffffff;
        this.scale = 1;
        this.setText(text);
    }

    public Label setText(String text) {
        if (text == null) {
            text = "";
        }
        this.text = text;
        invalidateSize();
        return this;
    }

    @Override
    protected void updateSize() {
        if (useDefaultFont) {
            setToDefaultFont();
        }

        float width = maxWidth;
        if (width == -1) {
            width = font.width(text) * scale;
        }
        this.setSize(width, font.lineHeight * scale);

        if (useDefaultFont) {
            setToMinecraftFont();
        }
    }

    public Label useDefaultFont() {
        useDefaultFont = true;
        invalidateSize();
        return this;
    }

    public Label setMaxWidth(float width) {
        this.maxWidth = width;
        invalidateSize();
        return this;
    }

    public Label setScale(float scale) {
        this.scale = scale;
        invalidateSize();
        return this;
    }

    public Label setForeground(int color) {
        this.foreground = color;
        return this;
    }

    private void setToDefaultFont() {
        ((MixinMinecraft) Minecraft.getInstance()).invokeSelectMainFont(false);
    }

    private void setToMinecraftFont() {
        Minecraft mc = Minecraft.getInstance();
        ((MixinMinecraft) mc).invokeSelectMainFont(mc.isEnforceUnicode());
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        super.render(poseStack, mouseX, mouseY, partialTicks);
        if (useDefaultFont) {
            setToDefaultFont();
        }

        String ellipsisText = AbstractComponent.ellipsisText(font, text, (int) (width / scale));
        if (scale != 1) {
            poseStack.pushPose();
            poseStack.scale(scale, scale, scale);
            font.draw(poseStack, ellipsisText, 0, 0, foreground);
            poseStack.popPose();
        } else {
            font.draw(poseStack, ellipsisText, 0, 0, foreground);
        }

        if (useDefaultFont) {
            setToMinecraftFont();
        }
    }
}
