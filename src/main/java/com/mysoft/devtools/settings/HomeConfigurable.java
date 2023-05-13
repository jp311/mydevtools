package com.mysoft.devtools.settings;

import com.intellij.openapi.options.Configurable;
import com.mysoft.devtools.views.settings.HomeComponent;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * https://plugins.jetbrains.com/docs/intellij/settings.html
 * @author hezd 2023/4/22
 */
public class HomeConfigurable implements Configurable {
    private HomeComponent homeComponent;

    // A default constructor with no arguments is required because this implementation
    // is registered as an applicationConfigurable EP

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "SDK: Application Settings Example";
    }

    @Override
    public JComponent getPreferredFocusedComponent() {
        return homeComponent.getContentPanel();
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        homeComponent = new HomeComponent();
        return homeComponent.getContentPanel();
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
        homeComponent = null;
    }
}
