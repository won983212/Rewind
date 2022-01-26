package com.won983212.rewind.ui.component;

import com.won983212.rewind.ui.Theme;
import com.won983212.rewind.ui.Thickness;
import com.won983212.rewind.ui.component.panel.StackPanel;

public class ReplayListItem extends StackPanel {
    public ReplayListItem() {
        super(Orientation.VERTICAL);
        addComponent(new Image().setPreferredMinimalSize(70, 30));
        addComponent(new Label("Title").setMaxWidth(70));
        addComponent(new Label("21-01-19 17:19:21").setScale(0.5f).setMaxWidth(70).useDefaultFont());
        setBackgroundColor(Theme.BACKGROUND);
        setPadding(new Thickness(2));
    }
}
