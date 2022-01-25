package com.won983212.rewind.ui.component.panel;

import com.won983212.rewind.ui.ComponentArea;
import com.won983212.rewind.ui.ComponentVec2;
import com.won983212.rewind.ui.component.AbstractComponent;

import java.util.ArrayList;

/**
 * GridPanel은 요소들을 테이블 방식으로 배치합니다.
 * 패널을 정해진 규칙에 따라 한 개 이상의 행과 열로 나누어 요소들을 배치합니다.
 * 대부분의 UI구성에 유용하게 사용됩니다.
 */
public class GridPanel extends Panel {
    private final ArrayList<CellLength> columns = new ArrayList<>();
    private final ArrayList<CellLength> rows = new ArrayList<>();


    /**
     * 여러개의 셀 크기를 택스트로 한번에 정의합니다. 셀 크기는 따옴표(,)로 구분합니다.
     * <ul>
     *  <li><code>FIXED</code>: "크기" (고정 크기 3.14 = "3.14")</li>
     *  <li><code>AUTO</code>: "auto"</li>
     *  <li><code>ALLOCATED</code>: "*비율" (가변 비율 크기 3 = "*3", 비율이 1이면 숫자 생략 가능)</li>
     * </ul>
     * ex) 고정 크기 3.14, 자동, 3배수 비율 크기 = "3.14,auto,*3"
     */
    public void addColumns(String text) {
        for (String ent : text.split(",")) {
            if (ent != null && ent.length() > 0) {
                addColumn(new LengthDefinition(ent.trim()));
            }
        }
    }

    public void addColumn(LengthDefinition column) {
        columns.add(new CellLength(column, 0));
    }

    public void addEmptyColumn() {
        columns.add(new CellLength(new LengthDefinition(LengthType.ALLOCATED, 1), 0));
    }

    public void addRows(String text) {
        for (String ent : text.split(",")) {
            if (ent != null && ent.length() > 0) {
                addRow(new LengthDefinition(ent.trim()));
            }
        }
    }

    public void addRow(LengthDefinition row) {
        rows.add(new CellLength(row, 0));
    }

    public void addEmptyRow() {
        rows.add(new CellLength(new LengthDefinition(LengthType.ALLOCATED, 1), 0));
    }

    private void measureMaxSize() {
        for (CellLength c : columns) {
            c.maxDesiredLength = 0;
        }

        for (CellLength r : rows) {
            r.maxDesiredLength = 0;
        }

        for (AbstractComponent obj : components) {
            GridLayoutMetadata layout = getLayoutData(obj);
            ComponentVec2 desired = obj.getMinSizeWithMargin();

            CellLength column = columns.get(layout.x);
            column.maxDesiredLength = Math.max(column.maxDesiredLength, desired.x / layout.xSpan);

            CellLength row = rows.get(layout.y);
            row.maxDesiredLength = Math.max(row.maxDesiredLength, desired.y / layout.ySpan);
        }
    }

    private float[] calculateActualLength(float totalLen, ArrayList<CellLength> cellLen) {
        float[] result = new float[cellLen.size()];
        double scale = 0;
        for (int i = 0; i < cellLen.size(); i++) {
            CellLength cell = cellLen.get(i);
            if (cell.lengthDef.type == LengthType.FIXED) {
                result[i] = (int) cell.lengthDef.argument;
            } else if (cell.lengthDef.type == LengthType.AUTO) {
                result[i] = cell.maxDesiredLength;
            }
            if (cell.lengthDef.type != LengthType.ALLOCATED) {
                totalLen -= result[i];
            } else {
                scale += cell.lengthDef.argument;
            }
        }

        scale = totalLen / scale;
        for (int i = 0; i < cellLen.size(); i++) {
            CellLength cell = cellLen.get(i);
            if (cell.lengthDef.type == LengthType.ALLOCATED) {
                result[i] = (int) (scale * cell.lengthDef.argument);
            }
        }

        return result;
    }

    private double calculateMinLength(ArrayList<CellLength> cellLen) {
        double length = 0;
        double cellSize = 0;
        double totalCellScales = 0;
        for (CellLength cell : cellLen) {
            if (cell.lengthDef.type == LengthType.FIXED) {
                length += cell.lengthDef.argument;
            } else if (cell.lengthDef.type == LengthType.AUTO) {
                length += cell.maxDesiredLength;
            } else if (cell.lengthDef.type == LengthType.ALLOCATED) {
                cellSize = Math.max(cellSize, cell.maxDesiredLength / cell.lengthDef.argument);
                totalCellScales += cell.lengthDef.argument;
            }
        }
        length += totalCellScales * cellSize;
        return length;
    }

