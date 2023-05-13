package com.mysoft.devtools.views.settings;

import javax.swing.*;

/**
 * @author hezd   2023/5/6
 */
public class EnvironmentComponent extends BaseSettingsComponent {
    private JPanel contentPanel;

    @Override
    public JComponent getContentPanel() {
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
