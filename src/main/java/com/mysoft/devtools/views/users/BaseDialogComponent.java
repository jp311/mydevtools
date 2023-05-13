package com.mysoft.devtools.views.users;

import com.intellij.openapi.ui.DialogWrapper;

import javax.swing.*;

/**
 * @author hezd   2023/5/7
 */
public abstract class BaseDialogComponent extends DialogWrapper {

    protected BaseDialogComponent() {
        super(true);
    }
    @Override
    protected abstract JComponent createCenterPanel() ;
}
