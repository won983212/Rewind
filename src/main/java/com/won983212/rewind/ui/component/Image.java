package com.won983212.rewind.ui.component;

import com.mojang.blaze3d.vertex.PoseStack;
import com.won983212.rewind.util.UIUtils;

public class Image extends AbstractComponent {
    // TODO Render Image
    @Override
    public void renderComponent(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        UIUtils.fillFloat(poseStack, 0, 0, getWidth(), getHeight(), 0xff00ff00);
    }
}
