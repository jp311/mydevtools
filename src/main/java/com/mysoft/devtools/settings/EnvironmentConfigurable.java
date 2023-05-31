package com.mysoft.devtools.settings;

import com.mysoft.devtools.views.settings.EnvironmentComponent;
import org.jetbrains.annotations.Nls;

import java.util.ResourceBundle;

/**
 * @author hezd 2023/4/27
 */
public class EnvironmentConfigurable extends BaseConfigurable<EnvironmentComponent> {
    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        ResourceBundle bundle = ResourceBundle.getBundle("CommonBundle");
        return bundle.getString("devtools.settings.environment.name");
    }

    @Override
    public EnvironmentComponent getSettingsComponent() {
        return new EnvironmentComponent();
    }
}
