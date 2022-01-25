package com.won983212.rewind.ui.screen;

import com.won983212.rewind.ui.UIScreen;
import com.won983212.rewind.ui.component.ReplayListItem;
import com.won983212.rewind.ui.component.panel.Panel;
import com.won983212.rewind.ui.component.panel.StackPanel;
import com.won983212.rewind.util.Lang;

public class ReplayListScreen extends UIScreen {
    public ReplayListScreen() {
        super(Lang.getComponent("title.replay_list"));
        disableDrawBackground();
        useBackscreenRendering();
        useContentCentering();
    }

    @Override
    protected void init(Panel rootPanel) {
        StackPanel panel = new StackPanel();
        panel.setGap(2);
        panel.addComponent(new ReplayListItem());
        panel.addComponent(new ReplayListItem());
        panel.addComponent(new ReplayListItem());
        rootPanel.addComponent(panel);
    }
}
