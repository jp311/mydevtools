package com.mysoft.devtools.settings;

import com.mysoft.devtools.views.settings.HomeComponent;
import org.jetbrains.annotations.Nls;

/**
 * <a href="https://plugins.jetbrains.com/docs/intellij/settings.html">...</a>
 *
 * @author hezd 2023/4/22
 */
public class HomeConfigurable extends BaseConfigurable<HomeComponent> {

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "SDK: Application Settings Example";
    }

    @Override
    public HomeComponent getSettingsComponent() {
        return new HomeComponent();
    }
}
