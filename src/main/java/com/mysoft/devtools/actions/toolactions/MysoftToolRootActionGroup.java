package com.mysoft.devtools.actions.toolactions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.actionSystem.Presentation;
import org.jetbrains.annotations.NotNull;

/**
 * @author hezd   2023/7/15
 */
public class MysoftToolRootActionGroup extends DefaultActionGroup {
    @Override
    public void update(@NotNull AnActionEvent e) {
        Presentation presentation = e.getPresentation();
        presentation.setVisible(true);
        presentation.setEnabled(true);
    }
}
