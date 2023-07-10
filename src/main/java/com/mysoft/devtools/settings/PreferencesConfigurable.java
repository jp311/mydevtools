package com.mysoft.devtools.settings;

import com.mysoft.devtools.bundles.LocalBundle;
import com.mysoft.devtools.views.settings.PreferencesComponent;
import org.jetbrains.annotations.Nls;

/**
 * @author hezd   2023/7/10
 */
public class PreferencesConfigurable extends BaseConfigurable<PreferencesComponent> {
    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return LocalBundle.message("devtools.settings.preferences.name");
    }

    @Override
    public PreferencesComponent getSettingsComponent() {
        return new PreferencesComponent();
    }
}
