package com.won983212.rewind.ui.component;

import com.mojang.blaze3d.vertex.PoseStack;
import com.won983212.rewind.ui.animate.InterpolatedColor;
import com.won983212.rewind.ui.animate.InterpolatedValue;
import com.won983212.rewind.util.Color;

public class RecordingIndicator extends AbstractComponent {
    private final InterpolatedValue<Color> color;

    public RecordingIndicator() {
        this.color = new InterpolatedColor(Color.of(0x99ff0000), Color.of(0x00ff0000), 40)
                .setLooping(InterpolatedValue.LoopingType.CONTINUOUS_LOOP);
        setSize(10, 10);
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        AbstractComponent.fillFloat(poseStack, x, y, x + width, y + height, 0x77ff0000);
        AbstractComponent.fillFloat(poseStack, x + 2, y + 2, x + width - 2, y + height - 2, color.tickAndGet(partialTicks).getArgb());
    }
}