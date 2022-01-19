package com.won983212.rewind.ui.component;

import com.won983212.rewind.ui.component.panel.Panel;

public class ReplayListItem extends Panel {
    public ReplayListItem() {
        super();
        addComponent(new Image().setSize(70, 30));
        addComponent(new Label("Title").setY(32));
        addComponent(new Label("21-01-19 17:19:21").setScale(0.5f).setMaxWidth(70)
                .useDefaultFont().setY(42));
        setBackgroundColor(0x99101010);
        setMargin(2);
    }
}
