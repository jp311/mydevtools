package com.mysoft.devtools.views.toolwindows;

import com.mysoft.devtools.views.BaseComponent;

import javax.swing.*;

/**
 * @author hezd 2023/5/31
 */
public abstract class BaseToolWindow extends BaseComponent {
    @Override
    public abstract JComponent getContentPanel();

    public abstract String getDisplayName();

    public abstract boolean isLockable();
}
