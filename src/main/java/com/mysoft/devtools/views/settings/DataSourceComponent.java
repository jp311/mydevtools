package com.mysoft.devtools.views.settings;

import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.components.JBScrollPane;
import com.mysoft.devtools.bundles.LocalBundle;
import com.mysoft.devtools.controls.DataSourceTable;
import com.mysoft.devtools.dtos.DbLinkDTO;
import com.mysoft.devtools.dtos.MysoftSettingsDTO;
import com.mysoft.devtools.services.AppSettingsStateService;
import com.mysoft.devtools.utils.idea.psi.IdeaNotifyUtil;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author hezd 2023/5/31
 */
public class DataSourceComponent extends BaseSettingsComponent {
    private JPanel contentPanel;
    private DataSourceTable table;

    private MysoftSettingsDTO settings = AppSettingsStateService.getInstance().getState();

    @Override
    public JComponent getContentPanel() {
        table = new DataSourceTable();
        ToolbarDecorator decorator = ToolbarDecorator.createDecorator(table);
        decorator.setAddAction(action -> table.newRow())
                .setRemoveAction(action -> table.removeSelectedRow());
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
        long hashCode1 = table.getData().stream().mapToInt(DbLinkDTO::hashCode).sum();
        long hashCode2 = 0L;
        if (settings != null && settings.dataSources != null) {
            hashCode2 = settings.dataSources.stream().mapToInt(DbLinkDTO::hashCode).sum();
        }

        return hashCode1 != hashCode2;
    }

    @Override
    public void apply() {
        if (!table.validateData()) {
            IdeaNotifyUtil.dialogError(LocalBundle.message("devtools.settings.datasource.validate.fail"));
            return;
        }
        if (settings == null) {
            settings = new MysoftSettingsDTO();
        }

        Map<String, List<DbLinkDTO>> collect = table.getData().stream().collect(Collectors.groupingBy(DbLinkDTO::getAlias));
        for (String name : collect.keySet()) {
            if (collect.get(name).size() > 1) {
                IdeaNotifyUtil.dialogError(LocalBundle.message("devtools.settings.datasource.name.repeat", name));
                return;
            }
        }
        settings.dataSources = table.getData();
        AppSettingsStateService.getInstance().loadState(settings);
    }

    @Override
    public void reset() {
        if (settings == null) {
            return;
        }
        table.setData(settings.dataSources);
    }

    @Override
    public void disposeUIResources() {
        table.setEnabled(false);
        contentPanel.setEnabled(false);
    }
}
