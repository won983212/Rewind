package com.won983212.rewind.ui.component;

import com.mojang.blaze3d.vertex.PoseStack;
import com.won983212.rewind.util.UIUtils;
import net.minecraft.network.chat.TextComponent;

@SuppressWarnings("UnusedReturnValue")
public class Label extends AbstractStyledComponent {
    private String text;
    private float scale = 1;
    private float maxWidth = -1;
    private boolean useWrapping = false;
    private boolean useDefaultFont = false;


    public Label(String text) {
        this.setText(text);
    }

    public Label setText(String text) {
        if (text == null) {
            text = "";
        }
        if (!text.equals(this.text)) {
            this.text = text;
            calculateSize();
        }
        return this;
    }

    private void calculateSize() {
        if (useDefaultFont) {
            UIUtils.setToDefaultFont();
        }

        float width = maxWidth;
        if (width == -1) {
            width = font.width(text) * scale;
        }

        int lineSize = 1;
        if (useWrapping) {
            lineSize = font.split(new TextComponent(text), 100).size();
        }

        setPreferredMinimalSize(width, font.lineHeight * lineSize * scale);
        invalidateSize();

        if (useDefaultFont) {
            UIUtils.setToMinecraftFont();
        }
    }

    public Label useDefaultFont() {
        if (!useDefaultFont) {
            useDefaultFont = true;
            calculateSize();
        }
        return this;
    }

    public Label useWrapping() {
        if (!useWrapping) {
            useWrapping = true;
            calculateSize();
        }
        return this;
    }

    public Label setMaxWidth(float width) {
        if (maxWidth != width) {
            this.maxWidth = width;
            calculateSize();
        }
        return this;
    }

    public Label setScale(float scale) {
        if (this.scale != scale) {
            this.scale = scale;
            calculateSize();
        }
        return this;
    }

    @Override
    public void renderComponent(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        super.renderComponent(poseStack, mouseX, mouseY, partialTicks);

        if (useDefaultFont) {
            UIUtils.setToDefaultFont();
        }

        String text = this.text;
        if (!useWrapping) {
            text = UIUtils.ellipsisText(font, this.text, (int) (getWidth() / scale));
        }

        if (scale != 1) {
            poseStack.pushPose();
            poseStack.scale(scale, scale, scale);
        }

        if (useWrapping) {
            UIUtils.drawText(font, poseStack, text, 0, 1, foregroundColor.getArgb(), 0, (int) getWidth());
        } else {
            UIUtils.drawText(font, poseStack, text, 0, 1, foregroundColor.getArgb());
        }

        if (scale != 1) {
            poseStack.popPose();
        }

        if (useDefaultFont) {
            UIUtils.setToMinecraftFont();
        }
    }
}
