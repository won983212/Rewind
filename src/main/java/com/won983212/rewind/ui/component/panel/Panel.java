package com.won983212.rewind.ui.component.panel;

import com.mojang.blaze3d.vertex.PoseStack;
import com.won983212.rewind.ui.component.AbstractComponent;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@SuppressWarnings("UnusedReturnValue")
public class Panel extends AbstractComponent implements ContainerEventHandler {
    private int margin;
    private int backgroundColor;
    private final List<AbstractComponent> components;

    @Nullable
    private AbstractComponent focused;
    private boolean isDragging;


    public Panel() {
        this.components = Lists.newArrayList();
        setBackgroundColor(0);
        setMargin(2);
    }

    public void addComponent(AbstractComponent component) {
        component.setParent(this);
        this.components.add(component);
        invalidateSize();
    }

    public Panel setMargin(int margin) {
        if (this.margin != margin) {
            this.margin = margin;
            invalidateSize();
        }
        return this;
    }

    public Panel setBackgroundColor(int color) {
        this.backgroundColor = color;
        return this;
    }

    public void invalidateSize() {
        super.invalidateSize();
        if (parent != null) {
            parent.invalidateSize();
        }
    }

    @Override
    protected void updateSize() {
        if (parent != null) {
            parent.updateSize();
            return;
        }
        packSizeImpl();
    }

    private void packSizeImpl() {
        float maxX = 0;
        float maxY = 0;

        for (AbstractComponent component : components) {
            if (component instanceof Panel panel && panel.hasChangedSize) {
                panel.packSizeImpl();
            }
            maxX = Math.max(maxX, component.getX() + component.getWidth());
            maxY = Math.max(maxY, component.getY() + component.getHeight());
        }

        setSize(maxX + margin * 2, maxY + margin * 2);
        hasChangedSize = false;
    }

    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        super.render(poseStack, mouseX, mouseY, partialTicks);

        if (((backgroundColor >> 24) & 0xff) > 0) {
            AbstractComponent.fillFloat(poseStack, 0, 0, width, height, backgroundColor);
        }

        poseStack.pushPose();
        poseStack.translate(margin, margin, 0);
        for (AbstractComponent component : components) {
            poseStack.translate(component.getX(), component.getY(), 0);
            component.render(poseStack, mouseX, mouseY, partialTicks);
            poseStack.translate(-component.getX(), -component.getY(), 0);
        }
        poseStack.popPose();
    }

    @Override
    @NotNull
    public List<? extends GuiEventListener> children() {
        return components;
    }

    @Override
    public boolean isDragging() {
        return this.isDragging;
    }

    @Override
    public void setDragging(boolean dragging) {
        this.isDragging = dragging;
    }

    @Nullable
    @Override
    public GuiEventListener getFocused() {
        return focused;
    }

    @Override
    public void setFocused(@Nullable GuiEventListener component) {
        this.focused = (AbstractComponent) component;
    }
}
