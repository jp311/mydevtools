package com.mysoft.devtools.actions.toolactions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.mysoft.devtools.views.users.MetadataSyncDialog;
import org.jetbrains.annotations.NotNull;

/**
 * 元数据同步
 *
 * @author hezd   2023/5/6
 */
public class MetadataSyncAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        MetadataSyncDialog dialog = new MetadataSyncDialog();
        dialog.show();
    }
}
