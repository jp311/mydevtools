package com.mysoft.devtools.views.settings;

import com.intellij.ui.components.JBCheckBox;
import com.mysoft.devtools.dtos.MysoftSettingsDTO;
import com.mysoft.devtools.services.AppSettingsStateService;

import javax.swing.*;

/**
 * @author hezd   2023/7/10
 */
public class PreferencesComponent extends BaseSettingsComponent {
    private JPanel contentPanel;
    private JBCheckBox ckUpdate;
    private MysoftSettingsDTO settings = AppSettingsStateService.getInstance().getState();

    @Override
    public JComponent getContentPanel() {
        ckUpdate.setSelected(settings.checkUpdate);
        return contentPanel;
    }

    @Override
    public JComponent getPreferredFocusedComponent() {
        return contentPanel;
    }

    @Override
    public boolean isModified() {
        return ckUpdate.isSelected() != settings.checkUpdate;
    }

    @Override
    public void apply() {
        settings.checkUpdate = ckUpdate.isSelected();
        AppSettingsStateService.getInstance().loadState(settings);
    }

    @Override
    public void reset() {
        ckUpdate.setSelected(settings.checkUpdate);
    }

    @Override
    public void disposeUIResources() {

    }
}
