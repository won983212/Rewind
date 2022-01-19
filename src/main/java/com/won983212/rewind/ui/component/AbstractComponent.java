package com.won983212.rewind.ui.component;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.renderer.GameRenderer;

@SuppressWarnings("UnusedReturnValue")
public abstract class AbstractComponent implements GuiEventListener {
    protected final Font font = Minecraft.getInstance().font;

    protected ComponentPanel parent;
    protected float x;
    protected float y;
    protected float width;
    protected float height;


    public abstract void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks);

    protected void setParent(ComponentPanel parent) {
        this.parent = parent;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    private void invalidateParentSize() {
        if (parent != null) {
            parent.invalidateSize();
        }
    }

    public AbstractComponent setX(float x) {
        if (this.x != x) {
            this.x = x;
            invalidateParentSize();
        }
        return this;
    }

    public AbstractComponent setY(float y) {
        if (this.y != y) {
            this.y = y;
            invalidateParentSize();
        }
        return this;
    }

    public AbstractComponent setWidth(float width) {
        if (this.width != width) {
            this.width = width;
            invalidateParentSize();
        }
        return this;
    }

    public AbstractComponent setHeight(float height) {
        if (this.height != height) {
            this.height = height;
            invalidateParentSize();
        }
        return this;
    }

    public AbstractComponent setPosition(float x, float y) {
        setX(x);
        setY(y);
        return this;
    }

    public AbstractComponent setSize(float width, float height) {
        setWidth(width);
        setHeight(height);
        return this;
    }

    public AbstractComponent setBounds(float x, float y, float width, float height) {
        setPosition(x, y);
        setSize(width, height);
        return this;
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
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
