package com.won983212.rewind.ui.component;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@SuppressWarnings("UnusedReturnValue")
public class ComponentPanel extends AbstractComponent implements ContainerEventHandler {
    private int margin;
    private int backgroundColor;
    private final List<AbstractComponent> components;
    private boolean needsUpdateSize;

    @Nullable
    private AbstractComponent focused;
    private boolean isDragging;


    public ComponentPanel() {
        this.components = Lists.newArrayList();
        setBackgroundColor(0);
        setMargin(2);
    }

    public void addComponent(AbstractComponent component) {
        component.setParent(this);
        this.components.add(component);
        invalidateSize();
    }

    public ComponentPanel setMargin(int margin) {
        if (this.margin != margin) {
            this.margin = margin;
            invalidateSize();
        }
        return this;
    }

    public ComponentPanel setBackgroundColor(int color) {
        this.backgroundColor = color;
        return this;
    }

    public void invalidateSize() {
        ComponentPanel current = this;
        while (current != null) {
            current.needsUpdateSize = true;
            current = current.parent;
        }
    }

    private void packSize() {
        if (parent != null) {
            parent.packSize();
            return;
        }
        packSizeImpl();
    }

    private void packSizeImpl() {
        float maxX = 0;
        float maxY = 0;

        for (AbstractComponent component : components) {
            if (component instanceof ComponentPanel componentPanel
                    && componentPanel.needsUpdateSize) {
                componentPanel.packSizeImpl();
            }
            maxX = Math.max(maxX, component.x + component.width);
            maxY = Math.max(maxY, component.y + component.height);
        }

        setSize(maxX + margin * 2, maxY + margin * 2);
        needsUpdateSize = false;
    }

    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        if (needsUpdateSize) {
            packSize();
        }

        if (((backgroundColor >> 24) & 0xff) > 0) {
            AbstractComponent.fillFloat(poseStack, x, y, x + width, y + height, backgroundColor);
        }

        poseStack.pushPose();
        poseStack.translate(x + margin, y + margin, 0);
        for (AbstractComponent component : components) {
            component.render(poseStack, mouseX, mouseY, partialTicks);
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
