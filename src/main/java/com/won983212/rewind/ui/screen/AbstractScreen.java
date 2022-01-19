package com.won983212.rewind.ui.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import com.won983212.rewind.ui.component.ComponentPanel;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class AbstractScreen extends Screen {
    protected ComponentPanel rootPanel;


    public AbstractScreen(Component title) {
        super(title);
        this.rootPanel = new ComponentPanel();
    }

    @Override
    public void render(@NotNull PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        rootPanel.render(poseStack, mouseX, mouseY, partialTicks);
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
