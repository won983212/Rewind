package com.won983212.rewind.ui.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import com.won983212.rewind.RewindMod;
import com.won983212.rewind.ui.Theme;
import com.won983212.rewind.ui.UIScreen;
import com.won983212.rewind.ui.Arrange;
import com.won983212.rewind.ui.VerticalArrange;
import com.won983212.rewind.ui.animate.EasingFunction;
import com.won983212.rewind.ui.animate.InterpolatedFloat;
import com.won983212.rewind.ui.animate.InterpolatedValue;
import com.won983212.rewind.ui.component.Label;
import com.won983212.rewind.ui.component.RecordingIndicator;
import com.won983212.rewind.ui.decorator.SolidBackground;
import com.won983212.rewind.ui.component.panel.Panel;
import com.won983212.rewind.util.Lang;
import org.jetbrains.annotations.NotNull;

public class RecordingStatusScreen extends UIScreen {

    private InterpolatedValue<Float> yPosition;
    private boolean destroyed = false;
    private boolean isStarted = false;
    private Label label;


    public RecordingStatusScreen() {
        super(Lang.getComponent("title.record_status"));
        init();
    }

    protected void init(Panel rootPanel) {
        this.yPosition = new InterpolatedFloat(10f, 10f, 0);

        rootPanel.setMargin(4);
        rootPanel.setPosition(10, -20);
        rootPanel.addDecorator(new SolidBackground(Theme.BACKGROUND));

        RecordingIndicator indicator = new RecordingIndicator();
        indicator.setBounds(0, 0, 9, 9);
        rootPanel.addComponent(indicator);

        this.label = new Label(Lang.getString("record.starting"));
        this.label.setX(15);
        rootPanel.addComponent(label);

        Arrange.alignVertical(indicator, label, VerticalArrange.MIDDLE);
    }

    public void setRecordingStage() {
        label.setText("00:00:00");
        isStarted = true;
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
    public void render(@NotNull PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        if (isStarted && RewindMod.RECORDER.isRecording()) {
            label.setText(Lang.tickToTimeString(RewindMod.RECORDER.getTickTime()));
        }
        contentLayer.setY(yPosition.tickAndGet(partialTicks));
        super.render(poseStack, mouseX, mouseY, partialTicks);
    }

    public boolean isDestroyed() {
        return destroyed;
    }
}
