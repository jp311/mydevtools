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

/**
 * @author hezd   2023/6/3
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class MyDefaultTableCellRenderer extends DefaultTableCellRenderer {
    private boolean hasError;
    protected static final Border ERROR_BORDER = new LineBorder(JBColor.RED);

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

        if (hasError) {
            label.setBorder(ERROR_BORDER);
        } else {
            label.setBorder(null);
        }

        label.setHorizontalAlignment(SwingConstants.CENTER);
        return label;
    }
}
