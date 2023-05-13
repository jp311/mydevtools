package com.mysoft.devtools.settings;

import com.intellij.openapi.options.Configurable;
import com.mysoft.devtools.views.settings.EnvironmentComponent;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.ResourceBundle;

/**
 * @author hezd 2023/4/27
 */
public class EnvironmentConfigurable implements Configurable {
    private EnvironmentComponent environmentComponent;

    // A default constructor with no arguments is required because this implementation
    // is registered as an applicationConfigurable EP

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        ResourceBundle bundle = ResourceBundle.getBundle("SettingsBundle");
        return bundle.getString("settings.names.environment");
    }

    @Override
    public JComponent getPreferredFocusedComponent() {
        return environmentComponent.getContentPanel();
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        environmentComponent = new EnvironmentComponent();
        return environmentComponent.getContentPanel();
    }

    @Override
    public boolean isModified() {
        return environmentComponent.isModified();
    }

    @Override
    public void apply() {
        environmentComponent.apply();
    }

    @Override
    public void reset() {
        environmentComponent.reset();
    }

    @Override
    public void disposeUIResources() {
        environmentComponent.disposeUIResources();
        environmentComponent = null;
    }
}
