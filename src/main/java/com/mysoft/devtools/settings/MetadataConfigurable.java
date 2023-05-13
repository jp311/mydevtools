package com.mysoft.devtools.settings;

import com.intellij.openapi.options.Configurable;
import com.mysoft.devtools.views.settings.MetadataComponent;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.ResourceBundle;

/**
 * 元数据相关设置
 * @author hezd   2023/5/6
 */
public class MetadataConfigurable implements Configurable {
    private MetadataComponent metadataComponent;

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
        return metadataComponent.getContentPanel();
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        metadataComponent = new MetadataComponent();
        return metadataComponent.getContentPanel();
    }

    @Override
    public boolean isModified() {
        return metadataComponent.isModified();
    }

    @Override
    public void apply() {
        metadataComponent.apply();
    }

    @Override
    public void reset() {
        metadataComponent.reset();
    }

    @Override
    public void disposeUIResources() {
        metadataComponent.disposeUIResources();
        metadataComponent = null;
    }
}
