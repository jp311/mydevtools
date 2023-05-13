package com.mysoft.devtools.settings;

import com.intellij.openapi.options.Configurable;
import com.mysoft.devtools.views.settings.GenerateCodeTemplateComponent;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * @author hezd 2023/4/30
 */
public class GenerateCodeTemplateConfigurable implements Configurable {
    private GenerateCodeTemplateComponent generateCodeTemplateComponent;

    // A default constructor with no arguments is required because this implementation
    // is registered as an applicationConfigurable EP

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "SDK: Application Settings Example";
    }

    @Override
    public JComponent getPreferredFocusedComponent() {
        return generateCodeTemplateComponent.getPreferredFocusedComponent();
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        generateCodeTemplateComponent = new GenerateCodeTemplateComponent();
        return generateCodeTemplateComponent.getContentPanel();
    }

    @Override
    public boolean isModified() {
        return generateCodeTemplateComponent.isModified();
    }

    @Override
    public void apply() {
        generateCodeTemplateComponent.apply();
    }

    @Override
    public void reset() {
        generateCodeTemplateComponent.reset();
    }

    @Override
    public void disposeUIResources() {
        generateCodeTemplateComponent = null;
    }
}