    @Override
    public ComponentVec2 measureMinSize() {
        ComponentVec2 size = new ComponentVec2();
        measureMaxSize();
        size.x = (int) calculateMinLength(columns);
        size.y = (int) calculateMinLength(rows);
        return getPositionOffset().toExpandedSize(size);
    }

    @Override
    public void arrangeChildren(ComponentArea available) {
        measureMaxSize();
        float[] widths = calculateActualLength(available.width, columns);
        float[] heights = calculateActualLength(available.height, rows);
        float[] stackedX = new float[widths.length + 1];
        float[] stackedY = new float[heights.length + 1];

        // calculate stackedX or Y
        stackedX[0] = available.x;
        stackedY[0] = available.y;
        for (int i = 1; i < stackedX.length; i++) {
            stackedX[i] = stackedX[i - 1] + widths[i - 1];
        }
        for (int i = 1; i < stackedY.length; i++) {
            stackedY[i] = stackedY[i - 1] + heights[i - 1];
        }

        for (AbstractComponent obj : components) {
            GridLayoutMetadata layout = getLayoutData(obj);
            ComponentArea availableChild = new ComponentArea(stackedX[layout.x], stackedY[layout.y], 0, 0);
            availableChild.width = stackedX[layout.x + layout.xSpan] - availableChild.x;
            availableChild.height = stackedY[layout.y + layout.ySpan] - availableChild.y;
            obj.arrange(availableChild);
        }
    }

    private static GridLayoutMetadata getLayoutData(AbstractComponent comp) {
        if (comp.layoutData == null || comp.layoutData.getClass() != GridLayoutMetadata.class) {
            comp.layoutData = new GridLayoutMetadata(0, 0, 1, 1);
        }
        return (GridLayoutMetadata) comp.layoutData;
    }

    public static AbstractComponent setLayout(AbstractComponent comp, int layoutX, int layoutY, int xSpan, int ySpan) {
        comp.layoutData = new GridLayoutMetadata(layoutX, layoutY, xSpan, ySpan);
        return comp;
    }

    /**
     * 셀 크기 타입
     * <ul>
     *  <li><code>FIXED</code>: 고정 크기입니다. 인수는 설정할 크기를 전달합니다.</li>
     *  <li><code>AUTO</code>: 자동 맞춤 크기입니다. 셀 내부 컴포넌트의 사이즈에 맞춰서 크기를 조절합니다. 인수는 없습니다.</li>
     *  <li><code>ALLOCATED</code>: 가변 비율 크기입니다. auto와 고정 크기셀을 할당하고 남은 영역을 비율에 맞춰 다른 allocated셀과 영역을 나누어 할당받습니다. 인수는 비율을 전달합니다.</li>
     * </ul>
     */
    public enum LengthType {
        FIXED,
        AUTO,
        ALLOCATED
    }

    public static class LengthDefinition {
        public double argument;
        public LengthType type;

        public LengthDefinition(LengthType type, double arg) {
            this.type = type;
            this.argument = arg;
        }

        public LengthDefinition(String text) {
            try {
                if (text.equals("auto")) {
                    this.type = LengthType.AUTO;
                } else if (text.charAt(0) == '*') {
                    this.type = LengthType.ALLOCATED;
                    this.argument = text.length() == 1 ? 1 : Double.parseDouble(text.substring(1));
                } else {
                    this.type = LengthType.FIXED;
                    this.argument = Double.parseDouble(text);
                }
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Unknown length definition format: " + text);
            }
        }
    }

    private static class CellLength {
        public LengthDefinition lengthDef;
        public float maxDesiredLength;

        public CellLength(LengthDefinition lenDef, int desired) {
            this.lengthDef = lenDef;
            this.maxDesiredLength = desired;
        }
    }

    private static class GridLayoutMetadata {
        public final int x;
        public final int y;
        public final int xSpan;
        public final int ySpan;

        private GridLayoutMetadata(int layoutX, int layoutY, int xSpan, int ySpan) {
            this.x = layoutX;
            this.y = layoutY;
            this.xSpan = xSpan;
            this.ySpan = ySpan;
        }
    }
}