package com.mysoft.devtools.actions.editoractions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.actionSystem.Presentation;
import org.jetbrains.annotations.NotNull;

/**
 * @author hezd   2023/7/15
 */
public class MysoftEditorRootActionGroup extends DefaultActionGroup {
    @Override
    public void update(@NotNull AnActionEvent e) {
        Presentation presentation = e.getPresentation();
        presentation.setVisible(false);
        presentation.setEnabled(false);
    }
}
