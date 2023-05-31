package com.mysoft.devtools.settings;

import com.mysoft.devtools.views.settings.HomeComponent;
import org.jetbrains.annotations.Nls;

import java.util.ResourceBundle;

/**
 * 元数据相关设置
 *
 * @author hezd   2023/5/6
 */
public class MetadataConfigurable extends BaseConfigurable<HomeComponent> {
    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        ResourceBundle bundle = ResourceBundle.getBundle("CommonBundle");
        return bundle.getString("devtools.settings.environment.name");
    }

    @Override
    public HomeComponent getSettingsComponent() {
        return new HomeComponent();
    }
}
