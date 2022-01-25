package com.won983212.rewind.ui;

import com.mojang.blaze3d.vertex.PoseStack;
import com.won983212.rewind.ui.component.panel.Panel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class UIScreen extends Screen {
    protected Panel contentLayer;
    protected Screen parent;
    private boolean contentCentering;
    private boolean drawBackground;


    public UIScreen(Component title) {
        super(title);
        this.drawBackground = true;
        this.contentCentering = false;
        this.contentLayer = new Panel();
    }

    protected void init(Panel rootPanel) {
    }

    public void disableDrawBackground() {
        drawBackground = false;
    }

    public void useContentCentering() {
        contentCentering = true;
    }

    public void useBackscreenRendering() {
        this.parent = Minecraft.getInstance().screen;
    }

    protected void selfInitialize() {
        Minecraft mc = Minecraft.getInstance();
        init(mc, mc.getWindow().getGuiScaledWidth(), mc.getWindow().getGuiScaledHeight());
    }

    @Override
    protected void init() {
        ComponentArea available = new ComponentArea(0, 0, width, height);
        contentLayer = new Panel(){
            @Override
            public void layout() {
                invalidateSize();
                arrange(available);
            }
        };

        init(contentLayer);
        if (contentCentering) {
            contentLayer.setArrange(Arrange.CENTER_MIDDLE);
        }

        contentLayer.arrange(available);
    }

    @Override
    public void render(@NotNull PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        if (parent != null) {
            parent.render(poseStack, 0, 0, partialTicks);
        }

        poseStack.pushPose();
        poseStack.translate(0, 0, 2);
        if (drawBackground) {
            fillGradient(poseStack, 0, 0, this.width, this.height, 0xC0101010, 0xD0101010);
        }
        contentLayer.render(poseStack, mouseX, mouseY, partialTicks);
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
        contentLayer.keyPressed(key, scanCode, modifiers);
        return super.keyPressed(key, scanCode, modifiers);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return contentLayer.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return contentLayer.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        return contentLayer.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollDelta) {
        return contentLayer.mouseScrolled(mouseX, mouseY, scrollDelta);
    }

    @Override
    public boolean keyReleased(int key, int scanCode, int modifiers) {
        return contentLayer.keyReleased(key, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char charIn, int keyIn) {
        return contentLayer.charTyped(charIn, keyIn);
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        contentLayer.mouseMoved(mouseX, mouseY);
    }
}
