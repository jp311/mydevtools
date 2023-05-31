package com.mysoft.devtools.actions.toolactions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.mysoft.devtools.views.users.ExecuteSqlDialog;
import org.jetbrains.annotations.NotNull;

/**
 * @author hezd 2023/5/31
 */
public class ExecuteSqlAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        ExecuteSqlDialog dialog = new ExecuteSqlDialog();
        dialog.show();
    }
}
