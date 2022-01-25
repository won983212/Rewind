package com.won983212.rewind.util;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import com.won983212.rewind.mixin.MixinMinecraft;
import com.won983212.rewind.ui.ComponentArea;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.util.FormattedCharSequence;

import java.util.List;

public class UIUtils {
    public static final int CENTER_HORIZONTAL = 1;
    public static final int CENTER_VERTICAL = 1 << 1;
    public static final int CENTER_BOTH = CENTER_HORIZONTAL | CENTER_VERTICAL;
    public static final int USE_SHADOW = 1 << 2;


    public static String ellipsisText(Font font, String str, int width) {
        int sizeStr = font.width(str);
        int sizeDots = font.width("...");
        if (sizeStr > width) {
            str = font.plainSubstrByWidth(str, width - sizeDots);
            str += "...";
        }
        return str;
    }

    public static void setToDefaultFont() {
        ((MixinMinecraft) Minecraft.getInstance()).invokeSelectMainFont(false);
    }

    public static void setToMinecraftFont() {
        Minecraft mc = Minecraft.getInstance();
        ((MixinMinecraft) mc).invokeSelectMainFont(mc.isEnforceUnicode());
    }

    public static int drawText(Font font, PoseStack pose, String text, float x, float y, int color) {
        return drawText(font, pose, text, x, y, color, 0);
    }

    public static int drawText(Font font, PoseStack pose, String text, float x, float y, int color, int flags) {
        return drawText(font, pose, text, x, y, color, flags, Integer.MAX_VALUE);
    }

    public static int drawText(Font font, PoseStack pose, String text, float x, float y, int color, int flags, int wrapWidth) {
        int len = font.width(text);
        List<FormattedCharSequence> texts = null;

        if (wrapWidth < len) {
            texts = font.split(new TextComponent(text), wrapWidth);
        }

        if ((flags & CENTER_HORIZONTAL) > 0) {
            int w = Math.min(wrapWidth, len);
            x = x - w / 2f;
        }

        if ((flags & CENTER_VERTICAL) > 0) {
            int h = font.lineHeight;
            if (wrapWidth < len) {
                h = texts.size() * font.lineHeight;
            }
            y = y - h / 2f;
        }

        if (texts != null) {
            for (FormattedCharSequence seq : texts) {
                if ((flags & USE_SHADOW) > 0) {
                    font.drawShadow(pose, seq, x, y, color);
                } else {
                    font.draw(pose, seq, x, y, color);
                }
                y += font.lineHeight;
            }
        } else {
            if ((flags & USE_SHADOW) > 0) {
                font.drawShadow(pose, text, x, y, color);
            } else {
                font.draw(pose, text, x, y, color);
            }
        }

        return len;
    }

    public static float snapToPixel(float value) {
        double guiScale = Minecraft.getInstance().getWindow().getGuiScale();
        return (float) ((int) (value * guiScale) / guiScale);
    }

    public static void fillFloat(PoseStack poseStack, ComponentArea area, int color) {
        fillFloat(poseStack, area.x, area.y, area.width, area.height, color);
    }

    public static void fillFloat(PoseStack poseStack, float minX, float minY, float maxX, float maxY, int color) {
        if (minX < maxX) {
            float temp = minX;
            minX = maxX;
            maxX = temp;
        }

        if (minY < maxY) {
            float temp = minY;
            minY = maxY;
            maxY = temp;
        }

        Matrix4f matrix = poseStack.last().pose();
        float f3 = (float) (color >> 24 & 255) / 255.0F;
        float f = (float) (color >> 16 & 255) / 255.0F;
        float f1 = (float) (color >> 8 & 255) / 255.0F;
        float f2 = (float) (color & 255) / 255.0F;
        BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
        RenderSystem.enableBlend();
        RenderSystem.disableTexture();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        bufferbuilder.vertex(matrix, minX, maxY, 0.0F).color(f, f1, f2, f3).endVertex();
        bufferbuilder.vertex(matrix, maxX, maxY, 0.0F).color(f, f1, f2, f3).endVertex();
        bufferbuilder.vertex(matrix, maxX, minY, 0.0F).color(f, f1, f2, f3).endVertex();
        bufferbuilder.vertex(matrix, minX, minY, 0.0F).color(f, f1, f2, f3).endVertex();
        bufferbuilder.end();
        BufferUploader.end(bufferbuilder);
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
    }
}
