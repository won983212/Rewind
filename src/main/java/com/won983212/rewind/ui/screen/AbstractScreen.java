package com.won983212.rewind.ui.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import com.won983212.rewind.ui.component.ComponentPanel;

public class AbstractScreen {
    protected ComponentPanel rootPanel;

    public AbstractScreen() {
        this.rootPanel = new ComponentPanel();
        init();
    }

    protected void init() {
    }

    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        rootPanel.render(poseStack, mouseX, mouseY, partialTicks);
    }
}
