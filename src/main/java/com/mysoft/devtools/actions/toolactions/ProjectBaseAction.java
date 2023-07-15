package com.mysoft.devtools.actions.toolactions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import org.jetbrains.annotations.NotNull;

/**
 * @author hezd   2023/7/15
 */
public abstract class ProjectBaseAction extends AnAction {
    @Override
    public void update(@NotNull AnActionEvent e) {
        Presentation presentation = e.getPresentation();
        presentation.setEnabled(e.getProject() != null);
    }
}
