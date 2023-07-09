package com.mysoft.devtools.settings;

import com.mysoft.devtools.bundles.LocalBundle;
import com.mysoft.devtools.views.settings.AIComponent;
import org.jetbrains.annotations.Nls;

/**
 * @author hezd   2023/7/9
 */
public class AIConfigurable extends BaseConfigurable<AIComponent> {
    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return LocalBundle.message("ai.configurable.title");
    }

    @Override
    public AIComponent getSettingsComponent() {
        return new AIComponent();
    }
}
