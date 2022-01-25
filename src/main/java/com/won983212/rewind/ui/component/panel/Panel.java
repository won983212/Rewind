package com.won983212.rewind.ui.component.panel;

import com.mojang.blaze3d.vertex.PoseStack;
import com.won983212.rewind.ui.ComponentArea;
import com.won983212.rewind.ui.ComponentVec2;
import com.won983212.rewind.ui.component.AbstractComponent;
import com.won983212.rewind.ui.component.AbstractStyledComponent;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@SuppressWarnings("UnusedReturnValue")
public class Panel extends AbstractStyledComponent implements ContainerEventHandler {
    protected final List<AbstractComponent> components;

    @Nullable
    private AbstractComponent focused;
    private boolean isDragging;


    public Panel() {
        this.components = Lists.newArrayList();
    }

    public void addComponent(AbstractComponent component) {
        component.setParent(this);
        this.components.add(component);
    }

    @Override
    public void invalidateSize() {
        for (AbstractComponent component : components) {
            component.invalidateSize();
        }
        super.invalidateSize();
    }

    protected void arrangeChildren(ComponentArea available) {
        for (AbstractComponent component : components) {
            component.arrange(available);
        }
    }

    @Override
    public void renderComponent(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        super.renderComponent(poseStack, mouseX, mouseY, partialTicks);
        for (AbstractComponent component : components) {
            component.render(poseStack, mouseX, mouseY, partialTicks);
        }
    }

    @Override
    public ComponentVec2 measureMinSize() {
        ComponentVec2 size = new ComponentVec2();
        for (AbstractComponent obj : components) {
            ComponentVec2 clientDim = obj.getMinSizeWithMargin();
            size.x = Math.max(size.x, obj.getX() + clientDim.x);
            size.y = Math.max(size.y, obj.getY() + clientDim.y);
        }
        return getPositionOffset().toExpandedSize(size);
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
