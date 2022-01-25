package com.won983212.rewind.ui.screen;

import com.won983212.rewind.ui.UIScreen;
import com.won983212.rewind.ui.component.panel.Panel;
import com.won983212.rewind.ui.component.ReplayListItem;
import com.won983212.rewind.util.Lang;

public class ReplayListScreen extends UIScreen {
    public ReplayListScreen() {
        super(Lang.getComponent("title.replay_list"));
        disableDrawBackground();
    }

    @Override
    protected void init(Panel rootPanel) {
        rootPanel.addComponent(new ReplayListItem().setPosition(20, 20));
        rootPanel.addComponent(new ReplayListItem().setPosition(110, 20));
        rootPanel.addComponent(Buttons.createTextButton("hello", (b) -> System.out.println("Clicked")).setPosition(30, 100));
    }
}
