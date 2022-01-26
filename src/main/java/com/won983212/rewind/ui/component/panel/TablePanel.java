package com.won983212.rewind.ui.component.panel;

import com.won983212.rewind.ui.ComponentArea;
import com.won983212.rewind.ui.ComponentVec2;
import com.won983212.rewind.ui.component.AbstractComponent;

/**
 * 고정된 column 개수를 갖는 테이블 패널입니다. row 개수는 컴포넌트 개수에 따라서 결정됩니다.
 * 각 column의 너비는 column에 존재하는 모든 요소들중에서 최대인 너비로 설정됩니다. row 높이도 같은 방식으로 정합니다.
 */
public class TablePanel extends Panel {
    private float[] columnSize;
    private float[] rowSize;
    private float gap = 2;
    private int columns;


    public TablePanel(int columns) {
        setColumns(columns);
    }

    public TablePanel setColumns(int columns) {
        this.columns = columns;
        return this;
    }

    public TablePanel setGap(float gap) {
        this.gap = gap;
        return this;
    }

    private void calculateLineSize() {
        int rows = (int) Math.ceil(components.size() / (float) columns);
        columnSize = new float[columns];
        rowSize = new float[rows];

        int idx = 0;
        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                if (idx >= components.size()) {
                    row = rows;
                    break;
                }
                ComponentVec2 componentSize = components.get(idx++).getMinSizeWithMargin();
                columnSize[column] = Math.max(columnSize[column], componentSize.x);
                rowSize[row] = Math.max(rowSize[row], componentSize.y);
            }
        }
    }

    @Override
    public ComponentVec2 measureMinSize() {
        if (components.size() == 0) {
            return new ComponentVec2();
        }

        calculateLineSize();
        ComponentVec2 size = new ComponentVec2();
        for (float width : columnSize) {
            size.x += width + gap;
        }
        for (float height : rowSize) {
            size.y += height + gap;
        }

        return size.add(-gap, -gap);
    }

    @Override
    public void arrangeChildren(ComponentArea available) {
        calculateLineSize();

        float x = available.x;
        float y = available.y;
        int col = 0;
        int row = 0;

        for (AbstractComponent component : components) {
            ComponentArea rect = new ComponentArea(x, y, columnSize[col], rowSize[row]);
            component.arrange(rect);
            x += columnSize[col] + gap;
            if (++col >= columns) {
                col = 0;
                x = 0;
                y += rowSize[row++] + gap;
            }
        }
    }
}
