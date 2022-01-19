package com.won983212.rewind.ui.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import com.won983212.rewind.ui.component.panel.Panel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class AbstractScreen extends Screen {
    protected Panel rootPanel;
    protected Screen parent;
    private boolean drawBackground;


    public AbstractScreen(Component title) {
        super(title);
        this.parent = Minecraft.getInstance().screen;
        this.drawBackground = true;
        this.rootPanel = new Panel();
    }

    protected void init(Panel rootPanel) {
    }

    public void disableDrawBackground() {
        drawBackground = false;
    }

    @Override
    protected void init() {
        rootPanel = new Panel();
        init(rootPanel);
    }

    @Override
    public void render(@NotNull PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        if (parent != null) {
            parent.render(poseStack, 0, 0, partialTicks);
        }

        poseStack.pushPose();
        poseStack.translate(rootPanel.getX(), rootPanel.getY(), 2);
        if (drawBackground) {
            poseStack.translate(0, 0, 2);
            fillGradient(poseStack, 0, 0, this.width, this.height, 0xC0101010, 0xD0101010);
        }
        rootPanel.render(poseStack, mouseX, mouseY, partialTicks);
        poseStack.popPose();
    }

    @Override
    public void tick() {
        if (parent != null) {
            parent.tick();
        }
    }

    @Override
    public boolean keyPressed(int key, int scanCode, int modifiers) {
        rootPanel.keyPressed(key, scanCode, modifiers);
        return super.keyPressed(key, scanCode, modifiers);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return rootPanel.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return rootPanel.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        return rootPanel.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollDelta) {
        return rootPanel.mouseScrolled(mouseX, mouseY, scrollDelta);
    }

    @Override
    public boolean keyReleased(int key, int scanCode, int modifiers) {
        return rootPanel.keyReleased(key, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char charIn, int keyIn) {
        return rootPanel.charTyped(charIn, keyIn);
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        rootPanel.mouseMoved(mouseX, mouseY);
    }
}
