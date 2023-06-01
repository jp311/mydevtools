package com.mysoft.devtools.views.settings;

import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.components.JBScrollPane;
import com.mysoft.devtools.controls.DataSourceTable;

import javax.swing.*;
import java.awt.*;

/**
 * @author hezd 2023/5/31
 */
public class DataSourceComponent extends BaseSettingsComponent {
    private JPanel contentPanel;
    private DataSourceTable table;

    @Override
    public JComponent getContentPanel() {
        table = new DataSourceTable();
        ToolbarDecorator decorator = ToolbarDecorator.createDecorator(table);
        decorator.setAddAction(action -> {
            table.newRow();
        }).setRemoveAction(action -> {
            table.removeSelectedRow();
        });
        contentPanel.setLayout(new BorderLayout());
        JPanel panel = decorator.createPanel();
        contentPanel.add(panel, BorderLayout.NORTH);

        JBScrollPane scrollPane = new JBScrollPane(table);

        contentPanel.add(scrollPane, BorderLayout.CENTER);
        return contentPanel;
    }

    @Override
    public JComponent getPreferredFocusedComponent() {
        return contentPanel;
    }

    @Override
    public boolean isModified() {
        return false;
    }

    @Override
    public void apply() {

    }

    @Override
    public void reset() {

    }

    @Override
    public void disposeUIResources() {

    }
}
