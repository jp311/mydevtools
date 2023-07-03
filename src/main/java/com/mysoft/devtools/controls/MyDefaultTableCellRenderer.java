package com.mysoft.devtools.controls;

import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBLabel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author hezd   2023/6/3
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class MyDefaultTableCellRenderer extends DefaultTableCellRenderer {
    protected static final Border ERROR_BORDER = new LineBorder(JBColor.RED);

    //key = rowIndex ，value = colIndexs
    private static Map<Integer, ArrayList<Integer>> errCells = new HashMap<>();

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        JBLabel label = new JBLabel(String.valueOf(value));
        if (isSelected) {
            label.setBackground(table.getSelectionBackground());
            label.setForeground(table.getSelectionForeground());
        } else {
            label.setBackground(table.getBackground());
            label.setForeground(table.getForeground());
        }

        label.setBorder(null);

        if (isErrorCell(row, column)) {
            label.setBorder(ERROR_BORDER);
        }

        label.setHorizontalAlignment(SwingConstants.CENTER);
        return label;
    }


    public boolean isErrorCell(int row, int col) {
        if (errCells.containsKey(row)) {
            ArrayList<Integer> cols = errCells.get(row);
            return cols.contains(col);
        }
        return false;
    }

    public void addErrorCell(int row, int col) {
        if (!errCells.containsKey(row)) {
            errCells.put(row, new ArrayList<>());
        }
        errCells.get(row).add(col);
    }

    public void removeErrorCell(int row, int col) {
        if (!errCells.containsKey(row)) {
            return;
        }
        errCells.get(row).removeIf(x -> x == col);
    }

    public void removeErrorRow(int row) {
        if (!errCells.containsKey(row)) {
            return;
        }
        errCells.remove(row);
    }
}
