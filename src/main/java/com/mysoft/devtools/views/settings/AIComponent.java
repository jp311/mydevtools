package com.mysoft.devtools.views.settings;

import com.intellij.ui.components.JBPasswordField;
import com.mysoft.devtools.dtos.MysoftSettingsDTO;
import com.mysoft.devtools.services.AppSettingsStateService;

import javax.swing.*;
import java.util.Objects;

/**
 * @author hezd   2023/7/9
 */
public class AIComponent extends BaseSettingsComponent {
    private JPanel contentPanel;
    private JTextField txtHost;
    private JBPasswordField txtUserName;
    private JBPasswordField txtPassword;

    private final MysoftSettingsDTO settings = AppSettingsStateService.getInstance().getState();

    @Override
    public JComponent getContentPanel() {
        txtUserName.setPasswordIsStored(true);
        txtPassword.setPasswordIsStored(true);

        txtHost.setText(settings.aiConfigurable.getHost());
        txtUserName.setText(settings.aiConfigurable.getUserName());
        txtPassword.setText(settings.aiConfigurable.getPassword());

        return contentPanel;
    }

    @Override
    public JComponent getPreferredFocusedComponent() {
        return txtHost;
    }

    @Override
    public boolean isModified() {
        return !Objects.equals(settings.aiConfigurable.getUserName(), getText(txtUserName))
                || !Objects.equals(settings.aiConfigurable.getPassword(), getText(txtPassword))
                || !Objects.equals(settings.aiConfigurable.getHost(), txtHost.getText());
    }

    private String getText(JBPasswordField passwordField) {
        return new String(passwordField.getPassword());
    }

    @Override
    public void apply() {
        settings.aiConfigurable.setHost(txtHost.getText());
        settings.aiConfigurable.setUserName(getText(txtUserName));
        settings.aiConfigurable.setPassword(getText(txtPassword));
        AppSettingsStateService.getInstance().loadState(settings);
    }

    @Override
    public void reset() {
        txtHost.setText(settings.aiConfigurable.getHost());
        txtUserName.setText(settings.aiConfigurable.getUserName());
        txtPassword.setText(settings.aiConfigurable.getPassword());
    }

    @Override
    public void disposeUIResources() {

    }
}
