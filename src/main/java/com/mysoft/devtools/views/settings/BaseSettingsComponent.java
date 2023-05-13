package com.mysoft.devtools.views.settings;

import com.mysoft.devtools.views.BaseComponent;

import javax.swing.*;

/**
 * @author hezd 2023/4/30
 */
public abstract class BaseSettingsComponent extends BaseComponent {


    public abstract JComponent getPreferredFocusedComponent();

    public abstract boolean isModified();

    public abstract void apply();

    public abstract void reset() ;

    public abstract void disposeUIResources();
}
