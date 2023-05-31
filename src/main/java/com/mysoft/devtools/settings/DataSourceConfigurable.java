package com.mysoft.devtools.settings;

import com.mysoft.devtools.views.settings.DataSourceComponent;
import org.jetbrains.annotations.Nls;

import java.util.ResourceBundle;

/**
 * @author hezd 2023/5/31
 */
public class DataSourceConfigurable extends BaseConfigurable<DataSourceComponent> {
    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        ResourceBundle bundle = ResourceBundle.getBundle("CommonBundle");
        return bundle.getString("devtools.settings.datasource.name");
    }

    @Override
    public DataSourceComponent getSettingsComponent() {
        return new DataSourceComponent();
    }
}
