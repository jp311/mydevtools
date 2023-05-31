package com.mysoft.devtools.settings;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.mysoft.devtools.views.settings.BaseSettingsComponent;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * @author hezd 2023/5/31
 */
public abstract class BaseConfigurable<T extends BaseSettingsComponent> implements Configurable {

    protected BaseSettingsComponent settingsComponent;

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public abstract String getDisplayName();

    public abstract T getSettingsComponent();

    @Override
    public @Nullable JComponent createComponent() {
        if (settingsComponent == null) {
            settingsComponent = getSettingsComponent();
        }
        return settingsComponent.getContentPanel();
    }

    @Override
    public JComponent getPreferredFocusedComponent() {
        return settingsComponent.getContentPanel();
    }

    @Override
    public boolean isModified() {
        return settingsComponent.isModified();
    }

    @Override
    public void apply() throws ConfigurationException {
        settingsComponent.apply();
    }

    @Override
    public void reset() {
        settingsComponent.reset();
    }

    @Override
    public void disposeUIResources() {
        settingsComponent.disposeUIResources();
        settingsComponent = null;
    }
}
