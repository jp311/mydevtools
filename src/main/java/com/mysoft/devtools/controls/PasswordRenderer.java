package com.mysoft.devtools.controls;

import com.intellij.ui.components.JBPasswordField;

import javax.swing.*;
import java.awt.*;

/**
 * @author hezd   2023/6/2
 */
public class PasswordRenderer extends MyDefaultTableCellRenderer {
    private final JBPasswordField passwordField = new JBPasswordField();

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        if (isSelected) {
            passwordField.setBackground(table.getSelectionBackground());
            passwordField.setForeground(table.getSelectionForeground());
        } else {
            passwordField.setBackground(table.getBackground());
            passwordField.setForeground(table.getForeground());
        }
        passwordField.setText(String.valueOf(value));
        passwordField.setOpaque(true);

        if (super.isErrorCell(row, column)) {
            passwordField.setBorder(ERROR_BORDER);
        } else {
            passwordField.setBorder(null);
        }
        return passwordField;
    }
}
