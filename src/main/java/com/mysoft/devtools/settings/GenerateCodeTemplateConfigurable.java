package com.mysoft.devtools.settings;

import com.mysoft.devtools.views.settings.GenerateCodeTemplateComponent;
import org.jetbrains.annotations.Nls;

/**
 * @author hezd 2023/4/30
 */
public class GenerateCodeTemplateConfigurable extends BaseConfigurable<GenerateCodeTemplateComponent> {
    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "SDK: Application Settings Example";
    }

    @Override
    public GenerateCodeTemplateComponent getSettingsComponent() {
        return new GenerateCodeTemplateComponent();
    }
}
