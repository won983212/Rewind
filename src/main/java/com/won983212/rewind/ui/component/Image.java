package com.won983212.rewind.ui.component;

import com.mojang.blaze3d.vertex.PoseStack;

public class Image extends AbstractComponent {
    // TODO Render Image
    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        super.render(poseStack, mouseX, mouseY, partialTicks);
        AbstractComponent.fillFloat(poseStack, 0, 0, width, height, 0xff00ff00);
    }
}
