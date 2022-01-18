package com.won983212.rewind.ui.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import com.won983212.rewind.ui.animate.EasingFunction;
import com.won983212.rewind.ui.animate.InterpolatedFloat;
import com.won983212.rewind.ui.animate.InterpolatedValue;
import com.won983212.rewind.ui.component.RecordingIndicator;
import com.won983212.rewind.ui.component.TextLabel;
import com.won983212.rewind.util.Lang;

public class RecordingStatusScreen extends AbstractScreen {

    private InterpolatedValue<Float> yPosition;
    private boolean destroyed = false;

    protected void init() {
        this.yPosition = new InterpolatedFloat(10f, 10f, 0);
        this.rootPanel.setMargin(4);
        this.rootPanel.setPosition(10, -20);
        this.rootPanel.setBackgroundColor(0x99000000);
        this.rootPanel.addComponent(new RecordingIndicator().setBounds(0, 0, 9, 9));
        this.rootPanel.addComponent(new TextLabel(Lang.getString("recording")).setX(15));
    }

    public void animateShow() {
        this.yPosition = new InterpolatedFloat(-20f, 10f, 10)
                .setEasing(EasingFunction.QUAD_IN);
    }

    public void animateHide() {
        this.yPosition = new InterpolatedFloat(10f, -20f, 10)
                .setEasing(EasingFunction.QUAD_OUT)
                .setOnComplete(() -> destroyed = true);
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        rootPanel.setY(yPosition.tickAndGet(partialTicks));
        super.render(poseStack, mouseX, mouseY, partialTicks);
    }

    public boolean isDestroyed() {
        return destroyed;
    }
}
